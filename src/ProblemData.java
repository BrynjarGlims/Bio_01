import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {

    private int numVehicles;
    private int numDepots;
    private int numCustomers;
    private ArrayList<Integer> maxDurations = new ArrayList<>();
    private ArrayList<Integer> maxLoads = new ArrayList<>();
    private ArrayList<List<Integer>> customerData = new ArrayList<>();
    private ArrayList<List<Integer>> depotData = new ArrayList<>();


    public FileReader() {}

    public void readFile(String path){
        try {
            FileInputStream fstream = new FileInputStream(path);
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

            System.out.println(this.numVehicles);

            System.out.println(this.numCustomers);

            System.out.println(this.numDepots);

            System.out.println(this.maxDurations);

            System.out.println(this.maxLoads);

            System.out.println(this.customerData);

            System.out.println(this.depotData);




        }
        catch (Exception e){}
    }

    public static void main(String[] args){
        FileReader fr = new FileReader();
        fr.readFile("input/p22");

    }
}
