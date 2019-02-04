import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    private ArrayList<ArrayList<Integer>> genome;
    private ProblemData  data = new ProblemData();
    private ArrayList<Integer> unvisitedCustomers = new ArrayList<>();
    private ArrayList<Integer> depotStartCap = new ArrayList<>();
    private ArrayList<Integer> depotEndCap = new ArrayList<>();


    public Genome(String path){
        data.readFile(path);
        this.genome = generateGenome();
    }


    public ArrayList<ArrayList<Integer>> generateGenome(){
        for (int i = 0 ; i < data.getNumCustomers() ; i++){
            unvisitedCustomers.add(i);
        }

        for (int i = 0 ; i < data.getNumDepots() ; i++){
            depotStartCap.add(data.getNumVehicles());
            depotEndCap.add(0);
        }
        ArrayList<ArrayList<Integer>> genome = new ArrayList<>();

        while (!unvisitedCustomers.isEmpty()){ //creates routes for visiting all customers
            ArrayList<Integer> route = createRoute();
            genome.add(route);
        }
        return genome;

    }
    private ArrayList<Integer> createRoute(){ //creates a route for a single vehicle, starting and ending at depot
        ArrayList<Integer> route = new ArrayList<>();
        route.add(selectRandomStartDepot());


        int cap = data.getMaxLoads().get(route.get(0)-data.getNumCustomers()); //max load of the current vehicle
        int currentLoad = 0;
        while (currentLoad <= cap && unvisitedCustomers.size() > 0){ //creates a route for a single vehicle
            int randomCustomerIndex = ThreadLocalRandom.current().nextInt(0, unvisitedCustomers.size());
            int randomCustomer = unvisitedCustomers.get(randomCustomerIndex);

            int load = data.getCustomerData().get(randomCustomer).get(4); //4 is the load index of data

            //Adds the customer to the route, and the load needed for that customer to total load
            if (currentLoad + load <= cap){ //checks if adding load capacity
                route.add(randomCustomer + 1); //one indiced
                unvisitedCustomers.remove(randomCustomerIndex);
                currentLoad += load;
            }
            else {break;}
        }
        route.add(selectRandomEndDepot());
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


    public static void main(String[] args){
        Genome g = new Genome("input/p01");


        for (ArrayList<Integer> a : g.getGenome()){
            System.out.println(a);
        }

    }

    public ArrayList<ArrayList<Integer>> getGenome(){
        return this.genome;
    }


}
