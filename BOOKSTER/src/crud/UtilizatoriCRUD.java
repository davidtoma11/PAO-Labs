package crud;


import service.*;
import java.sql.*;
import java.util.Scanner;

public class UtilizatoriCRUD {
    private static final Scanner scanner = new Scanner(System.in);

    public static void meniuUtilizator() {
        while (true) {
            System.out.println("\n--- Meniu Utilizator ---");
            System.out.println("1. Adaugă utilizator");
            System.out.println("2. Afișează toți utilizatorii");
            System.out.println("3. Actualizează utilizator");
            System.out.println("4. Șterge utilizator");
            System.out.println("5. Înapoi la meniul principal");
            System.out.print("Alege opțiunea: ");

            int optiune = scanner.nextInt();
            scanner.nextLine();

            switch (optiune) {
                case 1 -> adaugaUtilizator();
                case 2 -> afiseazaUtilizatori();
                case 3 -> actualizeazaUtilizator();
                case 4 -> stergeUtilizator();
                case 5 -> {
                    System.out.println("Revenire la meniul principal...");
                    return;
                }
                default -> System.out.println("Opțiune invalidă. Încearcă din nou.");
            }
        }
    }

    private static boolean existaUtilizator(Connection conn, int id) throws SQLException {
        String sql = "SELECT 1 FROM PERSOANA WHERE id=? AND tip='UTILIZATOR'";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        boolean exists = rs.next();
        rs.close();
        stmt.close();
        return exists;
    }

