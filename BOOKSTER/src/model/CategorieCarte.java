package model;

public class CategorieCarte {
    private int id;
    private String nume;

    public CategorieCarte(int id, String nume) {
        this.id = id;
        this.nume = nume;
    }
    public CategorieCarte() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    @Override
    public String toString() {
        return "model.CategorieCarte{" +
                "id=" + id +
                ", nume='" + nume + '\'' +
                '}';
    }
}

