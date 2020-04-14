package com.mfaotp.mfaotpgenerator.services;

import com.mfaotp.mfaotpgenerator.entities.UserOTPInfoEntity;
import com.mfaotp.mfaotpgenerator.entities.UserOTPInfoPK;
import com.mfaotp.mfaotpgenerator.repo.UserOTPInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class OtpService {

    @Autowired
    public UserOTPInfoRepository userOTPInfoRepository;

    @Autowired
    public EmailService myEmailService;

    public int generateOTP() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    public void generateAndSendOTP(Long userId, String emailId, String sessionId) {
        int generatedOTP = generateOTP();
        log.info("Generated otp for userid " + userId + " is " + generatedOTP);
        myEmailService.sendOtpMessage(emailId, "Email OTP Verification", "Hi, \n\n\nPlease verify your OTP. \n\nYour OTP number is " + generatedOTP +"\n\n\n Regards, \n MFA WARRIERS");
        userOTPInfoRepository.save(new UserOTPInfoEntity(new UserOTPInfoPK(userId, sessionId), String.valueOf(generatedOTP), "Active", new Timestamp(new Date().getTime())));
    }

    public boolean validateOTP(Long userId, String otpValue, String sessionId) {
        List<UserOTPInfoEntity> userOTPInfoEntityList = userOTPInfoRepository.findByUserOTPInfoPKAndStatusAndOtpValue(new UserOTPInfoPK(userId, sessionId), "Active", otpValue);
        if (userOTPInfoEntityList.size() > 0) {
            log.info("OTP validation successful for usedId " + userId);
            return true;
        }
        log.info("OTP validation failed for usedId " + userId);
        return false;
    }

    public void deactivateOTP(Long userId, String sessionId) {
        UserOTPInfoPK userOTPInfoPK = new UserOTPInfoPK(userId, sessionId);
        List<UserOTPInfoEntity> userOTPInfoEntityList = userOTPInfoRepository.findByUserOTPInfoPK(userOTPInfoPK);
        if (userOTPInfoEntityList.size() > 0) {
            UserOTPInfoEntity userOTPInfoEntity = userOTPInfoEntityList.get(0);
            userOTPInfoEntity.setStatus("InActive");
            userOTPInfoRepository.save(userOTPInfoEntity);
            log.info("OTP deactivated for userId " + userId);
        }
    }
}

