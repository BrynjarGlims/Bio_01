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

        System.setProperty("gs.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

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

    public void visualize(ArrayList<List<Integer>> customers, ArrayList<List<Integer>> depots, ArrayList<Route> genome) {
        addArrayToGraph(customers, "customer");
        addArrayToGraph(depots, "depot");

        addRoutes(genome);

        graph.display(false);

    }

    public static void main(String[] args) {
        GraphVisualization graph = new GraphVisualization();
        ProblemData f = new ProblemData();

        String path = "input/p69";

        f.readFile(path);


        GenomeGenerator genomeGenerator = new GenomeGenerator(path);

        ArrayList<Route> genome_data = genomeGenerator.generateGenome().getGenome();


        Genome genome1 = genomeGenerator.generateGenome();
        Genome genome2 = genomeGenerator.generateGenome();

        Double fitness1 = genome1.fitness();
        Double fitness2 = genome2.fitness();

        System.out.println(genome_data);


        ArrayList<Genome> population = new ArrayList<>(Arrays.asList(genome1, genome2));
        ArrayList<Genome> sus = Selection.stochasticUniversalSampling(population, 100);
        ArrayList<Genome> elite = Selection.elitism(population, 2);
        ArrayList<Genome> sa = Selection.stochasticAcceptance(population, 10);

        graph.visualize(f.getCustomerData(), f.getDepotData(), genome_data);

    }
}
