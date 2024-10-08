CREATE TABLE ROUTINE (
    ID BIGINT AUTO_INCREMENT NOT NULL,
    MEMBER_KEY BIGINT NOT NULL,
    TITLE VARCHAR(30) NOT NULL,
    MEMO VARCHAR(1000) NOT NULL,
    START_DATE DATE,
    END_DATE DATE,
    MONTH_CYCLE INT,
    WEEK_CYCLE INT,
    DAY_CYCLE INT,
    START_TIME TIME,
    END_TIME TIME,
    ALARM_BEFORE TIME,
    TIME_PART VARCHAR(10),
    REPRESENT_EMOTICON VARCHAR(300) DEFAULT ':)',
    REACTION_CNT INT DEFAULT 0,
    CREATED_AT TIME DEFAULT CURRENT_TIME,
    UPDATED_AT TIME,
    PRIMARY KEY(ID)
);

--INSERT INTO ROUTINE(MEMBER_KEY, TITLE, MEMO) VALUES("1","TITLE1","MEMO1");
--INSERT INTO ROUTINE(MEMBER_KEY, TITLE, MEMO) VALUES("2","TITLE2","MEMO2");
--INSERT INTO ROUTINE(MEMBER_KEY, TITLE, MEMO) VALUES("3","TITLE3","MEMO3");
--INSERT INTO ROUTINE(MEMBER_KEY, TITLE, MEMO) VALUES("4","TITLE4","MEMO4");