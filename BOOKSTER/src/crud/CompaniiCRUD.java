package crud;

import service.*;
import java.sql.*;
import java.util.Scanner;

public class CompaniiCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuCompanie() {
        while (true) {
            System.out.println("\n--- Meniu Companii ---");
            System.out.println("1. Adaugă companie");
            System.out.println("2. Afișează toate companiile");
            System.out.println("3. Actualizează companie");
            System.out.println("4. Șterge companie");
            System.out.println("5. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaCompanie();
                case 2 -> afiseazaCompanii();
                case 3 -> actualizeazaCompanie();
                case 4 -> stergeCompanie();
                case 5 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static void adaugaCompanie() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Companie ---");
            System.out.print("Nume companie: ");
            String nume = scanner.nextLine();

            String sql = "INSERT INTO COMPANIE (nume) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nume);
            stmt.executeUpdate();

            int id;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }

            System.out.println("Companie adăugată cu succes!");
            AuditService.logAction("ADMIN - adaugaCompanie cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare companie: " + e.getMessage());
        }
    }

    private static void afiseazaCompanii() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM COMPANIE")) {

            System.out.println("\n--- Lista Companii ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Nume: %s%n",
                        rs.getInt("id"), rs.getString("nume"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare companii: " + e.getMessage());
        }
    }

    private static void actualizeazaCompanie() {
        afiseazaCompanii();
        System.out.print("\nIntrodu ID-ul companiei de actualizat: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\nIntrodu noile date:");
            System.out.print("Nume companie: ");
            String nume = scanner.nextLine();

            String sql = "UPDATE COMPANIE SET nume=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nume);
            stmt.setInt(2, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Companie actualizată cu succes!");
                AuditService.logAction("ADMIN - actualizeazaCompanie cu ID: " + id);
            } else {
                System.out.println("Compania nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare companie: " + e.getMessage());
        }
    }

    private static void stergeCompanie() {
        afiseazaCompanii();
        System.out.print("\nIntrodu ID-ul companiei de șters: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Ștergere anulată.");
            return;
        }

        try (Connection conn = DataInitialization.getConnection()) {
            String sql = "DELETE FROM COMPANIE WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Companie ștearsă cu succes!");
                AuditService.logAction("ADMIN - stergeCompanie cu ID: " + id);
            } else {
                System.out.println("Compania nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere companie: " + e.getMessage());
        }
    }
}
