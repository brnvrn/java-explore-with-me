DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS locations CASCADE;
DROP TABLE IF EXISTS participation_requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;
DROP TABLE IF EXISTS comments CASCADE;


CREATE TABLE IF NOT EXISTS users(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
name VARCHAR(250) NOT NULL,
email VARCHAR(254) UNIQUE
);

CREATE TABLE IF NOT EXISTS  categories(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
name VARCHAR(50) UNIQUE
);

CREATE TABLE IF NOT EXISTS  locations(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
lat FLOAT NOT NULL,
lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS  events(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
annotation VARCHAR(2000) NOT NULL,
category_id BIGINT NOT NULL,
description VARCHAR(7000) NOT NULL,
event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
location_id BIGINT,
paid Boolean NOT NULL DEFAULT FALSE,
participant_limit INTEGER DEFAULT 0,
request_moderation BOOLEAN DEFAULT TRUE,
title VARCHAR(120) UNIQUE NOT NULL,
initiator_id BIGINT NOT NULL,
created_on TIMESTAMP WITHOUT TIME ZONE,
published_date TIMESTAMP WITHOUT TIME ZONE,
confirmed_requests BIGINT,
event_state VARCHAR(30),
views INT DEFAULT 0,
CONSTRAINT events_to_categories FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE,
CONSTRAINT events_to_locations FOREIGN KEY(location_id) REFERENCES locations(id) ON DELETE CASCADE,
CONSTRAINT events_to_users FOREIGN KEY(initiator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participation_requests(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
created TIMESTAMP WITHOUT TIME ZONE,
event_id BIGINT NOT NULL,
requester_id BIGINT NOT NULL,
status VARCHAR(30) NOT NULL,
CONSTRAINT participation_to_events FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
CONSTRAINT participation_to_users FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE CASCADE,
CONSTRAINT unique_event_requester UNIQUE(event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
pinned BOOLEAN NOT NULL,
title VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events(
compilation_id BIGINT NOT NULL,
event_id BIGINT NOT NULL,
CONSTRAINT ce_to_compilations FOREIGN KEY(compilation_id) REFERENCES compilations(id) ON DELETE CASCADE,
CONSTRAINT ce_to_events FOREIGN KEY(event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments(
id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
text VARCHAR(2000) NOT NULL,
commentator_id BIGINT NOT NULL,
event_id BIGINT NOT NULL,
created  TIMESTAMP WITHOUT TIME ZONE,
status VARCHAR(30) NOT NULL,
points INT NOT NULL,
CONSTRAINT comments_to_users FOREIGN KEY (commentator_id) REFERENCES users(id) ON DELETE CASCADE,
CONSTRAINT comments_to_events FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);