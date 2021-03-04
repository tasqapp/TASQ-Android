package tasq.app;

public enum Priority {
    EXTREME,
    HIGH,
    MEDIUM,
    LOW;

    public static Priority getPriorityFromString(String pri) {
        switch (pri.toUpperCase()) {
            case "EXTREME":
                return EXTREME;
            case "HIGH":
                return HIGH;
            case "MEDIUM":
                return MEDIUM;
            case "LOW":
            default:
                return LOW;
        }
    }

    public static String getCapaitalizedStringFromPriority(Priority priority) {
        String str = priority.name().toLowerCase();
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
