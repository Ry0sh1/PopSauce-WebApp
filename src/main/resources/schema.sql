CREATE TABLE IF NOT EXISTS game(
    id BIGINT AUTO_INCREMENT,
    code TEXT,
    host BIGINT,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS pictures(
    id BIGINT AUTO_INCREMENT,
    content TEXT,
    category TEXT,
    game_id BIGINT,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS player(
    id BIGINT AUTO_INCREMENT,
    username TEXT,
    game_id BIGINT,
    primary key (id)
);

ALTER TABLE game ADD FOREIGN KEY (host) REFERENCES player(id);
ALTER TABLE pictures ADD FOREIGN KEY (game_id) REFERENCES game(id);
ALTER TABLE player ADD FOREIGN KEY (game_id) REFERENCES game(id);