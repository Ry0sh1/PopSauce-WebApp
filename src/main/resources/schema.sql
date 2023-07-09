CREATE TABLE IF NOT EXISTS game(
    id BIGSERIAL,
    code TEXT,
    host_id BIGINT,
    current_timer int,
    current_picture_id BIGINT,
    setting_id BIGINT,
    started boolean,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS pictures(
    id BIGSERIAL,
    content bytea,
    category TEXT,
    right_guess text,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS player(
    id BIGSERIAL,
    username TEXT,
    points INT,
    game_id BIGINT,
    primary key (id)
);
CREATE TABLE IF NOT EXISTS setting(
    id BIGSERIAL,
    guess_timer int,
    result_timer int,
    category text,
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS pictures_games(
    pictures_id BIGINT,
    games_id BIGINT,
    place int,
    PRIMARY KEY (pictures_id,games_id)
);

ALTER TABLE pictures_games ADD FOREIGN KEY (pictures_id) REFERENCES pictures(id);
ALTER TABLE pictures_games ADD FOREIGN KEY (games_id) REFERENCES game(id);