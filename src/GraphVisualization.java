import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

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
                    "   fill-color: #777777;" +
                    "}" +
                    "node.depot {" +
                    "   size: 10px;" +
                    "}";

    private String[] COLORS = {"blue", "green", "red", "cyan", "purple", "magenta", "orange", "brown", "teal", "pink"};

    public GraphVisualization() {
        graph = new MultiGraph("Network");

        graph.addAttribute("ui.stylesheet", STYLESHEET);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

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
            Edge edge = graph.addEdge(edge_id, from, to);
            edge.setAttribute("ui.style", String.format("fill-color: %s;", color));
        }
    }

    public void visualize(ArrayList<List<Integer>> customers, ArrayList<List<Integer>> depots, ArrayList<Route> genome) {
        addArrayToGraph(customers, "customer");
        addArrayToGraph(depots, "depot");

        addRoutes(genome);

        Viewer viewer = graph.display();
        viewer.disableAutoLayout();

    }

    public static void main(String[] args) {
        GraphVisualization graph = new GraphVisualization();
        ProblemData f = new ProblemData();

        //ArrayList<List<Integer>> genomeGenerator = new ArrayList<>();

        //List<Integer> route = new ArrayList<>(Arrays.asList(51, 1, 2, 3, 51));
        //List<Integer> route2 = new ArrayList<>(Arrays.asList(52, 5, 16, 34, 51));
        //genomeGenerator.add(route);
        //genomeGenerator.add(route2);

        String path ="input/p23";
        f.readFile(path);

        GenomeGenerator genomeGenerator = new GenomeGenerator(path);

        ArrayList<Route> genome_data = genomeGenerator.generateGenome().getGenome();
        System.out.println(genome_data);

        graph.visualize(f.getCustomerData(), f.getDepotData(), genome_data);

    }
}
