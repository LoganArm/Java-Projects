public class multiUse {

    static void multiUse(int n) {
        for (int i = 1; i <= n; i++) {
            System.out.println(i);
        }
    }

    static void multiUse(int a, int b) {
        System.out.println("Average: " + (a +b) / 2.0);
    }

        static void multiUse(int a, int b, int c) {
        int max = a;
        if (b > max) {
            max = b;
        }
        if (c > max) {
            max = c;
        }
        System.out.println("Maximum: " + max);
    }
 
    public static void main(String[] args) {
        multiUse(5);
        multiUse(4, 9);
        multiUse(3, 12, 7);
    }
}