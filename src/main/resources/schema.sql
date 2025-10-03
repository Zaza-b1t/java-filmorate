CREATE TABLE IF NOT EXISTS mpa (
    id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres (
    id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id        INTEGER PRIMARY KEY AUTO_INCREMENT,
    email     VARCHAR(255) NOT NULL,
    login     VARCHAR(100) NOT NULL,
    name      VARCHAR(255),
    birthday  DATE NOT NULL,
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_login CHECK (login <> '')
);

CREATE TABLE IF NOT EXISTS films (
    id             INTEGER PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(255) NOT NULL,
    description    VARCHAR(2000),
    release_date   DATE NOT NULL,
    duration       INTEGER NOT NULL,
    mpa_id         INTEGER NOT NULL,
    CONSTRAINT ck_films_duration CHECK (duration > 0),
    CONSTRAINT fk_films_mpa FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id  INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_fg_film  FOREIGN KEY (film_id)  REFERENCES films(id)  ON DELETE CASCADE,
    CONSTRAINT fk_fg_genre FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS likes (
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_likes_film  FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_user  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id    INTEGER NOT NULL,
    friend_id  INTEGER NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_fr_user   FOREIGN KEY (user_id)   REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_fr_friend FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

ALTER TABLE friendship
    ADD COLUMN IF NOT EXISTS status VARCHAR(12)
        DEFAULT 'PENDING';

ALTER TABLE friendship
    ADD CONSTRAINT IF NOT EXISTS ck_friendship_self CHECK (user_id <> friend_id);

ALTER TABLE friendship
    ADD CONSTRAINT IF NOT EXISTS ck_friendship_status
        CHECK (status IN ('PENDING','CONFIRMED'));

UPDATE friendship f
SET status = 'CONFIRMED'
WHERE status IS NULL
   OR status NOT IN ('PENDING','CONFIRMED');

UPDATE friendship f
SET status = 'CONFIRMED'
WHERE EXISTS (
    SELECT 1 FROM friendship r
    WHERE r.user_id = f.friend_id
      AND r.friend_id = f.user_id
);

