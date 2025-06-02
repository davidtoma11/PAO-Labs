package model;

import java.time.LocalDate;

public class RecenzieCarte {
    private int id;
    private Carte carte;
    private Utilizator utilizator;
    private String recenzie;
    private int scor; // 1-5
    private LocalDate dataRecenziei;

    public RecenzieCarte(int id, Carte carte, Utilizator utilizator, String recenzie, int scor, LocalDate dataRecenziei) {
        this.id = id;
        this.carte = carte;
        this.utilizator = utilizator;
        this.recenzie = recenzie;
        this.scor = scor;
        this.dataRecenziei = dataRecenziei;
    }

    public RecenzieCarte() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }

    public Utilizator getUtilizator() {
        return utilizator;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    public String getRecenzie() {
        return recenzie;
    }

    public void setRecenzie(String recenzie) {
        this.recenzie = recenzie;
    }

    public int getScor() {
        return scor;
    }

    public void setScor(int scor) {
        this.scor = scor;
    }

    public LocalDate getDataRecenziei() {
        return dataRecenziei;
    }

    public void setDataRecenziei(LocalDate dataRecenziei) {
        this.dataRecenziei = dataRecenziei;
    }

    @Override
    public String toString() {
        return "model.RecenzieCarte{" +
                "id=" + id +
                ", carte=" + carte +
                ", utilizator=" + utilizator +
                ", recenzie='" + recenzie + '\'' +
                ", scor=" + scor +
                ", dataRecenziei=" + dataRecenziei +
                '}';
    }
}
