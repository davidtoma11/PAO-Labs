package main;

import model.Administrator;
import model.Utilizator;
import service.AuditService;
import service.AutentificareService;
import service.DataInitialization;
import ui.MeniuAdministrator;
import ui.MeniuUtilizator;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static final int MAX_INCERCARI_LOGIN = 3;

    public static void main(String[] args) {
        try {
            DataInitialization dataService = DataInitialization.getInstance();
            dataService.loadAllData();

            AutentificareService authService = new AutentificareService(
                    dataService.getUtilizatori(),
                    dataService.getAdministratori(),
                    dataService.getCompanii()
            );

            pornesteAplicatia(authService, dataService);
        } catch (SQLException e) {
            System.err.println("Eroare la inițializarea bazei de date: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Eroare neașteptată:");
            e.printStackTrace();
        }
    }

    private static void pornesteAplicatia(AutentificareService authService, DataInitialization dataService) {
        Scanner scanner = new Scanner(System.in);
        boolean inExecutie = true;

        while (inExecutie) {
            afiseazaMeniuPrincipal();
            String optiune = scanner.nextLine().trim();

            switch (optiune) {
                case "1" -> proceseazaAutentificare(authService, dataService, scanner);
                case "2" -> {
                    authService.creareContNou(scanner);
                    AuditService.logAction("Creare cont nou");
                }
                case "0" -> {
                    if (confirmareIesire(scanner)) {
                        AuditService.logAction("Ieșire aplicație");
                        System.out.println("Se procesează ieșirea...");
                        inExecutie = false;
                    }
                }
                default -> {
                    System.out.println("Opțiune invalidă!");
                    AuditService.logAction("Opțiune invalidă selectată în meniu principal");
                }
            }
        }

        scanner.close();
    }

    private static void afiseazaMeniuPrincipal() {
        System.out.println("\n===== BIBLIOTECA ONLINE =====");
        System.out.println("1. Autentificare");
        System.out.println("2. Creare cont nou");
        System.out.println("0. Ieșire");
        System.out.print("Alege o opțiune: ");
    }

    private static void proceseazaAutentificare(AutentificareService authService,
                                                DataInitialization dataService,
                                                Scanner scanner) {
        for (int i = 0; i < MAX_INCERCARI_LOGIN; i++) {
            Object utilizator = authService.login(scanner);
            if (utilizator != null) {
                if (utilizator instanceof Administrator admin) {
                    AuditService.logAction("Logare administrator cu ID: " + admin.getId());
                } else if (utilizator instanceof Utilizator user) {
                    AuditService.logAction("Logare utilizator cu ID: " + user.getId());
                } else {
                    AuditService.logAction("Logare necunoscută");
                }

                pornesteMeniulDupaRol(utilizator, scanner, dataService);
                return;
            }

            System.out.printf("Autentificare eșuată - Email sau parolă incorecte - Încercări rămase: %d%n",
                    MAX_INCERCARI_LOGIN - i - 1);
            AuditService.logAction("Eșec autentificare");
        }

        System.out.println("Număr maxim de încercări atins.");
        AuditService.logAction("Blocare acces după prea multe încercări");
    }

    private static void pornesteMeniulDupaRol(Object utilizator, Scanner scanner, DataInitialization dataService) {
        if (utilizator instanceof Administrator admin) {
            new MeniuAdministrator(admin, scanner, dataService).start();
        } else if (utilizator instanceof Utilizator user) {
            new MeniuUtilizator(user, scanner, dataService).start();
        } else {
            System.err.println("Tip necunoscut de utilizator.");
            AuditService.logAction("Eroare: utilizator de tip necunoscut în meniul principal");
        }
    }

    private static boolean confirmareIesire(Scanner scanner) {
        System.out.print("Sigur dorești să ieși? (da/nu): ");
        return scanner.nextLine().trim().equalsIgnoreCase("da");
    }
}

