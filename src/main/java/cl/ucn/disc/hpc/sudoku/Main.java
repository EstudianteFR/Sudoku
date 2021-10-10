package cl.ucn.disc.hpc.sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

    private static int[][] matrix;
    private static int n;
    private static int sqrt;

    public static void main(String[] args) {
        readFile();
        printMatrix();
    }

    private static void readFile() {
        try {

            File myObj = new File("src/main/resources/9x9-unsolved.txt");
            Scanner myReader = new Scanner(myObj);
            n = Integer.parseInt(myReader.nextLine());
            sqrt = (int) Math.sqrt(n);
            matrix = new int[n][n];

            for(int i =0; i < n; i++) {
                String line = myReader.nextLine();
                int size = line.length();
                String numStr = "";
                int row = 0;
                for(int j = 0; j < size; j++){
                    if (line.charAt(j) != ','){
                        numStr += line.charAt(j);
                    }else{
                        matrix[i][row] = Integer.parseInt(numStr);
                        row++;
                        numStr = "";
                    }
                }

                matrix[i][row] = Integer.parseInt(numStr);

            }} catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void printMatrix() {
        for (int row = 0; row <n; row++){
            for (int col = 0; col < n; col++) {
                int num = matrix[row][col];
                if (num < 10) {
                    System.out.print("0");
                }
                System.out.print( num + " ");

                if (col > 0 && ((col+1) % sqrt) == 0){
                    System.out.print(" ");
                }
            }
            if (row > 0 && ((row+1) % sqrt) == 0){
                System.out.println();
            }
            System.out.println();
        }
    }
}