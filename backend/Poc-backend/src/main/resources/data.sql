CREATE SEQUENCE IF NOT EXISTS user_seq START 1;

ALTER TABLE users ALTER COLUMN id SET DEFAULT nextval('user_seq');

-- Roles
INSERT INTO roles (role_name)
VALUES ('VISITOR'),
       ('ARTIST');

-- Artists
WITH inserted_user AS (
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('henkie123', 'henkie@example.com', '$2a$10$5o//Br13HThXamXA7W93Nu0Ey5EtaKkFzvVy0EEqBmg8tZ8DLQYG2', CURRENT_DATE, 'profile/henkie.jpg')
    RETURNING id
    )
INSERT INTO artists (id, first_name, last_name, city, type_of_art, biography)
SELECT id, 'Henkie', 'Jansen', 'Amsterdam', 'Schilderijen',
       'Hallo, Henkie hier. Ik ben een beginnend abstract schilder met een grote liefde voor kleur, beweging en experiment. Mijn werk ontstaat vaak intuïtief: ik laat me leiden door gevoel en moment, en probeer dat vast te leggen op het doek. Ik werk voornamelijk met acryl- en olieverf en ben niet bang om te spelen met contrasten. Naast vrij werk sta ik ook open voor commissies en samenwerkingen.'
FROM inserted_user;

WITH inserted_user AS (
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('anne123', 'anne@example.com', '$2a$10$o2.a9U2vLLkJ5l8UvGcKZu3thpjyGxHpO0ZxUvgjHEWeqDPYoSATO', CURRENT_DATE, 'profile/anne.jpg')
    RETURNING id
    )
INSERT INTO artists (id, first_name, last_name, city, type_of_art, biography)
SELECT id, 'Anne', 'de Vries', 'Rotterdam', 'Fotografie',
       'Ik ben Anne en ik fotografeer inmiddels meer dan tien jaar met veel passie en toewijding. Mijn focus ligt vooral op portretfotografie, waarbij ik probeer de persoonlijkheid en emotie van mijn onderwerp vast te leggen. Daarnaast werk ik graag met dieren en natuurlijke omgevingen. Mijn stijl is vaak ingetogen en zwart-wit, zodat de aandacht volledig naar het verhaal in het beeld gaat.'
FROM inserted_user;

WITH inserted_user AS (
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('claudia123', 'claudia@example.com', '$2a$10$QBIQ/J/vhp1prik.SXKprOE9CS.oQutZEwEavjnR8Igr.jhTIQus6', CURRENT_DATE, 'profile/claudia.jpg')
    RETURNING id
    )
INSERT INTO artists (id, first_name, last_name, city, type_of_art, biography)
SELECT id,'Claudia', 'Bos', 'Utrecht', 'Beeldhouwen',
       'Als beeldhouwer werk ik voornamelijk met marmer en zeepsteen, materialen die mij uitdagen om geduld en precisie te combineren. Mijn beelden zijn vaak geïnspireerd op de menselijke vorm en klassieke thema’s, maar krijgen altijd een moderne interpretatie. Ik geloof dat een sculptuur van alle kanten iets anders mag vertellen en nodig de kijker uit om letterlijk om het werk heen te bewegen.'
FROM inserted_user;

WITH inserted_user AS (
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('PaulaMarijke', 'paula@example.com', '$2a$10$Tl2hNeEKYNS5IDxqNs4fJ.yuN3zYgjN5UOXu.zaq5OyjwKANZrMrq', CURRENT_DATE, 'profile/paula.jpg')
    RETURNING id
    )
INSERT INTO artists (id, first_name, last_name, city, type_of_art, biography)
SELECT id,'Paula', 'Jansen', 'Zwolle', 'Schilderijen',
       'Wat maakt mijn schilderijen echt van mij? Je weet het wanneer je het ziet. In mijn abstracte werken nodig ik de kijker uit om zelf betekenis te ontdekken en persoonlijke associaties te maken. Emotie, dynamiek en het spel tussen licht en donker vormen de kern van mijn werk. Ik werk in lagen en laat het proces zichtbaar, zodat elk schilderij blijft veranderen naarmate je er langer naar kijkt.'
FROM inserted_user;

-- Visitors
WITH inserted_user AS (
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('pietjelovesart', 'pietje@example.com', '$2a$10$IGvGc9xnZ38nYp/gJhFIF.wmIIirkFiKR6dA9cuW5GKNW4nxH1i4C', CURRENT_DATE, 'profile/pietje.jpg')
    RETURNING id
    )
INSERT INTO visitors (id, name)
SELECT id, 'Pietje'
FROM inserted_user;

WITH inserted_user AS (
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('artlover', 'susan@example.com', '$2a$10$yA60NwrZjzkt8.gG9RDI..6GpWfVC0gsEy5oZpMX7IX89/C9f6aaK', CURRENT_DATE, 'profile/susan.jpg')
    RETURNING id
    )
