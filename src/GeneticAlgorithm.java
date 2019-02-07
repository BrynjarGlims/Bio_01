import org.json.JSONObject;

import java.util.ArrayList;

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
        if (this.numElite > 0) {
            ArrayList<Genome> elites = Selection.elitism(this.population, this.numElite);
        }
        //SELECTION
        ArrayList<Genome> selected = Selection.stochasticUniversalSampling(this.population, this.selectionRate);
        //CROSSOVER
        if (this.crossoverRate > 0) {
            selected = crossover.generateNextGeneration(selected);
        }
        //MUTATION
        if (this.mutationRateSwapRoute > 0) {
            selected = mutator.mutatePopulationRoute(selected, this.mutationRateSwapRoute);
        }

        if (this.mutationRateSwapGlobal > 0) {
            selected = mutator.mutatePopulationGlobal(selected, this.mutationRateSwapGlobal);
        }

        return new Population(this.data, selected);
    }

    public static void main(String[] args){

    }

}


