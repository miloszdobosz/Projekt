import javafx.application.Platform;

import java.util.Random;

import static java.lang.System.out;

public class SimulationEngine implements Runnable{
    public static Random random = new Random();
    public static int day = 0;
    public static int animalCount;
    public static int delay;

    private Map leftMap;
    private Map rightMap;

    private App app;

    public SimulationEngine(TorusMap leftMap, RectangularMap rightMap, App app) {
        this.leftMap = leftMap;
        this.rightMap = rightMap;
        this.app = app;
    }

    @Override
    public void run() {
        createAnimals();
        Platform.runLater(new Thread(() -> app.primaryStage.setScene(app.createMainScene())));

        while (leftMap.getAnimalCount() != 0 || rightMap.getAnimalCount() != 0) {
            synchronized (leftMap) {
                leftMap.update();
            }
            synchronized (rightMap) {
                rightMap.update();
            }
            Platform.runLater(new Thread(() -> app.update()));

            day++;

            try {
                Thread.sleep(delay);
            } catch (Exception exception) {
                out.println(exception);
            }
        }
    }

    public void createAnimals() {
        Vector2d position;
        Genome genome;

        int ex = leftMap.mapEnd.x;
        int ey = leftMap.mapEnd.y;

        for (int i = 0; i < animalCount; i++) {
            do {
                int x = SimulationEngine.random.nextInt(ex);
                int y = SimulationEngine.random.nextInt(ey);
                position = new Vector2d(x, y);
            } while (leftMap.isOccupied(position));

            genome = new Genome();
            leftMap.place(new Animal(leftMap, day, position, genome));
            rightMap.place(new Animal(rightMap, day, position, genome));
        }
    }

}
