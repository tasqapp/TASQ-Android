package tasq.app;

import java.util.Comparator;

public class TaskPriorityComparator implements Comparator<Task> {

    @Override
    public int compare(Task o1, Task o2) {
        return Integer.compare(o1.getPriority().ordinal(), o2.getPriority().ordinal());
    }
}
