public class ArraysAndEnhancedLoops {
    public static void main(String[] args) {

        int[][] a = {
            {1, 2, 3},
            {4, 5, 6}
        };
        int[][] b = {
            {7, 8},
            {9, 10},
            {11, 12}
        };

        int[][] result = new int[2][2];

        int i = 0;                          
        for (int[] rowA : a) {
            for (int j = 0; j < 2; j++) {    
                int sum = 0;
                int k = 0;                  
                for (int valueA : rowA) {
                    sum = sum + valueA * b[k][j];
                    k++;
                }
                result[i][j] = sum;
            }
            i++;
        }

        System.out.println("Result matrix:");
        for (int[] row : result) {
            for (int value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }
}