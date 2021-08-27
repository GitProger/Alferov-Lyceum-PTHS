CREATE DATABASE kettle;
CONNECT kettle;

CREATE TABLE IF NOT EXISTS kettles (
    id       INTEGER,      -- id of a teapot
    room     VARCHAR(100), -- id of a room
    time     INTEGER,      -- time when a teapot will be biold
    volume   INTEGER       -- volume of water in teapot
) CHARACTER SET=utf8;
