package service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static final String AUDIT_FILE = "audit.csv";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logAction(String actionName) {
        String timestamp = LocalDateTime.now().format(dtf);
        String line = actionName + "," + timestamp;
        writeToFile(line);
    }

    private static void writeToFile(String line) {
        try (PrintWriter out = new PrintWriter(new FileWriter(AUDIT_FILE, true))) {
            out.println(line);
        } catch (IOException e) {
            System.err.println("Eroare la scrierea auditului: " + e.getMessage());
        }
    }
}


