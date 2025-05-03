import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookStoreTest {
    private static List<Book> books = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n1. Adaugă carte");
            System.out.println("2. Afișează cărți");
            System.out.println("3. Actualizează carte");
            System.out.println("4. Șterge carte");
            System.out.println("5. Ieșire");
            System.out.print("Alegeți o opțiune: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumă newline

            switch (choice) {
                case 1:
                    createBook();
                    break;
                case 2:
                    readBooks();
                    break;
                case 3:
                    updateBook();
                    break;
                case 4:
                    deleteBook();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Opțiune invalidă!");
            }
        }
        scanner.close();
    }

    // Create
    private static void createBook() {
        System.out.print("Titlu: ");
        String title = scanner.nextLine();

        System.out.print("Autor: ");
        String author = scanner.nextLine();

        System.out.print("Editura: ");
        String publisher = scanner.nextLine();

        int pageCount;
        do {
            System.out.print("Număr pagini (trebuie să fie > 0): ");
            pageCount = scanner.nextInt();
            scanner.nextLine(); // Consumă newline
        } while (pageCount <= 0);

        books.add(new Book(title, author, publisher, pageCount));
        System.out.println("Carte adăugată cu succes!");
    }

    // Read
    private static void readBooks() {
        if (books.isEmpty()) {
            System.out.println("Nu există cărți în bibliotecă!");
            return;
        }

        for (int i = 0; i < books.size(); i++) {
            System.out.println("\nCarte #" + (i + 1));
            displayBookInfo(books.get(i));
        }
    }

    // Update
    private static void updateBook() {
        readBooks();
        if (books.isEmpty()) return;

        System.out.print("Selectați cartea de actualizat (nr.): ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine(); // Consumă newline

        if (index < 0 || index >= books.size()) {
            System.out.println("Index invalid!");
            return;
        }

        Book book = books.get(index);
        System.out.print("Noul titlu (" + book.getTitle() + "): ");
        book.setTitle(scanner.nextLine());

        System.out.print("Noul autor (" + book.getAuthor() + "): ");
        book.setAuthor(scanner.nextLine());

        System.out.print("Noua editură (" + book.getPublisher() + "): ");
        book.setPublisher(scanner.nextLine());

        int pageCount;
        do {
            System.out.print("Noul număr pagini (" + book.getPageCount() + "): ");
            pageCount = scanner.nextInt();
            scanner.nextLine();
        } while (pageCount <= 0);
        book.setPageCount(pageCount);

        System.out.println("Carte actualizată cu succes!");
    }

    // Delete
    private static void deleteBook() {
        readBooks();
        if (books.isEmpty()) return;

        System.out.print("Selectați cartea de șters (nr.): ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        if (index < 0 || index >= books.size()) {
            System.out.println("Index invalid!");
            return;
        }

        books.remove(index);
        System.out.println("Carte ștearsă cu succes!");
    }

    // Afișare formatată
    private static void displayBookInfo(Book book) {
        System.out.println("BOOK_TITLE: " + book.getTitle().toUpperCase());
        System.out.println("BOOK_AUTHOR: " + book.getAuthor());
        System.out.println("BOOK_PUBLISHER: " + book.getPublisher().toLowerCase());
        System.out.println("PAGES: " + book.getPageCount());
    }
}