/**
 * HANNAH BUZARD
 * DAVID KIPNIS
 * TYLER KJELDGAARD
 * DANIEL SHTUNYUK
 *
 * WESTERN WASHINGTON UNIVERSITY
 * CSCI 412 - WINTER 2021
 *
 * TASQ APPLICATION PROJECT
 */

/**
 * Task Class
 * Object that keeps track of an individual task's attributes, such as
 * the task name, due date, color tag, reminders, and subtasks
 */

package tasq.app;

public class Task {
    private String taskText;
    private String dueDate;
    private String color;
    private boolean completed;
    private String priority = "Low";

    public Task(String newColor, String date, String text, boolean completed) {
        this.taskText = text;
        this.dueDate = date;
        this.color = newColor;
        this.completed = completed;
    }

    public Task(String newColor, String date, String text, boolean completed, String priority) {
        this.taskText = text;
        this.dueDate = date;
        this.color = newColor;
        this.completed = completed;
        this.priority = priority ;
    }

    public static String getText(Task task) {
        return task.taskText;
    }

    public static String getDate(Task task) {
        return task.dueDate;
    }

    public static String getColor(Task task) {
        return task.color;
    }

    public void setText(String text) {
        this.taskText = text;
    }

    public void setDate(String date) {
        this.dueDate = date;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getPriority() { return priority; }

    public void setPriority(String priority) { this.priority = priority; }
}
