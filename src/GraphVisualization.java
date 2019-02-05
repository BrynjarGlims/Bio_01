import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GraphVisualization {

    private Graph graph;
    private HashMap<Integer, String> color_map = new HashMap<>();

    private String STYLESHEET =
            "graph {" +
            "   fill-color: #eeeeee;" +
            "}" +
            "node {" +
            "   size: 7px;" +
            "}" +
            "node.depot {" +
            "   size: 10px;" +
            "}";

    private String[] COLORS = {"blue", "green", "red", "cyan", "purple", "magenta", "orange"};

    public GraphVisualization() {
        graph = new MultiGraph("Network");

        graph.addAttribute("ui.stylesheet", STYLESHEET);

    }

    private void addArrayToGraph(ArrayList<List<Integer>> array, String type) {
        for (List<Integer> element : array) {
            int id = element.get(0) - 1;
            int x = element.get(1);
            int y = element.get(2);

            Node node = graph.addNode(Integer.toString(id));
            node.setAttribute("xyz", x, y);
            node.addAttribute("ui.class", type);

            if (type.equals("depot")) {
                // set unique color for each depot. Keep the colors for corresponding routes
                color_map.putIfAbsent(id, COLORS[color_map.size()]);
                System.out.println(color_map);
                String color = color_map.get(id);
                System.out.println(id);
                node.setAttribute("ui.style", String.format("fill-color: %s;", color));
            }
        }
    }

    private void addRoutes(ArrayList<ArrayList<Integer>> genome) {
        for (ArrayList<Integer> route : genome) {
            addRoute(route);
        }
    }

    private void addRoute(ArrayList<Integer> route) {
        int starting_depot = route.get(0);

        // get color for starting depot from color map
        System.out.println(starting_depot);
        String color = color_map.get(starting_depot);

        for (int i = 0; i < route.size() - 1; i++) {
            int from = route.get(i);
            int to = route.get(i+1);
            String edge_id = from + "-" + to;
            Edge edge = graph.addEdge(edge_id, from, to);
            edge.setAttribute("ui.style", String.format("fill-color: %s;", color));
        }
    }

    public void visualize(ArrayList<List<Integer>> customers, ArrayList<List<Integer>> depots, ArrayList<ArrayList<Integer>> genome) {
        addArrayToGraph(customers, "customer");
        addArrayToGraph(depots, "depot");

        addRoutes(genome);

        Viewer viewer = graph.display();
        viewer.disableAutoLayout();

    }


    public static void main(String[] args) {
        GraphVisualization graph = new GraphVisualization();
        ProblemData f = new ProblemData();

        //ArrayList<List<Integer>> genome = new ArrayList<>();

        //List<Integer> route = new ArrayList<>(Arrays.asList(51, 1, 2, 3, 51));
        //List<Integer> route2 = new ArrayList<>(Arrays.asList(52, 5, 16, 34, 51));
        //genome.add(route);
        //genome.add(route2);

        f.readFile("input/p69");

        Genome genome = new Genome("input/p69");

        ArrayList<ArrayList<Integer>> genome_data = genome.getGenome();
        System.out.println(genome_data);

        graph.visualize(f.getCustomerData(), f.getDepotData(), genome_data);

    }
}
