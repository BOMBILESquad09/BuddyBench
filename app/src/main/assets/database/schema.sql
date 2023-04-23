
DROP TABLE IF EXISTS sport;
DROP TABLE IF EXISTS court;
DROP TABLE IF EXISTS court_time;
DROP TABLE IF EXISTS invitation;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_sport;


CREATE  TABLE sport(
    name TEXT PRIMARY KEY NOT NULL
);

CREATE  TABLE user(
    id INTEGER PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    surname TEXT NOT NULL,
    nickname TEXT NOT NULL,
    birthdate TEXT NOT NULL,
    location TEXT NOT NULL,
    email TEXT NOT NULL,
    reliability INTEGER NOT NULL
);


CREATE  TABLE court(
    id INTEGER PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    location TEXT NOT NULL,
    fee_hour INTEGER NOT NULL,
    sport TEXT NOT NULL,
    fee_equipment INTEGER NOT NULL,
    path TEXT NOT NULL,
    FOREIGN KEY (sport) REFERENCES sport(name) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE  TABLE user_sport(
    id INTEGER  PRIMARY KEY NOT NULL,
    user INTEGER NOT NULL,
    sport TEXT NOT NULL,
    skill TEXT NOT NULL,
    games_played INTEGER NOT NULL,
    games_organized INTEGER NOT NULL,
    FOREIGN KEY (sport) REFERENCES sport(name) ON DELETE CASCADE ON UPDATE CASCADE,
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

CREATE  TABLE reservation(
    id INTEGER PRIMARY KEY NOT NULL,
    user INTEGER NOT NULL,
    court INTEGER NOT NULL,
    date TEXT NOT NULL,
    start_time INTEGER  NOT NULL,
    equipment INTEGER NOT NULL,
    FOREIGN KEY (user) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
    FOREIGN KEY (court) REFERENCES court(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE  TABLE invitation(
    id INTEGER PRIMARY KEY NOT NULL,
    confirmed INTEGER NOT NULL,
    presence INTEGER NOT NULL,
    reservation INTEGER NOT NULL,
    user INTEGER NOT NULL,
    FOREIGN KEY (reservation) REFERENCES reservation(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user) REFERENCES user(id) ON DELETE CASCADE ON UPDATE CASCADE
);


INSERT INTO user (id, name, surname, nickname, birthdate, location, email, reliability) VALUES (
    1, 'Jacopo', 'De Cristofaro', 'Jeff', '1999-04-27', 'New York', 'jeff@buddybench.com', 80
);
INSERT INTO user (id, name, surname, nickname, birthdate, location, email, reliability) VALUES (
    0, 'Vittorio', 'Arpino', 'TheNextLayer', '1999-09-30', 'Scafati', 'varpino@buddybench.it', 70
);

INSERT INTO sport VALUES ("TENNIS");
INSERT INTO sport VALUES ("BASKETBALL");
INSERT INTO sport VALUES ("FOOTBALL");
INSERT INTO sport VALUES ("VOLLEYBALL");


INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment)
VALUES (1, 'Central Park Tennis Courts', '123 Main St', 'New York, NY', 50, "TENNIS", "court1", 10);
INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment)
VALUES (2, 'Lincoln Park Court', '456 Elm St', 'Chicago, IL', 30, "TENNIS", "court2", 10);
INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment)
VALUES (3, 'Golden Gate Park Field', '789 Oak St', 'San Francisco, CA', 40, "BASKETBALL", "court3", 10);
INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment)
VALUES (4, 'Riverside Park Diamond', '321 Pine St', 'New York, NY', 20, "BASKETBALL", "court4", 10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment)
VALUES (5, 'Griffith Park Courts', '555 Maple St', 'Los Angeles, CA', 60, "FOOTBALL", "court5", 10);

INSERT INTO court (id, name, address, location, fee_hour, sport,path,  fee_equipment) VALUES (6, 'Boston Common Court', '777 Tremont St', 'Boston, MA', 25, "TENNIS", "court6", 10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (7, 'Lakefront Field', '888 Lakeshore Dr', 'Chicago, IL', 35, "BASKETBALL", "court7", 10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (8, 'Battery Park Diamond', '444 Battery Pl', 'New York, NY', 15, "FOOTBALL", "court8", 10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (9, 'Santa Monica Volleyball Court', '101 Ocean Ave', 'Santa Monica, CA', 45, "TENNIS", "court9", 10);
INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (10, 'Grant Park Courts', '777 State St', 'Chicago, IL', 55, "BASKETBALL","court10",  10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (11, 'Fenway Park Diamond', '4 Yawkey Way', 'Boston, MA', 50, "BASKETBALL","court11",  10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (12, 'Venice Beach Court', '101 Pacific Ave', 'Venice, CA', 20, "TENNIS", "court12", 10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (13, 'Brooklyn Bridge Park Field', '334 Furman St', 'Brooklyn, NY', 30, "FOOTBALL", "court13", 10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (14, 'Central Park Field', '123 Main St', 'New York, NY', 25, "TENNIS","court14",  10);

INSERT INTO court (id, name, address, location, fee_hour, sport, path, fee_equipment) VALUES (15, 'Echo Park Courts', '1010 Glendale Blvd', 'Los Angeles, CA', 35, "TENNIS", "court15", 10);

INSERT INTO reservation (id, user, court, date, start_time, equipment) VALUES (
    0, 1, 5, '2023-04-30', 18, false
);

INSERT INTO reservation (id, user, court, date, start_time, equipment) VALUES (
    1, 1, 6, '2023-04-27', 18, false
);

INSERT INTO reservation (id, user, court, date, start_time, equipment)
VALUES 
    
    (2, 1, 8, '2023-05-16', 14, false),
    (3, 1, 11, '2023-05-17', 9, false),
    (4, 1, 14, '2023-05-18', 16, false),
    (5, 1, 3, '2023-05-19', 11, false),
    (6, 1, 6, '2023-05-15', 10, false);

INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time)
VALUES (0, 1, 1, "6:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time)
VALUES (1, 1, 2, "6:00", "18:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time)
VALUES (2, 1, 3, "6:00", "14:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time)
VALUES (3, 1, 4, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time)
VALUES (4, 1, 5, "8:00", "23:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time)
VALUES (5, 1, 6, "10:00", "21:00");
