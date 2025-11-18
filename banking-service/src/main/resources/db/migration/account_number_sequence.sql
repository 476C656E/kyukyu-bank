-- 계좌번호 시퀀스를 위한 테이블 생성
CREATE TABLE IF NOT EXISTS account_number_sequence (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) COMMENT '계좌번호 일련번호 생성용 시퀀스 테이블';

-- 시퀀스 초기화 (필요한 경우)
-- INSERT INTO account_number_sequence VALUES ();
-- DELETE FROM account_number_sequence;
-- ALTER TABLE account_number_sequence AUTO_INCREMENT = 1;
