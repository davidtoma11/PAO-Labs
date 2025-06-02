package crud;

import service.*;
import java.sql.*;
import java.util.Scanner;

public class RecenziiCartiCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuRecenzie() {
        while (true) {
            System.out.println("\n--- Meniu Recenzii ---");
            System.out.println("1. Adaugă recenzie");
            System.out.println("2. Afișează toate recenziile");
            System.out.println("3. Actualizează recenzie");
            System.out.println("4. Șterge recenzie");
            System.out.println("5. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaRecenzie();
                case 2 -> afiseazaRecenzii();
                case 3 -> actualizeazaRecenzie();
                case 4 -> stergeRecenzie();
                case 5 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static void adaugaRecenzie() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Recenzie ---");

            System.out.print("ID Carte: ");
            int carteId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("ID Utilizator: ");
            int utilizatorId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Scor (1-5): ");
            int scor = scanner.nextInt();
            scanner.nextLine();

            if (scor < 1 || scor > 5) {
                System.out.println("Scor invalid! Trebuie să fie între 1 și 5.");
                return;
            }

            System.out.print("Text recenzie: ");
            String text = scanner.nextLine();

            String dataRecenzie = java.time.LocalDate.now().toString();

            String sql = "INSERT INTO RECENZIE (carte_id, utilizator_id, scor, text, data_recenzie) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, carteId);
            stmt.setInt(2, utilizatorId);
            stmt.setInt(3, scor);
            stmt.setString(4, text);
            stmt.setString(5, dataRecenzie);
            stmt.executeUpdate();

            int id;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }

            System.out.println("Recenzie adăugată cu succes!");
            AuditService.logAction("ADMIN - adaugaRecenzie cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare recenzie: " + e.getMessage());
        }
    }

    private static void afiseazaRecenzii() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT r.id, c.titlu AS carte, CONCAT(p.nume, ' ', p.prenume) AS utilizator,
                       r.scor, r.text, r.data_recenzie
                FROM RECENZIE r
                JOIN CARTE c ON r.carte_id = c.id
                JOIN UTILIZATOR u ON r.utilizator_id = u.persoana_id
                JOIN PERSOANA p ON u.persoana_id = p.id
                """)) {

            System.out.println("\n--- Lista Recenzii ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Carte: %s | Utilizator: %s%n",
                        rs.getInt("id"), rs.getString("carte"), rs.getString("utilizator"));
                System.out.printf("Scor: %d/5 | Data: %s%n",
                        rs.getInt("scor"), rs.getString("data_recenzie"));
                System.out.println("Recenzie: " + rs.getString("text"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare recenzii: " + e.getMessage());
        }
    }

    private static void actualizeazaRecenzie() {
        afiseazaRecenzii();
        System.out.print("\nIntrodu ID-ul recenziei de actualizat: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\nIntrodu noile date:");
            System.out.print("Scor (1-5): ");
            int scor = scanner.nextInt();
            scanner.nextLine();

            if (scor < 1 || scor > 5) {
                System.out.println("Scor invalid! Trebuie să fie între 1 și 5.");
                return;
            }

            System.out.print("Text recenzie: ");
            String text = scanner.nextLine();

            String sql = "UPDATE RECENZIE SET scor=?, text=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, scor);
            stmt.setString(2, text);
            stmt.setInt(3, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Recenzie actualizată cu succes!");
                AuditService.logAction("ADMIN - actualizeazaRecenzie cu ID: " + id);
            } else {
                System.out.println("Recenzia nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare recenzie: " + e.getMessage());
        }
    }

    private static void stergeRecenzie() {
        afiseazaRecenzii();
        System.out.print("\nIntrodu ID-ul recenziei de șters: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Ștergere anulată.");
            return;
        }

        try (Connection conn = DataInitialization.getConnection()) {
            String sql = "DELETE FROM RECENZIE WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Recenzie ștearsă cu succes!");
                AuditService.logAction("ADMIN - stergeRecenzie cu ID: " + id);
            } else {
                System.out.println("Recenzia nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere recenzie: " + e.getMessage());
        }
    }
}
