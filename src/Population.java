import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Population {

    private GenomeGenerator generator;
    private ArrayList<Genome> population = new ArrayList<>();
    private ProblemData data;

    public Population(String path, int populationSize) {
        this.generator = new GenomeGenerator(path);
        this.data = generator.getData();
        for (int i = 0 ; i < populationSize ; i++){
            population.add(generator.generateGenome());
        }
    }



    public ArrayList<Genome> generatePopulation(int populationSize){
        ArrayList<Genome> population = new ArrayList<>();
        for (int i = 0 ; i < populationSize ; i++){
            population.add(generator.generateGenome());
        }
        return population;
    }





    public static void main(String[] args){
        Population p = new Population("input/p01", 2);

        ArrayList<Genome> population = p.generatePopulation(64);
        for(int i = 0; i < 10000 ; i++){
            population = p.generatePopulation(10);
        }
        Genome g1 = population.get(0);
        for (List<Integer> d : p.data.getDepotData()){
            if(g1.startDepots(d.get(0)-1) > p.data.getNumVehicles())
            System.out.println("invalid route");
        }
        for (Route r : g1.getGenome()){
            if (r.getRouteLoad() > r.getRouteLoadCapacity())
            System.out.println("invalid route");
        }
        GraphVisualization graph = new GraphVisualization();


        System.out.println(g1.fitness());

        graph.visualize(p.data.getCustomerData(), p.data.getDepotData(), g1.getGenome());


    }




}
