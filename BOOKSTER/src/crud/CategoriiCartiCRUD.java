package crud;

import service.*;
import java.sql.*;
import java.util.Scanner;

public class CategoriiCartiCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuCategorie() {
        while (true) {
            System.out.println("\n--- Meniu Categorii Carte ---");
            System.out.println("1. Adaugă categorie");
            System.out.println("2. Afișează toate categoriile");
            System.out.println("3. Actualizează categorie");
            System.out.println("4. Șterge categorie");
            System.out.println("5. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaCategorie();
                case 2 -> afiseazaCategorii();
                case 3 -> actualizeazaCategorie();
                case 4 -> stergeCategorie();
                case 5 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static void adaugaCategorie() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Categorie ---");
            System.out.print("Nume categorie: ");
            String nume = scanner.nextLine();

            String sql = "INSERT INTO CATEGORIE_CARTE (nume) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, nume);
            stmt.executeUpdate();

            int id;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }

            System.out.println("Categorie adăugată cu succes!");
            AuditService.logAction("ADMIN - adaugaCategorie cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare categorie: " + e.getMessage());
        }
    }

    private static void afiseazaCategorii() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM CATEGORIE_CARTE")) {

            System.out.println("\n--- Lista Categorii ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Nume: %s%n",
                        rs.getInt("id"), rs.getString("nume"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare categorii: " + e.getMessage());
        }
    }

    private static void actualizeazaCategorie() {
        afiseazaCategorii();
        System.out.print("\nIntrodu ID-ul categoriei de actualizat: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\nIntrodu noile date:");
            System.out.print("Nume categorie: ");
            String nume = scanner.nextLine();

            String sql = "UPDATE CATEGORIE_CARTE SET nume=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nume);
            stmt.setInt(2, id);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Categorie actualizată cu succes!");
                AuditService.logAction("ADMIN - actualizeazaCategorie cu ID: " + id);
            } else {
                System.out.println("Categoria nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare categorie: " + e.getMessage());
        }
    }

    private static void stergeCategorie() {
        afiseazaCategorii();
        System.out.print("\nIntrodu ID-ul categoriei de șters: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Ștergere anulată.");
            return;
        }

        try (Connection conn = DataInitialization.getConnection()) {
            String sql = "DELETE FROM CATEGORIE_CARTE WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Categorie ștearsă cu succes!");
                AuditService.logAction("ADMIN - stergeCategorie cu ID: " + id);
            } else {
                System.out.println("Categoria nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere categorie: " + e.getMessage());
        }
    }
}