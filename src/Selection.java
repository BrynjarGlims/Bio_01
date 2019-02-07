import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

public class Selection {

    /**
     * Returns a sample of a population using Stochastic Universal Sampling.
     * A genome can be selected more than once, depending on the fraction to sample.
     * Each genome will be represented with the expected weight of their fitness.
     * @param population  a list of genomes to sample from
     * @param fraction    fraction of the population to sample
     * @return            the sampled subset of the population
     */
    public static ArrayList<Genome> stochasticUniversalSampling(Population population, double fraction) {
        ArrayList<Genome> genomes = population.getPopulation();

        int population_size = genomes.size();
        int n = (int) (population_size * fraction);
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
                Genome newGenome = new Genome(new ArrayList<>(genome.getGenome()));
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
     * @param fraction    fraction of the population to sample
     * @return            the sampled subset of the population
     */
    public static ArrayList<Genome> stochasticAcceptance(ArrayList<Genome> population, double fraction) {
        int n = (int) (population.size() * fraction);
        int popSize = population.size();
        double max_fitness = 0.0;
        ArrayList<Double> fitness_values = new ArrayList<>();

        for (Genome genome : population) {
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
                individual = (int) (popSize * ThreadLocalRandom.current().nextDouble());
                if (ThreadLocalRandom.current().nextDouble() < fitness_values.get(individual) / max_fitness) {
                    break;
                }
            }
            selected.add(population.get(individual % popSize));
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

        Collections.sort(genomes, Collections.reverseOrder());

        ArrayList<Genome> elites = new ArrayList<>();

        // build a copy of the top n individuals
        for (Genome original : genomes.subList(0, n)) {
            elites.add(new Genome(original));
        }

        return elites;
    }
}