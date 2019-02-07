import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

public class GeneticAlgorithm {

    private Population population;
    private ProblemData data = new ProblemData();
    private int populationSize;
    private int numGenerations;
    private int numElite;
    private double crossoverRate;
    private double mutationRateSwapRoute;
    private double mutationRateSwapGlobal;
    private double selectionRate = 1;

    private Crossover crossover;
    private Mutation mutator;

    public GeneticAlgorithm(JSONObject parameters, String dataPath){
        data.readFile(dataPath);
        this.populationSize = parameters.getInt("populationSize");
        this.numGenerations = parameters.getInt("numGenerations");
        this.numElite = parameters.getInt("numElite");
        this.crossoverRate = parameters.getDouble("crossoverRate");
        this.mutationRateSwapRoute = parameters.getDouble("mutationRateSwapRoute");
        this.mutationRateSwapGlobal = parameters.getDouble("mutationRateSwapGlobal");
        this.selectionRate = parameters.getDouble("selectionRate");
        population = new Population(data, populationSize);

        this.crossover = new Crossover(this.data);
        this.mutator = new Mutation();
    }

    public Population nextGeneration() {
        //ELITE
        ArrayList<Genome> elites = new ArrayList<>();
        if (this.numElite > 0) {
             elites = Selection.elitism(this.population, this.numElite);
        }
        //SELECTION
        ArrayList<Genome> selected = Selection.stochasticUniversalSampling(this.population, this.selectionRate);
        //CROSSOVER
        System.out.println("Before crossover");
        System.out.println(selected.size());
        if (this.crossoverRate > 0) {
            selected = crossover.generateNextGeneration(selected);
            System.out.println("After Crossover");
            System.out.println(selected.size());
        }
        //MUTATION
        if (this.mutationRateSwapRoute > 0) {
            System.out.println("Before mutation");
            System.out.println(selected.toString());
            selected = mutator.mutatePopulationRoute(selected, this.mutationRateSwapRoute);
            System.out.println("After mutation");
            System.out.println(selected.toString());
        }

        if (this.mutationRateSwapGlobal > 0) {
            selected = mutator.mutatePopulationGlobal(selected, this.mutationRateSwapGlobal);
        }

        if (this.numElite > 0) {
            ArrayList<Integer> indices = new ArrayList<>();
            for (int j = 0 ; j < populationSize ; j++){
                indices.add(j);
            }
            Collections.shuffle(indices);
            List<Integer> outIndices = indices.subList(0, numElite);

            for (int i = 0 ; i < numElite ; i++){
                selected.set(outIndices.get(i), elites.get(i));
            }

        }

        return new Population(this.data, selected);
    }


    public Genome run(){
        for(int i = 0 ; i < numGenerations ; i++){
            population = nextGeneration();
        }
        Collections.sort(population.getPopulation(), Collections.reverseOrder());
        return population.getPopulation().get(0);
    }

    public static void main(String[] args){

        ProblemData data = new ProblemData();
        data.readFile("input/p01");
        GenomeGenerator gg = new GenomeGenerator(data);

        Genome g1 = gg.generateGenome();
        Genome g2 = new Genome(g1);

        Route r1 = g1.randomRoute();
        Route r2 = new Route(r1);
        r1.getNodes().set(0, 0);

        JSONObject parameters = JSONReader.readJSONFile("parameters.json");
        String datapath = "input/P01";
        GeneticAlgorithm GA = new GeneticAlgorithm(parameters, datapath);
        Genome g = GA.run();
        GraphVisualization graph = new GraphVisualization();
        graph.visualize(GA.data, g);
    }

}


