package ex1;

public class RandomOutTask implements Task {
    private int randomNumber;

    public RandomOutTask() {
        this.randomNumber = (int)(Math.random() * 100); // Generează număr între 0-99
    }

    @Override
    public void execute() {
        System.out.println("Numar generat aleator: " + randomNumber);
    }
}