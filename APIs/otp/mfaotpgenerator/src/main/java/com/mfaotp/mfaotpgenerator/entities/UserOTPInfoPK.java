package com.mfaotp.mfaotpgenerator.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserOTPInfoPK implements Serializable {

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "SESSION_ID")
    private String sessionId;
}
