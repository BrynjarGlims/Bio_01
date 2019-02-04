import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class GraphVisualization {

    private Graph graph;

    private String STYLESHEET =
            "graph {" +
            "   fill-color: #eeeeee;" +
            "}" +
            "node {" +
            "   size: 7px;" +
            "}" +
            "node.depot {" +
            "   size: 10px;" +
            "   fill-color: blue;" +
            "}";

    private String[] COLORS = {"rgb(255,0,0)", "rgb(2,63,165)", "rgb(125,135,185)", "rgb(190,193,212)",
            "rgb(214,188,192)", "rgb(187,119,132)", "rgb(142,6,59)", "rgb(74,111,227)", "rgb(133,149,225)",
            "rgb(181,187,227)", "rgb(230,175,185)", "rgb(224,123,145)", "rgb(211,63,106)", "rgb(17,198,56)",
            "rgb(141,213,147)", "rgb(198,222,199)", "rgb(234,211,198)", "rgb(240,185,141)", "rgb(239,151,8)",
            "rgb(15,207,192)", "rgb(156,222,214)", "rgb(213,234,231)", "rgb(243,225,235)", "rgb(246,196,225)",
            "rgb(247,156,212)"};

    public GraphVisualization() {
        graph = new MultiGraph("Network");

        graph.addAttribute("ui.stylesheet", STYLESHEET);

    }

    private void addArrayToGraph(ArrayList<List<Integer>> array, String type) {
        for (List<Integer> element : array) {
            int id = element.get(0);
            int x = element.get(1);
            int y = element.get(2);

            Node node = graph.addNode(Integer.toString(id));
            node.setAttribute("xyz", x, y);
            node.addAttribute("ui.class", type);
        }
    }

    private void addRoutes(ArrayList<ArrayList<Integer>> genome) {
        for (int i = 0; i < genome.size(); i++) {
            addRoute(genome.get(i), i);
        }
    }

    private void addRoute(ArrayList<Integer> route, int index) {
        for (int i = 0; i < route.size() - 1; i++) {
            int from = route.get(i);
            int to = route.get(i+1);
            String edge_id = from + "-" + to;
            Edge edge = graph.addEdge(edge_id, from, to);
            edge.setAttribute("ui.style", String.format("fill-color: %s;", COLORS[index]));
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
