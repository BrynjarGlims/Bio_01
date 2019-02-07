import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TestyBoii {
    private int value;

    public TestyBoii(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        TestyBoii boii1 = new TestyBoii(12);
        TestyBoii boii2 = new TestyBoii(9);
        TestyBoii boii3 = new TestyBoii(255);

        ArrayList<TestyBoii> testyBoiiiiis = new ArrayList<TestyBoii>();
        testyBoiiiiis.add(boii1);
        testyBoiiiiis.add(boii2);
        testyBoiiiiis.add(boii3);

        Collections.sort(testyBoiiiiis, new Comparator<TestyBoii>() {
            @Override
            public int compare(TestyBoii o1, TestyBoii o2) {
                if (o1.value == o2.value)
                    return 0;
                return o1.value < o2.value ? 1 : -1;
            }
        });

        for (TestyBoii t : testyBoiiiiis) {
            System.out.println(t.value);
        }
    }
}