INSERT INTO visitors (id, name)
SELECT id, 'Susan'
FROM inserted_user;

WITH inserted_user AS (
INSERT INTO users (username, email, password, date_of_registration, profile_picture)
VALUES ('jaapiejaap', 'jaap@example.com', '$2a$10$A67gTJBDB0SCgsrrA5LwpOKLVbkTikSw5QYrcuCKTcL7pRoQn4CL6', CURRENT_DATE, 'profile/jaap.jpg')
    RETURNING id
    )
INSERT INTO visitors (id, name)
SELECT id, 'Jaap'
FROM inserted_user;

-- Joined table user_role
INSERT INTO user_role (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE role_name='ARTIST') FROM users WHERE username IN ('henkie123','anne123','claudia123','PaulaMarijke');

INSERT INTO user_role (user_id, role_id)
SELECT id, (SELECT id FROM roles WHERE role_name='VISITOR') FROM users WHERE username IN ('pietjelovesart','artlover','jaapiejaap');

-- Genres
INSERT INTO genres (name)
VALUES ('Abstract'), ('Modern'), ('Portret'),
       ('Romantisch'), ('Impressionistisch'), ('Marmer'),
       ('Olieverf'), ('Zwart wit'), ('Dieren'),
       ('Natuur'), ('Klassiek'), ('Kubisme'),
       ('Schilderij'), ('Fotografie'), ('Beeld');

-- Artworks
INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Zonsopgang bij de rivier', 250.00, 'AVAILABLE', id, 80, 60, 2 FROM artists WHERE first_name='Henkie';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Zonsondergang blauw', 400.00, 'SOLD', id, 100, 70, 2 FROM artists WHERE first_name='Henkie';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Kind', 75.00, 'AVAILABLE', id, 30, 40, 0 FROM artists WHERE first_name='Anne';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Vrouw rokend', 75.00, 'AVAILABLE', id, 30, 40, 0 FROM artists WHERE first_name='Anne';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Oude man', 60.00, 'AVAILABLE', id, 30, 30, 0 FROM artists WHERE first_name='Anne';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Giraffe', 80.00, 'AVAILABLE', id, 30, 60, 0 FROM artists WHERE first_name='Anne';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Mooie vrouw', 89.00, 'AVAILABLE', id, 50, 35, 0 FROM artists WHERE first_name='Anne';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Kubistische vrouw', 250.00, 'ONLOAN', id, 30, 30, 45 FROM artists WHERE first_name='Claudia';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Gladiator', 349.00, 'AVAILABLE', id, 20, 20, 40 FROM artists WHERE first_name='Claudia';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'Aphrodite', 399.00, 'AVAILABLE', id, 20, 20, 38 FROM artists WHERE first_name='Claudia';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'No.1', 500.00, 'SOLD', id, 30, 40, 2 FROM artists WHERE first_name='Paula';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'No.2', 550.00, 'AVAILABLE', id, 40, 50, 2 FROM artists WHERE first_name='Paula';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'No.3', 450.00, 'AVAILABLE', id, 40, 30, 2 FROM artists WHERE first_name='Paula';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'No.4', 650.00, 'SOLD', id, 40, 80, 2 FROM artists WHERE first_name='Paula';

INSERT INTO artworks (title, price, availability, artist_id, width_in_cm, length_in_cm, height_in_cm)
SELECT 'No.5', 650.00, 'AVAILABLE', id, 40, 80, 2 FROM artists WHERE first_name='Paula';


