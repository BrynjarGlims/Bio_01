import org.json.JSONObject;

public class GeneticAlgorithm {

    private Population population;
    private ProblemData data = new ProblemData();
    private int populationSize;
    private int numGenerations;
    private int numElite;
    private int crossoverRate;
    private int mutationRateSwapRoute;
    private int mutationRateSwapGlobal;
    private int selectionRate = 1;

    public GeneticAlgorithm(JSONObject parameters, String dataPath){
        data.readFile(dataPath);
        this.populationSize = parameters.getInt("populationSize");
        this.numGenerations = parameters.getInt("numGenerations");
        this.numElite = parameters.getInt("numElite");
        this.crossoverRate = parameters.getInt("crossoverRate");
        this.mutationRateSwapRoute = parameters.getInt("mutationRateSwapRoute");
        this.mutationRateSwapGlobal = parameters.getInt("mutationRateSwapGlobal");
        this.selectionRate = parameters.getInt("selectionRate");
        population = new Population(data, populationSize);
    }

    public static void main(String[] args){
        //SELECTION

        //CROSSOVER

        //MUTATION

        //ELITE
    }

}


