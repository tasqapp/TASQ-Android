package tasq.app;

public class Task {
    private String  taskText;
    private String  dueDate;
    private String color;

    public Task(String newColor,String date,String text){
        taskText = text;
        dueDate = date;
        color = newColor;
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
}
