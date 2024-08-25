CREATE TABLE MEMBER (
    REC_KEY BIGINT AUTO_INCREMENT NOT NULL,
    USER_ID VARCHAR(30) NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    INTRODUCE VARCHAR(1000),
    REACTION_CNT INT DEFAULT 0,
    CREATED_AT TIME DEFAULT CURRENT_TIME,
    UPDATED_AT TIME,
    PRIMARY KEY(REC_KEY)
);

INSERT INTO MEMBER(USER_ID, PASSWORD) VALUES('ID1', 'PW1');
INSERT INTO MEMBER(USER_ID, PASSWORD) VALUES('ID2', 'PW2');
INSERT INTO MEMBER(USER_ID, PASSWORD) VALUES('ID3', 'PW3');
INSERT INTO MEMBER(USER_ID, PASSWORD) VALUES('ID4', 'PW4');