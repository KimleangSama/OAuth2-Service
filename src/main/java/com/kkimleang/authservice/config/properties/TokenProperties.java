package com.kkimleang.authservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties(prefix = "token")
public class TokenProperties {
    private Integer accessTokenExpiresHours;
    private Integer refreshTokenExpiresHours;
    private String domain;
}