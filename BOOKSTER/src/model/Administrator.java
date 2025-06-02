package model;

public class Administrator extends Persoana {
    private String parola;

    public Administrator(int id, String nume, String prenume, String email, int anNastere, String parola) {
        super(id, nume, prenume, email, anNastere);
        this.parola = parola; // Fără verificarea parolei
    }

    public Administrator() {}

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola; // Fără verificarea parolei
    }
}

