package ui;

import crud.*;
import model.Administrator;
import service.DataInitialization;

import java.util.Scanner;

public class MeniuAdministrator {
    private final Administrator admin;
    private final Scanner scanner;
    private final DataInitialization dataService;

    public MeniuAdministrator(Administrator admin, Scanner scanner, DataInitialization dataService) {
        this.admin = admin;
        this.scanner = scanner;
        this.dataService = dataService;
    }

    public void start() {
        int optiune = 0;

        do {
            System.out.println("\n===== MENIU ADMINISTRATOR =====");
            System.out.println("1. Utilizatori");
            System.out.println("2. Carti");
            System.out.println("3. Autori");
            System.out.println("4. Categorii Carte");
            System.out.println("5. Recenzii Carte");
            System.out.println("6. Comenzi");
            System.out.println("7. Companii");
            System.out.println("0. Iesire");
            System.out.print("Alege o optiune: ");

            try {
                optiune = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Introdu un numar valid!");
                continue;
            }

            switch (optiune) {
                case 1 -> {
                    System.out.println("\n--- Meniu Utilizatori ---");
                    UtilizatoriCRUD.meniuUtilizator();
                }
                case 2 -> {
                    System.out.println("\n--- Meniu Carti ---");
                    CartiCRUD.meniuCarte();
                }
                case 3 -> {
                    System.out.println("\n--- Meniu Autori ---");
                    AutoriCRUD.meniuAutor();
                }
                case 4 -> {
                    System.out.println("\n--- Meniu Categorii Carte ---");
                    CategoriiCartiCRUD.meniuCategorie();
                }
                case 5 -> {
                    System.out.println("\n--- Meniu Recenzii Carte ---");
                    RecenziiCartiCRUD.meniuRecenzie();
                }
                case 6 -> {
                    System.out.println("\n--- Meniu Comenzi ---");
                    ComenziCRUD.meniuComanda();
                }
                case 7 -> {
                    System.out.println("\n--- Meniu Companii ---");
                    CompaniiCRUD.meniuCompanie();
                }
                case 0 -> System.out.println("Iesire din meniul administrator...");
                default -> System.out.println("Optiune invalida. Incearca din nou.");
            }

        } while (optiune != 0);
    }
}
