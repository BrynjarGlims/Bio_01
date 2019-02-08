import java.util.List;

public class Depot {
    private int id;
    private int x;
    private int y;
    private int vehicleCapacity;
    private int maxDuration;
    private int maxLoad;

    public Depot(List<Integer> depotData, ProblemData data) {
        this.id = depotData.get(0);
        this.x = depotData.get(1);
        this.y = depotData.get(2);
        this.vehicleCapacity = data.getNumVehicles();
        this.maxDuration = data.getMaxLoads().get(0);
        this.maxDuration = data.getMaxDurations().get(0);
    }
}
