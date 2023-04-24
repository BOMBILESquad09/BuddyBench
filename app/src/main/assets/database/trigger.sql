

    IF (SELECT C.closing_time - C.opening_time FROM
        court C, court_time CT 
        WHERE NEW.court = C.id AND
        CT.court = C.id AND day_of_week 
        AND day_of_week = CT.day_of_week
        NEW.date = R.date) = (SELECT COUNT(*) FROM reservation R
        WHERE R.id = NEW.court AND NEW.date = R.date ) THEN
        INSERT INTO unavailable_court_date (id, date)
        VALUES (NEW.id, NEW.date);
    END IF;