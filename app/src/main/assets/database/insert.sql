--- User informations
INSERT INTO user (id,name,surname,nickname,birthdate,location,email,reliability)
VALUES (1,"Vittorio","Arpino","TheNextLayer","1999-09-30","Scafati","varpino@buddybench.it",70);
INSERT INTO user (id,name,surname,nickname,birthdate,location,email,reliability)
VALUES (2,"Jacopo","De Cristofaro","Jeff","1999-04-27","New York","jeff@buddybench.com",80);

-- Sports Available
INSERT INTO sport
VALUES ("TENNIS");
INSERT INTO sport
VALUES ("BASKETBALL");
INSERT INTO sport
VALUES ("FOOTBALL");
INSERT INTO sport
VALUES ("VOLLEYBALL");

-- Courts Informations

INSERT INTO court (
        id,
        name,
        address,
        location,
        fee_hour,
        sport,
        phone_number,
        path,
        fee_equipment,
        rating,
        n_reviews
    )
VALUES
    (1,'Central Park Tennis','123 Main St','New York, NY',50,'TENNIS','555-987-6543','court1',11,4.3,23),
    (2,'Lincoln Park','456 Elm St','Chicago, IL',30,'TENNIS','555-234-5678','court2',5,3.5,50),
    (3,'Golden Gate Park Field','789 Oak St','San Francisco, CA',40,'BASKETBALL','555-876-5432','court3',6,3.8,35),
    (4,'Riverside Park Diamond','321 Pine St','New York, NY',20,'BASKETBALL','555-345-6789','court4',4,3.1,13),
    (5,'Griffith Park','555 Maple St','Los Angeles, CA',60,'FOOTBALL','555-789-0123','court5',7,4.5,73),
    (6,'Boston Common Court','777 Tremont St','Boston, MA',25,'TENNIS','555-456-7890','court6',3,3.4,14),
    (7,'Lakefront Field','888 Lakeshore Dr','Chicago, IL',35,'BASKETBALL','555-890-1234','court7',5,3.6,68),
    (8,'Battery Park Diamond','444 Battery Pl','New York, NY',15,'FOOTBALL','555-567-8901','court8',6,3.8,35),
    (9,'Santa Monica','101 Ocean Ave','Santa Monica, CA',45,'TENNIS','555-321-0987','court9',3,4.3,23),
    (10,'Grant Park Courts','777 State St','Chicago, IL',55,'BASKETBALL','555-678-9012','court10',8,3.6,12),
    (11,'Fenway Park Diamond','4 Yawkey Way','Boston, MA',50,'BASKETBALL','555-234-5678','court11',4,3.4,14),
    (12,'Venice Beach Court','101 Pacific Ave','Venice, CA',20,'TENNIS','555-876-5432','court12',4,4.3,23),
    (13,'Brooklyn Bridge Park Field','334 Furman St','Brooklyn, NY',30,'FOOTBALL','555-345-6789','court13',6,4.5,73),
    (14,'Central Park Field','123 Main St','New York, NY',25,'TENNIS','555-789-0123','court14',4,3.6,12),
    (15,'Echo Park Courts','1010 Glendale Blvd','Los Angeles, CA',35,'TENNIS','555-456-7890','court15',3,3.6,68),
    (16, "Volley Court", "124 Victorian St", "New York, NY", 15, "VOLLEYBALL",'555-890-1234',"court16", 5, 4.5, 30),
    (17, "Beach Volley Paradise", "342 Dominican St", "Los Angeles, LA", 20, "VOLLEYBALL",'555-432-1098',"court17", 10, 4.2, 45),
    (18, "Green Volley", "333 Tremont St", "New Jersey, NJ", 12, "VOLLEYBALL",'555-987-6543',"court18", 3, 4.0, 15),
    (19, "Volley Palace", "938 Main St", "Los Angeles, LA", 18, "VOLLEYBALL",'555-321-0987',"court19", 8, 4.8, 75),
    (20, "Volleyball Arena","238 Mapi St", "New York, NY", 22, "VOLLEYBALL",'555-765-4321',"court20", 15, 4.7, 60);



