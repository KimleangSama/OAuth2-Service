package com.kkimleang.authservice.service.user;

import com.kkimleang.authservice.model.*;
import com.kkimleang.authservice.repository.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {
    private final VerificationCodeRepository verificationCodeRepository;

    public VerificationCode save(User user, String code) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setUser(user);
        return verificationCodeRepository.save(verificationCode);
    }

    public User findByCode(String verificationCode) {
        try {
            Optional<VerificationCode> code = verificationCodeRepository.findByCode(verificationCode);
            return code.map(VerificationCode::getUser).orElse(null);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return null;
        }
    }
}
