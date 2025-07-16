CREATE OR REPLACE PROCEDURE get_top10_channels_by_post_count(
    p_result OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN p_result FOR
        SELECT *
        FROM (
            SELECT
                c.id,
                c.name,
                COUNT(b.id) AS post_count
            FROM channel c
            LEFT JOIN board b
                ON c.id = b.channel_id
            GROUP BY c.id, c.name
            ORDER BY COUNT(b.id) DESC
        )
    WHERE ROWNUM <= 10;
END get_top10_channels_by_post_count;