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
        if (this.crossoverRate > 0) {
            selected = crossover.generateNextGeneration(selected, crossoverRate);
            }
        //MUTATION
        if (this.mutationRateSwapRoute > 0) {
            selected = mutator.mutatePopulationRoute(selected, this.mutationRateSwapRoute);
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


    public Genome run(boolean saveHistory){
        ArrayList<Double> data = new ArrayList<>();
        for(int i = 0 ; i < numGenerations ; i++){
            if (saveHistory){
                data.add(Writer.round(population.meanFitness(),2));
            }
            population = nextGeneration();

            if ((i % 100) == 0) {
                System.out.println(String.format("Generation %d, mean fitness %.2f", i, population.meanFitness()));
            }

        }
        if (saveHistory){
            String name = this.data.path +"_generations_" + numGenerations + "_mutationRate_route_global_"+mutationRateSwapRoute+"_"+mutationRateSwapGlobal+
                    "_crossRate_"+crossoverRate;
            Writer.historyWriter(name, data);
        }
        Collections.sort(population.getPopulation(), Collections.reverseOrder());
        return population.getPopulation().get(0);
    }

    public static void main(String[] args){

        JSONObject parameters = JSONReader.readJSONFile("parameters.json");
        String datapath = "input/P02";
        GeneticAlgorithm GA = new GeneticAlgorithm(parameters, datapath);
        Genome g = GA.run(true);
        GraphVisualization graph = new GraphVisualization();
        System.out.println(g.fitness(false));
        graph.visualize(GA.data, g);
        //Writer.writer(g);
    }
}