    private static void adaugaUtilizator() {
        try (Connection conn = DataInitialization.getConnection()) {
            System.out.println("\n--- Adăugare Utilizator ---");

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
                if (parola.length() >= 6) {
                    break;
                }
                System.out.println("Parola trebuie să aibă minim 6 caractere!");
            }

            System.out.print("ID Companie (0 dacă nu există): ");
            int companieId = scanner.nextInt();
            scanner.nextLine();

            String dataInregistrare = java.time.LocalDate.now().toString();


            // Adaugă în PERSOANA
            String sqlPersoana = "INSERT INTO PERSOANA (nume, prenume, email, an_nastere, tip) VALUES (?, ?, ?, ?, 'UTILIZATOR')";
            PreparedStatement stmtPersoana = conn.prepareStatement(sqlPersoana, Statement.RETURN_GENERATED_KEYS);

            stmtPersoana.setString(1, nume);
            stmtPersoana.setString(2, prenume);
            stmtPersoana.setString(3, email);
            stmtPersoana.setInt(4, anNastere);
            stmtPersoana.executeUpdate();

            // Obține ID-ul generat
            int idPersoana;
            try (ResultSet rs = stmtPersoana.getGeneratedKeys()) {
                rs.next();
                idPersoana = rs.getInt(1);
            }

            // Adaugă în UTILIZATOR
            String sqlUtilizator = "INSERT INTO UTILIZATOR (persoana_id, parola, companie_id, data_inregistrare) VALUES (?, ?, ?, ?)";
            PreparedStatement stmtUtilizator = conn.prepareStatement(sqlUtilizator);

            stmtUtilizator.setInt(1, idPersoana);
            stmtUtilizator.setString(2, parola);
            stmtUtilizator.setInt(3, companieId == 0 ? null : companieId);
            stmtUtilizator.setString(4, dataInregistrare);
            stmtUtilizator.executeUpdate();

            System.out.println("Utilizator adăugat cu succes!");
            AuditService.logAction("ADMIN - adaugaUtilizator cu ID: " + idPersoana);

        } catch (SQLException e) {
            System.out.println("Eroare la adăugare utilizator: " + e.getMessage());
        }
    }

    private static void afiseazaUtilizatori() {
        try (Connection conn = DataInitialization.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("""
                SELECT p.id, p.nume, p.prenume, p.email, p.an_nastere,
                       u.parola, c.nume AS companie, u.data_inregistrare
                FROM PERSOANA p
                JOIN UTILIZATOR u ON p.id = u.persoana_id
                LEFT JOIN COMPANIE c ON u.companie_id = c.id
                """)) {

            System.out.println("\n--- Lista Utilizatori ---");
            boolean existaUtilizatori = false;

            while (rs.next()) {
                existaUtilizatori = true;
                System.out.printf("ID: %d | Nume: %s %s | Email: %s | Naștere: %d%n",
                        rs.getInt("id"),
                        rs.getString("nume"),
                        rs.getString("prenume"),
                        rs.getString("email"),
                        rs.getInt("an_nastere"));
                System.out.printf("Companie: %s | Data înregistrare: %s%n",
                        rs.getString("companie") != null ? rs.getString("companie") : "N/A",
                        rs.getString("data_inregistrare"));
                System.out.println("------------------------");
            }

            if (!existaUtilizatori) {
                System.out.println("Nu există utilizatori în baza de date.");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la afișare utilizatori: " + e.getMessage());
        }
    }

    private static void actualizeazaUtilizator() {
        afiseazaUtilizatori();
        System.out.print("\nIntrodu ID-ul utilizatorului de actualizat: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = DataInitialization.getConnection()) {
            if (!existaUtilizator(conn, id)) {
                System.out.println("Utilizatorul cu ID-ul " + id + " nu există!");
                return;
            }


            String dataInregistrare = null;
            String sqlSelectData = "SELECT data_inregistrare FROM UTILIZATOR WHERE persoana_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlSelectData)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dataInregistrare = rs.getString("data_inregistrare");
                } else {
                    System.out.println("Nu s-a găsit utilizatorul în tabela UTILIZATOR.");
                    return;
                }
            }


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
                if (parola.length() >= 6) {
                    break;
                }
                System.out.println("Parola trebuie să aibă minim 6 caractere!");
            }

            System.out.print("ID Companie (0 dacă nu există): ");
            int companieId = scanner.nextInt();
            scanner.nextLine();


            String sqlPersoana = "UPDATE PERSOANA SET nume=?, prenume=?, email=?, an_nastere=? WHERE id=?";
            PreparedStatement stmtPersoana = conn.prepareStatement(sqlPersoana);
            stmtPersoana.setString(1, nume);
            stmtPersoana.setString(2, prenume);
            stmtPersoana.setString(3, email);
            stmtPersoana.setInt(4, anNastere);
            stmtPersoana.setInt(5, id);
            stmtPersoana.executeUpdate();


            String sqlUtilizator = "UPDATE UTILIZATOR SET parola=?, companie_id=?, data_inregistrare=? WHERE persoana_id=?";
            PreparedStatement stmtUtilizator = conn.prepareStatement(sqlUtilizator);
            stmtUtilizator.setString(1, parola);
            if (companieId == 0) {
                stmtUtilizator.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmtUtilizator.setInt(2, companieId);
            }
            stmtUtilizator.setString(3, dataInregistrare);
            stmtUtilizator.setInt(4, id);
            stmtUtilizator.executeUpdate();

            System.out.println("Utilizator actualizat cu succes!");
            AuditService.logAction("ADMIN - actualizareUtilizator cu ID: " + id);

        } catch (SQLException e) {
            System.out.println("Eroare la actualizare utilizator: " + e.getMessage());
        }
    }


    private static void stergeUtilizator() {
        afiseazaUtilizatori();
        System.out.print("\nIntrodu ID-ul utilizatorului de șters: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Ești sigur? (da/nu): ");
        if (!scanner.nextLine().equalsIgnoreCase("da")) {
            System.out.println("Ștergere anulată.");
            return;
        }

        try (Connection conn = DataInitialization.getConnection()) {
            // Șterge din PERSOANA (se șterge automat din UTILIZATOR datorită CASCADE)
            String sql = "DELETE FROM PERSOANA WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            if (stmt.executeUpdate() > 0) {
                System.out.println("Utilizator șters cu succes!");
                AuditService.logAction("ADMIN - stergeUtilizator cu ID: " + id);
            } else {
                System.out.println("Utilizatorul nu a fost găsit!");
            }

        } catch (SQLException e) {
            System.out.println("Eroare la ștergere utilizator: " + e.getMessage());
        }
    }
}