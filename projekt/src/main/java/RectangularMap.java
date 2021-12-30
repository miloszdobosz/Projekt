public class RectangularMap extends Map {
    public RectangularMap(int width, int height, double jungleRatio) {
        super(width, height, jungleRatio);
    }

    @Override
    public void positionChanged(Animal animal, Vector2d newPosition) {
        if (canMoveTo(newPosition)) {
            super.positionChanged(animal, newPosition);
        }
    }

    private boolean canMoveTo(Vector2d position) {
        return position.follows(mapStart) && position.precedes(mapEnd);
    }
}
