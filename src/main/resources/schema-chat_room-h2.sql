CREATE TABLE CHAT_ROOM (
    ID BIGINT AUTO_INCREMENT NOT NULL,
    ROOM_NAME VARCHAR(30) NOT NULL,
    ROOM_STATE CHAR(1) NOT NULL,
    CREATED_AT TIME DEFAULT CURRENT_TIME,
    UPDATED_AT TIME,
    PRIMARY KEY(ID)
);

INSERT INTO CHAT_ROOM(ROOM_NAME, ROOM_STATE) VALUES('tmp1', '1');
INSERT INTO CHAT_ROOM(ROOM_NAME, ROOM_STATE) VALUES('tmp2', '1');
INSERT INTO CHAT_ROOM(ROOM_NAME, ROOM_STATE) VALUES('tmp3', '1');
INSERT INTO CHAT_ROOM(ROOM_NAME, ROOM_STATE) VALUES('tmp4', '1');
INSERT INTO CHAT_ROOM(ROOM_NAME, ROOM_STATE) VALUES('tmp5', '1');