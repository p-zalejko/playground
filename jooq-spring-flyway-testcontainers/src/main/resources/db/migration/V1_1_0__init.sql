CREATE TYPE measurementUnit AS ENUM('CM');

CREATE TABLE measurement
(
    id        serial PRIMARY KEY,
    value     decimal NOT NULL,
    timestamp TIMESTAMP    NOT NULL,
    unit measurementUnit NOT NULL
)