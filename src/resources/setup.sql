-- CREATE DATABASE festival_db;
USE festival_db;

CREATE TABLE FestivalDay (
    day ENUM('DAY1', 'DAY2', 'DAY3') PRIMARY KEY
);
INSERT INTO FestivalDay (day) VALUES ('DAY1'), ('DAY2'), ('DAY3');

CREATE TABLE Organizer (
    id_organizer INT AUTO_INCREMENT PRIMARY KEY,
    companyName VARCHAR(100) NOT NULL,
    organizerName VARCHAR(100) NOT NULL
);

CREATE TABLE Event (
    id_event INT AUTO_INCREMENT PRIMARY KEY,
    day ENUM('DAY1', 'DAY2', 'DAY3') NOT NULL,
    id_organizer INT,
    eventName VARCHAR(100) NOT NULL,
    startTime TIME NOT NULL,
    endTime TIME NOT NULL,
    FOREIGN KEY (day) REFERENCES FestivalDay(day),
	FOREIGN KEY (id_organizer) REFERENCES Organizer(id_organizer) ON DELETE SET NULL
    -- daca un organizator este sters, eventurile raman in baza de date
);


CREATE TABLE Game (
     id_game INT AUTO_INCREMENT PRIMARY KEY,
    gameName VARCHAR(100) NOT NULL,
    isOpenAllNight BOOLEAN NOT NULL,
    hasPrize BOOLEAN NOT NULL,
    maxCapacity INT NOT NULL
);

CREATE TABLE CampEats (
    id_event INT PRIMARY KEY,
    vendorName VARCHAR(100) NOT NULL,
    openUntilLate BOOLEAN NOT NULL,
    FOREIGN KEY (id_event) REFERENCES Event(id_event) ON DELETE CASCADE
);

CREATE TABLE CampEatsFoodType (
    id_event INT,
    foodType VARCHAR(100),
    PRIMARY KEY (id_event, foodType),
    FOREIGN KEY (id_event) REFERENCES CampEats(id_event) ON DELETE CASCADE
);

CREATE TABLE Concert (
    id_event INT PRIMARY KEY,
    artist VARCHAR(100) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_event) REFERENCES Event(id_event) ON DELETE CASCADE
);

CREATE TABLE DJ (
     id_event INT PRIMARY KEY,
    djName VARCHAR(100) NOT NULL,
    isMainStage BOOLEAN NOT NULL,
    FOREIGN KEY (id_event) REFERENCES Event(id_event) ON DELETE CASCADE
);

CREATE TABLE FunZone (
    id_event INT PRIMARY KEY,
    FOREIGN KEY (id_event) REFERENCES Event(id_event) ON DELETE CASCADE
);

CREATE TABLE FunZoneGames (
	id_event INT NOT NULL,
    id_game INT NOT NULL,
    PRIMARY KEY (id_event, id_game),
    FOREIGN KEY (id_event) REFERENCES FunZone(id_event) ON DELETE CASCADE,
    FOREIGN KEY (id_game) REFERENCES Game(id_game) ON DELETE CASCADE
);

CREATE TABLE GlobalTalks (
    id_event INT PRIMARY KEY,
    speakerName VARCHAR(100) NOT NULL,
    topic VARCHAR(100) NOT NULL,
    seats INT NOT NULL,
    FOREIGN KEY (id_event) REFERENCES Event(id_event) ON DELETE CASCADE
);

CREATE TABLE Ticket (
    code VARCHAR(20) PRIMARY KEY,
    price DOUBLE NOT NULL
);

CREATE TABLE TicketUnder25 (
    code VARCHAR(20) PRIMARY KEY,
    discountPercentage DOUBLE NOT NULL,
    FOREIGN KEY (code) REFERENCES Ticket(code)
);

CREATE TABLE Participant (
    id_participant INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20),
    participantName VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    FOREIGN KEY (code) REFERENCES Ticket(code) ON DELETE CASCADE
);


CREATE TABLE ParticipantEvent (
    id_participant INT NOT NULL,
    id_event INT NOT NULL,
    PRIMARY KEY (id_participant, id_event),
    FOREIGN KEY (id_participant) REFERENCES Participant(id_participant) ON DELETE CASCADE,
    FOREIGN KEY (id_event) REFERENCES Event(id_event) ON DELETE CASCADE
);

CREATE TABLE GlobalTalkSeat (
    id_participant INT,
    id_event INT,
    PRIMARY KEY (id_participant, id_event),
    FOREIGN KEY (id_participant) REFERENCES Participant(id_participant) ON DELETE CASCADE,
    FOREIGN KEY (id_event) REFERENCES Event(id_event) ON DELETE CASCADE
);





