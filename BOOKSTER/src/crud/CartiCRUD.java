package crud;

import service.*;
import java.sql.*;
import java.util.Scanner;

public class CartiCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuCarte() {
        while (true) {
            System.out.println("\n--- Meniu Carte ---");
            System.out.println("1. Adaugă carte");
            System.out.println("2. Afișează toate cărțile");
            System.out.println("3. Actualizează carte");
            System.out.println("4. Șterge carte");
            System.out.println("5. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaCarte();
                case 2 -> afiseazaCarti();
                case 3 -> actualizeazaCarte();
                case 4 -> stergeCarte();
                case 5 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static void adaugaCarte() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Carte ---");

            System.out.print("Titlu: ");
            String titlu = scanner.nextLine();

            System.out.print("ID Autor: ");
            int autorId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("ID Categorie: ");
            int categorieId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("An publicare: ");
            int anPublicare = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Disponibil (true/false): ");
            boolean disponibil = scanner.nextBoolean();
            scanner.nextLine();

            String sql = "INSERT INTO CARTE (titlu, autor_id, categorie_id, an_publicatie, disponibil) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, titlu);
            stmt.setInt(2, autorId);
            stmt.setInt(3, categorieId);
            stmt.setInt(4, anPublicare);
            stmt.setBoolean(5, disponibil);
            stmt.executeUpdate();

            int idCarte;
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                rs.next();
                idCarte = rs.getInt(1);
            }

            System.out.println("Carte adăugată cu succes!");
            AuditService.logAction("ADMIN - adaugaCarte cu ID: " + idCarte);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare carte: " + e.getMessage());
        }
    }

    private static void afiseazaCarti() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT c.id, c.titlu, CONCAT(p.nume, ' ', p.prenume) AS autor,
                       cat.nume AS categorie, c.an_publicatie, c.disponibil
                FROM CARTE c
                JOIN AUTOR a ON c.autor_id = a.persoana_id
                JOIN PERSOANA p ON a.persoana_id = p.id
                JOIN CATEGORIE_CARTE cat ON c.categorie_id = cat.id
                """)) {

            System.out.println("\n--- Lista Cărți ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Titlu: %s | Autor: %s | Categorie: %s%n",
                        rs.getInt("id"), rs.getString("titlu"), rs.getString("autor"), rs.getString("categorie"));
                System.out.printf("An publicare: %d | Disponibil: %s%n",
                        rs.getInt("an_publicatie"), rs.getBoolean("disponibil") ? "Da" : "Nu");
                System.out.println("------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare cărți: " + e.getMessage());
        }
    }

    private static void actualizeazaCarte() {
        afiseazaCarti();
        System.out.print("\nIntrodu ID-ul cărții de actualizat: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\nIntrodu noile date:");
            System.out.print("Titlu: ");
            String titlu = scanner.nextLine();

            System.out.print("ID Autor: ");
            int autorId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("ID Categorie: ");
            int categorieId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("An publicare: ");
            int anPublicare = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Disponibil (true/false): ");
            boolean disponibil = scanner.nextBoolean();
            scanner.nextLine();

            String sql = "UPDATE CARTE SET titlu=?, autor_id=?, categorie_id=?, an_publicatie=?, disponibil=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, titlu);
            stmt.setInt(2, autorId);
            stmt.setInt(3, categorieId);
            stmt.setInt(4, anPublicare);
            stmt.setBoolean(5, disponibil);
            stmt.setInt(6, id);
            stmt.executeUpdate();

            System.out.println("Carte actualizată cu succes!");
            AuditService.logAction("ADMIN - actualizeazaCarte cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare carte: " + e.getMessage());
        }
    }

    private static void stergeCarte() {
        afiseazaCarti();
        System.out.print("\nIntrodu ID-ul cărții de șters: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Ștergere anulată.");
            return;
        }

        try (Connection conn = DataInitialization.getConnection()) {
            String sql = "DELETE FROM CARTE WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Carte ștearsă cu succes!");
                AuditService.logAction("ADMIN - stergeCarte cu ID: " + id);
            } else {
                System.out.println("Cartea nu a fost găsită!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere carte: " + e.getMessage());
        }
    }
}