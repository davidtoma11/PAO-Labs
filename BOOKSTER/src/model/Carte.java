package model;

import java.util.ArrayList;
import java.util.List;


public class Carte {
    private int id;
    private String titlu;
    private Autor autor;
    private CategorieCarte categorie;
    private int anPublicare;
    private boolean disponibilitate;
    private List<RecenzieCarte> recenzii;

    public Carte(int id, String titlu, Autor autor, CategorieCarte categorie, int anPublicare, boolean disponibilitate) {
        this.id = id;
        this.titlu = titlu;
        this.autor = autor;
        this.categorie = categorie;
        this.anPublicare = anPublicare;
        this.disponibilitate = disponibilitate;
        this.recenzii = recenzii != null ? recenzii : new ArrayList<>();
    }

    public Carte(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public CategorieCarte getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieCarte categorie) {
        this.categorie = categorie;
    }

    public int getAnPublicare() {
        return anPublicare;
    }

    public void setAnPublicare(int anPublicare) {
        this.anPublicare = anPublicare;
    }

    public boolean isDisponibilitate() {
        return disponibilitate;
    }

    public void setDisponibilitate(boolean disponibilitate) {
        this.disponibilitate = disponibilitate;
    }

    public List<RecenzieCarte> getRecenzii() {
        return recenzii;
    }

    public void setRecenzii(List<RecenzieCarte> recenzii) {
        this.recenzii = recenzii != null ? recenzii : new ArrayList<>();
    }
}
