CREATE DATABASE IF NOT EXISTS bookster;
USE bookster;

-- 2. Tabela PERSOANA (clasa părinte)
CREATE TABLE PERSOANA (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nume VARCHAR(50) NOT NULL,
    prenume VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    an_nastere INT,
    tip ENUM('AUTOR', 'UTILIZATOR', 'ADMINISTRATOR') NOT NULL
);

-- 3. Tabela AUTOR (moștenire din PERSOANA)
CREATE TABLE AUTOR (
    persoana_id INT PRIMARY KEY,
    nationalitate VARCHAR(50),
    biografie TEXT,
    FOREIGN KEY (persoana_id) REFERENCES PERSOANA(id) ON DELETE CASCADE
);

-- 4. Tabela UTILIZATOR (moștenire din PERSOANA)
CREATE TABLE UTILIZATOR (
    persoana_id INT PRIMARY KEY,
    parola VARCHAR(100) NOT NULL,
    companie_id INT,
    data_inregistrare DATE,
    FOREIGN KEY (persoana_id) REFERENCES PERSOANA(id) ON DELETE CASCADE,
    FOREIGN KEY (companie_id) REFERENCES COMPANIE(id)
);

-- 5. Tabela ADMINISTRATOR (moștenire din PERSOANA)
CREATE TABLE ADMINISTRATOR (
    persoana_id INT PRIMARY KEY,
    parola VARCHAR(100) NOT NULL,
    FOREIGN KEY (persoana_id) REFERENCES PERSOANA(id) ON DELETE CASCADE
);

-- 6. Tabela COMPANIE
CREATE TABLE COMPANIE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nume VARCHAR(100) NOT NULL
);

-- 7. Tabela CATEGORIE_CARTE
CREATE TABLE CATEGORIE_CARTE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nume VARCHAR(50) NOT NULL
);

-- 8. Tabela CARTE
CREATE TABLE CARTE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titlu VARCHAR(100) NOT NULL,
    autor_id INT NOT NULL,
    categorie_id INT NOT NULL,
    an_publicatie INT,
    disponibil BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (autor_id) REFERENCES AUTOR(persoana_id),
    FOREIGN KEY (categorie_id) REFERENCES CATEGORIE_CARTE(id)
);

-- 9. Tabela COMANDA
CREATE TABLE COMANDA (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilizator_id INT NOT NULL,
    data_comenzii DATE NOT NULL,
    data_returnarii DATE,
    status ENUM('ACTIVA', 'FINALIZATA', 'ANULATA') DEFAULT 'ACTIVA',
    FOREIGN KEY (utilizator_id) REFERENCES UTILIZATOR(persoana_id)
);

-- 10. Tabela RECENZIE
CREATE TABLE RECENZIE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    carte_id INT NOT NULL,
    utilizator_id INT NOT NULL,
    scor INT CHECK (scor BETWEEN 1 AND 5),
    text TEXT,
    data_recenzie DATE,
    FOREIGN KEY (carte_id) REFERENCES CARTE(id),
    FOREIGN KEY (utilizator_id) REFERENCES UTILIZATOR(persoana_id)
);

-- 11. Tabela de legătură COMANDA_CARTE (Many-to-Many)
CREATE TABLE COMANDA_CARTE (
    comanda_id INT NOT NULL,
    carte_id INT NOT NULL,
    PRIMARY KEY (comanda_id, carte_id),
    FOREIGN KEY (comanda_id) REFERENCES COMANDA(id),
    FOREIGN KEY (carte_id) REFERENCES CARTE(id)
);

SHOW TABLES FROM bookster;