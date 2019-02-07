import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Mutation {

    public Mutation() {}

    public List<Integer> shuffledIndices(int n) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 1; i < n-1; i++) indexes.add(i);
        Collections.shuffle(indexes);
        return indexes;
    }

    public ArrayList<Genome> mutatePopulationRoute(ArrayList<Genome> genomes, double mutationRate) {

        ArrayList<Genome> mutatedGenomes = new ArrayList<>();

        for (Genome genome : genomes) {
            if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
                genome = mutateGenomeRoute(genome);
            }
            mutatedGenomes.add(genome);
        }
        return mutatedGenomes;
    }

    public ArrayList<Genome> mutatePopulationGlobal(ArrayList<Genome> genomes, double mutationRate) {
        ArrayList<Genome> mutatedGenomes = new ArrayList<>();

        for (Genome genome : genomes) {
            if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
                genome = mutateGenomeGlobal(genome);
            }
            mutatedGenomes.add(genome);
        }
        return mutatedGenomes;
    }

    public Route mutateRoute(Route route) {
        Route newRoute = new Route(new ArrayList<>(route.getNodes()), route.getData());
        ArrayList<Integer> nodes = newRoute.getNodes();

        List<Integer> indexes = shuffledIndices(nodes.size());
        Collections.swap(nodes, indexes.get(0), indexes.get(1));

        return newRoute;
    }

    public Genome mutateGenomeRoute(Genome genome) {

        Genome newGenome = new Genome(new ArrayList<>(genome.getGenome()));

        if (newGenome.getGenome().size() <= 3) {
            return newGenome;
        }

        int index = ThreadLocalRandom.current().nextInt(0, newGenome.getGenome().size());
        Route route = newGenome.getGenome().get(index);

        Route mutatedRoute = mutateRoute(route);
        newGenome.getGenome().set(index, mutatedRoute);

        return newGenome;
    }

    public Genome mutateGenomeGlobal(Genome genome) {
        Genome newGenome = new Genome(new ArrayList<>(genome.getGenome()));
        ArrayList<Route> routes = newGenome.getGenome();

        if (routes.size() <= 1) {
            return newGenome;
        }

        // find a source customer to swap
        List<Integer> indices = shuffledIndices(routes.size());

        Route source = routes.get(indices.get(0));
        Route newSource = new Route(new ArrayList<>(source.getNodes()), source.getData());

        Route target = routes.get(indices.get(1));
        Route newTarget = new Route(new ArrayList<>(target.getNodes()), target.getData());

        int indexToSwap = ThreadLocalRandom.current().nextInt(1, source.getNodes().size() - 1);
        int customerToSwap = source.getNodes().get(indexToSwap);

        int targetIndexToSwap = ThreadLocalRandom.current().nextInt(1, target.getNodes().size() - 1);
        int targetCustomerToSwap = target.getNodes().get(targetIndexToSwap);

        newSource.getNodes().set(indexToSwap, targetCustomerToSwap);
        newTarget.getNodes().set(targetIndexToSwap, customerToSwap);

        if (newSource.getRouteLoad() <= newSource.getRouteLoadCapacity()
                & newTarget.getRouteLoad() <= newTarget.getRouteLoadCapacity()) {
            newGenome.getGenome().set(indices.get(0), newSource);
            newGenome.getGenome().set(indices.get(1), newTarget);
        }

        return newGenome;
    }

}
