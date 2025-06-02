package service;

import model.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.io.*;

public class DataInitialization {
    private static DataInitialization instance;
    private static final String PROPERTIES_FILE = "db.properties";
    private static Properties properties;

    // Listele de date
    private List<Utilizator> utilizatori = new ArrayList<>();
    private List<Administrator> administratori = new ArrayList<>();
    private List<Autor> autori = new ArrayList<>();
    private List<Companie> companii = new ArrayList<>();
    private List<Carte> carti = new ArrayList<>();
    private List<Comanda> comenzi = new ArrayList<>();
    private List<RecenzieCarte> recenzii = new ArrayList<>();
    private List<CategorieCarte> categorii = new ArrayList<>();

    // Constructor privat
    private DataInitialization() {}

    // Metodă Singleton
    public static synchronized DataInitialization getInstance() {
        if (instance == null) {
            instance = new DataInitialization();
        }
        return instance;
    }

    static {
        try (InputStream input = DataInitialization.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            properties = new Properties();
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Eroare la încărcarea configurației DB", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    public void loadAllData() throws SQLException {
        loadCategorii();
        loadCompanii();
        loadAutori();
        loadUtilizatori();
        loadAdministratori();
        loadCarti();
        loadComenzi();
        loadRecenzii();
    }

    public void loadCategorii() throws SQLException {
        String query = "SELECT * FROM CATEGORIE_CARTE";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                CategorieCarte categorie = new CategorieCarte();
                categorie.setId(rs.getInt("id"));
                categorie.setNume(rs.getString("nume"));
                categorii.add(categorie);
            }
            System.out.println("Încărcat " + categorii.size() + " categorii");
        }
    }

    public void loadCompanii() throws SQLException {
        String query = "SELECT * FROM COMPANIE";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Companie companie = new Companie();
                companie.setId(rs.getInt("id"));
                companie.setNume(rs.getString("nume"));
                companie.setUtilizatori(new ArrayList<>());
                companii.add(companie);
            }
            System.out.println("Încărcat " + companii.size() + " companii");
        }
    }

    public void loadAutori() throws SQLException {
        String query = """
            SELECT p.id, p.nume, p.prenume, p.email, p.an_nastere,
                   a.nationalitate, a.biografie
            FROM PERSOANA p
            JOIN AUTOR a ON p.id = a.persoana_id
            """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Autor autor = new Autor();
                autor.setId(rs.getInt("id"));
                autor.setNume(rs.getString("nume"));
                autor.setPrenume(rs.getString("prenume"));
                autor.setEmail(rs.getString("email"));
                autor.setAnNastere(rs.getInt("an_nastere"));
                autor.setNationalitate(rs.getString("nationalitate"));
                autor.setBiografie(rs.getString("biografie"));
                autori.add(autor);
            }
            System.out.println("Încărcat " + autori.size() + " autori");
        }
    }

    public void loadUtilizatori() throws SQLException {
        String query = """
            SELECT p.id, p.nume, p.prenume, p.email, p.an_nastere,
                   u.parola, u.data_inregistrare, u.companie_id
            FROM PERSOANA p
            JOIN UTILIZATOR u ON p.id = u.persoana_id
            """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Utilizator utilizator = new Utilizator();
                utilizator.setId(rs.getInt("id"));
                utilizator.setNume(rs.getString("nume"));
                utilizator.setPrenume(rs.getString("prenume"));
                utilizator.setEmail(rs.getString("email"));
                utilizator.setAnNastere(rs.getInt("an_nastere"));
                utilizator.setParola(rs.getString("parola"));
                utilizator.setDataInregistrare(rs.getDate("data_inregistrare").toLocalDate());

                // Setează compania afiliată
                int companieId = rs.getInt("companie_id");
                if (!rs.wasNull()) {
                    companii.stream()
                            .filter(c -> c.getId() == companieId)
                            .findFirst()
                            .ifPresent(companie -> {
                                utilizator.setCompanieAfiliata(companie);
                                companie.getUtilizatori().add(utilizator);
                            });
                }

                utilizatori.add(utilizator);
            }
            System.out.println("Încărcat " + utilizatori.size() + " utilizatori");
        }
    }

    public void loadAdministratori() throws SQLException {
        String query = """
        SELECT p.id, p.nume, p.prenume, p.email, p.an_nastere,
               a.parola
        FROM PERSOANA p
        JOIN ADMINISTRATOR a ON p.id = a.persoana_id
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Administrator admin = new Administrator();
                // Setează atributele moștenite din Persoana
                admin.setId(rs.getInt("id"));
                admin.setNume(rs.getString("nume"));
                admin.setPrenume(rs.getString("prenume"));
                admin.setEmail(rs.getString("email"));
                admin.setAnNastere(rs.getInt("an_nastere"));
                // Setează atributele specifice Administratorului
                admin.setParola(rs.getString("parola"));

                administratori.add(admin);
            }
            System.out.println("Încărcat " + administratori.size() + " administratori");
        }
    }

    public void loadCarti() throws SQLException {
        String query = """
            SELECT c.id, c.titlu, c.autor_id, c.categorie_id, 
                   c.an_publicatie, c.disponibil
            FROM CARTE c
            """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Carte carte = new Carte();
                carte.setId(rs.getInt("id"));
                carte.setTitlu(rs.getString("titlu"));
                carte.setAnPublicare(rs.getInt("an_publicatie"));
                carte.setDisponibilitate(rs.getBoolean("disponibil"));

                // Setează autorul
                int autorId = rs.getInt("autor_id");
                autori.stream()
                        .filter(a -> a.getId() == autorId)
                        .findFirst()
                        .ifPresent(carte::setAutor);

                // Setează categoria
                int categorieId = rs.getInt("categorie_id");
                categorii.stream()
                        .filter(cat -> cat.getId() == categorieId)
                        .findFirst()
                        .ifPresent(carte::setCategorie);

                carte.setRecenzii(new ArrayList<>());
                carti.add(carte);
            }
            System.out.println("Încărcat " + carti.size() + " cărți");
        }
    }

    public void loadComenzi() throws SQLException {
        // Încărcare comenzi de bază
        String queryComanda = "SELECT * FROM COMANDA";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryComanda)) {
            while (rs.next()) {
                Comanda comanda = new Comanda();
                comanda.setId(rs.getInt("id"));
                comanda.setDataComenzii(rs.getDate("data_comenzii").toLocalDate());
                comanda.setDataReturnarii(rs.getDate("data_returnarii") != null ?
                        rs.getDate("data_returnarii").toLocalDate() : null);
                comanda.setStatus(rs.getString("status"));

                // Setează utilizatorul
                int utilizatorId = rs.getInt("utilizator_id");
                utilizatori.stream()
                        .filter(u -> u.getId() == utilizatorId)
                        .findFirst()
                        .ifPresent(comanda::setUtilizator);

                comanda.setCarti(new ArrayList<>());
                comenzi.add(comanda);
            }
            System.out.println("Încărcat " + comenzi.size() + " comenzi");
        }

        // Încărcare relații COMANDA_CARTE
        String queryComandaCarte = "SELECT * FROM COMANDA_CARTE";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryComandaCarte)) {
            while (rs.next()) {
                int comandaId = rs.getInt("comanda_id");
                int carteId = rs.getInt("carte_id");

                comenzi.stream()
                        .filter(c -> c.getId() == comandaId)
                        .findFirst()
                        .ifPresent(comanda -> {
                            carti.stream()
                                    .filter(c -> c.getId() == carteId)
                                    .findFirst()
                                    .ifPresent(carte -> comanda.getCarti().add(carte));
                        });
            }
            System.out.println("Încărcat " + comenzi.stream().mapToInt(c -> c.getCarti().size()).sum() + " relații comandă-carte");
        }
    }

    public void loadRecenzii() throws SQLException {
        String query = """
            SELECT r.id, r.carte_id, r.utilizator_id, 
                   r.scor, r.text, r.data_recenzie
            FROM RECENZIE r
            """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                RecenzieCarte recenzie = new RecenzieCarte();
                recenzie.setId(rs.getInt("id"));
                recenzie.setScor(rs.getInt("scor"));
                recenzie.setRecenzie(rs.getString("text"));
                recenzie.setDataRecenziei(rs.getDate("data_recenzie").toLocalDate());

                // Setează cartea
                int carteId = rs.getInt("carte_id");
                carti.stream()
                        .filter(c -> c.getId() == carteId)
                        .findFirst()
                        .ifPresent(carte -> {
                            recenzie.setCarte(carte);
                            carte.getRecenzii().add(recenzie);
                        });

                // Setează utilizatorul
                int utilizatorId = rs.getInt("utilizator_id");
                utilizatori.stream()
                        .filter(u -> u.getId() == utilizatorId)
                        .findFirst()
                        .ifPresent(recenzie::setUtilizator);

                recenzii.add(recenzie);
            }
            System.out.println("Încărcat " + recenzii.size() + " recenzii");
        }
    }



    // Getters pentru toate listele
    public List<Utilizator> getUtilizatori() { return utilizatori; }
    public List<Administrator> getAdministratori() { return administratori; }
    public List<Autor> getAutori() { return autori; }
    public List<Companie> getCompanii() { return companii; }
    public List<Carte> getCarti() { return carti; }
    public List<Comanda> getComenzi() { return comenzi; }
    public List<RecenzieCarte> getRecenzii() { return recenzii; }
    public List<CategorieCarte> getCategorii() { return categorii; }
}