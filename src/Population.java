import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Population {

    private GenomeGenerator generator;
    private ArrayList<Genome> population = new ArrayList<>();
    private ProblemData data;

    public Population(String path, int populationSize) {
        this.generator = new GenomeGenerator(path);
        this.data = generator.getData();
        for (int i = 0 ; i < populationSize ; i++){
            population.add(generator.generateGenome());
        }
    }

    public Genome bestCostRouteCrossver(Genome genome1, Genome genome2){

        ArrayList<Genome> newPopulation = new ArrayList<>();

        //Randomly select a route from each genome
        Route randomRoute1 = genome1.randomRoute();
        Route randomRoute2 = genome2.randomRoute();

        //remove depot at start and end
        List<Integer> removedRoute1 = randomRoute1.getNodes().subList(1, randomRoute1.getNodes().size()-1);
        List<Integer> removedRoute2 = randomRoute2.getNodes().subList(1, randomRoute2.getNodes().size()-1);

        //create next generation genomes
        Genome newGenome1 = new Genome(genome1.getGenome());
        Genome newGenome2 = new Genome(genome2.getGenome());

        //remove the nodes selected in genome A from B, and vice versa

        Iterator<Integer> iterator1 = removedRoute1.iterator();
        while(iterator1.hasNext()){
            int i = iterator1.next();
            removeCustomer(newGenome2,i);

        }


        //add the removed nodes back in again at optimal place according to fitness
        Genome nextGen =insertRemovedNodes(removedRoute1, newGenome2);
        ArrayList<Route> newGenome = new ArrayList<>();
        for (int i = 0 ; i < nextGen.getGenome().size() ; i++){
            if (nextGen.getGenome().get(i).getNodes().size() != 2){
                newGenome.add(nextGen.getGenome().get(i));
            }
        }
        Genome out = new Genome(newGenome);


        return out;
    }


    private Genome insertRemovedNodes(List<Integer> removed, Genome genome){
        Iterator<Integer> iterator1 = removed.iterator();
        System.out.println(removed.size());
        while(iterator1.hasNext()){
            int customer = iterator1.next();

            Map nearestDepot = nearestDepot(customer, genome);
            Route optRoute = null;
            int optCol = 0;
            double fitness = (double) nearestDepot.get("distance");

            Iterator<Route> iterator2 = genome.getGenome().iterator();
            while(iterator2.hasNext()){
                Route r = iterator2.next();
                for (int j = 1 ; j < r.getNodes().size() - 1 ; j++){
                    int dist = 0;
                    if (r.hasCapacity(customer)) {

                        genome.insertNode(r, j, customer);

                        if (j == 1) {
                            dist += Route.nodeDistance(data.getDepotData().get(r.getNodes().get(j - 1)-data.getNumCustomers()),
                                    data.getCustomerData().get(customer));
                        }
                        else {
                            dist += Route.nodeDistance(data.getCustomerData().get(r.getNodes().get(j - 1)),
                                    data.getCustomerData().get(customer));
                        }
                        if(j == r.getNodes().size() - 2){
                            dist += Route.nodeDistance(data.getDepotData().get(r.getNodes().get(j + 1)-data.getNumCustomers()),
                                    data.getCustomerData().get(customer));
                        }
                        else{
                            dist += Route.nodeDistance(data.getCustomerData().get(r.getNodes().get(j + 1)),
                                    data.getCustomerData().get(customer));
                        }
                        if(dist < fitness){
                            optRoute = r;
                            optCol = j;
                            fitness = dist;
                        }
                        genome.removeNode(r, j);
                    }
                }
            }
            if (fitness == (double) nearestDepot.get("distance")) {
                System.out.println("added Route");
                Route route = createRoute((int)nearestDepot.get("depot") - data.getNumCustomers(), customer);
                genome.addRoute(route);

                System.out.println(genome.getNumNodes());
                }
            else {
                System.out.println("inserted node");
                genome.insertNode(optRoute, optCol, customer);

                System.out.println(genome.getNumNodes());
            }

        }
        return genome;

    }

    private Map nearestDepot(int customer, Genome genome){
        double nearestDebot = Double.POSITIVE_INFINITY;
        int depotNumber = -1;
        Iterator<List<Integer>> iterator = data.getDepotData().iterator();
        while(iterator.hasNext()){
            List<Integer> depot = iterator.next();
            double dist = 0;
            if (genome.startDepots(depot.get(0)) < data.getNumVehicles()){
                dist += Route.nodeDistance(data.getCustomerData().get(customer),depot) * 2;
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

    private int startDepot(Genome genome){
        int startDepot = data.getNumCustomers() + ThreadLocalRandom.current().nextInt(0, data.getNumDepots());
        if (genome.startDepots(startDepot) < data.getNumVehicles()){
            return startDepot;
        }
        return startDepot(genome);
    }


    private int endDepot(Genome genome){
        int endDepot = data.getNumCustomers() + ThreadLocalRandom.current().nextInt(0, data.getNumDepots());
        if (genome.endDepots(endDepot) < data.getNumVehicles()){
            return endDepot;
        }
        return endDepot(genome);
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

    public ArrayList<ArrayList<Integer>> addCustomer(ArrayList<ArrayList<Integer>> input, int customer, int row, int col){
        input.get(row).set(col,customer);
        return input;
    }


    private int getRandomCustomer(ArrayList<Integer> remainingCustomers){
        int randomCustomerIndex = ThreadLocalRandom.current().nextInt(0, remainingCustomers.size());
        return remainingCustomers.get(randomCustomerIndex);
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

    private int findCustomerAtLoc(int row, int col, Genome genome){
        return genome.getGenome().get(row).getNodes().get(col);
    }

    public ArrayList<Genome> getPopulation(){
        return population;
    }

    public List<Integer> shuffledIndices(int n) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < n; i++) indexes.add(i);
        Collections.shuffle(indexes);
        return indexes;
    }

    public Route mutateRoute(Route route) {
        Route newRoute = new Route(route.getNodes(), route.getData());
        ArrayList<Integer> nodes = newRoute.getNodes();

        List<Integer> indexes = shuffledIndices(nodes.size());
        Collections.swap(nodes, indexes.get(0), indexes.get(1));

        return newRoute;
    }

    public Genome mutateGenomeRoute(Genome genome) {
        Genome newGenome = new Genome(genome.getGenome());
        int index = (int) (newGenome.getGenome().size() * ThreadLocalRandom.current().nextDouble());
        Route route = newGenome.getGenome().get(index);

        Route mutatedRoute = mutateRoute(route);
        newGenome.getGenome().set(index, mutatedRoute);

        return newGenome;
    }

    public Genome mutateGenome(Genome genome) {
        Genome newGenome = new Genome(genome.getGenome());
        ArrayList<Route> routes = newGenome.getGenome();

        if (routes.size() <= 1) {
            return newGenome;
        }

        // find a source customer to swap
        List<Integer> indices = shuffledIndices(routes.size());

        Route source = routes.get(indices.get(0));
        Route newSource = new Route(source.getNodes(), source.getData());

        Route target = routes.get(indices.get(1));
        Route newTarget = new Route(target.getNodes(), target.getData());

        int indexToSwap = ThreadLocalRandom.current().nextInt(0, source.getNodes().size());
        int customerToSwap = source.getNodes().get(indexToSwap);

        int targetIndexToSwap = ThreadLocalRandom.current().nextInt(0, target.getNodes().size());
        int targetCustomerToSwap = target.getNodes().get(targetIndexToSwap);

        if (newSource.hasCapacity(targetCustomerToSwap) & newTarget.hasCapacity(customerToSwap)) {
            newSource.getNodes().set(indexToSwap, targetCustomerToSwap);
            newTarget.getNodes().set(targetIndexToSwap, customerToSwap);
        }

        newGenome.removeRoute(0);
        newGenome.removeRoute(1);
        newGenome.addRoute(newSource);
        newGenome.addRoute(newTarget);

        return newGenome;
    }

    public static void main(String[] args){
        Population p = new Population("input/p10", 2);

        Genome g1 = p.getPopulation().get(0);
        Genome g2 = p.getPopulation().get(1);

        Genome nextGen = p.bestCostRouteCrossver(g1,g2);


        GraphVisualization graph = new GraphVisualization();

        System.out.println(g2);

        System.out.println(nextGen);

    graph.visualize(p.data.getCustomerData(), p.data.getDepotData(), nextGen.getGenome());


    }




}
