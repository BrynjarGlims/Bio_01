import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;

public class Writer {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static void historyWriter(String name, ArrayList<Double> data) {
        BufferedWriter writer = null;
        StringBuilder sb = new StringBuilder();
        for (double r : data){
            sb.append(r);
            sb.append(" ");
        }
        String dataString = sb.toString();
        try {
            File logFile = new File("histories/" + name);

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write(dataString);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public static void genomeWriter(Genome genome){
        BufferedWriter writer = null;
        ProblemData data = genome.getGenome().get(0).getData();
        try {
            //create a temporary file

            File logFile = new File("ourSolutions/" + data.path);

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write((round(genome.fitness(false),2)) +"\n");

            ArrayList<Route> routes = genome.getGenome();
            routes.sort(new Comparator<Route>() {
                @Override
                public int compare(Route o1, Route o2) {
                    return o1.getNodes().get(0).compareTo(o2.getNodes().get(0));
                }
            });

            for (int i = 0 ; i < routes.size() ; i++){

                Route r = routes.get(i);
                String out = "";
                out += r.getNodes().get(0) + "\t";
                int vehicleNum = 1;
                int depot = r.getNodes().get(0);
                for (int j = 0 ; j < i ; j++){
                    if(routes.get(j).getNodes().get(0) == depot){
                        vehicleNum += 1;
                    }
                }
                out += vehicleNum + "\t" + round(r.routeFitness(),2) + "\t" + r.getRouteLoad() + "\t";
                out += r.getNodes().get(r.getNodes().size() - 1) + "\t";

                String customers = r.toString().trim();
                int first = customers.indexOf(" ");
                int last = customers.lastIndexOf(" ");

                out += customers.substring(first, last) + "\n";
                writer.write(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}
