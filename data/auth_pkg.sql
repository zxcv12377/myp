CREATE OR REPLACE PACKAGE auth_pkg AS
  PROCEDURE VERIFY_EMAIL_BY_TOKEN(p_token IN VARCHAR2, p_result OUT VARCHAR2);
END auth_pkg;
/

CREATE OR REPLACE PACKAGE BODY auth_pkg AS
  PROCEDURE VERIFY_EMAIL_BY_TOKEN(p_token IN VARCHAR2, p_result OUT VARCHAR2) IS
    v_email     VERIFICATION_TOKEN.email%TYPE;
    v_password  VERIFICATION_TOKEN.password%TYPE;
    v_nickname  VERIFICATION_TOKEN.nickname%TYPE;
    v_expires   VERIFICATION_TOKEN.expires_at%TYPE;
	v_id 		MEMBER.id%TYPE;

  BEGIN
    SELECT email, password, nickname, expires_at
      INTO v_email, v_password, v_nickname, v_expires
      FROM VERIFICATION_TOKEN
     WHERE token = p_token;

    IF v_expires < SYSTIMESTAMP THEN
      p_result := 'EXPIRED';
      DELETE FROM VERIFICATION_TOKEN WHERE token = p_token;
      RETURN;
    END IF;

    DECLARE
      cnt INTEGER;
    BEGIN
      SELECT COUNT(*) INTO cnt FROM MEMBER WHERE email = v_email;
      IF cnt > 0 THEN
        p_result := 'DUPLICATE';
        DELETE FROM VERIFICATION_TOKEN WHERE token = p_token;
        RETURN;
      END IF;
    END;

    INSERT INTO MEMBER(
    email, password, nickname, emailverified, created_date, updated_date)
    VALUES (
    v_email, v_password, v_nickname, 1, SYSTIMESTAMP, SYSTIMESTAMP
    )
    RETURNING id INTO v_id;
    
    INSERT INTO MEMBER_ROLES(member_id, role)
    VALUES (v_id, 'USER');

    DELETE FROM VERIFICATION_TOKEN WHERE token = p_token;
    p_result := 'SUCCESS';
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      p_result := 'INVALID';
    WHEN DUP_VAL_ON_INDEX THEN
      p_result := 'DUPLICATE';
    WHEN OTHERS THEN
      p_result := 'DB_ERROR';
  END VERIFY_EMAIL_BY_TOKEN;
END auth_pkg;
/
-- 컴파일 오류 확인
ALTER PACKAGE TEST.AUTH_PKG COMPILE BODY;
SELECT SEQUENCE, LINE, TEXT
  FROM ALL_ERRORS
 WHERE OWNER = 'TEST'
   AND NAME  = 'AUTH_PKG'
   AND TYPE  = 'PACKAGE BODY'
 ORDER BY SEQUENCE;