-- Joined table artwork_images
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/sunset2.jpg' FROM artworks a WHERE a.title='Zonsopgang bij de rivier';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/sunset3.jpg' FROM artworks a WHERE a.title='Zonsopgang bij de rivier';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/sundown1.png' FROM artworks a WHERE a.title='Zonsondergang blauw';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/sundown2.png' FROM artworks a WHERE a.title='Zonsondergang blauw';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/sundown3.png' FROM artworks a WHERE a.title='Zonsondergang blauw';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/sundown4.png' FROM artworks a WHERE a.title='Zonsondergang blauw';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/sundown5.png' FROM artworks a WHERE a.title='Zonsondergang blauw';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/child1.jpg' FROM artworks a WHERE a.title='Kind';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/child2.jpg' FROM artworks a WHERE a.title='Kind';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/smoking1.jpg' FROM artworks a WHERE a.title='Vrouw rokend';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/smoking2.jpg' FROM artworks a WHERE a.title='Vrouw rokend';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/smoking3.jpg' FROM artworks a WHERE a.title='Vrouw rokend';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/man1.jpg' FROM artworks a WHERE a.title='Oude man';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/giraf1.jpg' FROM artworks a WHERE a.title='Giraffe';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/woman1.jpg' FROM artworks a WHERE a.title='Mooie vrouw';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/statue1.jpg' FROM artworks a WHERE a.title='Kubistische vrouw';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/statue2.jpg' FROM artworks a WHERE a.title='Kubistische vrouw';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/statue3.jpg' FROM artworks a WHERE a.title='Kubistische vrouw';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/statue4.jpg' FROM artworks a WHERE a.title='Kubistische vrouw';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/warrior1.jpg' FROM artworks a WHERE a.title='Gladiator';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/warrior2.jpg' FROM artworks a WHERE a.title='Gladiator';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/warrior3.jpg' FROM artworks a WHERE a.title='Gladiator';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/marble1.jpg' FROM artworks a WHERE a.title='Aphrodite';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/marble2.jpg' FROM artworks a WHERE a.title='Aphrodite';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/marble3.jpg' FROM artworks a WHERE a.title='Aphrodite';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no1-1.jpg' FROM artworks a WHERE a.title='No.1';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no1-2.jpg' FROM artworks a WHERE a.title='No.1';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no1-3.jpg' FROM artworks a WHERE a.title='No.1';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no1-4.jpg' FROM artworks a WHERE a.title='No.1';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no1-5.jpg' FROM artworks a WHERE a.title='No.1';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no2-1.jpg' FROM artworks a WHERE a.title='No.2';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no2-2.jpg' FROM artworks a WHERE a.title='No.2';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no2-3.jpg' FROM artworks a WHERE a.title='No.2';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no3-1.jpg' FROM artworks a WHERE a.title='No.3';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no3-2.jpg' FROM artworks a WHERE a.title='No.3';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no3-3.jpg' FROM artworks a WHERE a.title='No.3';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no3-4.jpg' FROM artworks a WHERE a.title='No.3';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no4-1.jpg' FROM artworks a WHERE a.title='No.4';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no4-2.jpg' FROM artworks a WHERE a.title='No.4';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no4-3.jpg' FROM artworks a WHERE a.title='No.4';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no4-4.jpg' FROM artworks a WHERE a.title='No.4';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no4-5.jpg' FROM artworks a WHERE a.title='No.4';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no4-6.jpg' FROM artworks a WHERE a.title='No.4';

INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no5-1.jpg' FROM artworks a WHERE a.title='No.5';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no5-2.jpg' FROM artworks a WHERE a.title='No.5';
INSERT INTO artwork_images (artwork_id, image)
SELECT a.id, 'artworks/no5-3.jpg' FROM artworks a WHERE a.title='No.5';

-- Joined table artworks_genres
INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Zonsopgang bij de rivier' AND g.name IN ('Abstract','Romantisch','Impressionistisch','Olieverf','Natuur','Schilderij');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Zonsondergang blauw' AND g.name IN ('Abstract','Romantisch','Impressionistisch','Olieverf','Natuur','Schilderij');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Kind' AND g.name IN ('Modern','Portret','Zwart wit','Fotografie');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Vrouw rokend' AND g.name IN ('Portret','Zwart wit','Klassiek','Fotografie');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Oude man' AND g.name IN ('Portret','Zwart wit','Fotografie');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Giraffe' AND g.name IN ('Zwart wit','Dieren','Fotografie');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Mooie vrouw' AND g.name IN ('Modern','Portret','Zwart wit','Fotografie');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Kubistische vrouw' AND g.name IN ('Modern','Kubisme','Beeld');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Gladiator' AND g.name IN ('Marmer','Klassiek','Beeld');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='Aphrodite' AND g.name IN ('Marmer','Klassiek','Beeld');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='No.1' AND g.name IN ('Abstract','Olieverf','Schilderij');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='No.2' AND g.name IN ('Abstract','Olieverf','Schilderij');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='No.3' AND g.name IN ('Abstract','Olieverf','Schilderij');

INSERT INTO artworks_genres (artwork_id, genre_id)
SELECT a.id, g.id FROM artworks a, genres g
WHERE a.title='No.4' AND g.name IN ('Abstract','Olieverf','Schilderij');

-- Joined table visitor_favorites
INSERT INTO visitor_favorites (visitor_id, artwork_id)
SELECT v.id, a.id FROM visitors v, artworks a WHERE v.name='Pietje' AND a.title IN ('Zonsopgang bij de rivier','Zonsondergang blauw','No.1','No.2');

INSERT INTO visitor_favorites (visitor_id, artwork_id)
SELECT v.id, a.id FROM visitors v, artworks a WHERE v.name='Susan' AND a.title IN ('Zonsondergang blauw','Kind','Vrouw rokend','Oude man','Mooie vrouw','No.3','No.4');

INSERT INTO visitor_favorites (visitor_id, artwork_id)
SELECT v.id, a.id FROM visitors v, artworks a WHERE v.name='Jaap' AND a.title IN ('Gladiator','Aphrodite','No.1','No.2','No.3','No.4');
