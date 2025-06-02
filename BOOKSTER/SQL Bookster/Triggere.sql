-- Pentru AUTOR
DELIMITER //
CREATE TRIGGER trg_autor_before_insert
BEFORE INSERT ON AUTOR
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM UTILIZATOR WHERE persoana_id = NEW.persoana_id
        UNION ALL
        SELECT 1 FROM ADMINISTRATOR WHERE persoana_id = NEW.persoana_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Persoana deja are un rol atribuit (UTILIZATOR/ADMINISTRATOR)';
    END IF;
END//
DELIMITER ;

-- Pentru UTILIZATOR
DELIMITER //
CREATE TRIGGER trg_utilizator_before_insert
BEFORE INSERT ON UTILIZATOR
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM AUTOR WHERE persoana_id = NEW.persoana_id
        UNION ALL
        SELECT 1 FROM ADMINISTRATOR WHERE persoana_id = NEW.persoana_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Persoana deja are un rol atribuit (AUTOR/ADMINISTRATOR)';
    END IF;
END//
DELIMITER ;

-- Pentru ADMINISTRATOR
DELIMITER //
CREATE TRIGGER trg_administrator_before_insert
BEFORE INSERT ON ADMINISTRATOR
FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM AUTOR WHERE persoana_id = NEW.persoana_id
        UNION ALL
        SELECT 1 FROM UTILIZATOR WHERE persoana_id = NEW.persoana_id
    ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Persoana deja are un rol atribuit (AUTOR/UTILIZATOR)';
    END IF;
END//
DELIMITER ;
