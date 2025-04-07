import java.util.Scanner;

public class ex2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int[] array1 = readarray(scanner);
        int[] array2 = readarray(scanner);


        sortArray(array1);
        sortArray(array2);

        int[] mergedArray = mergeSortedArrays(array1, array2);

        System.out.print("Tabloul interclasat: ");
        printArray(mergedArray);

        scanner.close();
    }

    public static int[] readarray(Scanner scanner) {
        int number;
        do {
            System.out.print("Introduceți un număr întreg (>=5): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Numărul nu este întreg!");
                scanner.next();
                System.out.print("Introduceți un număr întreg (>=5): ");
            }
            number = scanner.nextInt();
            if (number < 5) {
                System.out.println("Numărul este prea mic!");
            }
        } while (number < 5);

        int[] array = new int[number];
        for (int i = 0; i < number; i++) {
            System.out.print("Elementul " + (i + 1) + ": ");
            while (!scanner.hasNextInt()) {
                System.out.println("Numărul nu este întreg!");
                scanner.next();
                System.out.print("Elementul " + (i + 1) + ": ");
            }
            array[i] = scanner.nextInt();
            scanner.nextLine(); // Curăță bufferul după fiecare număr
        }
        return array;
    }

    public static void printArray(int[] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static void sortArray(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    public static int[] mergeSortedArrays(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;

        // Interclasare cu 2 pointeri
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                result[k++] = a[i++];
            } else {
                result[k++] = b[j++];
            }
        }

        // Copiem restul elementelor rămase
        while (i < a.length) {
            result[k++] = a[i++];
        }

        while (j < b.length) {
            result[k++] = b[j++];
        }

        return result;
    }
}
