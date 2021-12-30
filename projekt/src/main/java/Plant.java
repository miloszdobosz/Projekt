import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class Plant extends MapElement{
    public static int energy;

    Vector2d position;

    public Plant(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return position;
    }

    public String toString() {
        return "*";
    }

    @Override
    public Shape view() {
        Shape view = super.view();
        view.setFill(Color.rgb(100, 150, 0));
        return view;
    }
}
