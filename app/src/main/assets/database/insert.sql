INSERT INTO user (
        id,
        name,
        surname,
        nickname,
        birthdate,
        location,
        email,
        reliability
    )
VALUES (
        1,
        'Jacopo',
        'De Cristofaro',
        'Jeff',
        '1999-04-27',
        'New York',
        'jeff@buddybench.com',
        80
    );
INSERT INTO user (
        id,
        name,
        surname,
        nickname,
        birthdate,
        location,
        email,
        reliability
    )
VALUES (
        0,
        'Vittorio',
        'Arpino',
        'TheNextLayer',
        '1999-09-30',
        'Scafati',
        'varpino@buddybench.it',
        70
    );
INSERT INTO sport
VALUES ("TENNIS");
INSERT INTO sport
VALUES ("BASKETBALL");
INSERT INTO sport
VALUES ("FOOTBALL");
INSERT INTO sport
VALUES ("VOLLEYBALL");
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        1,
        'Central Park Tennis',
        '123 Main St',
        'New York, NY',
        50,
        "TENNIS",
        "court1",
        11,
        4.3,
        23
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        2,
        'Lincoln Park',
        '456 Elm St',
        'Chicago, IL',
        30,
        "TENNIS",
        "court2",
        5,
        3.5,
        50
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        3,
        'Golden Gate Park Field',
        '789 Oak St',
        'San Francisco, CA',
        40,
        "BASKETBALL",
        "court3",
        6,
        3.8,
        35
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        4,
        'Riverside Park Diamond',
        '321 Pine St',
        'New York, NY',
        20,
        "BASKETBALL",
        "court4",
        4,
        3.1,
        13
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        5,
        'Griffith Park',
        '555 Maple St',
        'Los Angeles, CA',
        60,
        "FOOTBALL",
        "court5",
        7,
        4.5,
        73
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        6,
        'Boston Common Court',
        '777 Tremont St',
        'Boston, MA',
        25,
        "TENNIS",
        "court6",
        3,
        3.4,
        14
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        7,
        'Lakefront Field',
        '888 Lakeshore Dr',
        'Chicago, IL',
        35,
        "BASKETBALL",
        "court7",
        5,
        3.6,
        68
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        8,
        'Battery Park Diamond',
        '444 Battery Pl',
        'New York, NY',
        15,
        "FOOTBALL",
        "court8",
        6,
        3.8,
        35
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        9,
        'Santa Monica Volleyball',
        '101 Ocean Ave',
        'Santa Monica, CA',
        45,
        "TENNIS",
        "court9",
        3,
        4.3,
        23
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        10,
        'Grant Park Courts',
        '777 State St',
        'Chicago, IL',
        55,
        "BASKETBALL",
        "court10",
        8,
        3.6,
        12
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        11,
        'Fenway Park Diamond',
        '4 Yawkey Way',
        'Boston, MA',
        50,
        "BASKETBALL",
        "court11",
        4,
        3.4,
        14
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        12,
        'Venice Beach Court',
        '101 Pacific Ave',
        'Venice, CA',
        20,
        "TENNIS",
        "court12",
        4,
        4.3,
        23
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        13,
        'Brooklyn Bridge Park Field',
        '334 Furman St',
        'Brooklyn, NY',
        30,
        "FOOTBALL",
        "court13",
        6,
        4.5,
        73
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        14,
        'Central Park Field',
        '123 Main St',
        'New York, NY',
        25,
        "TENNIS",
        "court14",
        4,
        3.6,
        12
    );
INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES (
        15,
        'Echo Park Courts',
        '1010 Glendale Blvd',
        'Los Angeles, CA',
        35,
        "TENNIS",
        "court15",
        3,
        3.6,
        68
    );


INSERT INTO court_time (
        id,
        court,
        day_of_week,
        opening_time,
        closing_time
    )
VALUES (0, 1, 1, "6:00", "20:00");
INSERT INTO court_time (
        id,
        court,
        day_of_week,
        opening_time,
        closing_time
    )
VALUES (1, 1, 2, "6:00", "20:00");
INSERT INTO court_time (
        id,
        court,
        day_of_week,
        opening_time,
        closing_time
    )
VALUES (2, 1, 3, "6:00", "20:00");
INSERT INTO court_time (
        id,
        court,
        day_of_week,
        opening_time,
        closing_time
    )
VALUES (3, 1, 4, "6:00", "20:00");
INSERT INTO court_time (
        id,
        court,
        day_of_week,
        opening_time,
        closing_time
    )
VALUES (4, 1, 5, "6:00", "20:00");
INSERT INTO court_time (
        id,
        court,
        day_of_week,
        opening_time,
        closing_time
    )
VALUES (5, 1, 6, "10:00", "20:00");



INSERT INTO reservation (id, user, court, date, start_time, end_time, equipment)
VALUES (2, 0, 8, '2023-05-16', 14, 15, false),
    (3, 1, 11, '2023-05-17', 9, 10, false),
    (4, 0, 14, '2023-05-18', 16, 17, false),
    (5, 0, 3, '2023-05-19', 11, 12,false),
    (6, 1, 6, '2023-05-15', 10, 11,false),
    (0, 0, 5, '2023-04-30', 18, 19,false);

