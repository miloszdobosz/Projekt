import java.util.*;

public class Map implements IPositionChangeObserver{
    protected HashMap<Vector2d, LinkedList<Animal>> animals = new HashMap<>();
    protected HashMap<Vector2d, Plant> plants = new HashMap<>();

    private int animalCount = 0;
    private int plantCount = 0;

    public final Vector2d mapStart;
    public final Vector2d mapEnd;

    public final Vector2d jungleStart;
    public final Vector2d jungleEnd;

    public Map(int width, int height, double jungleRatio) {
        mapStart = new Vector2d(0, 0);
        mapEnd = new Vector2d(width, height);

        int jungleWidth = (int) ((double) width * Math.sqrt(jungleRatio/100));
        int jungleHeight = (int) ((double) height * Math.sqrt(jungleRatio/100));

        jungleStart = new Vector2d((width - jungleWidth) / 2, (height - jungleHeight) / 2);
        jungleEnd = new Vector2d((width + jungleWidth) / 2, (height + jungleHeight) / 2);
    }





    public void update() {
        killAndMove();
        eatAndReproduce();
        growPlants();
    }

    private void killAndMove() {
        LinkedList<Animal> dead = new LinkedList<>();
        LinkedList<Animal> alive = new LinkedList<>();

        animals.forEach((position, field) -> {
            for (Animal animal : field) {
                if (animal.getEnergy() <= 0) {
                    dead.add(animal);
                } else {
                    alive.add(animal);
                }
            }
        });

        for (Animal animal : dead) {
            remove(animal);
        }

        for (Animal animal : alive) {
            animal.move();
        }
    }

    private void eatAndReproduce() {
        animals.forEach((position, field) -> {

            int number = equallyStrong(field);

            // Jedzienie
            if (plants.containsKey(position)) {
                plants.remove(position);
                plantCount--;

                // Dzielenie energii między najsilniejsze zwierzęta na polu,
                // Operacje są na intach więc część energii jest tracona (np na walkę o pożywienie)

                int energy = Plant.energy / number;
                for (int j = 0; j < number; j++) {
                    field.get(j).addEnergy(energy);
                }
            }

            // Rozmnażanie, drugie najsilniejsze zwierzę musi mieć wystarczającą ilość energii
            if (field.size() > 2 && field.get(1).getEnergy() >= Animal.startEnergy / 2) {
                if (number < 2) {

                } else {
                    int r1 = SimulationEngine.random.nextInt(number);
                    int r2;
                    do {
                        r2 = SimulationEngine.random.nextInt(number);
                    } while (r1 == r2);

                    Animal[] parents = new Animal[]{field.get(r1), field.get(r2)};
                    Animal animal = new Animal(this, SimulationEngine.day, position, parents);
                    place(animal);
                }
            }
        });
    }

    private void growPlants() {
        int x = SimulationEngine.random.nextInt(jungleStart.x, jungleEnd.x);
        int y = SimulationEngine.random.nextInt(jungleStart.y, jungleEnd.y);

        Vector2d position = new Vector2d(x, y);
        plants.put(position, new Plant(position));

        do {
            x = SimulationEngine.random.nextInt(mapStart.x, mapEnd.x);
            y = SimulationEngine.random.nextInt(mapStart.y, mapEnd.y);

            position = new Vector2d(x, y);
        } while (position.follows(jungleStart) && position.precedes(jungleEnd));

        plants.put(position, new Plant(position));
        plantCount++;
    }

    // Ile jest zwierząt o największej energii na polu
    private int equallyStrong(LinkedList<Animal> field) {
        int number = 0;
        int firstEnergy = field.getFirst().getEnergy();
        for (Animal animal : field) {
            number++;
            if (animal.getEnergy() < firstEnergy) {
                break;
            }
        }
        return number;
    }





    public void place(Animal animal) {
        Vector2d position = animal.getPosition();
        LinkedList<Animal> field;

        if (animals.containsKey(position)) {
            field = animals.get(position);
        } else {
            field = new LinkedList<>();
        }

        insert(field, animal);
        animals.put(position, field);
        animalCount++;
    }

    private void insert(LinkedList<Animal> field, Animal animal) {
        // Znajduje miejsce dla zwierzęcia w liście sortowanej malejąco według energii
        int i = 0;
        if (!field.isEmpty()) {
            int energy = animal.getEnergy();
            for (Animal other : field) {
                i++;
                if (other.getEnergy() < energy) {
                    break;
                }
            }
        }

        field.add(i, animal);
    }

    public void remove(Animal animal) {
        Vector2d position = animal.getPosition();

        LinkedList<Animal> field = animals.get(position);
        field.remove(animal);

        if (field.isEmpty()) {
            animals.remove(position, field);
        }

        animalCount--;
    }





    public boolean isOccupied(Vector2d position) {
        return animals.containsKey(position) || plants.containsKey(position);
    }

    public Object objectAt(Vector2d position){
        if (animals.containsKey(position)){
            return animalAt(position);
        } else {
            return plantAt(position);
        }
    }

    public Plant plantAt(Vector2d position) {
        return plants.get(position);
    }

    public Animal animalAt(Vector2d position) {
        return animals.get(position).get(0);
    }

    public Set<Vector2d> getPlantPositions() {
        return plants.keySet();
    }

    public Set<Vector2d> getAnimalPositions() {
        return animals.keySet();
    }

    public int getAnimalCount() {
        return animalCount;
    }

    public int getPlantCount() {
        return plantCount;
    }




    @Override
    public void positionChanged(Animal animal, Vector2d newPosition) {
        remove(animal);
        animal.setPosition(newPosition);
        place(animal);
    }

}
