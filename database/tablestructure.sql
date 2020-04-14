
-------------------------------------------
--user information
-------------------------------------------

CREATE TABLE user_info
(
    USER_ID INT NOT NULL,
    USER_NAME VARCHAR(100) NOT NULL,
    EMAIL_ID VARCHAR(100),
    CONTACT_NUMBER VARCHAR(60) NOT NULL,
	PASSWORD_HASH VARCHAR(200) NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CREATED_BY VARCHAR(100) DEFAULT 'USER',
    UPDATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UPDATED_BY VARCHAR(100) DEFAULT 'USER',
    PRIMARY KEY (USER_ID)
);


-------------------------------------------
--user otp values
-------------------------------------------

create table user_otp (
USER_ID INT NOT NULL,
OTP VARCHAR(50) NOT NULL,
CREATED_AT DATETIME default CURRENT_TIMESTAMP,
IS_OTP_ACTIVE INT NOT NULL default 0,
);


-- user session
-------------------------------------------

-------------------------------------------

create table user_session(
USER_ID INT NOT NULL,
SESSION_ID VARCHAR(100) NOT NULL,
IS_SESSION_ACTIVE INT NOT NULL default 0,
SESSION_ACCESSED DATETIME default CURRENT_TIMESTAMP
);


-------------------------------------------
-- rest api mapping
-------------------------------------------

create table rest_api (
REST_POINT VARCHAR(100) NOT NULL,
REST_URI VARCHAR(250) NOT NULL
);


