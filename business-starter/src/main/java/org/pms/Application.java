package org.pms;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@Configurable
@EnableFeignClients(basePackages = "org.pms.infrastructure.adapter.port")
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
