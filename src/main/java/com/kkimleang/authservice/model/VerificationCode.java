package com.kkimleang.authservice.model;

import jakarta.persistence.*;
import java.io.*;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_verification_codes")
public class VerificationCode extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String code;
    private Boolean isExpired = false;
    private Boolean isUsed = false;
}
