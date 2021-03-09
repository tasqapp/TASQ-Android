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

import com.google.android.gms.maps.model.LatLng;

public class Task {
    private String taskText;
    private String dueDate;
    private String color;
    private Priority priority;
    private String address;
    private LatLng location;
    private boolean completed;

    public Task(String newColor, String date, String text, Priority priority, boolean completed) {
        taskText = text;
        dueDate = date;
        color = newColor;
        this.priority = priority;
        this.completed = completed;
    }

    public Task(String newColor, String date, String text, Priority priority, boolean completed, String address) {
        taskText = text;
        dueDate = date;
        color = newColor;
        this.priority = priority;
        this.completed = completed;
        this.address = address;
        this.location = null;
    }

    public Task(String newColor, String date, String text, Priority priority, boolean completed, String address, LatLng location) {
        taskText = text;
        dueDate = date;
        color = newColor;
        this.priority = priority;
        this.completed = completed;
        this.address = address;
        this.location = location;
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

    public Priority getPriority() {
        return this.priority;
    }
    
    public String getAddress() {
        return address;
    }

    public LatLng getLocation() {
        return location;
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

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
