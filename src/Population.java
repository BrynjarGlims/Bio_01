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



    public ArrayList<Genome> generatePopulation(int populationSize){
        ArrayList<Genome> population = new ArrayList<>();
        for (int i = 0 ; i < populationSize ; i++){
            population.add(generator.generateGenome());
        }
        return population;
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
        Population p = new Population("input/p01", 2);

        ArrayList<Genome> population = p.generatePopulation(64);
        for(int i = 0; i < 10000 ; i++){
            population = p.generatePopulation(10);
        }
        Genome g1 = population.get(0);
        for (List<Integer> d : p.data.getDepotData()){
            if(g1.startDepots(d.get(0)-1) > p.data.getNumVehicles())
            System.out.println("invalid route");
        }
        for (Route r : g1.getGenome()){
            if (r.getRouteLoad() > r.getRouteLoadCapacity())
            System.out.println("invalid route");
        }
        GraphVisualization graph = new GraphVisualization();


        System.out.println(g1.fitness());

        graph.visualize(p.data.getCustomerData(), p.data.getDepotData(), g1.getGenome());


    }




}
