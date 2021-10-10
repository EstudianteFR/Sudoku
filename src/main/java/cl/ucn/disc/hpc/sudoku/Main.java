package cl.ucn.disc.hpc.sudoku;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Main {

    private static int[][][] matrix;
    private static int n;
    private static int sqrt;

    public static void main(String[] args) {
        readFile();
        printMatrix();

        fillOptions();
    }

    private static void fillOptions() {

        fillBox();
        fillRow();
        fillCol();
        writeValue();
        printMatrix();
    }

    private static void writeValue() {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (matrix[row][col][0] == 0 ){

                    int counter = 0;
                    int num = 0;
                    for (int possible = 1; possible <= n; possible++) {

                        if (matrix[row][col][possible] > 0){
                            counter++;
                            num = matrix[row][col][possible];
                        }
                        if (counter > 1){
                            break;
                        }
                    }
                    if (counter == 1){
                        matrix[row][col][0] = num;
                    }

                }

            }
        }
    }

    private static void fillCol() {

        // For each row
        for (int col = 0; col < n; col++) {

            // list of no possibles
            List<Integer> noPossibleList = new ArrayList<Integer>(n);

            // For each column
            for (int row = 0; row < n; row++) {

                // Save value
                int value = matrix[row][col][0];

                // if cell is not free
                if (value > 0) {

                    // add to no possibles
                    noPossibleList.add(value);
                }
            }

            // now I have all no possibles values

            for (int num: noPossibleList) {
                for (int row2 = 0; row2 < n; row2++) {
                    for (int idPossible = 1; idPossible <= n; idPossible++) {
                        if (num == matrix[row2][col][idPossible]) {
                            matrix[row2][col][idPossible] = 0;
                            break;
                        }
                    }

                }
            }

        }

    }

    private static void fillRow() {

        // For each row
        for (int row = 0; row < n; row++) {

            // list of no possibles
            List<Integer> noPossibleList = new ArrayList<Integer>(n);

            // For each column
            for (int col = 0; col < n; col++) {

                // Save value
                int value = matrix[row][col][0];

                // if cell is not free
                if (value > 0) {

                    // add to no possibles
                    noPossibleList.add(value);
                }
            }

            // now I have all no possibles values

            for (int num: noPossibleList) {
                for (int col2 = 0; col2 < n; col2++) {
                    for (int idPossible = 1; idPossible <= n; idPossible++) {
                        if (num == matrix[row][col2][idPossible]) {
                            matrix[row][col2][idPossible] = 0;
                            break;
                        }
                    }

                }
            }

        }
    }

    private static void fillBox() {
        int rowBound = 0;
        int colBound = 0;
        int aux = 0;

        // each box
        for (int i =0; i < n; i++) {
            if (aux == sqrt){
                rowBound += sqrt;
                aux = 0;
                colBound = 0;
            }

            int itRow = 0;
            List<Integer> list = new ArrayList<Integer>(n);

            for (int j = rowBound; itRow < sqrt; j++, itRow++){
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    int value = matrix[j][k][0];
                    if (value > 0){
                        list.add(value);
                    }
                }
            }

            itRow = 0;
            for (int j = rowBound; itRow < sqrt; j++, itRow++){
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    int value = matrix[j][k][0];
                    if (value == 0){
                        int iterator = 1;
                        for (int l = 1; l <= n; l++) {
                            if (!list.contains(l)){
                                matrix[j][k][iterator] = l;
                                iterator++;
                            }
                        }
                    }
                }
            }

            aux++;
            colBound += sqrt;
        }

    }

    private static void readFile() {
        try {

            File myObj = new File("src/main/resources/9x9-unsolved.txt");
            Scanner myReader = new Scanner(myObj);
            n = Integer.parseInt(myReader.nextLine());
            sqrt = (int) Math.sqrt(n);
            matrix = new int[n][n][n+1];

            for(int i =0; i < n; i++) {
                String line = myReader.nextLine();
                int size = line.length();
                String numStr = "";
                int col = 0;
                for(int j = 0; j < size; j++){
                    if (line.charAt(j) != ','){
                        numStr += line.charAt(j);
                    }else{
                        matrix[i][col][0] = Integer.parseInt(numStr);
                        col++;
                        numStr = "";
                    }
                }

                matrix[i][col][0] = Integer.parseInt(numStr);

            }} catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void printMatrix() {
        for (int row = 0; row <n; row++){
            for (int col = 0; col < n; col++) {
                int num = matrix[row][col][0];
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