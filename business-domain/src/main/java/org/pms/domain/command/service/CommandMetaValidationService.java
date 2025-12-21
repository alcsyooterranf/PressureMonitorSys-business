package org.pms.domain.command.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.pms.types.BizCode;
import org.pms.types.BizException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alcsyooterranf
 * @program PressureMonitorSys-business
 * @description 指令元数据校验服务
 * @create 2025/12/15
 */
@Slf4j
@Service
public class CommandMetaValidationService {
	
	// Jackson ObjectMapper
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	// 模板变量引用正则表达式，匹配 ${args.xxx} 格式
	private static final Pattern TEMPLATE_VAR_PATTERN = Pattern.compile("\\$\\{args\\.([a-zA-Z0-9_]+)\\}");
	
	// JSON Schema Factory
	private static final JsonSchemaFactory SCHEMA_FACTORY = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
	
	/**
	 * 校验payload_schema字段
	 *
	 * @param payloadSchemaJson payload schema JSON字符串
	 * @param serviceIdentifier 服务标识符
	 */
	public void validatePayloadSchema(String payloadSchemaJson, String serviceIdentifier) {
		try {
			// 解析JSON字符串为JsonNode
			JsonNode schemaNode = OBJECT_MAPPER.readTree(payloadSchemaJson);

			// 校验必须包含serviceIdentifier、inputSchema、aepContentTemplate
			if (!schemaNode.has("serviceIdentifier")) {
				throw new BizException(BizCode.COMMAND_META_PAYLOAD_SCHEMA_MISSING_FIELD, "payload_schema必须包含serviceIdentifier字段");
			}
			if (!schemaNode.has("inputSchema")) {
				throw new BizException(BizCode.COMMAND_META_PAYLOAD_SCHEMA_MISSING_FIELD, "payload_schema必须包含inputSchema字段");
			}
			if (!schemaNode.has("aepContentTemplate")) {
				throw new BizException(BizCode.COMMAND_META_PAYLOAD_SCHEMA_MISSING_FIELD, "payload_schema必须包含aepContentTemplate字段");
			}

			// 校验service_identifier必须等于schema内serviceIdentifier
			String schemaServiceIdentifier = schemaNode.get("serviceIdentifier").asText();
			if (!serviceIdentifier.equals(schemaServiceIdentifier)) {
				throw new BizException(BizCode.COMMAND_META_SERVICE_IDENTIFIER_ERROR);
			}

			// 校验inputSchema结构
			JsonNode inputSchema = schemaNode.get("inputSchema");
			if (inputSchema == null || !inputSchema.isObject()) {
				throw new BizException(BizCode.COMMAND_META_PAYLOAD_SCHEMA_MISSING_FIELD, "inputSchema必须是JSON对象");
			}

			validateInputSchema(inputSchema);

			// 校验aepContentTemplate模板
			JsonNode aepContentTemplate = schemaNode.get("aepContentTemplate");
			validateAepContentTemplate(aepContentTemplate, inputSchema);

		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			log.error("校验payload_schema异常", e);
			throw new BizException(BizCode.COMMAND_META_PAYLOAD_SCHEMA_PARSE_ERROR, "payload_schema格式错误: " + e.getMessage());
		}
	}
	
