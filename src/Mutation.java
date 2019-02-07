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
        System.out.println(route.getNodes());
        Route newRoute = new Route(route);
        ArrayList<Integer> nodes = newRoute.getNodes();

        List<Integer> indexes = shuffledIndices(nodes.size());
        Collections.swap(nodes, indexes.get(0), indexes.get(1));

        return newRoute;
    }

    public Genome mutateGenomeRoute(Genome genome) {

        Genome newGenome = new Genome(genome);

        int index = ThreadLocalRandom.current().nextInt(0, newGenome.getGenome().size());
        Route route = newGenome.getGenome().get(index);

        if (route.getNodes().size() <= 3) {
            System.out.println("Size less than three");
        } else {
            Route mutatedRoute = mutateRoute(route);
            newGenome.getGenome().set(index, mutatedRoute);
        }

        return newGenome;
    }

    public Genome mutateGenomeGlobal(Genome genome) {
        Genome newGenome = new Genome(genome);
        ArrayList<Route> routes = newGenome.getGenome();

        if (routes.size() <= 1) {
            return newGenome;
        }

        // find a source customer to swap
        List<Integer> indices = shuffledIndices(routes.size());

        Route source = routes.get(indices.get(0));
        Route newSource = new Route(source);

        Route target = routes.get(indices.get(1));
        Route newTarget = new Route(target);

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
