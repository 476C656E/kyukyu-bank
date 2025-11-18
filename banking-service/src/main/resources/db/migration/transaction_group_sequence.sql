-- 거래 그룹 시퀀스 테이블
CREATE TABLE IF NOT EXISTS transaction_group_sequence (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 시퀀스 초기화 예시 (필요시 사용)
-- INSERT INTO transaction_group_sequence VALUES ();
-- DELETE FROM transaction_group_sequence;
-- ALTER TABLE transaction_group_sequence AUTO_INCREMENT = 1;
