import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class GenomeGenerator {


    private ProblemData  data;
    private ArrayList<Integer> unvisitedCustomers;
    private ArrayList<Integer> depotStartCap;
    private ArrayList<Integer> depotEndCap;

    public GenomeGenerator(ProblemData data) {
        this.data = data;
    }

    private void prepareData(){

        ArrayList<Integer> unvisitedCustomers = new ArrayList<>();
        ArrayList<Integer> depotStartCap = new ArrayList<>();
        ArrayList<Integer> depotEndCap = new ArrayList<>();

        for (int i = 0 ; i < data.getNumCustomers() ; i++){
            unvisitedCustomers.add(i);
        }

        for (int i = 0 ; i < data.getNumDepots() ; i++){
            depotStartCap.add(data.getNumVehicles());
            depotEndCap.add(0);
        }

        this.unvisitedCustomers = unvisitedCustomers;
        this.depotStartCap = depotStartCap;
        this.depotEndCap = depotEndCap;
    }


    public Genome generateGenome(){
        prepareData();
        ArrayList<Route> nodes = new ArrayList<>();

        while (!unvisitedCustomers.isEmpty()){ //creates routes for visiting all customers
            Route route = createRoute();
            nodes.add(route);
        }
        Genome genome = new Genome(nodes);

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

            int load = getCustomerDemand(randomCustomer); //4 is the load index of data

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

    private int getCustomerDemand(int customerIndex){
        return data.getCustomerData().get(customerIndex).get(4); //4 is index of the demand for customer
    }
}
