public class TorusMap extends Map {

    public TorusMap(int width, int height, double jungleRatio) {
        super(width, height, jungleRatio);
    }

    @Override
    public void positionChanged(Animal animal, Vector2d newPosition) {
        super.positionChanged(animal, fold(newPosition));
    }

    private Vector2d fold(Vector2d position) {
        return new Vector2d(Math.floorMod(position.x, mapEnd.x), Math.floorMod(position.y, mapEnd.y));
    }
}
