import java.util.*;
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

        return new Genome(nodes);

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
        return new Route(nodes, data);
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

        return randomNum + data.getNumCustomers(); //depot is indexed after customers

    }

    private int getCustomerDemand(int customerIndex){
        return data.getCustomerData().get(customerIndex).get(4); //4 is index of the demand for customer
    }

    public void generateClusteredGenome() {
        prepareData();
        ArrayList<List<Integer>> customers = data.getCustomerData();
        ArrayList<List<Integer>> depots = data.getDepotData();
        ArrayList<List<Integer>> customerCopy = new ArrayList<>();

        Map<Integer, List<Double>> customerToDepotMap = customerToDepotDistance();

        for (List<Integer> customer : customers) {
            customerCopy.add(new ArrayList<>(customer));
        }

        Collections.shuffle(customerCopy);

        int customerId;
        int depotId;

        for (List<Integer> customer : customerCopy) {
            customerId = customer.get(0) - 1;
            List<Double> nearestDepots = customerToDepotMap.get(customerId);

            for (List<Integer> depot : depots) {
                depotId = depot.get(0) - 1;
            }
        }
        // Map customer distance to all depots
        // shuffle customers
        // for each customer:
        // choose closest depot.
        // for each route: try to add customer to route
        // else: routeCap reached ?
        //  no : add new route. add customer to route
        //  yes : try next depot.
    }

    private Map<Integer, List<Double>> customerToDepotDistance(){
        List<Double> distances;
        List<Integer> sortedDepots = new ArrayList<>();

        List<Integer> customerData;
        List<Integer> depotData;
        int customerId;
        int depotId;
        Map<Integer, List<Double>> customerToDepot = new HashMap<>();

        for (int i = 0; i < data.getNumCustomers(); i++) {
            customerData = data.getCustomerData().get(i);
            customerId = customerData.get(0) - 1;

            distances = new ArrayList<>();

            for (int j = 0; j < data.getNumDepots(); j++) {
                // add distance from customer to depot
                depotData = data.getDepotData().get(j);
                depotId = depotData.get(0) - 1;
                distances.add(nodeDistance(customerId, depotId));
                sortedDepots.add(depotId);
            }
            customerToDepot.put(customerId, distances);
        }



        return customerToDepot;
    }

    private double nodeDistance(int id1, int id2){ //euclidean distance between nodes
        List<Integer> node1 = getData(id1);
        List<Integer> node2 =getData(id2);
        int x1 = node1.get(1);
        int y1 = node1.get(2);

        int x2 = node2.get(1);
        int y2 = node2.get(2);
        return Math.sqrt(Math.pow(x1 - x2, 2)+ Math.pow(y1 - y2, 2));
    }

    private List<Integer> getData(int id){
        List<Integer> node;
        if(id < data.getNumCustomers()){
            node = data.getCustomerData().get(id);
        }
        else{
            node = data.getDepotData().get(id - data.getNumCustomers());
        }
        return node;
    }

    public static void main(String[] args) {
    }
}
