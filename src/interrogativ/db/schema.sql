DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS surveys;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
       username TEXT PRIMARY KEY,
       password TEXT,
       created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE surveys (
       url TEXT PRIMARY KEY,
       owner TEXT,
       survey TEXT,
       thankyou TEXT,
       markdown TEXT,
       created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY(owner) REFERENCES users(username)
);

CREATE TABLE answers (
       id INTEGER PRIMARY KEY ASC,
       informant TEXT,
       survey TEXT,
       answer TEXT,
       answered TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       UNIQUE(informant, survey),
       FOREIGN KEY(survey) REFERENCES survey(url)
);
