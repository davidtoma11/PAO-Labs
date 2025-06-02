package ui;

import model.Utilizator;
import service.DataInitialization;
import service.AuditService;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class MeniuUtilizator {
    private final Utilizator utilizator;
    private final Scanner scanner;
    private final DataInitialization dataService;

    public MeniuUtilizator(Utilizator utilizator, Scanner scanner, DataInitialization dataService) {
        this.utilizator = utilizator;
        this.scanner = scanner;
        this.dataService = dataService;
    }

    public void start() {
        int optiune = 0;

        do {
            System.out.println("\n===== MENIU UTILIZATOR =====");
            System.out.println("1. Gestionare comenzi");
            System.out.println("2. Scrie recenzie");
            System.out.println("3. Vizualizeaza colegi de companie");
            System.out.println("0. Iesire");
            System.out.print("Alege o optiune: ");

            try {
                optiune = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar valid!");
                continue;
            }

            switch (optiune) {
                case 1 -> gestionareComenzi();
                case 2 -> scrieRecenzie();
                case 3 -> vizualizeazaColegi();
                case 0 -> System.out.println("Iesire din meniul utilizator...");
                default -> System.out.println("Optiune invalida. Incearca din nou.");
            }

        } while (optiune != 0);
    }

    private void gestionareComenzi() {
        int optiune = 0;

        do {
            System.out.println("\n--- Gestionare Comenzi ---");
            System.out.println("1. Vezi comenzile mele active");
            System.out.println("2. Vezi istoric comenzi");
            System.out.println("3. Plaseaza comanda");
            System.out.println("4. Anulează comandă");
            System.out.println("0. Inapoi");
            System.out.print("Alege o optiune: ");

            try {
                optiune = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar valid!");
                continue;
            }

            switch (optiune) {
                case 1 -> afiseazaComenziActive();
                case 2 -> afiseazaIstoricComenzi();
                case 3 -> plaseazaComanda();
                case 4 -> anuleazaComanda();
                case 0 -> System.out.println("Revenire la meniul principal...");
                default -> System.out.println("Optiune invalida. Incearca din nou.");
            }
        } while (optiune != 0);
    }

    private void afiseazaComenziActive() {
        try (Connection conn = this.dataService.getConnection()) {
            String sql = """
                SELECT c.id, c.data_comenzii, GROUP_CONCAT(carte.titlu SEPARATOR ', ') AS carti
                FROM COMANDA c
                JOIN COMANDA_CARTE cc ON c.id = cc.comanda_id
                JOIN CARTE carte ON cc.carte_id = carte.id
                WHERE c.utilizator_id = ? AND c.status = 'ACTIVA'
                GROUP BY c.id
                """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.utilizator.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Comenzile tale active ---");
            boolean existaComenzi = false;

            while (rs.next()) {
                existaComenzi = true;
                System.out.printf("Comanda #%d din %s%n",
                        rs.getInt("id"), rs.getString("data_comenzii"));
                System.out.println("Cărți: " + rs.getString("carti"));
                System.out.println("------------------------");
            }

            if (!existaComenzi) {
                System.out.println("Nu ai comenzi active momentan.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare comenzi: " + e.getMessage());
        }
    }

    private void afiseazaIstoricComenzi() {
        try (Connection conn = this.dataService.getConnection()) {
            String sql = """
                SELECT c.id, c.data_comenzii, c.data_returnarii, c.status, 
                       GROUP_CONCAT(carte.titlu SEPARATOR ', ') AS carti
                FROM COMANDA c
                JOIN COMANDA_CARTE cc ON c.id = cc.comanda_id
                JOIN CARTE carte ON cc.carte_id = carte.id
                WHERE c.utilizator_id = ?
                GROUP BY c.id
                ORDER BY c.data_comenzii DESC
                LIMIT 10
                """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, this.utilizator.getId());
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n--- Ultimele tale comenzi ---");
            boolean existaComenzi = false;

            while (rs.next()) {
                existaComenzi = true;
                System.out.printf("Comanda #%d | %s | Status: %s%n",
                        rs.getInt("id"), rs.getString("data_comenzii"), rs.getString("status"));
                System.out.println("Cărți: " + rs.getString("carti"));
                if (rs.getString("data_returnarii") != null) {
                    System.out.println("Returnată la: " + rs.getString("data_returnarii"));
                }
                System.out.println("------------------------");
            }

            if (!existaComenzi) {
                System.out.println("Nu ai nicio comandă în istoric.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare istoric: " + e.getMessage());
        }
    }

    private void plaseazaComanda() {
        try (Connection conn = this.dataService.getConnection()) {
            // Afișăm cărțile disponibile
            System.out.println("\n--- Cărți disponibile ---");
            String sqlCarti = "SELECT id, titlu FROM CARTE WHERE disponibil = TRUE";
            Statement stmtCarti = conn.createStatement();
            ResultSet rsCarti = stmtCarti.executeQuery(sqlCarti);

            while (rsCarti.next()) {
                System.out.printf("ID: %d | %s%n", rsCarti.getInt("id"), rsCarti.getString("titlu"));
            }

            System.out.print("\nIntrodu ID-ul cărții dorite: ");
            int carteId = scanner.nextInt();
            scanner.nextLine();

            // Verificăm dacă cartea este disponibilă
            String sqlVerifica = "SELECT 1 FROM CARTE WHERE id = ? AND disponibil = TRUE";
            PreparedStatement stmtVerifica = conn.prepareStatement(sqlVerifica);
            stmtVerifica.setInt(1, carteId);
            ResultSet rsVerifica = stmtVerifica.executeQuery();

            if (!rsVerifica.next()) {
                System.out.println("Cartea nu este disponibilă sau ID invalid!");
                return;
            }

            // Creăm comanda
            String sqlComanda = "INSERT INTO COMANDA (utilizator_id, data_comenzii, status) VALUES (?, ?, 'ACTIVA')";
            PreparedStatement stmtComanda = conn.prepareStatement(sqlComanda, Statement.RETURN_GENERATED_KEYS);
            stmtComanda.setInt(1, this.utilizator.getId());
            stmtComanda.setString(2, LocalDate.now().toString());
            stmtComanda.executeUpdate();

            int comandaId;
            try (ResultSet rs = stmtComanda.getGeneratedKeys()) {
                rs.next();
                comandaId = rs.getInt(1);
            }

            // Adăugăm cartea la comandă
            String sqlComandaCarte = "INSERT INTO COMANDA_CARTE (comanda_id, carte_id) VALUES (?, ?)";
            PreparedStatement stmtComandaCarte = conn.prepareStatement(sqlComandaCarte);
            stmtComandaCarte.setInt(1, comandaId);
            stmtComandaCarte.setInt(2, carteId);
            stmtComandaCarte.executeUpdate();

            // Marcăm cartea ca indisponibilă
            String sqlUpdateCarte = "UPDATE CARTE SET disponibil = FALSE WHERE id = ?";
            PreparedStatement stmtUpdateCarte = conn.prepareStatement(sqlUpdateCarte);
            stmtUpdateCarte.setInt(1, carteId);
            stmtUpdateCarte.executeUpdate();

            System.out.println("Comanda #" + comandaId + " a fost creată cu succes!");
            AuditService.logAction("UTILIZATOR " + this.utilizator.getId() + " - a creat comanda #" + comandaId);

        } catch (SQLException e) {
            System.out.println("Eroare la creare comandă: " + e.getMessage());
        }
    }

    private void anuleazaComanda() {
        afiseazaComenziActive();
        System.out.print("\nIntrodu ID-ul comenzii de anulat: ");
        int comandaId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur că vrei să anulezi această comandă? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Anulare abandonată.");
            return;
        }

        try (Connection conn = this.dataService.getConnection()) {
            // Verificăm dacă comanda aparține utilizatorului curent
            String sqlVerifica = "SELECT 1 FROM COMANDA WHERE id = ? AND utilizator_id = ?";
            PreparedStatement stmtVerifica = conn.prepareStatement(sqlVerifica);
            stmtVerifica.setInt(1, comandaId);
            stmtVerifica.setInt(2, this.utilizator.getId());
            ResultSet rsVerifica = stmtVerifica.executeQuery();

            if (!rsVerifica.next()) {
                System.out.println("Comanda nu există sau nu ți-e atribuită!");
                return;
            }

            // Obținem cărțile din comandă pentru a le marca ca disponibile
            String sqlCarti = "SELECT carte_id FROM COMANDA_CARTE WHERE comanda_id = ?";
            PreparedStatement stmtCarti = conn.prepareStatement(sqlCarti);
            stmtCarti.setInt(1, comandaId);
            ResultSet rsCarti = stmtCarti.executeQuery();

            while (rsCarti.next()) {
                String sqlUpdateCarte = "UPDATE CARTE SET disponibil = TRUE WHERE id = ?";
                PreparedStatement stmtUpdateCarte = conn.prepareStatement(sqlUpdateCarte);
                stmtUpdateCarte.setInt(1, rsCarti.getInt("carte_id"));
                stmtUpdateCarte.executeUpdate();
            }

            // Actualizăm statusul comenzii
            String sqlUpdate = "UPDATE COMANDA SET status = 'ANULATA' WHERE id = ?";
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setInt(1, comandaId);
            stmtUpdate.executeUpdate();

            System.out.println("Comanda #" + comandaId + " a fost anulată cu succes!");
            AuditService.logAction("UTILIZATOR " + this.utilizator.getId() + " - a anulat comanda #" + comandaId);

        } catch (SQLException e) {
            System.out.println("Eroare la anulare comandă: " + e.getMessage());
        }
    }

    private void scrieRecenzie() {
        try (Connection conn = this.dataService.getConnection()) {
            // Afișăm cărțile pe care le-a avut în comandă utilizatorul
            String sqlCarti = """
                SELECT DISTINCT c.id, c.titlu
                FROM CARTE c
                JOIN COMANDA_CARTE cc ON c.id = cc.carte_id
                JOIN COMANDA cmd ON cc.comanda_id = cmd.id
                WHERE cmd.utilizator_id = ? AND cmd.status = 'FINALIZATA'
                """;

            PreparedStatement stmtCarti = conn.prepareStatement(sqlCarti);
            stmtCarti.setInt(1, this.utilizator.getId());
            ResultSet rsCarti = stmtCarti.executeQuery();

            System.out.println("\n--- Cărți pentru care poți scrie recenzie ---");
            boolean existaCarti = false;

            while (rsCarti.next()) {
                existaCarti = true;
                System.out.printf("ID: %d | %s%n", rsCarti.getInt("id"), rsCarti.getString("titlu"));
            }

            if (!existaCarti) {
                System.out.println("Nu ai cărți finalizate pentru care să poți scrie recenzie.");
                return;
            }

            System.out.print("\nIntrodu ID-ul cărții pentru recenzie: ");
            int carteId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Scor (1-5 stele): ");
            int scor = scanner.nextInt();
            scanner.nextLine();

            if (scor < 1 || scor > 5) {
                System.out.println("Scor invalid! Trebuie să fie între 1 și 5.");
                return;
            }

            System.out.print("Recenzie (max 500 caractere): ");
            String textRecenzie = scanner.nextLine();

            if (textRecenzie.length() > 500) {
                textRecenzie = textRecenzie.substring(0, 500);
                System.out.println("Recenzie a fost trunchiată la 500 caractere.");
            }

            String sql = "INSERT INTO RECENZIE (carte_id, utilizator_id, scor, text, data_recenzie) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, carteId);
            stmt.setInt(2, this.utilizator.getId());
            stmt.setInt(3, scor);
            stmt.setString(4, textRecenzie);
            stmt.setString(5, LocalDate.now().toString());
            stmt.executeUpdate();

            System.out.println("Recenzie adăugată cu succes!");
            AuditService.logAction("UTILIZATOR " + this.utilizator.getId() + " - a adăugat recenzie pentru carte ID: " + carteId);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare recenzie: " + e.getMessage());
        }
    }

    private void vizualizeazaColegi() {
        try (Connection conn = this.dataService.getConnection()) {
            // Obținem compania utilizatorului curent
            String sqlCompanie = "SELECT companie_id FROM UTILIZATOR WHERE persoana_id = ?";
            PreparedStatement stmtCompanie = conn.prepareStatement(sqlCompanie);
            stmtCompanie.setInt(1, this.utilizator.getId());
            ResultSet rsCompanie = stmtCompanie.executeQuery();

            if (!rsCompanie.next() || rsCompanie.getInt("companie_id") == 0) {
                System.out.println("Nu faci parte din nicio companie sau compania nu este setată.");
                return;
            }

            int companieId = rsCompanie.getInt("companie_id");

            // Obținem colegii
            String sqlColegi = """
                SELECT p.id, p.nume, p.prenume, p.email
                FROM PERSOANA p
                JOIN UTILIZATOR u ON p.id = u.persoana_id
                WHERE u.companie_id = ? AND p.id != ?
                """;

            PreparedStatement stmtColegi = conn.prepareStatement(sqlColegi);
            stmtColegi.setInt(1, companieId);
            stmtColegi.setInt(2, this.utilizator.getId());
            ResultSet rsColegi = stmtColegi.executeQuery();

            System.out.println("\n--- Colegii tăi din companie ---");
            boolean existaColegi = false;

            while (rsColegi.next()) {
                existaColegi = true;
                System.out.printf("%s %s | Email: %s%n",
                        rsColegi.getString("nume"), rsColegi.getString("prenume"), rsColegi.getString("email"));
                System.out.println("------------------------");
            }

            if (!existaColegi) {
                System.out.println("Nu ai colegi în companie sau informațiile sunt incomplete.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare colegi: " + e.getMessage());
        }
    }

}