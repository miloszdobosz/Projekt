import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class App extends Application {
    TorusMap leftMap;
    RectangularMap rightMap;

    GridPane leftMapGrid;
    GridPane rightMapGrid;

    Label leftCount = new Label(
            "Liczba wszystkich zwierząt na lewej mapie: 0\n" +
            "Liczba wszystkich roślin na lewej mapie: 0"
    );
    Label rightCount = new Label(
            "Liczba wszystkich zwierząt na prawej mapie: 0\n" +
            "Liczba wszystkich roślin na prawej mapie: 0"
    );

    Label leftGenes = new Label("Geny wybranego zwierzęcia: KLIKNIJ ZWIERZĘ LEWYM PRZYCISKIEM MYSZY");
    Label rightGenes = new Label("Geny wybranego zwierzęcia: KLIKNIJ ZWIERZĘ LEWYM PRZYCISKIEM MYSZY");

    Label legend = new Label(
            "CZERWONY - Zwierzęta,\n" +
            "ZIELONY - Rośliny,\n\n" +
            "Lewa mapa umożliwia przechodzenie na drugą stronę po dojściu do granicy, zaś prawa nie."
    );
    Label apology = new Label(
            "Bardzo przepraszam za ogólne wybrakowanie aplikacji, wiem że wygląda to marnie,\n" +
                    "proszę zajrzeć do implementacji, część symulacyjna jest chyba nieco mniej rozczarowująca niż GUI."
    );

    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(createInputScene());
        primaryStage.show();
    }

    public void update() {
        updateMapGrid(leftMap, leftMapGrid, leftGenes);
        updateMapGrid(rightMap, rightMapGrid, rightGenes);
        leftCount.setText(
                "Liczba wszystkich zwierząt na lewej mapie: " +
                leftMap.getAnimalCount() +
                "\nLiczba wszystkich roślin na lewej mapie: " +
                leftMap.getPlantCount()
        );
        rightCount.setText(
                "Liczba wszystkich zwierząt na prawej mapie: " +
                        rightMap.getAnimalCount() +
                        "\nLiczba wszystkich roślin na prawej mapie: " +
                        rightMap.getPlantCount()
        );
    }

    private void updateMapGrid(Map map, GridPane grid, Label genes) {
        grid.getChildren().clear();

        map.getPlantPositions().forEach((position) -> {
            grid.add(map.plantAt(position).view(), position.x, position.y);
        });

        map.getAnimalPositions().forEach((position) -> {
            Animal animal = map.animalAt(position);
            Shape view = animal.view();
            view.setOnMouseClicked(event -> genes.setText("Geny wybranego zwierzęcia: " + animal.getGenome().toString()));
            grid.add(view, position.x, position.y);
        });
    }

    public Scene createInputScene() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        List<Label> labels = Arrays.asList(
                new Label("Szerokość map: "),
                new Label("Wysokość map: "),
                new Label("Część zajęta przez dżunglę: "),
                new Label("Energia Startowa zwierząt: "),
                new Label("Energia potrzebna do ruchu: "),
                new Label("Energia ze zjedzenia rośliny: "),
                new Label("Liczba zwierząt: "),
                new Label("Długość dnia w milisekundach: ")
        );

        List<TextField> fields = Arrays.asList(
                new TextField("50"),
                new TextField("50"),
                new TextField("20"),
                new TextField("50"),
                new TextField("1"),
                new TextField("10"),
                new TextField("10"),
                new TextField("300")
        );

        for (int i = 0; i < labels.size(); i++) {
            Label label = labels.get(i);
            TextField field = fields.get(i);

            GridPane.setHalignment(label, HPos.RIGHT);

            grid.add(label, 0, i, 1, 1);
            grid.add(field, 1, i, 1, 1);
        }

        Button run = new Button("Rozpocznij");
        grid.add(run, 2, 0);

        final Label feedback = new Label();
        grid.add(feedback, 1, fields.size(), 2, 1);

        run.setOnAction(e -> {
            for (TextField field : fields) {
                if (field.getText().isEmpty()) {
                    feedback.setText("Musisz wypełnić wszystkie pola.");
                    return;
                }
            }

            int width, height;
            float jungleRatio;

            try {
                width = Integer.parseInt(fields.get(0).getText());
                height = Integer.parseInt(fields.get(1).getText());
                jungleRatio = Float.parseFloat(fields.get(2).getText());

                Animal.startEnergy = Integer.parseInt(fields.get(3).getText());
                Animal.moveEnergy = Integer.parseInt(fields.get(4).getText());
                Plant.energy = Integer.parseInt(fields.get(5).getText());

                SimulationEngine.animalCount = Integer.parseInt(fields.get(6).getText());
                SimulationEngine.delay = Integer.parseInt(fields.get(7).getText());
            } catch (Exception exception) {
                feedback.setText("Wartości muszą być liczbami");
                return;
            }

            leftMap = new TorusMap(width, height, jungleRatio);
            rightMap = new RectangularMap(width, height, jungleRatio);

            Thread thread = new Thread(new SimulationEngine(leftMap, rightMap,  this));
            thread.start();
        });

        return new Scene(grid);
    }

    public Scene createMainScene() {
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);

        leftMapGrid = createMapGrid(leftMap);
        rightMapGrid = createMapGrid(rightMap);

        grid.add(leftMapGrid, 0, 0);
        grid.add(rightMapGrid, 1, 0);

        grid.add(leftCount, 0, 1);
        grid.add(rightCount, 1, 1);
        grid.add(leftGenes, 0, 2);
        grid.add(rightGenes, 1, 2);

        grid.add(legend, 0, 3);
        grid.add(apology, 1, 3);

        return new Scene(grid);
    }

    public GridPane createMapGrid(Map map) {
        int width = map.mapEnd.x;
        int height = map.mapEnd.y;

        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();


        MapElement.size = 0.9 * Math.min(screenWidth / 2, screenHeight) / Math.max(width, height);

        ColumnConstraints cc = new ColumnConstraints(MapElement.size);
        RowConstraints rc = new RowConstraints(MapElement.size);

        GridPane grid = new GridPane();

        for (int i = 0; i < width; i++) {
            grid.getColumnConstraints().add(cc);
        }
        for (int i = 0; i < height; i++) {
            grid.getRowConstraints().add(rc);
        }

        return grid;
    }
}
