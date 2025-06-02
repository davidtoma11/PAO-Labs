package service;

import model.Administrator;
import model.Utilizator;
import model.Companie;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Date;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class AutentificareService {
    private List<Utilizator> utilizatori;
    private List<Administrator> administratori;
    private List<Companie> companii;

    public AutentificareService(List<Utilizator> utilizatori, List<Administrator> administratori, List<Companie> companii) {
        this.utilizatori = Objects.requireNonNull(utilizatori, "Lista utilizatori nu poate fi null");
        this.administratori = Objects.requireNonNull(administratori, "Lista administratori nu poate fi null");
        this.companii = Objects.requireNonNull(companii, "Lista companii nu poate fi null");
    }

    public Object login(Scanner scanner) {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Parolă: ");
        String parola = scanner.nextLine().trim();

        // Verificare administratori
        for (Administrator admin : administratori) {
            if (admin.getEmail().equalsIgnoreCase(email) && admin.getParola().equals(parola)) {
                System.out.println("Autentificare reușită ca ADMINISTRATOR.");
                return admin;
            }
        }

        // Verificare utilizatori
        for (Utilizator user : utilizatori) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getParola().equals(parola)) {
                System.out.println("Autentificare reușită ca UTILIZATOR.");
                return user;
            }
        }

        return null;
    }

    public Utilizator creareContNou(Scanner scanner) {
        System.out.println("\n--- CREARE CONT NOU ---");

        // Colectare date de bază
        System.out.print("Nume: ");
        String nume = scanner.nextLine().trim();

        System.out.print("Prenume: ");
        String prenume = scanner.nextLine().trim();

        // Validare email
        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
            if (email.contains("@") && !email.startsWith("@") && !email.endsWith("@")) {
                String finalEmail = email.toLowerCase();
                if (utilizatori.stream().noneMatch(u -> u.getEmail().equalsIgnoreCase(finalEmail))) {
                    break;
                } else {
                    System.out.println("Email deja folosit.");
                }
            } else {
                System.out.println("Email invalid. Folosește formatul corect (ex: user@example.com).");
            }
        }

        // Validare an naștere
        int anNastere;
        while (true) {
            try {
                System.out.print("An naștere: ");
                anNastere = Integer.parseInt(scanner.nextLine());
                if (anNastere > 1900 && anNastere < LocalDate.now().getYear()) {
                    break;
                } else {
                    System.out.println("An invalid. Introduceți un an între 1900 și " + (LocalDate.now().getYear() - 1));
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduceți un an valid (format numeric).");
            }
        }

        // Validare parolă
        String parola;
        while (true) {
            System.out.print("Parolă (minim 6 caractere): ");
            parola = scanner.nextLine().trim();
            if (parola.length() >= 6) {
                break;
            } else {
                System.out.println("Parola trebuie să aibă minim 6 caractere.");
            }
        }

        // Selectare companie afiliată
        Companie companieAfiliata = null;
        if (!companii.isEmpty()) {
            System.out.println("Companii disponibile:");
            companii.forEach(c -> System.out.println("- " + c.getNume()));

            while (true) {
                System.out.print("Nume companie afiliată (lasă gol pentru niciuna): ");
                String numeCompanie = scanner.nextLine().trim();

                if (numeCompanie.isEmpty()) {
                    break;
                }

                Optional<Companie> companieOpt = companii.stream()
                        .filter(c -> c.getNume().equalsIgnoreCase(numeCompanie))
                        .findFirst();

                if (companieOpt.isPresent()) {
                    companieAfiliata = companieOpt.get();
                    break;
                } else {
                    System.out.println("Companie negăsită. Încearcă din nou sau lasă gol.");
                }
            }
        }

        // Încărcare configurație baza de date
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("Fișierul db.properties nu a fost găsit în classpath");
            }
            prop.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Eroare la încărcarea configurației bazei de date", e);
        }

        String url = prop.getProperty("db.url");
        String user = prop.getProperty("db.user");
        String password = prop.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new RuntimeException("Configurația bazei de date este incompletă în db.properties");
        }

        // Operațiuni baza de date
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            connection.setAutoCommit(false); // Începem o tranzacție

            // Pasul 1: Inserare în tabela PERSOANA
            String sqlPersoana = "INSERT INTO PERSOANA (nume, prenume, email, an_nastere, tip) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psPersoana = connection.prepareStatement(sqlPersoana, Statement.RETURN_GENERATED_KEYS)) {
                psPersoana.setString(1, nume);
                psPersoana.setString(2, prenume);
                psPersoana.setString(3, email);
                psPersoana.setInt(4, anNastere);
                psPersoana.setString(5, "UTILIZATOR");

                int affectedRows = psPersoana.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Crearea persoanei a eșuat, niciun rând inserat.");
                }

                // Obținem ID-ul generat
                try (ResultSet generatedKeys = psPersoana.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int persoanaId = generatedKeys.getInt(1);

                        // Pasul 2: Inserare în tabela UTILIZATOR
                        String sqlUtilizator = "INSERT INTO UTILIZATOR (persoana_id, parola, companie_id, data_inregistrare) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement psUtilizator = connection.prepareStatement(sqlUtilizator)) {
                            psUtilizator.setInt(1, persoanaId);
                            psUtilizator.setString(2, parola);

                            if (companieAfiliata != null) {
                                psUtilizator.setInt(3, companieAfiliata.getId());
                            } else {
                                psUtilizator.setNull(3, Types.INTEGER);
                            }

                            psUtilizator.setDate(4, Date.valueOf(LocalDate.now()));
                            psUtilizator.executeUpdate();

                            // Commit tranzacție dacă totul este OK
                            connection.commit();

                            // Creăm obiectul Utilizator
                            Utilizator utilizatorNou = new Utilizator(
                                    persoanaId,
                                    nume,
                                    prenume,
                                    email,
                                    anNastere,
                                    parola,
                                    companieAfiliata,
                                    LocalDate.now(),
                                    new ArrayList<>()
                            );

                            // Adăugăm utilizatorul în lista din memorie
                            utilizatori.add(utilizatorNou);

                            // Actualizăm lista utilizatori din companie (dacă există)
                            if (companieAfiliata != null) {
                                companieAfiliata.getUtilizatori().add(utilizatorNou);
                            }

                            System.out.println("Cont creat cu succes! ID utilizator: " + persoanaId);
                            return utilizatorNou;
                        }
                    } else {
                        throw new SQLException("Crearea persoanei a eșuat, nu s-a generat ID-ul.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la crearea contului în baza de date:");
            e.printStackTrace();
            System.out.println("Nu s-a putut crea contul. Te rugăm să încerci din nou.");
        }

        return null;
    }

    public List<Utilizator> getUtilizatori() {
        return utilizatori;
    }

    public List<Administrator> getAdministratori() {
        return administratori;
    }
}