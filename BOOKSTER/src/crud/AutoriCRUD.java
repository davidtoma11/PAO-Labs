package crud;

import service.*;
import java.sql.*;
import java.util.Scanner;

public class AutoriCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuAutor() {
        while (true) {
            System.out.println("\n--- Meniu Autori ---");
            System.out.println("1. Adaugă autor");
            System.out.println("2. Afișează toți autorii");
            System.out.println("3. Actualizează autor");
            System.out.println("4. Șterge autor");
            System.out.println("5. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaAutor();
                case 2 -> afiseazaAutori();
                case 3 -> actualizeazaAutor();
                case 4 -> stergeAutor();
                case 5 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static void adaugaAutor() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Autor ---");

            System.out.print("Nume: ");
            String nume = scanner.nextLine();

            System.out.print("Prenume: ");
            String prenume = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("An naștere: ");
            int anNastere = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Naționalitate: ");
            String nationalitate = scanner.nextLine();

            System.out.print("Biografie: ");
            String biografie = scanner.nextLine();

            String sqlPersoana = "INSERT INTO PERSOANA (nume, prenume, email, an_nastere, tip) VALUES (?, ?, ?, ?, 'AUTOR')";
            PreparedStatement stmtPersoana = conn.prepareStatement(sqlPersoana, Statement.RETURN_GENERATED_KEYS);

            stmtPersoana.setString(1, nume);
            stmtPersoana.setString(2, prenume);
            stmtPersoana.setString(3, email);
            stmtPersoana.setInt(4, anNastere);
            stmtPersoana.executeUpdate();

            int idPersoana;
            try (ResultSet rs = stmtPersoana.getGeneratedKeys()) {
                rs.next();
                idPersoana = rs.getInt(1);
            }

            String sqlAutor = "INSERT INTO AUTOR (persoana_id, nationalitate, biografie) VALUES (?, ?, ?)";
            PreparedStatement stmtAutor = conn.prepareStatement(sqlAutor);
            stmtAutor.setInt(1, idPersoana);
            stmtAutor.setString(2, nationalitate);
            stmtAutor.setString(3, biografie);
            stmtAutor.executeUpdate();

            System.out.println("Autor adăugat cu succes!");
            AuditService.logAction("ADMIN - adaugaAutor cu ID: " + idPersoana);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare autor: " + e.getMessage());
        }
    }

    private static void afiseazaAutori() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT p.id, p.nume, p.prenume, p.email, p.an_nastere,
                       a.nationalitate, a.biografie
                FROM PERSOANA p
                JOIN AUTOR a ON p.id = a.persoana_id
                """)) {

            System.out.println("\n--- Lista Autori ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Nume: %s %s | Email: %s | Naștere: %d | Naționalitate: %s%n",
                        rs.getInt("id"), rs.getString("nume"), rs.getString("prenume"),
                        rs.getString("email"), rs.getInt("an_nastere"), rs.getString("nationalitate"));
                System.out.println("Biografie: " + rs.getString("biografie"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare autori: " + e.getMessage());
        }
    }

    private static void actualizeazaAutor() {
        afiseazaAutori();
        System.out.print("\nIntrodu ID-ul autorului de actualizat: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\nIntrodu noile date:");
            System.out.print("Nume: ");
            String nume = scanner.nextLine();

            System.out.print("Prenume: ");
            String prenume = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("An naștere: ");
            int anNastere = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Naționalitate: ");
            String nationalitate = scanner.nextLine();

            System.out.print("Biografie: ");
            String biografie = scanner.nextLine();

            String sqlPersoana = "UPDATE PERSOANA SET nume=?, prenume=?, email=?, an_nastere=? WHERE id=?";
            PreparedStatement stmtPersoana = conn.prepareStatement(sqlPersoana);
            stmtPersoana.setString(1, nume);
            stmtPersoana.setString(2, prenume);
            stmtPersoana.setString(3, email);
            stmtPersoana.setInt(4, anNastere);
            stmtPersoana.setInt(5, id);
            stmtPersoana.executeUpdate();

            String sqlAutor = "UPDATE AUTOR SET nationalitate=?, biografie=? WHERE persoana_id=?";
            PreparedStatement stmtAutor = conn.prepareStatement(sqlAutor);
            stmtAutor.setString(1, nationalitate);
            stmtAutor.setString(2, biografie);
            stmtAutor.setInt(3, id);
            stmtAutor.executeUpdate();

            System.out.println("Autor actualizat cu succes!");
            AuditService.logAction("ADMIN - actualizeazaAutor cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare autor: " + e.getMessage());
        }
    }

    private static void stergeAutor() {
        afiseazaAutori();
        System.out.print("\nIntrodu ID-ul autorului de șters: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Ștergere anulată.");
            return;
        }

        try (Connection conn = DataInitialization.getConnection()) {
            String sql = "DELETE FROM PERSOANA WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Autor șters cu succes!");
                AuditService.logAction("ADMIN - stergeAutor cu ID: " + id);
            } else {
                System.out.println("Autorul nu a fost găsit!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere autor: " + e.getMessage());
        }
    }
}