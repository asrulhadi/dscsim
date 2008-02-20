CREATE TABLE CODE_TEXT ( 
CODE CHAR(2) PRIMARY KEY, 
TEXT CHAR(32)
);

CREATE TABLE ADDRESS_BOOK (
ID CHAR(9) PRIMARY KEY,
NAME CHAR(15), 
TYPE_CD CHAR(2)
);


CREATE TABLE DSCMESSAGE (
UID TIMESTAMP,
SENDER CHAR(9), 
RECIPIENT CHAR(9) NULL, 
CALL_TYPE_CD CHAR(32),
NATURE_CD CHAR(32) , 
CATAGORY_CD CHAR(32),  
POS_LAT_DEG INT,
POS_LAT_MIN INT,
POS_LAT_HEM CHAR(1),
POS_LON_DEG INT,
POS_LON_MIN INT,
POS_LON_HEM CHAR(1),
POS_TIME_HRS INT,
POS_TIME_MIN INT,
CHANNEL INT,
COMPLIANCE_CD CHAR(32),
COMPLIANCE_REASON_CD CHAR(32),
ACKD_TIME TIMESTAMP,
PRIMARY KEY(UID, SENDER)

);




