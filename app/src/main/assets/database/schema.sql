
DROP TABLE IF EXISTS sport;
DROP TABLE IF EXISTS court;
DROP TABLE IF EXISTS court_timetable;
DROP TABLE IF EXISTS invitation;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_sport;


CREATE  TABLE sport(
    name VARCHAR(20) PRIMARY KEY
);

CREATE  TABLE user(
    id NUMBER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    birthdate DATE NOT NULL,
    location VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    reliability NUMBER NOT NULL
);

CREATE  TABLE court(
    id NUMBER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    fee_hour NUMBER NOT NULL,
    sport VARCHAR(20) NOT NULL,
    FOREIGN KEY (sport) REFERENCES sport(name)
);

CREATE  TABLE user_sport(
    id NUMBER  PRIMARY KEY,
    user NUMBER NOT NULL,
    sport VARCHAR(20) NOT NULL,
    skill VARCHAR(20) NOT NULL,
    games_played NUMBER NOT NULL,
    games_organized NUMBER NOT NULL,
    FOREIGN KEY (sport) REFERENCES sport(name),
    FOREIGN KEY (user) REFERENCES user(id)
);

CREATE TABLE court_time(
    id NUMBER PRIMARY KEY,
    court NUMBER NOT NULL,
    day_of_week NUMBER NOT NULL,
    opening_time NUMBER NOT NULL,
    closing_time NUMBER NOT NULL,
    FOREIGN KEY (court) REFERENCES court(id)
);

CREATE  TABLE reservation(
    id NUMBER PRIMARY KEY,
    user NUMBER NOT NULL,
    court NUMBER NOT NULL,
    date DATE NOT NULL,
    start_time NUMBER  NOT NULL,
    end_time NUMBER NOT NULL,
    FOREIGN KEY (user) REFERENCES user(id)
    FOREIGN KEY (court) REFERENCES court(id)
);

CREATE  TABLE invitation(
    id NUMBER PRIMARY KEY,
    confirmed BIT NOT NULL,
    presence BIT NOT NULL,
    reservation NUMBER NOT NULL,
    user NUMBER NOT NULL,
    FOREIGN KEY (reservation) REFERENCES reservation(id),
    FOREIGN KEY (user) REFERENCES user(id)
);