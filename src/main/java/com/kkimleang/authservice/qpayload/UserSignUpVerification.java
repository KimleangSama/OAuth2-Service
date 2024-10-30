package com.kkimleang.authservice.qpayload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UserSignUpVerification {
    private String email;
    private String username;
    private String code;
}
