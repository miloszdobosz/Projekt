import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.ArrayList;

public class Animal extends MapElement{
    public static int startEnergy;
    public static int moveEnergy;

    private Vector2d position;
    private Orientation orientation = Orientation.values()[SimulationEngine.random.nextInt(8)];
    private int energy;

    private int birth;
    private Genome genome;

    private Map map;
    private ArrayList<IPositionChangeObserver> observers = new ArrayList<>();

    public Animal(Map map, int day, Vector2d initialPosition, Genome genome) {
        this.map = map;
        this.birth = day;
        this.position = initialPosition;
        this.energy = startEnergy;
        this.genome = genome;

        if (map instanceof IPositionChangeObserver) {
            this.observers.add((IPositionChangeObserver) map);
        }
    }

    public Animal(Map map, int day, Vector2d initialPosition, Animal[] parents) {
        this(map, day, initialPosition, new Genome(parents));

        int e0 = parents[0].getEnergy() / 4;
        int e1 = parents[1].getEnergy() / 4;
        parents[0].addEnergy(-e0);
        parents[1].addEnergy(-e1);
        this.energy = e0 + e1;
    }





    public Vector2d getPosition() {
        return position;
    }

//    public Orientation getOrientation() {return orientation;}

    public int getEnergy() {return energy;}

    public Genome getGenome() {
        return genome;
    }





    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void addEnergy(int energy) {
        this.energy += energy;
    }





    public void move() {
        Orientation change = genome.random();

        switch (change) {
            case N -> positionChanged(position.add(orientation.toUnitVector()));
            case S -> positionChanged(position.subtract(orientation.toUnitVector()));
            default -> orientation = orientation.add(change);
        }

        addEnergy(-moveEnergy);
    }





    public void addObserver(IPositionChangeObserver observer) {
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer) {
        this.observers.remove(observer);
    }

    public void positionChanged(Vector2d newPosition) {
        for (IPositionChangeObserver observer: observers) {
            observer.positionChanged(this, newPosition);
        }
    }





    public String toString() {
        return "A";
    }

    @Override
    public Shape view() {
        Shape view = super.view();
        int energyColour = (energy * 200 / startEnergy) ;
        energyColour = energyColour <= 255? energyColour : 255;
        view.setFill(Color.rgb(energyColour, 0, 50));
        return view;
    }
}
