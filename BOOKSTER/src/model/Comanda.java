package model;

import java.time.LocalDate;
import java.util.List;

public class Comanda {
    private int id;
    private Utilizator utilizator;
    private List<Carte> carti;
    private LocalDate dataComenzii;
    private LocalDate dataReturnarii;  // Atributul pentru data returnării
    private String status;

    public Comanda(int id, Utilizator utilizator, List<Carte> carti, LocalDate dataComenzii, LocalDate dataReturnarii, String status) {
        this.id = id;
        this.utilizator = utilizator;
        this.carti = carti;
        this.dataComenzii = dataComenzii;
        this.dataReturnarii = dataReturnarii;  // Inițializarea datei de returnare
        this.status = status;
    }

    public Comanda() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilizator getUtilizator() {
        return utilizator;
    }

    public void setUtilizator(Utilizator utilizator) {
        this.utilizator = utilizator;
    }

    public List<Carte> getCarti() {
        return carti;
    }

    public void setCarti(List<Carte> carti) {
        this.carti = carti;
    }

    public LocalDate getDataComenzii() {
        return dataComenzii;
    }

    public void setDataComenzii(LocalDate dataComenzii) {
        this.dataComenzii = dataComenzii;
    }

    public LocalDate getDataReturnarii() {
        return dataReturnarii;
    }

    public void setDataReturnarii(LocalDate dataReturnarii) {
        this.dataReturnarii = dataReturnarii;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