	/**
	 * 校验inputSchema结构（使用JSON Schema Validator）
	 *
	 * @param inputSchema inputSchema JSON对象
	 */
	private void validateInputSchema(JsonNode inputSchema) {
		// 定义inputSchema的元Schema（meta-schema）
		String metaSchemaJson = """
				{
				  "$schema": "http://json-schema.org/draft-07/schema#",
				  "type": "object",
				  "required": ["type", "properties", "additionalProperties"],
				  "properties": {
				    "type": {
				      "type": "string",
				      "const": "object"
				    },
				    "properties": {
				      "type": "object",
				      "minProperties": 1,
				      "patternProperties": {
				        ".*": {
				          "type": "object",
				          "required": ["type"],
				          "properties": {
				            "type": {
				              "type": "string",
				              "enum": ["string", "integer", "boolean", "object", "array", "double"]
				            }
				          }
				        }
				      }
				    },
				    "required": {
				      "type": "array",
				      "items": {
				        "type": "string"
				      }
				    },
				    "additionalProperties": {
				      "type": "boolean",
				      "const": false
				    }
				  },
				  "additionalProperties": true
				}
				""";
		
		try {
			// 创建元Schema
			JsonNode metaSchemaNode = OBJECT_MAPPER.readTree(metaSchemaJson);
			JsonSchema metaSchema = SCHEMA_FACTORY.getSchema(metaSchemaNode);

			// 校验inputSchema
			Set<ValidationMessage> errors = metaSchema.validate(inputSchema);
			if (!errors.isEmpty()) {
				String errorMsg = errors.stream()
						.map(ValidationMessage::getMessage)
						.reduce((a, b) -> a + "; " + b)
						.orElse("未知错误");
				throw new BizException(BizCode.COMMAND_META_INPUT_SCHEMA_FORMAT_ERROR, "inputSchema格式错误: " + errorMsg);
			}

			// 额外校验：required中的字段必须都在properties中
			if (inputSchema.has("required")) {
				JsonNode required = inputSchema.get("required");
				JsonNode properties = inputSchema.get("properties");

				for (JsonNode requiredField : required) {
					String fieldName = requiredField.asText();
					if (!properties.has(fieldName)) {
						throw new BizException(BizCode.COMMAND_META_INPUT_SCHEMA_FORMAT_ERROR, "required中的字段 " + fieldName + " 必须在properties中定义");
					}
				}
			}

		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			log.error("校验inputSchema异常", e);
			throw new BizException(BizCode.COMMAND_META_INPUT_SCHEMA_VALIDATE_ERROR, "inputSchema校验异常: " + e.getMessage());
		}
	}
	
	/**
	 * 校验aepContentTemplate模板
	 *
	 * @param aepContentTemplate aepContentTemplate对象
	 * @param inputSchema        inputSchema JSON对象
	 */
	private void validateAepContentTemplate(JsonNode aepContentTemplate, JsonNode inputSchema) {
		// 模板最终必须能生成一个JSON对象（不能生成字符串、数组等）
		if (!aepContentTemplate.isObject()) {
			throw new RuntimeException("aepContentTemplate必须是JSON对象");
		}
		
		JsonNode properties = inputSchema.get("properties");
		
		// 提取模板中所有的变量引用
		Set<String> referencedFields = extractTemplateVariables(aepContentTemplate.toString());
		
		// 校验所有引用的字段都在properties中存在
		for (String field : referencedFields) {
			if (!properties.has(field)) {
				throw new RuntimeException("aepContentTemplate中引用的字段 ${args." + field + "} 在inputSchema的properties中不存在");
			}
		}
	}
	
	/**
	 * 提取模板中的所有变量引用
	 *
	 * @param template 模板字符串
	 * @return 引用的字段集合
	 */
	private Set<String> extractTemplateVariables(String template) {
		Set<String> variables = new HashSet<>();
		Matcher matcher = TEMPLATE_VAR_PATTERN.matcher(template);
		while (matcher.find()) {
			variables.add(matcher.group(1));
		}
		return variables;
	}
	
	/**
	 * 校验args参数是否符合inputSchema定义
	 *
	 * @param argsJson        args JSON字符串
	 * @param inputSchemaJson inputSchema JSON字符串
	 */
	public void validateArgs(String argsJson, String inputSchemaJson) {
		try {
			// 解析args和inputSchema
			JsonNode argsNode = OBJECT_MAPPER.readTree(argsJson);
			JsonNode inputSchemaNode = OBJECT_MAPPER.readTree(inputSchemaJson);

			// 使用JSON Schema Validator校验args
			JsonSchema schema = SCHEMA_FACTORY.getSchema(inputSchemaNode);
			Set<ValidationMessage> errors = schema.validate(argsNode);

			if (!errors.isEmpty()) {
				String errorMsg = errors.stream()
						.map(ValidationMessage::getMessage)
						.reduce((a, b) -> a + "; " + b)
						.orElse("未知错误");
				throw new BizException(BizCode.COMMAND_META_ARGS_VALIDATE_ERROR, "args参数校验失败: " + errorMsg);
			}

		} catch (BizException e) {
			throw e;
		} catch (Exception e) {
			log.error("校验args参数异常", e);
			throw new BizException(BizCode.COMMAND_META_ARGS_VALIDATE_ERROR, "args参数格式错误: " + e.getMessage());
		}
	}
	
}


