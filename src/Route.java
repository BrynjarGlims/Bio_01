import java.util.ArrayList;
import java.util.List;

public class Route {

    private ArrayList<Integer> nodes;
    private ProblemData data;

    public Route(ArrayList<Integer> nodes, ProblemData data) {
        this.nodes = nodes;
        this.data = data;
    }

    public ArrayList<Integer> getNodes(){
        return nodes;
    }

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
    
    public double nodeDistance(List<Integer> node1, List<Integer> node2){ //euclidean distance between nodes
        int x1 = node1.get(1);
        int y1 = node1.get(2);

        int x2 = node2.get(1);
        int y2 = node2.get(2);
        return Math.sqrt(Math.pow(x1 - x2, 2)+ Math.pow(y1 - y2, 2));
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
