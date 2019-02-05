import java.util.ArrayList;

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


        ArrayList<Integer> tempRoute = new ArrayList<>(getNodes());
        tempRoute.set(0, tempRoute.get(0) - data.getNumCustomers());
        tempRoute.set(-1, tempRoute.get(tempRoute.size()-1) - data.getNumCustomers());
        for (int i = 1 ; i < nodes.size() - 2 ; i++){

        }

        return distance;
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
