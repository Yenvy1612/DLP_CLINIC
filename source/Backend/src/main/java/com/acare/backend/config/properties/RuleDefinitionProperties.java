package com.acare.backend.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rule-definition")
@Getter
@Setter

public class RuleDefinitionProperties {
    private Integer timeBasedStarttime;
    private Integer timeBasedEndtime;
    private Integer volumeBased;
    private Integer requestLimit;
    
}
