public enum Orientation {
    N, NE, E, SE, S, SW, W, NW;

    public Vector2d toUnitVector() {
        return switch (this) {
            case N -> new Vector2d(0, 1);
            case NE -> new Vector2d(1, 1);
            case E -> new Vector2d(1, 0);
            case SE -> new Vector2d(1, -1);
            case S -> new Vector2d(0, -1);
            case SW -> new Vector2d(-1, -1);
            case W -> new Vector2d(-1, 0);
            case NW -> new Vector2d(-1, 1);
        };
    }

    public Orientation add(Orientation other) {
        return this.values()[(this.ordinal() + other.ordinal()) % 8];
    }
}
