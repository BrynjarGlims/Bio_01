import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Genome {

    private ArrayList<Route> genome;

    public Genome(ArrayList<Route> genome){
        this.genome = genome;
    }


    public void replaceNode(Route route, int col, int customer){
        genome.get(genome.indexOf(route)).getNodes().set(col, customer);
    }

    public void insertNode(Route route, int col, int customer){
        genome.get(genome.indexOf(route)).getNodes().add(col, customer);

    }

    public void removeNode(Route route, int col){
        genome.get(genome.indexOf(route)).getNodes().remove(col);
    }

    public int startDepots(int depotId){
        int count = 0;
        for (Route r : genome){
            if (r.getNodes().get(0) == depotId){
                count += 1;
            }
        }
        return count;
    }

    public int endDepots(int depotId){
        int count = 0;
        for (Route r : genome){
            if (r.getNodes().get(r.getNodes().size() - 1) == depotId){
                count += 1;
            }
        }
        return count;
    }
    public void addRoute(Route route){
        genome.add(route);
    }

    public void removeRoute(int index){
        genome.remove(index);
    }
    public int getNumNodes(){
        int sum = 0;
        for(Route r : genome){
            sum += r.getNodes().size();
        }

        return sum;
    }

    public double distance(){
        double fitness = 0;
        for (Route r : genome){
            fitness += r.routeDistance();
        }
        return fitness;
    }
    public double fitness(){
        double fitness = 0;
        for (Route r : genome){
            fitness += r.routeFitness();
        }
        return fitness;

    }

    public ArrayList<Route> getGenome(){
        return genome;
    }

    public Route randomRoute(){
        return getGenome().get(ThreadLocalRandom.current().nextInt(0, getGenome().size()));
    }


    public int longestRoute(){
        int length = 0;
        for (Route r : getGenome()){
            if (r.getNodes().size() > length) {
                length = r.getNodes().size();
            }
        }
        return length;
    }
    public int numRoutes(){
        return getGenome().size();
    }



    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Route r : genome){
            sb.append(r.toString());
            sb.append("\n");
        }
        return sb.toString();
    }




}
