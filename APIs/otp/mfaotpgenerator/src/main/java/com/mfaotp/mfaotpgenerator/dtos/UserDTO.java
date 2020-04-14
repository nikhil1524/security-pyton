package com.mfaotp.mfaotpgenerator.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotNull
    Long userId;

    @NotNull
    String sessionId;

    @Email
    String emailId;

    String otpValue;

}
