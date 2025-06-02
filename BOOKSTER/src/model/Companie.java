package model;

import java.util.List;
import java.util.ArrayList;

public class Companie {
    private int id;
    private String nume;
    private List<Utilizator> utilizatori;

    public Companie(int id, String nume, List<Utilizator> utilizatori) {
        this.id = id;
        this.nume = nume;
        this.utilizatori = (utilizatori == null) ? new ArrayList<>() : utilizatori;  // Dacă utilizatori este null, instanțiem o listă goală
    }

    public Companie() {}

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

    public List<Utilizator> getUtilizatori() {
        return utilizatori;
    }

    public void setUtilizatori(List<Utilizator> utilizatori) {
        this.utilizatori = (utilizatori == null) ? new ArrayList<>() : utilizatori;
    }
}
