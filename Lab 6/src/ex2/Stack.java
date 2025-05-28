package ex2;


import ex1.Task;

public class Stack implements Container {
    private Task[] tasks;
    private int top;
    private static final int INITIAL_CAPACITY = 10;

    public Stack() {
        tasks = new Task[INITIAL_CAPACITY];
        top = -1;
    }

    @Override
    public void add(Task task) {
        if (top == tasks.length - 1) {
            resize();
        }
        tasks[++top] = task;
    }

    @Override
    public Task remove() {
        if (isEmpty()) {
            throw new IllegalStateException("Stiva este goala");
        }
        return tasks[top--];
    }

    @Override
    public int size() {
        return top + 1;
    }

    @Override
    public boolean isEmpty() {
        return top == -1;
    }

    private void resize() {
        Task[] newTasks = new Task[tasks.length * 2];
        System.arraycopy(tasks, 0, newTasks, 0, tasks.length);
        tasks = newTasks;
    }
}
