import java.util.List;

public class Customer {
    private int id;
    private int x;
    private int y;
    private int duration;
    private int demand;

    public Customer(List<Integer> customerData) {
        this.id = customerData.get(0) - 1;
        this.x = customerData.get(1);
        this.y = customerData.get(2);
        this.duration = customerData.get(3);
        this.demand = customerData.get(4);
    }

    public Customer(Customer c) {
        this.id = c.id;
        this.x = c.x;
        this.y = c.y;
        this.duration = c.duration;
        this.demand = c.demand;
    }
}