INSERT INTO facility(facility_id, name) VALUES (0, "Shower");
INSERT INTO facility(facility_id, name) VALUES (1, "Dress room");

INSERT INTO court_facility(cf_id, court, facility) VALUES (0,1,0);
INSERT INTO court_facility(cf_id, court, facility) VALUES (1,1,1);


-- Some reservations (Compatibility with the date/time for court)
INSERT INTO reservation (id, user, court, date, start_time, end_time, equipment)
VALUES
    (2, 0, 8, '2023-05-16', 14, 15, true),
    (3, 1, 11, '2023-05-17', 9, 10, false),
    (4, 0, 14, '2023-05-18', 16, 17, false),
    (5, 0, 3, '2023-05-19', 11, 12,true),
    (6, 1, 6, '2023-05-15', 10, 11,false),
    (0, 0, 5, '2023-04-28', 18, 19,true),
    (7, 0, 5, '2023-05-01', 18, 19,false);

-- Some UserSports Rows
INSERT INTO user_sport (id,user,sport,skill,games_played,games_organized, achievements) VALUES
(0,0,"VOLLEYBALL","NEWBIE",9,10, "");
INSERT INTO user_sport (id,user,sport,skill,games_played,games_organized, achievements) VALUES
(1,0,"BASKETBALL","NEWBIE",10,10, "");
INSERT INTO user_sport (id,user,sport,skill,games_played,games_organized, achievements) VALUES
(2,0,"FOOTBALL","NEWBIE",11,10, "");


