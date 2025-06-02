package model;

public class Persoana {
    private int id;
    private String nume;
    private String prenume;
    private String email;
    private int anNastere;

    public Persoana(int id, String nume, String prenume, String email, int anNastere) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
        this.anNastere = anNastere;
    }

    public Persoana() {}

    public int getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getEmail() {
        return email;
    }

    public int getAnNastere() {
        return anNastere;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAnNastere(int anNastere) {
        this.anNastere = anNastere;
    }
}
