package com.kkimleang.authservice.config.oauth2.user;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public abstract sealed class OAuth2UserInfo permits GoogleOAuth2UserInfo, GithubOAuth2UserInfo, FacebookOAuth2UserInfo {
    protected Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getProfileURL();
}