--- Inserted Court Time Table
INSERT INTO court_time (id,court,day_of_week,opening_time,closing_time) VALUES
(0, 1, 1, "6:00", "20:00"),
(1, 1, 2, "6:00", "20:00"),
(2, 1, 3, "6:00", "20:00"),
(3, 1, 4, "6:00", "20:00"),
(4, 1, 5, "6:00", "20:00"),
(5, 1, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(6, 2, 1, "6:00", "20:00"),
(7, 2, 2, "6:00", "20:00"),
(8, 2, 3, "6:00", "20:00"),
(9, 2, 4, "6:00", "20:00"),
(10, 2, 5, "6:00", "20:00"),
(11, 2, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(12, 3, 1, "6:00", "20:00"),
(13, 3, 2, "6:00", "20:00"),
(14, 3, 3, "6:00", "20:00"),
(15, 3, 4, "6:00", "20:00"),
(16, 3, 5, "6:00", "20:00"),
(17, 3, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(18, 4, 1, "6:00", "20:00"),
(19, 4, 2, "6:00", "20:00"),
(20, 4, 3, "6:00", "20:00"),
(21, 4, 4, "6:00", "20:00"),
(22, 4, 5, "6:00", "20:00"),
(23, 4, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(24, 5, 1, "6:00", "20:00"),
(25, 5, 2, "6:00", "20:00"),
(26, 5, 3, "6:00", "20:00"),
(27, 5, 4, "6:00", "20:00"),
(28, 5, 5, "6:00", "20:00"),
(29, 5, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(30, 6, 1, "8:00", "18:00"),
(31, 6, 2, "8:00", "18:00"),
(32, 6, 3, "8:00", "18:00"),
(33, 6, 4, "8:00", "18:00"),
(34, 6, 5, "8:00", "18:00"),
(35, 6, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(36, 7, 1, "6:00", "20:00"),
(37, 7, 2, "6:00", "20:00"),
(38, 7, 3, "6:00", "20:00"),
(39, 7, 4, "6:00", "20:00"),
(40, 7, 5, "6:00", "20:00"),
(41, 7, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(42, 8, 1, "8:00", "18:00"),
(43, 8, 2, "8:00", "18:00"),
(44, 8, 3, "8:00", "18:00"),
(45, 8, 4, "8:00", "18:00"),
(46, 8, 5, "8:00", "18:00"),
(47, 8, 6, "10:00", "23:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(48, 9, 1, "8:00", "13:00"),
(49, 9, 2, "8:00", "13:00"),
(50, 9, 3, "8:00", "13:00"),
(51, 9, 4, "8:00", "13:00"),
(52, 9, 5, "8:00", "13:00"),
(53, 9, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(54, 10, 1, "8:00", "18:00"),
(55, 10, 2, "8:00", "18:00"),
(56, 10, 3, "8:00", "18:00"),
(57, 10, 4, "8:00", "18:00"),
(58, 10, 5, "8:00", "18:00"),
(59, 10, 6, "10:00", "13:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(60, 11, 1, "10:00", "23:00"),
(61, 11, 2, "10:00", "23:00"),
(62, 11, 3, "10:00", "23:00"),
(63, 11, 4, "10:00", "23:00"),
(64, 11, 5, "10:00", "23:00"),
(65, 11, 6, "10:00", "13:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(66, 12, 1, "6:00", "20:00"),
(67, 12, 2, "6:00", "20:00"),
(68, 12, 3, "6:00", "20:00"),
(69, 12, 4, "6:00", "20:00"),
(70, 12, 5, "6:00", "20:00"),
(71, 12, 6, "10:00", "23:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(72, 13, 1, "6:00", "23:00"),
(73, 13, 2, "6:00", "23:00"),
(74, 13, 3, "6:00", "23:00"),
(75, 13, 4, "6:00", "23:00"),
(76, 13, 5, "6:00", "23:00"),
(77, 13, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(78, 14, 1, "10:00", "18:00"),
(79, 14, 2, "10:00", "18:00"),
(80, 14, 3, "10:00", "18:00"),
(81, 14, 4, "10:00", "18:00"),
(82, 14, 5, "10:00", "18:00"),
(83, 14, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(84, 15, 1, "10:00", "20:00"),
(85, 15, 2, "10:00", "20:00"),
(86, 15, 3, "10:00", "20:00"),
(87, 15, 4, "10:00", "20:00"),
(88, 15, 5, "8:00", "23:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(89, 16, 1, "10:00", "20:00"),
(90, 16, 2, "10:00", "20:00"),
(91, 16, 3, "10:00", "20:00"),
(92, 16, 4, "10:00", "20:00"),
(93, 16, 5, "8:00", "23:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(94, 17, 1, "6:00", "13:00"),
(95, 17, 2, "6:00", "13:00"),
(96, 17, 3, "6:00", "13:00"),
(97, 17, 4, "6:00", "18:00"),
(98, 17, 5, "6:00", "18:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(99, 18, 1, "10:00", "18:00"),
(100, 18, 2, "10:00", "18:00"),
(101, 18, 3, "10:00", "18:00"),
(102, 18, 4, "6:00", "18:00"),
(103, 18, 5, "6:00", "18:00"),
(104, 18, 6, "10:00", "20:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(105, 19, 1, "6:00", "13:00"),
(106, 19, 2, "6:00", "13:00"),
(107, 19, 3, "6:00", "13:00"),
(108, 19, 4, "6:00", "13:00"),
(109, 19, 5, "8:00", "23:00");
INSERT INTO court_time (id, court, day_of_week, opening_time, closing_time) VALUES
(110, 20, 1, "10:00", "18:00"),
(111, 20, 2, "10:00", "18:00"),
(112, 20, 3, "10:00", "18:00"),
(113, 20, 4, "6:00", "18:00"),
(114, 20, 5, "10:00", "18:00"),
(115, 20, 6, "10:00", "20:00");

INSERT INTO review (review_id, court, user, description, date, rating) VALUES (
    0,1,1,"One of the best court in town!", "2023-04-04", 5
);

INSERT INTO review (review_id, court, user, description, date, rating) VALUES (
    1,1,2,"Cool dress room!", "2023-04-03", 4
);

