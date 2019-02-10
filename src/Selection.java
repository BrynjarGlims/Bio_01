import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

public class Selection {

    /**
     * Returns a sample of a population using Stochastic Universal Sampling.
     * A genome can be selected more than once, depending on the fraction to sample.
     * Each genome will be represented with the expected weight of their fitness.
     * @param population  a list of genomes to sample from
     * @return            the sampled subset of the population
     */
    public static ArrayList<Genome> stochasticUniversalSampling(Population population) {
        ArrayList<Genome> genomes = population.getPopulation();

        int n = genomes.size();
        double total_fitness = 0.0;

        for (Genome genome : genomes) {
            total_fitness += genome.fitness(true);
        }

        double fitness_sum = 0.0;
        double start = ThreadLocalRandom.current().nextDouble();
        ArrayList<Genome> selected = new ArrayList<>();
        int idx = 0;

        for (Genome genome : genomes) {
            fitness_sum += genome.fitness(true) / total_fitness * n;

            while (fitness_sum > start + idx) {
                Genome newGenome = new Genome(genome);
                selected.add(newGenome);
                idx++;
            }
        }
        return selected;
    }

    /**
     * Returns a sample of the population using Stochastic Acceptance.
     * This is a faster alternative to FPS with approximately equal probability distribution.
     * https://arxiv.org/pdf/1109.3627.pdf
     * @param population  a list of genomes to sample from
     * @return            the sampled subset of the population
     */
    public static ArrayList<Genome> stochasticAcceptance(Population population) {
        ArrayList<Genome> genomes = population.getPopulation();

        int n = genomes.size();
        double max_fitness = 0.0;
        ArrayList<Double> fitness_values = new ArrayList<>();

        for (Genome genome : genomes) {
            double fitness = genome.fitness(true);
            fitness_values.add(fitness);
            if (fitness > max_fitness) {
                max_fitness = fitness;
            }
        }

        ArrayList<Genome> selected = new ArrayList<>();
        int individual;

        for (int i = 0; i < n; i++) {
            while (true) {
                individual = (int) (n * ThreadLocalRandom.current().nextDouble());
                if (ThreadLocalRandom.current().nextDouble() < fitness_values.get(individual) / max_fitness) {
                    break;
                }
            }
            Genome newGenome = new Genome(genomes.get(individual % n));
            selected.add(newGenome);
        }

        return selected;
    }

    /**
     * Selects a copy of the genomes with highest fitness in the population.
     * These can be added to subsequent generations to ensure that crossover and mutation
     * does not ruin some of the best individuals.
     * @param population  a list of genomes to sample from
     * @param n           the number of top genomes to keep
     * @return            genomes with highest fitness in the population
     */
    public static ArrayList<Genome> elitism(Population population, int n) {
        // sort population by fitness DESCENDING
        ArrayList<Genome> genomes = population.getPopulation();

        genomes.sort(Collections.reverseOrder());

        ArrayList<Genome> elites = new ArrayList<>();

        // build a copy of the top n individuals
        for (Genome original : genomes.subList(0, n)) {
            elites.add(new Genome(original));
        }

        return elites;
    }

    public static ArrayList<Genome> tournamentSelection(Population population, int size) {
        ArrayList<Genome> selected = new ArrayList<>();

        for (int i = 0; i < population.getPopulation().size(); i++) {
            selected.add(tournament(population, size));
        }
        return selected;
    }

    private static Genome tournament(Population population, int size) {
        ArrayList<Genome> genomes = population.getPopulation();

        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 1; i < genomes.size(); i++) indices.add(i);
        Collections.shuffle(indices);

        ArrayList<Genome> selected = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            selected.add(new Genome(genomes.get(indices.get(i))));
        }

        selected.sort(Collections.reverseOrder());

        if (ThreadLocalRandom.current().nextDouble() < 0.6) {
            return selected.get(0);
        } else {
            return selected.get(ThreadLocalRandom.current().nextInt(0, size));
        }
    }
}