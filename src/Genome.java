import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    private ArrayList<Route> genome;
    private ProblemData  data = new ProblemData();
    private ArrayList<Integer> unvisitedCustomers = new ArrayList<>();
    private ArrayList<Integer> depotStartCap = new ArrayList<>();
    private ArrayList<Integer> depotEndCap = new ArrayList<>();


    public Genome(String path){
        data.readFile(path);
        this.genome = generateGenome();
    }


    private ArrayList<Route> generateGenome(){
        for (int i = 0 ; i < data.getNumCustomers() ; i++){
            unvisitedCustomers.add(i);
        }

        for (int i = 0 ; i < data.getNumDepots() ; i++){
            depotStartCap.add(data.getNumVehicles());
            depotEndCap.add(0);
        }
        ArrayList<Route> genome = new ArrayList<>();

        while (!unvisitedCustomers.isEmpty()){ //creates routes for visiting all customers
            Route route = createRoute();
            genome.add(route);
        }
        return genome;

    }
    private Route createRoute(){ //creates a route for a single vehicle, starting and ending at depot
        ArrayList<Integer> nodes = new ArrayList<>();
        nodes.add(selectRandomStartDepot());


        int cap = data.getMaxLoads().get(nodes.get(0)-data.getNumCustomers()); //max load of the current vehicle
        int currentLoad = 0;
        int stopCondition = 0;
        while (currentLoad <= cap && unvisitedCustomers.size() > 0){ //creates a route for a single vehicle
            int randomCustomerIndex = ThreadLocalRandom.current().nextInt(0, unvisitedCustomers.size());
            int randomCustomer = unvisitedCustomers.get(randomCustomerIndex);

            int load = data.getCustomerData().get(randomCustomer).get(4); //4 is the load index of data

            //Adds the customer to the route, and the load needed for that customer to total load

            if (currentLoad + load <= cap){ //checks if adding load capacity
                nodes.add(randomCustomer); //one indiced

                unvisitedCustomers.remove(randomCustomerIndex);
                currentLoad += load;
            }
            else if(stopCondition < 2) {
                stopCondition += 1;
            }
            else {break;}
        }
        nodes.add(selectRandomEndDepot());
        Route route = new Route(nodes, data);
        return route;
    }


    private int selectRandomStartDepot(){
        int randomNum = ThreadLocalRandom.current().nextInt(0, depotStartCap.size());
        if (depotStartCap.get(randomNum) > 0){
            depotStartCap.set(randomNum, depotStartCap.get(randomNum) - 1);
            return randomNum + data.getNumCustomers(); //depot is indexed after customers
        }
        return selectRandomStartDepot();
    }

    private int selectRandomEndDepot(){
        int randomNum = ThreadLocalRandom.current().nextInt(0, depotEndCap.size());
        if (depotEndCap.get(randomNum) < data.getNumVehicles()){
            depotEndCap.set(randomNum, depotEndCap.get(randomNum) + 1);
            return randomNum + data.getNumCustomers(); //depot is indexed after customers
        }
        return selectRandomEndDepot();
    }

    public double fitness(){
        double fitness = 0;
        for (Route r : genome){
            fitness += r.routeDistance();
        }
        return fitness;
    }


    public static void main(String[] args){
        Genome g = new Genome("input/p01");
        System.out.println(g.fitness());



    }

    public ArrayList<Route> getGenome(){
        return this.genome;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Route r : genome){
            sb.append(r.toString());
            sb.append("\n");
        }
        return sb.toString();
    }


}
