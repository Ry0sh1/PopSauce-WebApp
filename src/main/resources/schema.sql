CREATE TABLE IF NOT EXISTS game(
    id BIGSERIAL,
    code TEXT,
    host_id BIGINT,
    actual_timer int,
    actual_picture_id BIGINT,
    setting_id BIGINT,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS pictures(
    id BIGSERIAL,
    content bytea,
    category TEXT,
    game_id BIGINT,
    right_guess text,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS player(
    id BIGSERIAL,
    username TEXT,
    points INT,
    primary key (id)
);
CREATE TABLE IF NOT EXISTS setting(
    id BIGSERIAL,
    guess_timer int,
    result_timer int,
    category text,
    PRIMARY KEY (id)
);