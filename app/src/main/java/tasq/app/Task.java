package tasq.app;

public class Task {
    private String  taskText;
    private String  dueDate;
    private String color;
    private boolean completed ;

    public Task(String newColor,String date,String text, boolean completed){
        taskText = text;
        dueDate = date;
        color = newColor;
        this.completed = completed ;
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

    public void setText(String text) { this.taskText = text; }

    public void setDate(String date) { this.dueDate = date; }

    public void setColor(String color) { this.color = color; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }
}
