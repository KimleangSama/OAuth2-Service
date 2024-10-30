package com.kkimleang.authservice.dto.user;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class VerificationCodeRequest {
    private UUID userId;
    private String code;
}
