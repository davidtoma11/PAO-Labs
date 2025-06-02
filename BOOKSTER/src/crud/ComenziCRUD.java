package crud;

import service.*;
import java.sql.*;
import java.util.Scanner;

public class ComenziCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuComanda() {
        while (true) {
            System.out.println("\n--- Meniu Comandă ---");
            System.out.println("1. Adaugă comandă");
            System.out.println("2. Afișează toate comenzile");
            System.out.println("3. Actualizează comandă");
            System.out.println("4. Șterge comandă");
            System.out.println("5. Adaugă carte la comandă");
            System.out.println("6. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaComanda();
                case 2 -> afiseazaComenzi();
                case 3 -> actualizeazaComanda();
                case 4 -> stergeComanda();
                case 5 -> adaugaCarteLaComanda();
                case 6 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static void adaugaComanda() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Comandă ---");

            System.out.print("ID Utilizator: ");
            int utilizatorId = scanner.nextInt();
            scanner.nextLine();

            String dataComenzii = java.time.LocalDate.now().toString();

            System.out.print("Status (ACTIVA/FINALIZATA/ANULATA): ");
            String status = scanner.nextLine();

            String sql = "INSERT INTO COMANDA (utilizator_id, data_comenzii, status) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1, utilizatorId);
            stmt.setString(2, dataComenzii);
            stmt.setString(3, status);
            stmt.executeUpdate();

            int comandaId;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                comandaId = rs.getInt(1);
            }

            System.out.println("Comandă adăugată cu succes! ID: " + comandaId);
            AuditService.logAction("ADMIN - adaugaComanda cu ID: " + comandaId);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare comandă: " + e.getMessage());
        }
    }

    private static void afiseazaComenzi() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT c.id, CONCAT(p.nume, ' ', p.prenume) AS utilizator,
                       c.data_comenzii, c.data_returnarii, c.status
                FROM COMANDA c
                JOIN UTILIZATOR u ON c.utilizator_id = u.persoana_id
                JOIN PERSOANA p ON u.persoana_id = p.id
                """)) {

            System.out.println("\n--- Lista Comenzi ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Utilizator: %s | Data comandă: %s%n",
                        rs.getInt("id"), rs.getString("utilizator"), rs.getString("data_comenzii"));
                System.out.printf("Data returnare: %s | Status: %s%n",
                        rs.getString("data_returnarii") != null ? rs.getString("data_returnarii") : "N/A",
                        rs.getString("status"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare comenzi: " + e.getMessage());
        }
    }

    private static void actualizeazaComanda() {
        afiseazaComenzi();
        System.out.print("\nIntrodu ID-ul comenzii de actualizat: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\nIntrodu noile date:");
            System.out.print("Data returnării (YYYY-MM-DD, lasă gol dacă nu există): ");
            String dataReturnarii = scanner.nextLine();

            System.out.print("Status (ACTIVA/FINALIZATA/ANULATA): ");
            String status = scanner.nextLine();

            String sql = "UPDATE COMANDA SET data_returnarii=?, status=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, dataReturnarii.isEmpty() ? null : dataReturnarii);
            stmt.setString(2, status);
            stmt.setInt(3, id);
            stmt.executeUpdate();

            System.out.println("Comandă actualizată cu succes!");
            AuditService.logAction("ADMIN - actualizeazaComanda cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare comandă: " + e.getMessage());
        }
    }

    private static void stergeComanda() {
        afiseazaComenzi();
        System.out.print("\nIntrodu ID-ul comenzii de șters: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Ștergere anulată.");
            return;
        }

        try (Connection conn = DataInitialization.getConnection()) {
            String sqlDeleteCarti = "DELETE FROM COMANDA_CARTE WHERE comanda_id=?";
            PreparedStatement stmtDeleteCarti = conn.prepareStatement(sqlDeleteCarti);
            stmtDeleteCarti.setInt(1, id);
            stmtDeleteCarti.executeUpdate();

            String sql = "DELETE FROM COMANDA WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Comandă ștearsă cu succes!");
                AuditService.logAction("ADMIN - stergeComanda cu ID: " + id);
            } else {
                System.out.println("Comanda nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere comandă: " + e.getMessage());
        }
    }

    private static void adaugaCarteLaComanda() {
        afiseazaComenzi();
        System.out.print("\nIntrodu ID-ul comenzii: ");
        int comandaId = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Cărți disponibile ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, titlu FROM CARTE WHERE disponibil = TRUE")) {

                while (rs.next()) {
                    System.out.printf("ID: %d | Titlu: %s%n", rs.getInt("id"), rs.getString("titlu"));
                }
            }

            System.out.print("\nIntrodu ID-ul cărții de adăugat: ");
            int carteId = scanner.nextInt();
            scanner.nextLine();

            String sql = "INSERT INTO COMANDA_CARTE (comanda_id, carte_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, comandaId);
            stmt.setInt(2, carteId);
            stmt.executeUpdate();

            String sqlUpdate = "UPDATE CARTE SET disponibil=FALSE WHERE id=?";
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
            stmtUpdate.setInt(1, carteId);
            stmtUpdate.executeUpdate();

            System.out.println("Carte adăugată la comandă cu succes!");
            AuditService.logAction("ADMIN - adaugaCarteLaComanda, Comanda ID: " + comandaId + ", Carte ID: " + carteId);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare carte la comandă: " + e.getMessage());
        }
    }
}