package inkball;

public enum Color {
    GREY(0),
    ORANGE(1),
    BLUE(2),
    GREEN(3),
    YELLOW(4);

    private final int value;

    Color(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Color fromValue(int value) {
        for (Color color : Color.values()) {
            if (color.value == value) {
                return color;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
