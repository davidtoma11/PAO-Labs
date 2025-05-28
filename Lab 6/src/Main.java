import ex1.Task;
import ex1.OutTask;
import ex1.RandomOutTask;
import ex1.CounterOutTask;
import ex2.Container;
import ex2.Stack;
import ex2.Queue;

public class Main {
    public static void main(String[] args) {
        // Testare Task-uri
        Task outTask = new OutTask("Mesaj de test");
        outTask.execute();

        Task randomTask = new RandomOutTask();
        randomTask.execute();

        Task counterTask1 = new CounterOutTask();
        Task counterTask2 = new CounterOutTask();
        counterTask1.execute();
        counterTask2.execute();

        // Testare Stack
        System.out.println("\nTestare Stack:");
        Container stack = new Stack();
        stack.add(new OutTask("Task 1"));
        stack.add(new OutTask("Task 2"));
        stack.add(new OutTask("Task 3"));

        while (!stack.isEmpty()) {
            Task task = stack.remove();
            task.execute();
        }

        // Testare Queue
        System.out.println("\nTestare Queue:");
        Container queue = new Queue();
        queue.add(new OutTask("Task A"));
        queue.add(new OutTask("Task B"));
        queue.add(new OutTask("Task C"));

        while (!queue.isEmpty()) {
            Task task = queue.remove();
            task.execute();
        }
    }
}