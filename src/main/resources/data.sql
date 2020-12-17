INSERT INTO USER (ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, EMAIL) VALUES(TRUE, 'Andrea', 'Mussmann', '$2y$09$6uAsej13d7MvlTq419WTu.0KCjpttbJ.WIrfdRufSiqHO63xbRhmC', 'amuss', 'a.mussmann@swa.at')
INSERT INTO USER (ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, EMAIL) VALUES(TRUE, 'Clemens', 'Sauerwein', '$2y$09$6uAsej13d7MvlTq419WTu.0KCjpttbJ.WIrfdRufSiqHO63xbRhmC', 'csauer', 'c.sauerwein@swa.at')
INSERT INTO USER (ENABLED, FIRST_NAME, LAST_NAME, PASSWORD, USERNAME, EMAIL) VALUES(TRUE, 'Simon', 'Priller', '$2y$09$6uAsej13d7MvlTq419WTu.0KCjpttbJ.WIrfdRufSiqHO63xbRhmC', 'sprill', 's.priller@swa.at')
INSERT INTO USER_USER_ROLE (USER_USERNAME, ROLES) VALUES ('amuss', 'ADMIN') 
INSERT INTO USER_USER_ROLE (USER_USERNAME, ROLES) VALUES ('csauer', 'CUSTOMER') 
INSERT INTO USER_USER_ROLE (USER_USERNAME, ROLES) VALUES ('sprill', 'LIBRARIAN')
INSERT INTO MEDIA(MEDIAID, TITLE, PUBLISHING_DATE, LANGUAGE, TOTAL_AVAIL, CUR_BORROWED, MEDIA_TYPE) VALUES (1, '20.000 Meilen unter dem Meer', '2017-01-01', 'GE', 4, 0, 'BOOK')
INSERT INTO BOOK(MEDIAID, AUTHOR, ISBN) VALUES (1, 'Jules Vernes', '978-395-728045-9')
INSERT INTO MEDIA (MEDIAID, TITLE, PUBLISHING_DATE, LANGUAGE, TOTAL_AVAIL, CUR_BORROWED, MEDIA_TYPE) VALUES (2, '20.000 Meilen unter dem Meer', '2009-08-11', 'GE', 20, 0, 'AUDIOBOOK')
INSERT INTO AUDIO_BOOK (MEDIAID, AUTHOR, ISBN, LENGTH, SPEAKER) VALUES (2, 'Jules Vernes', '978-3-89813-905-2', 2640, 'Peter G. Hubler')
INSERT INTO MEDIA(MEDIAID, TITLE, PUBLISHING_DATE, LANGUAGE, TOTAL_AVAIL, CUR_BORROWED, MEDIA_TYPE) VALUES (3, 'Playboy', '2009-11-01', 'GE', 14, 0, 'MAGAZINE')
INSERT INTO MAGAZINE(MEDIAID, SERIES) VALUES (3, 'Monthly Playboy')
INSERT INTO MEDIA(MEDIAID, TITLE, PUBLISHING_DATE, LANGUAGE, TOTAL_AVAIL, CUR_BORROWED, MEDIA_TYPE) VALUES (4, 'The Silicon Valley Stroy', '1999-05-02', 'EN', 6, 0, 'VIDEO')
INSERT INTO VIDEO(MEDIAID, LENGTH) VALUES (4,5580)
INSERT INTO RESERVED(RESERVEDID, RESERVE_TIME, MEDIA_MEDIAID, USER_USERNAME) VALUES (1, '2020-12-14 18:36:14.1', 1, 'amuss')
INSERT INTO BORROWED(BORROWID, BORROW_DATE, MEDIA_MEDIAID, USER_USERNAME) VALUES (1, '2020-11-12', 3, 'csauer')
INSERT INTO BOOKMARKS(BOOKMARKID, MEDIA_MEDIAID, USER_USERNAME) VALUES (1, 2, 'csauer')
INSERT INTO MEDIA_BORROW_TIME(MEDIA_TYPE, ALLOWED_BORROW_TIME) VALUES ('VIDEO', 7)
INSERT INTO MEDIA_BORROW_TIME(MEDIA_TYPE, ALLOWED_BORROW_TIME) VALUES ('BOOK', 21)
INSERT INTO MEDIA_BORROW_TIME(MEDIA_TYPE, ALLOWED_BORROW_TIME) VALUES ('AUDIOBOOK', 14)
INSERT INTO MEDIA_BORROW_TIME(MEDIA_TYPE, ALLOWED_BORROW_TIME) VALUES ('MAGAZINE', 14)
