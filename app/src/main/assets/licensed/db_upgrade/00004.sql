CREATE TABLE book ( 
    _id          INT            PRIMARY KEY,
    abbreviation VARCHAR( 5 ),
    name         VARCHAR( 50 ) 
)
;
CREATE TABLE passage ( 
    _id     INTEGER PRIMARY KEY,
    book_id INTEGER,
    chapter INTEGER 
)
;
CREATE TABLE plan ( 
    _id      INTEGER        PRIMARY KEY
                            UNIQUE,
    month    INTEGER,
    day      INTEGER,
    section  INTEGER,
    item     INTEGER,
    passage_id INTEGER,
    override VARCHAR( 50 ) 
)
;
