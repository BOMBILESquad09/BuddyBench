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



Expected:
TableInfo{name='review', columns={date=Column{name='date', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, , , , , }, foreignKeys=[}, ], indices=[]}
Found:
TableInfo{name='review', columns={review_id=Column{name='review_id', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=1, defaultValue='undefined'}, court=Column{name='court', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, user=Column{name='user', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, , date=Column{name='date', type='DATE', affinity='1', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, rating=Column{name='rating', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}}, foreignKeys=[ForeignKey{referenceTable='user', onDelete='CASCADE +', onUpdate='CASCADE', columnNames=[user], referenceColumnNames=[id]}, ForeignKey{referenceTable='court', onDelete='CASCADE +', onUpdate='CASCADE', columnNames=[court], referenceColumnNames=[id]}], indices=[]}
