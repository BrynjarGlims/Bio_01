import java.util.ArrayList;
import java.util.List;

public class Route {

    private ArrayList<Integer> nodes;
    private ProblemData data;
    private int durationCap;

    public Route(ArrayList<Integer> nodes, ProblemData data) {
        this.nodes = nodes;
        this.data = data;
        this.durationCap = data.getMaxDurations().get((nodes.get(0) - data.getNumCustomers()));

    }

    public ArrayList<Integer> getNodes(){return nodes;}

    public double routeDistance(){
        double distance = 0;

        //first depot to first customer
        distance += nodeDistance(data.getDepotData().get(nodes.get(0) - data.getNumCustomers()),
                                data.getCustomerData().get(nodes.get(1)));


        //customer to customer
        for (int i = 1 ; i < nodes.size() - 2 ; i++){
            distance += nodeDistance(data.getCustomerData().get(nodes.get(i)),
                                    data.getCustomerData().get(nodes.get(i+1)));
        }
        //last customer to  last depot
        distance += nodeDistance(data.getCustomerData().get(nodes.get(nodes.size()-2)),
                                data.getDepotData().get(nodes.get(nodes.size()-1) - data.getNumCustomers()));

        return distance;
    }
    
    public static double nodeDistance(List<Integer> node1, List<Integer> node2){ //euclidean distance between nodes
        int x1 = node1.get(1);
        int y1 = node1.get(2);

        int x2 = node2.get(1);
        int y2 = node2.get(2);
        return Math.sqrt(Math.pow(x1 - x2, 2)+ Math.pow(y1 - y2, 2));
    }



    public double routeFitness(){
        double fitness = routeDistance();
        if (durationCap != 0) {
            if (fitness > durationCap){
                fitness += Math.pow(fitness-durationCap, 2);
            }
        }
        return  fitness;
    }

    public int getRouteLoad(){
        int load = 0;
        for (int i = 1 ; i < nodes.size() - 1 ; i++){
            load += data.getCustomerData().get(nodes.get(i)).get(4);
        }
        return load;
    }
    public int getRouteLoadCapacity(){
        return data.getMaxLoads().get(nodes.get(0)-data.getNumCustomers());
    }

    public int getCustomerDemand(int customerIndex){
        return data.getCustomerData().get(customerIndex).get(4); //4 is index of the demand for customer
    }

    public boolean hasCapacity(int customerIndex){
        return getRouteLoad() + getCustomerDemand(customerIndex) <= getRouteLoadCapacity();
    }


    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int r : nodes){
            sb.append(r);
            sb.append(" ");
        }
    return sb.toString();
    }
}
