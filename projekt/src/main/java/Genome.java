import java.util.Arrays;

public class Genome {
    public Orientation[] genes = new Orientation[32];

    public Genome() {
        Orientation[] orientations = Orientation.values();
        for (int i = 0; i < genes.length; i++) {
            genes[i] = orientations[SimulationEngine.random.nextInt(8)];
        }

        Arrays.sort(genes);
    }

    public Genome(Animal[] parents) {
        int order = SimulationEngine.random.nextInt(2);
        Animal first = parents[order];
        Animal second = parents[1 - order];

        int cut = findCut(first.getEnergy(), second.getEnergy());

        Orientation[] g = first.getGenome().genes;
        for (int i = 0; i < cut; i++) {
            genes[i] = g[i];
        }

        g = second.getGenome().genes;
        for (int i = cut; i < genes.length; i++) {
            genes[i] = g[i];
        }

        Arrays.sort(genes);
    }

    private int findCut(int firstEnergy, int secondEnergy) {
        return genes.length * firstEnergy / (firstEnergy + secondEnergy);
    }

    public Orientation random() {
        return genes[SimulationEngine.random.nextInt(genes.length)];
    }

    public String toString() {
        String string = new String();
        for (Orientation gene : genes) {
            string += gene.ordinal();
        }

        return string;
    }
}
