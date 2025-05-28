package ex1;

public class CounterOutTask implements Task {
    private static int counter = 0;
    private int instanceCounter;

    public CounterOutTask() {
        instanceCounter = ++counter;
    }

    @Override
    public void execute() {
        System.out.println("Valoare contor: " + instanceCounter);
    }
}