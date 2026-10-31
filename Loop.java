import java.util.Scanner;

public class Loop{
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int x = 0;
        int y = 0;
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n1 - Enter two (2) numbers (x and y). ");
            System.out.println("2 - Print all numbers between x and y. ");
            System.out.println("3 - Find the average of all numbers between x and y. ");
            System.out.println("4 - Find all prime numbers between x and y. ");
            System.out.println("5 - Exit ");
            choice = input.nextInt();

            if (choice == 1) {
                System.out.println("Enter x: ");
                x = input.nextInt();
                System.out.println("Enter y: ");
                y = input.nextInt();
             } else if (choice == 2) {
                for (int i = x; i <= y; i++) {
                    System.out.println(i);
                }
 
            } else if (choice == 3) {
                int sum = 0;
                for (int i = x; i <= y; i++) {
                    sum = sum + i;
                }
                System.out.println("Average: " + (double) sum / (y - x + 1));
 
            } else if (choice == 4) {
                for (int i = Math.max(x, 2); i <= y; i++) {
                    boolean prime = true;
                    for (int d = 2; d < i; d++) {
                        if (i % d == 0) {
                            prime = false;
                        }
                    }
                    if (prime) {
                        System.out.println(i);
                    }
                }
            }
        }
        input.close();
    }
}