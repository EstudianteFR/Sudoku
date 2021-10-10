package cl.ucn.disc.hpc.sudoku;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Slf4j
public class Main {

    private static int[][][] sudoku;
    private static int[][][] solution;
    private static int n;
    private static int sqrt;
    private static boolean solved = false;

    public static void main(String[] args) {
        readFile();
        printMatrix();

        fillOptions();
    }

    private static void fillOptions() {

        fillBox();
        fillRow();
        fillCol();

        while (!solved()){
            elimination();
            loneRanger();
        }

        printMatrix();
    }

    private static void loneRanger() {
        loneRangerBox();
    }

    private static void loneRangerBox() {

        int rowBound = 0;
        int colBound = 0;
        int aux = 0;
        List<Integer> possibleValues;
        for (int i =0; i < n; i++) {
            possibleValues = new ArrayList<Integer>(n*n);
            if (aux == sqrt){
                rowBound += sqrt;
                aux = 0;
                colBound = 0;
            }

            int itRow = 0;

            for (int j = rowBound; itRow < sqrt; j++, itRow++){
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    int value = sudoku[j][k][0];
                    if (value == 0){
                        for (int l = 1; l <= n; l++) {
                            int possibleValue = sudoku[j][k][l];
                            if (possibleValue > 0) {
                                possibleValues.add(possibleValue);
                            }
                        }
                    }
                }
            }
            Set<Integer> set =  new HashSet<Integer>(possibleValues);
            possibleValues.clear();
            possibleValues.addAll(set);
            int auxSize =  possibleValues.size();

            int auxList[][] = new int[auxSize][2];
            for (int j = 0; j < auxSize; j++) {
                auxList[j][0] = possibleValues.get(j);
            }

            itRow = 0;
            for (int j = rowBound; itRow < sqrt; j++, itRow++){
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    int value = sudoku[j][k][0];
                    if (value == 0){
                        for (int l = 1; l <= n; l++) {
                            int val = sudoku[j][k][l];
                            if (val > 0) {
                                int index = possibleValues.indexOf(val);
                                auxList[index][1] += 1;
                            }


                        }
                    }
                }
            }
            for (int m = 0; m < auxSize; m++) {
                if (auxList[m][1] == 1) {
                    itRow = 0;
                    for (int j = rowBound; itRow < sqrt; j++, itRow++){
                        int itCol = 0;
                        for (int k = colBound; itCol < sqrt; k++, itCol++) {
                            int value = sudoku[j][k][0];
                            if (value == 0){
                                for (int l = 1; l <= n; l++) {
                                    int val = sudoku[j][k][l];
                                    if (val == auxList[m][0]) {
                                        sudoku[j][k][0] = val;
                                        log.debug("Work in {},{} with val {}",j, k, val);
                                    }


                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private static void elimination() {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (sudoku[row][col][0] == 0 ){

                    int counter = 0;
                    int num = 0;
                    for (int possible = 1; possible <= n; possible++) {

                        if (sudoku[row][col][possible] > 0){
                            counter++;
                            num = sudoku[row][col][possible];
                        }
                        if (counter > 1){
                            break;
                        }
                    }
                    if (counter == 1){
                        sudoku[row][col][0] = num;
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
                int value = sudoku[row][col][0];

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
                        if (num == sudoku[row2][col][idPossible]) {
                            sudoku[row2][col][idPossible] = 0;
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
                int value = sudoku[row][col][0];

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
                        if (num == sudoku[row][col2][idPossible]) {
                            sudoku[row][col2][idPossible] = 0;
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
                    int value = sudoku[j][k][0];
                    if (value > 0){
                        list.add(value);
                    }
                }
            }

            itRow = 0;
            for (int j = rowBound; itRow < sqrt; j++, itRow++){
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    int value = sudoku[j][k][0];
                    if (value == 0){
                        int iterator = 1;
                        for (int l = 1; l <= n; l++) {
                            if (!list.contains(l)){
                                sudoku[j][k][iterator] = l;
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
            sudoku = new int[n][n][n+1];

            for(int i =0; i < n; i++) {
                String line = myReader.nextLine();
                int size = line.length();
                String numStr = "";
                int col = 0;
                for(int j = 0; j < size; j++){
                    if (line.charAt(j) != ','){
                        numStr += line.charAt(j);
                    }else{
                        sudoku[i][col][0] = Integer.parseInt(numStr);
                        col++;
                        numStr = "";
                    }
                }

                sudoku[i][col][0] = Integer.parseInt(numStr);

            }} catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }

        try {

            File myObj = new File("src/main/resources/9x9-solved.txt");
            Scanner myReader = new Scanner(myObj);
            n = Integer.parseInt(myReader.nextLine());
            sqrt = (int) Math.sqrt(n);
            solution = new int[n][n][n+1];

            for(int i =0; i < n; i++) {
                String line = myReader.nextLine();
                int size = line.length();
                String numStr = "";
                int col = 0;
                for(int j = 0; j < size; j++){
                    if (line.charAt(j) != ','){
                        numStr += line.charAt(j);
                    }else{
                        sudoku[i][col][0] = Integer.parseInt(numStr);
                        col++;
                        numStr = "";
                    }
                }

                sudoku[i][col][0] = Integer.parseInt(numStr);

            }} catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }

    private static void printMatrix() {
        for (int row = 0; row <n; row++){
            for (int col = 0; col < n; col++) {
                int num = sudoku[row][col][0];
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

    private static boolean solved() {
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                if (sudoku[row][col][0] != solution[row][col][0]){
                    return false;
                }
            }

        }
        return true;
    }
}