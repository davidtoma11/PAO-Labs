INSERT INTO PERSOANA (nume, prenume, email, an_nastere, tip) VALUES
('Muresan', 'Dorian', 'dorian.muresan@email.com', 1980, 'AUTOR'),
('Voiculescu', 'Anca', 'anca.voiculescu@email.com', 1975, 'AUTOR'),
('Neacsu', 'Radu', 'radu.neacsu@email.com', 1990, 'UTILIZATOR'),
('Gherman', 'Otilia', 'otilia.gherman@email.com', 1985, 'UTILIZATOR'),
('Toma', 'David', 'david.toma@email.com', 1970, 'ADMINISTRATOR'),
('Cernat', 'Sorina', 'sorina.cernat@email.com', 1995, 'UTILIZATOR'),
('Damian', 'Victor', 'victor.damian@email.com', 1982, 'AUTOR');


-- 2. Inserari in tabela COMPANIE
INSERT INTO COMPANIE (nume) VALUES
('Carturesti'),
('Humanitas'),
('Paralela 45'),
('RAO'),
('Litera');

-- 3. Inserari in tabela AUTOR
INSERT INTO AUTOR (persoana_id, nationalitate, biografie) VALUES
(1, 'romana', 'Autor cu experienta in proza contemporana, cu titluri apreciate de critici'),
(2, 'romana', 'Scriitoare remarcata pentru stilul intim si psihologic'),
(7, 'franceza', 'Autor de romane fantasy cu o viziune originala si voce distincta');

INSERT INTO UTILIZATOR (persoana_id, parola, companie_id, data_inregistrare) VALUES
(3, 'radu1234', 1, '2025-01-15'),
(4, 'passOti22', 3, '2024-03-22'),
(6, 'soriNpass', 2, '2025-02-10');


INSERT INTO ADMINISTRATOR (persoana_id, parola) VALUES
(5, 'parolaadmin1127');


INSERT INTO CATEGORIE_CARTE (nume) VALUES
('Fictiune'),
('Non-fictiune'),
('Fantasy'),
('Istoric'),
('Biografie'),
('Poezie'),
('Stiinta');


INSERT INTO CARTE (titlu, autor_id, categorie_id, an_publicatie, disponibil) VALUES
('Ecouri uitate', 1, 1, 2015, TRUE),
('Cuvinte din umbra', 2, 1, 2018, TRUE),
('Cronicile Nordului', 7, 3, 2020, TRUE),
('Umbre peste Carpati', 1, 4, 2010, FALSE),
('Pasii memoriei', 2, 5, 2019, TRUE),
('Foc si cenusa', 7, 3, 2021, TRUE),
('Adevaruri tacute', 1, 2, 2017, TRUE);


-- 8. Inserari in tabela COMANDA
INSERT INTO COMANDA (utilizator_id, data_comenzii, data_returnarii, status) VALUES
(3, '2025-01-10', '2023-01-30', 'FINALIZATA'),
(4, '2025-02-15', NULL, 'ACTIVA'),
(6, '2025-03-01', NULL, 'ACTIVA'),
(3, '2025-03-10', NULL, 'ANULATA');


INSERT INTO RECENZIE (carte_id, utilizator_id, scor, text, data_recenzie) VALUES
(1, 3, 5, 'O poveste impresionanta si profunda', '2025-02-05'),
(2, 4, 4, 'Scriitura eleganta, uneori greoaie', '2025-02-20'),
(3, 6, 5, 'Imaginatie bogata si actiune dinamica', '2025-03-05'),
(1, 6, 3, 'Bine scrisa, dar cu un final slab', '2025-03-12');


-- 10. Inserari in tabela COMANDA_CARTE
INSERT INTO COMANDA_CARTE (comanda_id, carte_id) VALUES
(1, 1),
(1, 2),
(2, 3),
(2, 5),
(3, 4),
(3, 7),
(4, 6);