import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Population {

    private GenomeGenerator generator;

    private ArrayList<Genome> population = new ArrayList<>();
    private ProblemData data;

    public Population(ProblemData data, int populationSize) {
        this.generator = new GenomeGenerator(data);
        this.data = data;
        for (int i = 0 ; i < populationSize ; i++){
            population.add(generator.generateGenome());
        }
    }
    public Population(ProblemData data, ArrayList<Genome> population){
        this.generator = new GenomeGenerator(data);
        this.data = data;
        this.population = population;

    }

    public ArrayList<Genome> generatePopulation(int populationSize){
        ArrayList<Genome> population = new ArrayList<>();
        for (int i = 0 ; i < populationSize ; i++){
            population.add(generator.generateGenome());
        }
        return population;
    }

    public ArrayList<Genome> getPopulation() {
        return population;
    }

    public double meanFitness() {
        double sum = 0;
        for (Genome g : population) {
            sum += g.fitness(false);
        }
        return sum / population.size();
    }
}
