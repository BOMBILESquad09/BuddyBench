DROP TABLE IF EXISTS sport;
DROP TABLE IF EXISTS court;
DROP TABLE IF EXISTS court_time;
DROP TABLE IF EXISTS invitation;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS user_sport;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS unavailable_court_date;
DROP TRIGGER IF EXISTS check_court_availability;
CREATE TABLE sport(sport_name TEXT PRIMARY KEY NOT NULL);
CREATE TABLE user(
    id INTEGER PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    nickname TEXT NOT NULL,
    birthdate TEXT NOT NULL,
    location TEXT NOT NULL,
    email TEXT NOT NULL,
    reliability INTEGER NOT NULL
);
CREATE TABLE court(
    id INTEGER PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    location TEXT NOT NULL,
    fee_hour INTEGER NOT NULL,
    sport TEXT NOT NULL,
    fee_equipment INTEGER NOT NULL,
    path TEXT NOT NULL,
    rating REAL NOT NULL,
    n_reviews INTEGER NOT NULL,
    FOREIGN KEY (sport) REFERENCES sport(sport_name) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE user_sport(
    id INTEGER PRIMARY KEY NOT NULL,
    user INTEGER NOT NULL,
    sport TEXT NOT NULL,
    skill TEXT NOT NULL,
    games_played INTEGER NOT NULL,
    games_organized INTEGER NOT NULL,
    FOREIGN KEY (sport) REFERENCES sport(sport_name) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE court_time(
    id INTEGER PRIMARY KEY NOT NULL,
    court INTEGER NOT NULL,
    day_of_week INTEGER NOT NULL,
    opening_time INTEGER NOT NULL,
    closing_time INTEGER NOT NULL,
    FOREIGN KEY (court) REFERENCES court(id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE reservation(
    id INTEGER PRIMARY KEY NOT NULL,
    user INTEGER NOT NULL,
    court INTEGER NOT NULL,
    date TEXT NOT NULL,
    start_time INTEGER NOT NULL,
    equipment INTEGER NOT NULL,
    FOREIGN KEY (user) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE FOREIGN KEY (court) REFERENCES court(id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE invitation(
    id INTEGER PRIMARY KEY NOT NULL,
    confirmed INTEGER NOT NULL,
    presence INTEGER NOT NULL,
    reservation INTEGER NOT NULL,
    user INTEGER NOT NULL,
    FOREIGN KEY (reservation) REFERENCES reservation(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE TABLE unavailable_court_date(
    id INTEGER NOT NULL,
    date DATE NOT NULL,
    PRIMARY KEY(id, date)
);


CREATE TRIGGER check_court_availability
AFTER INSERT INTO reservation
BEGIN

SET day_of_week = SELECT  CASE DATEPART(NEW.date,GETDATE())  
    WHEN 1 THEN 'SUNDAY' 
    WHEN 2 THEN 'MONDAY' 
    WHEN 3 THEN 'TUESDAY' 
    WHEN 4 THEN 'WEDNESDAY' 
    WHEN 5 THEN 'THURSDAY' 
    WHEN 6 THEN 'FRIDAY' 
    WHEN 7 THEN 'SATURDAY' 
END;

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
END;



