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

-- 조회수 카운트
CREATE OR REPLACE PROCEDURE increment_view_count(
	p_board_id IN BOARD.ID%TYPE	
)
AS
BEGIN
	UPDATE board
		SET view_count = view_count + 1
	WHERE ID = p_board_id;
END increment_view_count;


SELECT object_name, object_type, status
FROM user_objects
WHERE object_name = 'INCREMENT_VIEW_COUNT' AND object_type = 'PROCEDURE';

-- 추천수 TOP5
CREATE OR REPLACE PROCEDURE get_top5_boards_by_likes(
	p_result OUT SYS_REFCURSOR
)
AS
BEGIN
	OPEN p_result FOR
		SELECT *
		FROM (
			SELECT
			  b.*
			FROM board b
			LEFT JOIN likes_table l
			  ON b.id = l.board_id
			GROUP BY
			  b.id, b.title, b.content, b.view_count,
			  b.created_date, b.updated_date, b.member_id
      		ORDER BY COUNT(l.board_id) DESC
		)
		WHERE ROWNUM <= 5;
END get_top5_boards_by_likes;

SELECT owner, object_name, object_type, status
FROM   all_objects
WHERE  object_name = 'GET_TOP5_BOARDS_BY_LIKES';

SELECT line, position, text
FROM   user_errors
WHERE  name = 'GET_TOP5_BOARDS_BY_LIKES';

ALTER PROCEDURE get_top5_boards_by_likes COMPILE;