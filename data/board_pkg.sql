-- 페이지 나누기
CREATE OR REPLACE PROCEDURE get_board_page(
	p_page	IN NUMBER,
	p_size IN NUMBER,
	p_result OUT SYS_REFCURSOR
)
AS
BEGIN
	OPEN p_result FOR
	SELECT *
	FROM (
		SELECT b.*, ROW_NUMBER() OVER (ORDER BY created_date DESC) AS rn
		FROM board b
	)
	WHERE rn BETWEEN (p_page -1) * p_size + 1 AND p_page * p_size;
END;