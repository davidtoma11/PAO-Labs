package model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Utilizator extends Persoana {
    private String parola;
    private Companie companieAfiliata;
    private LocalDate dataInregistrare;
    private List<Comanda> comenzi;

    public Utilizator(int id, String nume, String prenume, String email, int anNastere,
                      String parola, Companie companieAfiliata, LocalDate dataInregistrare, List<Comanda> comenzi) {
        super(id, nume, prenume, email, anNastere);
        this.parola = parola;
        this.companieAfiliata = companieAfiliata;
        this.dataInregistrare = dataInregistrare;
        this.comenzi = (comenzi == null) ? new ArrayList<>() : comenzi;
    }

    public Utilizator() {}

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }

    public Companie getCompanieAfiliata() {
        return companieAfiliata;
    }

    public void setCompanieAfiliata(Companie companieAfiliata) {
        this.companieAfiliata = companieAfiliata;
    }

    public LocalDate getDataInregistrare() {
        return dataInregistrare;
    }

    public void setDataInregistrare(LocalDate dataInregistrare) {
        this.dataInregistrare = dataInregistrare;
    }

    public List<Comanda> getComenzi() {
        return comenzi;
    }

    public void setComenzi(List<Comanda> comenzi) {
        this.comenzi = (comenzi == null) ? new ArrayList<>() : comenzi;
    }
}


