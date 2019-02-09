import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneticAlgorithm {

    private Population population;
    private ProblemData data = new ProblemData();
    private int populationSize;
    private int numGenerations;
    private int numElite;
    private double crossoverRate;
    private double mutationRateSwapRoute;
    private double mutationRateSwapGlobal;
    private double selectionRate;

    private double[] benchmarks = {590.,480.,670.,1060.,790.,920.,930.,4750.,4160.,3970.,3870.,1360.,1370.,1330.,2630.,2680.,2700.,3980.,4000.,4150.,5920.,6030.,6100.};

    private String fileName;

    private Crossover crossover;
    private Mutation mutator;

    public GeneticAlgorithm(JSONObject parameters){
        this.populationSize = parameters.getInt("populationSize");
        this.numGenerations = parameters.getInt("numGenerations");
        this.numElite = parameters.getInt("numElite");
        this.crossoverRate = parameters.getDouble("crossoverRate");
        this.mutationRateSwapRoute = parameters.getDouble("mutationRateSwapRoute");
        this.mutationRateSwapGlobal = parameters.getDouble("mutationRateSwapGlobal");
        this.selectionRate = parameters.getDouble("selectionRate");

        this.fileName = parameters.getString("fileName");

        data.readFile(fileName);

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
        ArrayList<Genome> selected = Selection.stochasticUniversalSampling(this.population, 1);
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


    public Genome run(boolean saveHistory, boolean earlyStop){
        ArrayList<Double> data = new ArrayList<>();
        double bestFitness;
        int benchmarkIndex = Integer.parseInt(this.fileName.substring(1)) - 1;

        for(int i = 0 ; i < numGenerations ; i++){
            if (saveHistory){
                data.add(Writer.round(population.meanFitness(),2));
            }
            population = nextGeneration();

            if ((i % 100) == 0) {
                bestFitness = population.bestFitness();

                System.out.println(String.format("Generation %d, mean fitness %.2f, mean distance %.2f, best %.2f",
                        i, population.meanFitness(), population.meanDistance(), bestFitness));

                if (earlyStop) {
                    if (bestFitness < this.benchmarks[benchmarkIndex]) {
                        break;
                    }
                }
            }
        }
        if (saveHistory){
            String name = this.fileName +"_generations_" + numGenerations + "_mutationRate_route_global_"+mutationRateSwapRoute+"_"+mutationRateSwapGlobal+
                    "_crossRate_"+crossoverRate;
            Writer.historyWriter(name, data);
        }
        population.getPopulation().sort(Collections.reverseOrder());
        return population.getPopulation().get(0);
    }

    public static void main(String[] args){

        /*JSONObject parameters = JSONReader.readJSONFile("parameters.json");
        GeneticAlgorithm GA = new GeneticAlgorithm(parameters);
        Genome g = GA.run(true, false);
        GraphVisualization graph = new GraphVisualization();

        System.out.println(g.fitness(false));
        System.out.println(g.distance());
        graph.visualize(GA.data, g);

        //save graph to file
        graph.saveGraph("data/graphs/" + parameters.getString("fileName") + ".dgs");

        Writer.genomeWriter(g);
        */
         ProblemData data = new ProblemData();
         data.readSolutionFile("p09", "data/ourSolutions/P09.res");
         }
}


