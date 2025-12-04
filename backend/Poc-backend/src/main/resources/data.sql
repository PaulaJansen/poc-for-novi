-- Roles
INSERT INTO roles (id, role_name)
VALUES (1, 'VISITOR'),
       (2, 'ARTIST');

-- Artists
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('henkie123', 'henkie@example.com', 'password123', CURRENT_DATE, 'profile/henkie.jpg');
INSERT INTO artists (id, first_name, last_name, city, type_of_art, biography)
VALUES (1, 'Henkie', 'Jansen', 'Amsterdam', 'Schilderijen',
        'Hallo, Henkie hier. Ik ben net begonnen als abstracte schilder en doe ook commissies!');

INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('anne123', 'anne@example.com', 'password456', CURRENT_DATE, 'profile/anne.jpg');
INSERT INTO artists (id, first_name, last_name, city, type_of_art, biography)
VALUES (2, 'Anne', 'de Vries', 'Rotterdam', 'Fotografie',
        'Ik ben Anne en ik fotografeer al 10 jaar. Ik maak vooral portretten van mens en dier.');

INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('claudia123', 'claudia@example.com', 'password789', CURRENT_DATE, 'profile/claudia.jpg');
INSERT INTO artists (id, first_name, last_name, city, type_of_art, biography)
VALUES (3, 'Claudia', 'Bos', 'Utrecht', 'Beeldhouwen',
        'Als beeldhouwer breng ik de wereld tot leven in marmer en zeepsteen.');

-- Visitors
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('pietjelovesart', 'pietje@example.com', 'wachtwoord123', CURRENT_DATE, 'profile/pietje.jpg');
INSERT INTO visitors (id, name)
VALUES (4, 'Pietje');

INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('artlover', 'susan@example.com', 'wachtwoord456', CURRENT_DATE, 'profile/susan.jpg');
INSERT INTO visitors (id, name)
VALUES (5, 'Susan');

INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('jaapiejaap', 'jaap@example.com', 'wachtwoord789', CURRENT_DATE, 'profile/jaap.jpg');
INSERT INTO visitors (id, name)
VALUES (6, 'Jaap');

-- Joined table user_role
INSERT INTO user_role (user_id, role_id)
VALUES (1, 2),
       (2, 2),
       (3, 2),
       (4, 1),
       (5, 1),
       (6, 1);

-- Genres
INSERT INTO genres (id, name)
VALUES (1, 'Abstract'),
       (2, 'Modern'),
       (3, 'Portret'),
       (4, 'Romantisch'),
       (5, 'Impressionistisch'),
       (6, 'Marmer'),
       (7, 'Olieverf'),
       (8, 'Zwart wit'),
       (9, 'Dieren'),
       (10, 'Natuur'),
       (11, 'Klassiek'),
       (12, 'Kubisme');


-- Artworks
INSERT INTO artworks (id, title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
VALUES (10, 'Zonsopgang bij de rivier', 250.00, 'AVAILABLE', 1, 80, 60, 2),
       (20, 'Zonsondergang blauw', 400.00, 'SOLD', 1, 100, 70, 2),
       (30, 'Kind', 75.00, 'AVAILABLE', 2, 30, 40, 0),
       (40, 'Vrouw rokend', 75.00, 'AVAILABLE', 2, 30, 40, 0),
       (50, 'Oude man', 60.00, 'AVAILABLE', 2, 30, 30, 0),
       (60, 'Giraffe', 80.00, 'AVAILABLE', 2, 30, 60, 0),
       (70, 'Mooie vrouw', 89.00, 'AVAILABLE', 2, 50, 35, 0),
       (80, 'Kubistische vrouw', 250.00, 'ONLOAN', 3, 30, 30, 45),
       (90, 'Gladiator', 349.00, 'AVAILABLE', 3, 20, 20, 40),
       (100, 'Aphrodite', 399.00, 'AVAILABLE', 3, 20, 20, 38);

-- Joined table artwork_images
INSERT INTO artwork_images (artwork_id, image)
VALUES (10, 'artworks/sunset1.jpg'),
       (10, 'artworks/sunset2.jpg'),
       (10, 'artworks/sunset3.jpg'),
       (20, 'artworks/sundown1.jpg'),
       (20, 'artworks/sundown2.jpg'),
       (20, 'artworks/sundown3.jpg'),
       (30, 'artworks/child1.jpg'),
       (30, 'artworks/child2.jpg'),
       (40, 'artworks/smoking1.jpg'),
       (40, 'artworks/smoking2.jpg'),
       (40, 'artworks/smoking3.jpg'),
       (50, 'artworks/man1.jpg'),
       (60, 'artworks/giraf1.jpg'),
       (70, 'artworks/woman1.jpg'),
       (80, 'artworks/statue1.jpg'),
       (80, 'artworks/statue2.jpg'),
       (80, 'artworks/statue3.jpg'),
       (80, 'artworks/statue4.jpg'),
       (90, 'artworks/warrior1.jpg'),
       (90, 'artworks/warrior2.jpg'),
       (90, 'artworks/warrior3.jpg'),
       (100, 'artworks/marble1.jpg'),
       (100, 'artworks/marble2.jpg'),
       (100, 'artworks/marble3.jpg');

-- Joined table artworks_genres
INSERT INTO artworks_genres (artwork_id, genre_id)
VALUES (10, 1),
       (10, 4),
       (10, 5),
       (10, 7),
       (10, 10),
       (20, 1),
       (20, 4),
       (20, 5),
       (20, 7),
       (20, 10),
       (30, 2),
       (30, 3),
       (30, 8),
       (40, 3),
       (40, 8),
       (40, 11),
       (50, 3),
       (50, 8),
       (60, 8),
       (60, 9),
       (70, 2),
       (70, 3),
       (70, 8),
       (80, 2),
       (80, 12),
       (90, 6),
       (90, 11),
       (100, 6),
       (100, 11);

-- Joined table visitor_favorites
INSERT INTO visitor_favorites (visitor_id, artwork_id)
VALUES (4, 10),
       (4, 20),
       (5, 20),
       (5, 30),
       (5, 40),
       (5, 50),
       (5, 70),
       (6, 90),
       (6, 100);