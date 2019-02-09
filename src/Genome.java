import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Genome implements Comparable<Genome> {

    private ArrayList<Route> genome;

    public Genome(ArrayList<Route> genome){
        this.genome = genome;
    }

    public Genome(Genome original) {
        ArrayList<Route> genomeCopy = new ArrayList<>();

        for (Route route : original.getGenome()) {
            //ArrayList<Integer> nodes = new ArrayList<>(route.getNodes());
            genomeCopy.add(new Route(route));
        }

        this.genome = genomeCopy;
    }

    public void replaceNode(Route route, int col, int customer){
        genome.get(genome.indexOf(route)).getNodes().set(col, customer);
        route.updateFitness();
    }

    public void insertNode(Route route, int col, int customer){
        genome.get(genome.indexOf(route)).getNodes().add(col, customer);
        route.updateFitness();
    }

    public void removeNode(Route route, int col){
        genome.get(genome.indexOf(route)).getNodes().remove(col);
        route.updateFitness();
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
        double distance = 0;
        for (Route r : genome){
            distance += r.routeDistance();
        }
        return distance;
    }

    /**
     * Calculate the combined inverse fitness for a genome.
     * This lets us maximize the inverse fitness to solve the problem.
     * @return  inverse fitness of genome
     */
    public double fitness(boolean inverse){
        double fitness = 0;
        for (Route r : genome){
            fitness += r.getFitness();
        }
        if (inverse) {
            return 1 / fitness;
        } else {
            return fitness;
        }
    }

    public ArrayList<Route> getGenome(){
        return genome;
    }

    public Route randomRoute(){
        return getGenome().get(ThreadLocalRandom.current().nextInt(0, getGenome().size()));
    }

    public Route longestRoute(){
        double fitness = -1;
        Route longest = null;
        for (Route r : getGenome()){
            if (r.getFitness() > fitness) {
                longest = r;
                fitness = r.getFitness();
            }
        }
        return longest;
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

    public boolean isFeasible() {
        boolean feasible = true;
        for (Route r : this.getGenome()) {
            if (!r.getFeasibility()) {
                feasible = false;
                break;
            }
        }

        if (this.fitness(false) != this.distance()) {
            feasible = false;
        }

        return feasible;
    }

    @Override
    public int compareTo(Genome o) {
        return Double.compare(this.fitness(true), o.fitness(true));
    }
}
