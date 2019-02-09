import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ProblemData {


    private int numVehicles;
    private int numDepots;
    private int numCustomers;
    public String fileName;
    private ArrayList<Integer> maxDurations = new ArrayList<>();
    private ArrayList<Integer> maxLoads = new ArrayList<>();
    private ArrayList<List<Integer>> customerData = new ArrayList<>();
    private ArrayList<List<Integer>> depotData = new ArrayList<>();


    public ProblemData() {}

    public void readFile(String fileName){
        try {
            this.fileName = fileName;
            FileInputStream fstream = new FileInputStream("data/input/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String str = br.readLine();
            String[] data = str.split(" ");
            this.numVehicles = Integer.parseInt(data[0]);
            this.numCustomers = Integer.parseInt(data[1]);
            this.numDepots = Integer.parseInt(data[2]);


            for (int i = 0 ; i < numDepots ; i++){
                str = br.readLine();
                data = str.split(" ");
                this.maxDurations.add(Integer.parseInt(data[0]));
                this.maxLoads.add(Integer.parseInt(data[1]));
            }

            for (int i = 0 ; i < numCustomers ; i++){
                ArrayList<Integer> custInt = new ArrayList<>();
                str = br.readLine();
                ArrayList<String> custString = new ArrayList<>(Arrays.asList(str.trim().split("\\s+")));
                for(String s : custString)custInt.add(Integer.parseInt(s));

                this.customerData.add(custInt.subList(0, 5));
            }

            for (int i = 0 ; i < numDepots ; i++){
                ArrayList<Integer> depotInt = new ArrayList<>();
                str = br.readLine();
                ArrayList<String> depotString = new ArrayList<>(Arrays.asList(str.trim().split("\\s+")));

                for(String s : depotString)depotInt.add(Integer.parseInt(s));

                this.depotData.add(depotInt.subList(0,3));
            }
        }
        catch (Exception e){throw new IllegalStateException("File format not correct");}
    }

    public int getNumVehicles() {
        return numVehicles;
    }

    public int getNumDepots() {
        return numDepots;
    }

    public int getNumCustomers() {
        return numCustomers;
    }

    public ArrayList<Integer> getMaxDurations() {
        return maxDurations;
    }

    public ArrayList<Integer> getMaxLoads() {
        return maxLoads;
    }

    public ArrayList<List<Integer>> getCustomerData() {
        return customerData;
    }

    public ArrayList<List<Integer>> getDepotData() {
        return depotData;
    }


    //only data e.g. "p01" for datapath, complete relative path for solutionPath
    public void readSolutionFile(String dataPath, String solutionPath){
        readFile("data/input/" + dataPath);

        try {

            Scanner br = new Scanner(new File(solutionPath));

            /*FileInputStream fstream = new FileInputStream(solutionPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            */
            ArrayList<Route> routes = new ArrayList<>();
            System.out.println("fitness: " + br.nextLine());
            String str;
            String[] data;
            String[] customers;
            while(br.hasNext()){
                str = br.nextLine().trim();
                data = str.split("\t");
                customers = data[data.length-1].trim().split(" ");
                ArrayList<Integer> route= new ArrayList<>();
                route.add(Integer.parseInt(data[0]) + numCustomers - 1);
                for (String customer : customers){
                    route.add(Integer.parseInt(customer));
                }
                route.add(Integer.parseInt(data[4]) + numCustomers - 1);
                routes.add(new Route(route, this));


            }

            GraphVisualization graph = new GraphVisualization();
            graph.visualize(this, new Genome(routes));


        }
        catch (Exception e){
            throw new IllegalStateException(e);}
    }



}
