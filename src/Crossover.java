import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Crossover {

    private ProblemData data;

    public Crossover(ProblemData data){
        this.data = data;
    }

    public ArrayList<Genome> generateNextGeneration(ArrayList<Genome> prevGen, double crossoverRate){
        ArrayList<Genome> nextGen = new ArrayList<>();
        int size = prevGen.size();
        for (int i = 0 ; i < size ; i+= 2){
            Genome g1 = prevGen.remove(ThreadLocalRandom.current().nextInt(0, prevGen.size()));
            Genome g2 = prevGen.remove(ThreadLocalRandom.current().nextInt(0, prevGen.size()));
            if (ThreadLocalRandom.current().nextDouble(0,1) < crossoverRate) {
                for (Genome g : bestCostRouteCrossover(g1, g2)) {
                    nextGen.add(g);
                }
            }
            else{
                nextGen.add(g1);
                nextGen.add(g2);
            }
        }

        // set last depot to closest one
        for (Genome g : nextGen) {
            setNearestDepotEnd(g);
        }

        return nextGen;
    }

    public ArrayList<Genome> bestCostRouteCrossover(Genome genome1, Genome genome2){
        ArrayList<Genome> genomes = new ArrayList<>();

        Genome g1 = (twoGenomeCrossover(genome1, genome2));
        genomes.add(g1);
        genomes.add(twoGenomeCrossover(genome2, genome1));
        return genomes;
    }

    public Genome twoGenomeCrossover(Genome genome1, Genome genome2){
        //Randomly select a route from genome
        Route randomRoute;
        if (ThreadLocalRandom.current().nextDouble(0,1) < 0.8){
            randomRoute = genome2.randomRoute();
        }
        else{
            randomRoute = genome2.longestRoute();
        }
        //remove depot at start and end
        int startDepot = randomRoute.getNodes().get(0);
        int endDepot = randomRoute.getNodes().get(randomRoute.getNodes().size() - 1);

        List<Integer> removedRoute1 = randomRoute.getNodes().subList(1, randomRoute.getNodes().size()-1);

        //create next generation genomes
        Genome newGenome = new Genome(genome1);

        // Try to crossover. If one customer can't find a depot, abort.
        try {
            //replace start and end depot
            replaceDepot(startDepot,endDepot, newGenome);

            //remove the nodes selected in genome A from B, and vice versa
            Iterator<Integer> iterator1 = removedRoute1.iterator();
            while(iterator1.hasNext()) {
                int i = iterator1.next();
                removeCustomer(newGenome, i);
            }

            //add the removed nodes back in again at optimal place according to fitness
            insertRemovedNodes(removedRoute1, newGenome);

            //remove routes that has become empty
            for (int i = 0 ; i < newGenome.getGenome().size() ; i++){
                if (newGenome.getGenome().get(i).getNodes().size() == 2){
                    newGenome.removeRoute(i);
                }
            }
        }
        catch (Exception e) {
            return new Genome(genome1);
        }

        return newGenome;
    }

    private void replaceDepot(int startDepot, int endDepot, Genome genome) {
        if (genome.startDepots(startDepot) < data.getNumVehicles()) {
            double dist = Double.POSITIVE_INFINITY;
            Route route = null;


            Iterator<Route> iterator1 = genome.getGenome().iterator();
            while (iterator1.hasNext()) {
                Route r = iterator1.next();
                double tempDistance = r.nodeDistance(startDepot, r.getNodes().get(1));

                if (tempDistance < dist) {
                    dist = tempDistance;
                    route = r;
                }
            }
            genome.replaceNode(route, 0, startDepot);
        }


        double dist = Double.POSITIVE_INFINITY;
        Route route = null;
        Iterator<Route> iterator1 = genome.getGenome().iterator();
        while (iterator1.hasNext()) {
            Route r = iterator1.next();
            int depotIndex = r.getNodes().size();
            double tempDistance = r.nodeDistance(endDepot, r.getNodes().get(depotIndex - 2));
            if (tempDistance < dist) {

                dist = tempDistance;
                route = r;
            }
        }
        genome.replaceNode(route, route.getNodes().size() - 1, endDepot);
    }

    private void insertRemovedNodes(List<Integer> removed, Genome genome){
        Iterator<Integer> iterator1 = removed.iterator();
        while(iterator1.hasNext()){
            int customer = iterator1.next();

            Map nearestDepot = nearestDepot(customer, genome);
            Route optRoute = null;
            int optCol = 0;
            double fitness = (double) nearestDepot.get("distance") + genome.fitness(false);

            Iterator<Route> iterator2 = genome.getGenome().iterator();
            while(iterator2.hasNext()){
                Route r = iterator2.next();
                for (int j = 1 ; j < r.getNodes().size() ; j++){
                    double dist = 0;
                    if (r.hasCapacity(customer)) {

                        genome.insertNode(r, j, customer);
                        dist = genome.fitness(false);

                        if(dist < fitness){
                            optRoute = r;
                            optCol = j;
                            fitness = dist;
                        }
                        genome.removeNode(r, j);
            } } }
            if (fitness == (double) nearestDepot.get("distance")+ genome.fitness(false)) {
                Route route = createRoute((int)nearestDepot.get("depot") - data.getNumCustomers(), customer);
                genome.addRoute(route);
            }
            else {
                genome.insertNode(optRoute, optCol, customer);
            }
        }
    }

    private Map nearestDepot(int customer, Genome genome){
        double nearestDebot = Double.POSITIVE_INFINITY;
        int depotNumber = -1;
        Iterator<List<Integer>> iterator = data.getDepotData().iterator();
        while(iterator.hasNext()){
            List<Integer> depot = iterator.next();
            double dist = 0;
            if (genome.startDepots(depot.get(0)-1) < data.getNumVehicles()){
                dist += genome.getGenome().get(0).nodeDistance(customer,depot.get(0)-1) * 2;
                if (dist < nearestDebot){
                    depotNumber = depot.get(0)+data.getNumCustomers()-1;
                    nearestDebot = dist;
                }
            }
        }
        Map out = new HashMap();
        out.put("distance", nearestDebot);
        out.put("depot", depotNumber);
        return out;
    }


    public Route createRoute(int depot, int customer){
        ArrayList<Integer> nodes = new ArrayList<>();
        nodes.add(depot);
        nodes.add(customer);
        nodes.add(depot);
        return new Route(nodes, data);
    }

    public Genome removeCustomer(Genome genome, int customer){
        int route = findCustomerInGenome(customer, genome);
        int c = findCustomerInRoute(customer, route, genome);
        genome.getGenome().get(route).getNodes().remove(c);
        return genome;
    }


    private int findCustomerInGenome(int customer, Genome genome) {
        for (int i = 0; i < genome.getGenome().size(); i++) {
            if (genome.getGenome().get(i).getNodes().contains(customer)) {
                return i;
            }
        }
        return -1;
    }

    private int findCustomerInRoute(int customer, int r, Genome g){
        return g.getGenome().get(r).getNodes().indexOf(customer);
    }

    private void setNearestDepotEnd(Genome genome) {
        for (Route r : genome.getGenome()) {
            int nearestDepot = nearestDepot(r);

            int position = r.getNodes().size() - 1;

            if (r.getNodes().get(position) != nearestDepot) {
                r.getNodes().set(position, nearestDepot);
                r.updateFitness();
            }
        }
    }

    private int nearestDepot(Route route) {
        double minDistance = Double.POSITIVE_INFINITY;
        int bestDepotId = -1;

        Iterator<List<Integer>> iterator = this.data.getDepotData().iterator();

        int customerId = route.getNodes().get(route.getNodes().size() - 2);
        int depotId;

        while (iterator.hasNext()) {
            List<Integer> depot = iterator.next();
            depotId = depot.get(0) - 1;
            double dist = route.nodeDistance(customerId, depotId);
            if (dist < minDistance) {
                minDistance = dist;
                bestDepotId = depotId;
            }
        }
        return bestDepotId;
    }
}
