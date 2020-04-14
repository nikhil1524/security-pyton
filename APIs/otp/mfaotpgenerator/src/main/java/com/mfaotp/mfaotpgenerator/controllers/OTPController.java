package com.mfaotp.mfaotpgenerator.controllers;

import com.mfaotp.mfaotpgenerator.dtos.UserDTO;
import com.mfaotp.mfaotpgenerator.repo.UserOTPInfoRepository;
import com.mfaotp.mfaotpgenerator.services.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/otp")
public class OTPController {

    @Autowired
    public UserOTPInfoRepository userOTPInfoRepository;

    @Autowired
    public OtpService otpService;


    @PostMapping("/generateAndSendOTP")
    public ResponseEntity<Object> generateAndSendOTP(@Valid @RequestBody UserDTO userDTO) {
        try {
            otpService.generateAndSendOTP(userDTO.getUserId(), userDTO.getEmailId(), userDTO.getSessionId());
        } catch (Exception e) {
            Map<String, String> result = new HashMap<>();
            result.put("statusCode", "-1");
            result.put("Message", "failed");

            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        Map<String, String> result = new HashMap<>();
        result.put("statusCode", "1");
        result.put("Message", "success");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/validateOTP")
    public ResponseEntity<Boolean> validateOTP(@Valid @RequestBody UserDTO userDTO) {
        return new ResponseEntity<>(otpService.validateOTP(userDTO.getUserId(), userDTO.getOtpValue(), userDTO.getSessionId()), HttpStatus.OK);
    }

    @PostMapping("/deactivateOTP/{userId}/{sessionId}")
    public ResponseEntity<String> deactivateOTP(@Valid @RequestBody UserDTO userDTO) {
        otpService.deactivateOTP(userDTO.getUserId(), userDTO.getSessionId());
        return new ResponseEntity<>("OTP deactivated", HttpStatus.OK);
    }

}
