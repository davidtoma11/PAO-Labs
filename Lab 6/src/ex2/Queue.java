package ex2;

import ex1.Task;

public class Queue implements Container {
    private Task[] tasks;
    private int front, rear, size;
    private static final int INITIAL_CAPACITY = 10;

    public Queue() {
        tasks = new Task[INITIAL_CAPACITY];
        front = 0;
        rear = -1;
        size = 0;
    }

    @Override
    public void add(Task task) {
        if (size == tasks.length) {
            resize();
        }
        rear = (rear + 1) % tasks.length;
        tasks[rear] = task;
        size++;
    }

    @Override
    public Task remove() {
        if (isEmpty()) {
            throw new IllegalStateException("Coada este goala");
        }
        Task task = tasks[front];
        front = (front + 1) % tasks.length;
        size--;
        return task;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void resize() {
        Task[] newTasks = new Task[tasks.length * 2];
        for (int i = 0; i < size; i++) {
            newTasks[i] = tasks[(front + i) % tasks.length];
        }
        tasks = newTasks;
        front = 0;
        rear = size - 1;
    }
}