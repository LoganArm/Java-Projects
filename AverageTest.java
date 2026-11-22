
public class AverageTest {

    public double average(int[] numbers) {
        int sum = 0;

        for (int i = 0; i < numbers.length; i++) {
            sum += numbers[i];
        }

        return (double) sum / numbers.length;
    }

    public void testAverage() {
        int[] numbers = {10, 20, 30};
        if (average(numbers) != 20.0) {
            throw new AssertionError("Expected average to be 20.0");
        }
    }

    public void testAverage2() {
        int[] numbers = {5, 10, 15};
        if (average(numbers) != 10.0) {
            throw new AssertionError("Expected average to be 10.0");
        }
    }

    public static void main(String[] args) {
        AverageTest calc = new AverageTest();

        int[] numbers = {10, 20, 30};

        System.out.println("Average: " + calc.average(numbers));
    }
}
