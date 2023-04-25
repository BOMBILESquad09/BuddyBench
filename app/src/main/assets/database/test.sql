SELECT case cast (strftime('%w', '2023-04-25') as integer)
  when 0 then 'Sunday'
  when 1 then 'Monday'
  when 2 then 'Tuesday'
  when 3 then 'Wednesday'
  when 4 then 'Thursday'
  when 5 then 'Friday'
  else 'Saturday' end as pymtdate;


SELECT ((SELECT 1 from USER) = SELECT 1 FROM user) FROM user

SELECT 
(SELECT (SELECT 1 from USER) FROM USER = SELECT 1 FROM USER)
FROM USER
