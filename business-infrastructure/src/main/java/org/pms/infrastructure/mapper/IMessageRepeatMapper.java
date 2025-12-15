package org.pms.infrastructure.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMessageRepeatMapper {

    int insert(@Param("key") String key);

    int deleteByKey(@Param("key") String key);

}
