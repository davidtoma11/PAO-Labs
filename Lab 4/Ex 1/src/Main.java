public class Main {
    public static void main(String[] args) {
        // Testare BookstoreTest
        BookStoreTest.main(args);

        // Testare BookstoreCheck
        Book book1 = new Book("Harry Potter", "J.K. Rowling", "Bloomsbury", 500);
        Book book2 = new Book("Harry Potter", "J.K. Rowling", "BLOOMSBURY", 500);
        Book book3 = new Book("LotR", "Tolkien", "HARPER COLLINS", 1000);

        System.out.println("\nTest dublu exemplar: " +
                BookStoreCheck.isDuplicate(book1, book2)); // true
        System.out.println("Test carte mai groasă: " +
                BookStoreCheck.getThickerBook(book1, book3).getTitle()); // LotR
    }
}