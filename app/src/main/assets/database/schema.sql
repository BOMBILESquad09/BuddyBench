
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
    --image TEXT  NULL,
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
    0, 'Jacopo', 'De Cristofaro', 'Jeff', '1999-04-27', 'New York', 'jeff@buddybench.com', 80
);

INSERT INTO sport VALUES ("TENNIS");
INSERT INTO sport VALUES ("BASKETBALL");
INSERT INTO sport VALUES ("FOOTBALL");
INSERT INTO sport VALUES ("VOLLEYBALL");




INSERT INTO court (id, name, address, location, fee_hour, sport)
VALUES (1, 'Central Park Tennis Courts', '123 Main St', 'New York, NY', 50, "TENNIS");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (2, 'Lincoln Park Court', '456 Elm St', 'Chicago, IL', 30, "TENNIS");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (3, 'Golden Gate Park Field', '789 Oak St', 'San Francisco, CA', 40, "BASKETBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (4, 'Riverside Park Diamond', '321 Pine St', 'New York, NY', 20, "BASKETBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (5, 'Griffith Park Courts', '555 Maple St', 'Los Angeles, CA', 60, "FOOTBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (6, 'Boston Common Court', '777 Tremont St', 'Boston, MA', 25, "TENNIS");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (7, 'Lakefront Field', '888 Lakeshore Dr', 'Chicago, IL', 35, "BASKETBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (8, 'Battery Park Diamond', '444 Battery Pl', 'New York, NY', 15, "FOOTBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (9, 'Santa Monica Volleyball Court', '101 Ocean Ave', 'Santa Monica, CA', 45, "TENNIS");
INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (10, 'Grant Park Courts', '777 State St', 'Chicago, IL', 55, "BASKETBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (11, 'Fenway Park Diamond', '4 Yawkey Way', 'Boston, MA', 50, "BASKETBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (12, 'Venice Beach Court', '101 Pacific Ave', 'Venice, CA', 20, "TENNIS");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (13, 'Brooklyn Bridge Park Field', '334 Furman St', 'Brooklyn, NY', 30, "FOOTBALL");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (14, 'Central Park Field', '123 Main St', 'New York, NY', 25, "TENNIS");

INSERT INTO court (id, name, address, location, fee_hour, sport) VALUES (15, 'Echo Park Courts', '1010 Glendale Blvd', 'Los Angeles, CA', 35, "TENNIS");

INSERT INTO reservation (id, user, court, date, start_time) VALUES (
    0, 0, 5, '2023-04-30', 18
);

INSERT INTO reservation (id, user, court, date, start_time) VALUES (
    1, 0, 6, '2023-04-27', 18
);

INSERT INTO reservation (id, user, court, date, start_time) 
VALUES 
    
    (2, 0, 8, '2023-05-16', 14),
    (3, 0, 11, '2023-05-17', 9),
    (4, 0, 14, '2023-05-18', 16),
    (5, 0, 3, '2023-05-19', 11),
    (6, 0, 6, '2023-05-15', 10);




TableInfo{name='Invitation', columns={, , presence=Column{name='presence', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, confirmed=Column{name='confirmed', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, user=Column{name='user', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}}, foreignKeys=[ForeignKey{referenceTable='reservation', onDelete='CASCADE +', onUpdate='CASCADE', columnNames=[reservation], referenceColumnNames=[id]}, ForeignKey{referenceTable='user', onDelete='CASCADE +', onUpdate='CASCADE', columnNames=[user], referenceColumnNames=[id]}], indices=[]}
     Found:
    TableInfo{name='Invitation', columns={id=Column{name='id', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=1, defaultValue='undefined'}, confirmed=Column{name='confirmed', type='BIT', affinity='1', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, presence=Column{name='presence', type='BIT', affinity='1', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, reservation=Column{name='reservation', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}, user=Column{name='user', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='undefined'}}, foreignKeys=[ForeignKey{referenceTable='user', onDelete='CASCADE +', onUpdate='CASCADE', columnNames=[user], referenceColumnNames=[id]}, ForeignKey{referenceTable='reservation', onDelete='CASCADE +', onUpdate='CASCADE', columnNames=[reservation], referenceColumnNames=[id]}], indices=[]}
        at androidx.room.RoomOpenHelper.onCreate(RoomOpenHelper.kt:74)