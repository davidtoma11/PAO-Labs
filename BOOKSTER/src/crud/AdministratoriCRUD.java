package crud;

import service.*;
import java.sql.*;
import java.util.Scanner;

public class AdministratoriCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuAdministrator() {
        while (true) {
            System.out.println("\n--- Meniu Administrator ---");
            System.out.println("1. Adaugă administrator");
            System.out.println("2. Afișează toți administratorii");
            System.out.println("3. Actualizează administrator");
            System.out.println("4. Șterge administrator");
            System.out.println("5. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaAdministrator();
                case 2 -> afiseazaAdministratori();
                case 3 -> actualizeazaAdministrator();
                case 4 -> stergeAdministrator();
                case 5 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static void adaugaAdministrator() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Administrator ---");

            System.out.print("Nume: ");
            String nume = scanner.nextLine();

            System.out.print("Prenume: ");
            String prenume = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("An naștere: ");
            int anNastere = scanner.nextInt();
            scanner.nextLine();

            String parola;
            while (true) {
                System.out.print("Parolă (minim 6 caractere): ");
                parola = scanner.nextLine();
                if (parola.length() >= 6) break;
                System.out.println("Parola trebuie să aibă minim 6 caractere!");
            }

            String sqlPersoana = "INSERT INTO PERSOANA (nume, prenume, email, an_nastere, tip) VALUES (?, ?, ?, ?, 'ADMINISTRATOR')";
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

            String sqlAdministrator = "INSERT INTO ADMINISTRATOR (persoana_id, parola) VALUES (?, ?)";
            PreparedStatement stmtAdministrator = conn.prepareStatement(sqlAdministrator);
            stmtAdministrator.setInt(1, idPersoana);
            stmtAdministrator.setString(2, parola);
            stmtAdministrator.executeUpdate();

            System.out.println("Administrator adăugat cu succes!");
            AuditService.logAction("ADMIN - adaugaAdministrator cu ID: " + idPersoana);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare administrator: " + e.getMessage());
        }
    }

    private static void afiseazaAdministratori() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT p.id, p.nume, p.prenume, p.email, p.an_nastere
                FROM PERSOANA p
                JOIN ADMINISTRATOR a ON p.id = a.persoana_id
                """)) {

            System.out.println("\n--- Lista Administratori ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Nume: %s %s | Email: %s | Naștere: %d%n",
                        rs.getInt("id"), rs.getString("nume"), rs.getString("prenume"),
                        rs.getString("email"), rs.getInt("an_nastere"));
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare administratori: " + e.getMessage());
        }
    }

    private static void actualizeazaAdministrator() {
        afiseazaAdministratori();
        System.out.print("\nIntrodu ID-ul administratorului de actualizat: ");
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

            String parola;
            while (true) {
                System.out.print("Parolă (minim 6 caractere): ");
                parola = scanner.nextLine();
                if (parola.length() >= 6) break;
                System.out.println("Parola trebuie să aibă minim 6 caractere!");
            }

            String sqlPersoana = "UPDATE PERSOANA SET nume=?, prenume=?, email=?, an_nastere=? WHERE id=?";
            PreparedStatement stmtPersoana = conn.prepareStatement(sqlPersoana);
            stmtPersoana.setString(1, nume);
            stmtPersoana.setString(2, prenume);
            stmtPersoana.setString(3, email);
            stmtPersoana.setInt(4, anNastere);
            stmtPersoana.setInt(5, id);
            stmtPersoana.executeUpdate();

            String sqlAdministrator = "UPDATE ADMINISTRATOR SET parola=? WHERE persoana_id=?";
            PreparedStatement stmtAdministrator = conn.prepareStatement(sqlAdministrator);
            stmtAdministrator.setString(1, parola);
            stmtAdministrator.setInt(2, id);
            stmtAdministrator.executeUpdate();

            System.out.println("Administrator actualizat cu succes!");
            AuditService.logAction("ADMIN - actualizeazaAdministrator cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare administrator: " + e.getMessage());
        }
    }

    private static void stergeAdministrator() {
        afiseazaAdministratori();
        System.out.print("\nIntrodu ID-ul administratorului de șters: ");
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
                System.out.println("Administrator șters cu succes!");
                AuditService.logAction("ADMIN - stergeAdministrator cu ID: " + id);
            } else {
                System.out.println("Administratorul nu a fost găsit!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere administrator: " + e.getMessage());
        }
    }
}