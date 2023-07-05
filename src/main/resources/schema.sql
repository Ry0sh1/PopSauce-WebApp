CREATE TABLE IF NOT EXISTS game(
    id BIGSERIAL,
    code TEXT,
    host BIGINT,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS pictures(
    id BIGSERIAL,
    content bytea,
    category TEXT,
    game_id BIGINT,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS player(
    id BIGSERIAL,
    username TEXT,
    game_id BIGINT,
    primary key (id)
);

ALTER TABLE game ADD FOREIGN KEY (host) REFERENCES player(id);