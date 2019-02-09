import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class GraphVisualization {

    private Graph graph = new MultiGraph("Network");
    private HashMap<String, String> color_map = new HashMap<>();
    private ProblemData data;

    private String[] COLORS = {
            "#E5198D", "#E9AE01", "#009C94", "#16AA39", "#1678C1", "#9627BA", "#F16301", "#CF1919",
            "#B5CC18", "#975B33", "#5829BB"
    }; // pink, yellow, teal, green, blue, purple, orange, red, olive, brown, violet


    public GraphVisualization(ProblemData data) {
        this.data = data;

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        graph.addAttribute("ui.stylesheet", "url('style.css')");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

    }

    private void addArrayToGraph(ArrayList<List<Integer>> array, String type) {
        for (List<Integer> element : array) {
            int id = element.get(0);
            int x = element.get(1);
            int y = element.get(2);

            Node node;
            String nodeId;

            if (type.equals("depot")) {
                id = id - data.getNumCustomers();
                nodeId = "d" + (id);
                node = graph.addNode(nodeId);
            } else {
                nodeId = "c" + id;
                node = graph.addNode(nodeId);
            }

            node.setAttribute("xy", x, y);
            node.addAttribute("ui.class", type);
            node.addAttribute("ui.label", id);

            if (type.equals("depot")) {
                // set unique color for each depot. Keep the colors for corresponding routes
                color_map.putIfAbsent(nodeId, COLORS[color_map.size()]);
                String color = color_map.get(nodeId);
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

        String[] ids = new String[nodes.size()];

        ids[0] = "d" + (nodes.get(0) - data.getNumCustomers() + 1);
        ids[nodes.size() - 1] = "d" + (nodes.get(nodes.size() - 1) - data.getNumCustomers() + 1);

        for (int i = 1; i < nodes.size() - 1; i++) {
            ids[i] = "c" + (nodes.get(i) + 1);
        }

        // get color for starting depot from color map
        String color = color_map.get(ids[0]);

        for (int i = 0; i < nodes.size() - 1; i++) {
            String from = ids[i];
            String to = ids[i+1];
            String edge_id = from + "-" + to;
            Edge edge = graph.addEdge(edge_id, from, to, true);
            edge.setAttribute("ui.style", String.format("fill-color: %s;", color));
        }
    }

    public void visualize(Genome genome) {
        ArrayList<List<Integer>> customers = data.getCustomerData();
        ArrayList<List<Integer>> depots = data.getDepotData();

        // customers and depot nodes
        addArrayToGraph(customers, "customer");
        addArrayToGraph(depots, "depot");

        // every route between nodes in the genome
        addRoutes(genome.getGenome());

        graph.display(false);

    }

    public void visualize() {
        ArrayList<List<Integer>> customers = data.getCustomerData();
        ArrayList<List<Integer>> depots = data.getDepotData();

        addArrayToGraph(customers, "customer");
        addArrayToGraph(depots, "depot");

        graph.display(false);
    }

    public static void loadSavedGraph(String path) {
        Graph g = new MultiGraph("graph");
        try {
            g.read(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        g.display(false);
    }

    public void saveGraph(String path) {
        try {
            graph.write(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadSavedGraph("data/graphs/p01.dgs");
    }
}
