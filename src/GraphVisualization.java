import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GraphVisualization {

    private Graph graph = new MultiGraph("Network");
    private HashMap<Integer, String> color_map = new HashMap<>();

    private String[] COLORS = {
            "#E5198D", "#E9AE01", "#009C94", "#16AA39", "#1678C1", "#9627BA", "#F16301", "#CF1919",
            "#B5CC18", "#975B33", "#5829BB"
    }; // pink, yellow, teal, green, blue, purple, orange, red, olive, brown, violet


    public GraphVisualization() {

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        graph.addAttribute("ui.stylesheet", "url('style.css')");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

    }

    private void addArrayToGraph(ArrayList<List<Integer>> array, String type) {
        for (List<Integer> element : array) {
            int id = element.get(0) - 1;
            int x = element.get(1);
            int y = element.get(2);

            Node node = graph.addNode(Integer.toString(id));
            node.setAttribute("xy", x, y);
            node.addAttribute("ui.class", type);
            node.addAttribute("ui.label", id);

            if (type.equals("depot")) {
                // set unique color for each depot. Keep the colors for corresponding routes
                color_map.putIfAbsent(id, COLORS[color_map.size()]);
                String color = color_map.get(id);
                node.setAttribute("ui.style", String.format("fill-color: %s;", color));
            }
        }
    }

    private void addRoutes(ArrayList<Route> genome) {
        for (Route route : genome) {
            addRoute(route);
        }
    }

    private void addRoute(Route route) {
        List<Integer> nodes = route.getNodes();

        int starting_depot = nodes.get(0);

        // get color for starting depot from color map
        String color = color_map.get(starting_depot);

        for (int i = 0; i < nodes.size() - 1; i++) {
            int from = nodes.get(i);
            int to = nodes.get(i+1);
            String edge_id = from + "-" + to;
            Edge edge = graph.addEdge(edge_id, from, to, true);
            edge.setAttribute("ui.style", String.format("fill-color: %s;", color));
        }
    }

    public void visualize(ProblemData data, Genome genome) {

        // ArrayList<List<Integer>> customers, ArrayList<List<Integer>> depots, ArrayList<Route> genome

        ArrayList<List<Integer>> customers = data.getCustomerData();
        ArrayList<List<Integer>> depots = data.getDepotData();

        addArrayToGraph(customers, "customer");
        addArrayToGraph(depots, "depot");

        addRoutes(genome.getGenome());

        graph.display(false);

    }

    public void visualize(ProblemData data) {
        ArrayList<List<Integer>> customers = data.getCustomerData();
        ArrayList<List<Integer>> depots = data.getDepotData();

        addArrayToGraph(customers, "customer");
        addArrayToGraph(depots, "depot");

        graph.display(false);

    }

    public static void main(String[] args) {
    }
}
