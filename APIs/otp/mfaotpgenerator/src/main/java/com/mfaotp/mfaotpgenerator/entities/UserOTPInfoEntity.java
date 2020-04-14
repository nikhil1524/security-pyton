package com.mfaotp.mfaotpgenerator.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_OTP_INFO")
public class UserOTPInfoEntity {

    @EmbeddedId
    private UserOTPInfoPK userOTPInfoPK;

    @Basic
    @Column(name = "OTP_VALUE")
    private String otpValue;

    @Basic
    @Column(name = "STATUS")
    private String status;

    @Basic
    @Column(name = "DATE_CREATED")
    private Timestamp dateCreated;
}
