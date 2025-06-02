package model;

public class Autor extends Persoana {
    private String nationalitate;
    private String biografie;

    public Autor(int id, String nume, String prenume, String email, int anNastere,
                 String nationalitate, String biografie) {
        super(id, nume, prenume, email, anNastere);
        this.nationalitate = nationalitate;
        this.biografie = biografie;
    }

    public Autor() {}

    public String getNationalitate() {
        return nationalitate;
    }

    public void setNationalitate(String nationalitate) {
        this.nationalitate = nationalitate;
    }

    public String getBiografie() {
        return biografie;
    }

    public void setBiografie(String biografie) {
        this.biografie = biografie;
    }
}
