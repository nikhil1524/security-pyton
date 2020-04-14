package com.mfaotp.mfaotpgenerator.repo;

import com.mfaotp.mfaotpgenerator.entities.UserOTPInfoEntity;
import com.mfaotp.mfaotpgenerator.entities.UserOTPInfoPK;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOTPInfoRepository extends CrudRepository<UserOTPInfoEntity, UserOTPInfoPK> {

    List<UserOTPInfoEntity> findByUserOTPInfoPKAndStatusAndOtpValue(UserOTPInfoPK userOTPInfoPK, String status, String otpValue);

    List<UserOTPInfoEntity> findByUserOTPInfoPK(UserOTPInfoPK userOTPInfoPK);
}
