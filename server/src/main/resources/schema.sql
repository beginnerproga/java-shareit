CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  CHARACTER VARYING(20)                   NOT NULL,
    email CHARACTER VARYING(100)                  NOT NULL UNIQUE,
    CONSTRAINT PK_USERS PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  CHARACTER VARYING(300)                  NOT NULL,
    requestor_id BIGINT                                  NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT FK_USERS_REQUESTS FOREIGN KEY (requestor_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT PK_REQUESTS PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         CHARACTER VARYING(40)                   NOT NULL,
    description  CHARACTER VARYING(300)                  NOT NULL,
    is_available BOOLEAN                                 NOT NULL,
    owner_id     BIGINT                                  NOT NULL,
    request_id   BIGINT,
    CONSTRAINT PK_ITEMS PRIMARY KEY (id),
    CONSTRAINT FK_USERS_ITEMS FOREIGN KEY (owner_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT FK_REQUESTS_ITEMS FOREIGN KEY (request_id)
        REFERENCES requests (id)

);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT                                  NOT NULL,
    booker_id  BIGINT                                  NOT NULL,
    status     CHARACTER VARYING(10)                   NOT NULL,
    CONSTRAINT FK_ITEMS_BOOKINGS FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT FK_USERS_BOOKINGS FOREIGN KEY (booker_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT PK_BOOKINGS PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      CHARACTER VARYING(700)                  NOT NULL,
    item_id   BIGINT                                  NOT NULL,
    author_id BIGINT                                  NOT NULL,
    created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT PK_COMMENTS PRIMARY KEY (id),
    CONSTRAINT PK_ITEMS_COMMENTS FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT PK_USERS_COMMENTS FOREIGN KEY (author_id)
        REFERENCES users (id) ON DELETE CASCADE
);