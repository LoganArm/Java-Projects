import java.util.Scanner;

public class Calculator {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter x: ");
        double x = input.nextDouble();
        System.out.print("Enter y: ");
        double y = input.nextDouble();


        System.out.println(x + "+" + y + "=" + (x+y));
        System.out.println(x + "-" + y + "=" + (x-y));
        System.out.println(x + "*" + y + "=" + (x*y));
        System.out.println(x + "/" + y + "=" + (x/y));

        input.close();
    }
}