package cl.ucn.disc.hpc.sudoku;

import lombok.extern.java.Log;
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
        fillOptions();
    }

    private static void fillOptions() {

        fillBox();
        fillRow();
        fillCol();


        for (int i = 0; i < 100000; i++) {
            elimination();

            loneRanger();

            twins();
        }



        printMatrix();
    }

    private static void twins() {

        int twinCol1 = 0;
        int twinCol2 = 0;
        int twinRow = 0;

        // must exit only one pair of twins
        int twins = 0;

        // for each row
        for (int row = 0; row < n; row++) {

            // the coincidences
            List<Integer> coincidencesList = coincidencesList = new ArrayList<Integer>(n);

            // for each column
            for (int col = 0; col < n; col++) {

                // search for possibles values
                List<Integer> list = new ArrayList<Integer>(n);
                for (int possibleValue = 1; possibleValue <= n; possibleValue++) {
                    if ( sudoku[row][col][0] == 0 && sudoku[row][col][possibleValue] > 0) {
                        list.add(sudoku[row][col][possibleValue]);

                    }
                }

                // now I have a list with all possible values
                // I need to iterate to compare with possible values from another cell

                // for each col
                for (int col2 = col + 1 ; col2 < n; col2++) {

                    List<Integer> list2 = new ArrayList<Integer>(n);
                    for (int possibleValue = 1; possibleValue <= n; possibleValue++) {
                        if ( sudoku[row][col2][0] == 0 && sudoku[row][col2][possibleValue] > 0) {
                            list.add(sudoku[row][col2][possibleValue]);

                        }
                    }

                    // order list
                    Set<Integer> set = new HashSet<>(list2);
                    list2.clear();
                    list2.addAll(set);
                    Collections.sort(list2);

                    // order list
                    set = new HashSet<>(list);
                    list.clear();
                    list.addAll(set);
                    Collections.sort(list);

                    // get list's size
                    int maxL = list.size();
                    int maxL2 = list2.size();

                    // number of coincidences
                    int coincidence= 0;


                    // the lists must have the same size
                    if (maxL == maxL2) {

                        // for each value in list
                        for (int value : list){

                            // if both have same possible values save that coincidence
                            if(list2.contains(value)){
                                coincidence ++;
                                coincidencesList.add(value);
                            }
                        }

                        // the lists must differ in one element
                        if ((maxL - coincidence) == 1) {

                            // maybe twins but needs to check all cells
                            twins++;

                            if (twins == 1) {
                                twinCol1 = col;
                                twinCol2 = col2;
                                twinRow = row;
                            }else {
                                // cheack another row
                                col = n;
                                col2 = n;
                                twins = 0;
                            }
                        }
                    }

                }

            }

            if (twins == 1) {
                for (int possibleValuesIndex = 1; possibleValuesIndex <= n; possibleValuesIndex++) {
                    sudoku[row][twinCol1][possibleValuesIndex] = 0;
                    sudoku[row][twinCol2][possibleValuesIndex] = 0;
                }

                int it = 1;
                for (int coincidence : coincidencesList){
                    sudoku[row][twinCol1][it] = coincidence;
                    sudoku[row][twinCol2][it] = coincidence;
                    it++;

                }

            }

        }


    }

    private static void loneRanger() {
        loneRangerBox();

        loneRangerCol();

        loneRangerRow();


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
                                        cleanPossibleValues(j,k);
                                    }


                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private static void loneRangerRow() {

        List<Integer> possibleValues;

        // for each row
        for (int row = 0; row < n; row++) {
            possibleValues = new ArrayList<Integer>(n*n);

            // for each col
            for (int col = 0; col < n; col++) {

                // search for possible values for sudoku[row][col]
                for (int indexPossibleValue = 1; indexPossibleValue <= n; indexPossibleValue++) {
                    int possibleValue = sudoku[row][col][indexPossibleValue];
                    if (possibleValue > 0) {
                        possibleValues.add(possibleValue);
                    }
                }
            }

            // now I have all possible values of all row

            // remove duplicates
            Set<Integer> set =  new HashSet<Integer>(possibleValues);
            possibleValues.clear();
            possibleValues.addAll(set);


            int auxSize =  possibleValues.size();

            // list to count repetitions
            int uniquesList[][] = new int[auxSize][2];

            // initialize with zero for each no repetition value
            for (int j = 0; j < auxSize; j++) {
                uniquesList[j][0] = possibleValues.get(j);
                uniquesList[j][1] = 0;
            }

            // looking where are the possible values for columns
            for (int col = 0; col < n; col++) {

                // for each possible values increment counter
                for (int l = 1; l <= n; l++) {
                    int val = sudoku[row][col][l];
                    if (val > 0) {
                        int index = possibleValues.indexOf(val);
                        uniquesList[index][1] += 1;
                    }

                }
            }

            // now I have all unique values with his counter in matrix auxList with size auxSize

            // for each unique value
            for (int m = 0; m < auxSize; m++) {

                int countUniqueValue = uniquesList[m][1];
                int uniqueValue = uniquesList[m][0];

                // if a unique value its repeat only one time
                if (countUniqueValue == 1) {

                    // search where is the unique value
                    possibleValues = new ArrayList<Integer>(n*n);

                    // for each column
                    for (int col = 0; col < n; col++) {



                        // save the  value
                        int value = sudoku[row][col][0];

                        // if a cell is free
                        if (value == 0){

                            // for all possible values in its cell
                            for (int l = 1; l <= n; l++) {

                                // save a possible value
                                int val = sudoku[row][col][l];

                                // if possible value coincide with the unique value
                                if (val == uniqueValue) {


                                    sudoku[row][col][0] = uniqueValue;
                                    cleanPossibleValues(row,col);
                                    uniquesList[m][1] = -1;
                                    break;
                                }


                            }

                        }
                    }

                }
            }
        }
    }

    private static void loneRangerCol() {

        List<Integer> possibleValues;

        // for each row
        for (int col = 0; col < n; col++) {
            possibleValues = new ArrayList<Integer>(n*n);

            // for each col
            for (int row = 0; row < n; row++) {

                // search for possible values for sudoku[row][col]
                for (int indexPossibleValue = 1; indexPossibleValue <= n; indexPossibleValue++) {
                    int possibleValue = sudoku[row][col][indexPossibleValue];
                    if (possibleValue > 0) {
                        possibleValues.add(possibleValue);
                    }
                }
            }

            // now I have all possible values of all row

            // remove duplicates
            Set<Integer> set =  new HashSet<Integer>(possibleValues);
            possibleValues.clear();
            possibleValues.addAll(set);


            int auxSize =  possibleValues.size();

            // list to count repetitions
            int uniquesList[][] = new int[auxSize][2];

            // initialize with zero for each no repetition value
            for (int j = 0; j < auxSize; j++) {
                uniquesList[j][0] = possibleValues.get(j);
                uniquesList[j][1] = 0;
            }

            // looking where are the possible values for columns
            for (int row = 0; row < n; row++) {

                // for each possible values increment counter
                for (int l = 1; l <= n; l++) {
                    int val = sudoku[row][col][l];
                    if (val > 0) {
                        int index = possibleValues.indexOf(val);
                        uniquesList[index][1] += 1;
                    }

                }
            }

            // now I have all unique values with his counter in matrix auxList with size auxSize

            // for each unique value
            for (int m = 0; m < auxSize; m++) {

                int countUniqueValue = uniquesList[m][1];
                int uniqueValue = uniquesList[m][0];

                // if a unique value its repeat only one time
                if (countUniqueValue == 1) {

                    // search where is the unique value
                    possibleValues = new ArrayList<Integer>(n*n);

                    // for each column
                    for (int row = 0; row < n; row++) {


                        // save the  value
                        int value = sudoku[row][col][0];

                        // if a cell is free
                        if (value == 0){

                            // for all possible values in its cell
                            for (int l = 1; l <= n; l++) {

                                // save a possible value
                                int val = sudoku[row][col][l];

                                // if possible value coincide with the unique value
                                if (val == uniqueValue) {


                                    sudoku[row][col][0] = uniqueValue;
                                    cleanPossibleValues(row,col);
                                    uniquesList[m][1] = -1;
                                    break;
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

                        cleanPossibleValues(row, col);
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

            File myObj = new File("src/main/resources/9x9-unsolved-normal.txt");
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

            File myObj = new File("src/main/resources/9x9-unsolved-normal.txt");
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
                        solution[i][col][0] = Integer.parseInt(numStr);
                        col++;
                        numStr = "";
                    }
                }

                solution[i][col][0] = Integer.parseInt(numStr);

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

    private static void cleanPossibleValues (int row, int col){


        int val = sudoku[row][col][0];


        // clean in r,col
        for (int r = 0; r < n; r++) {
            for (int possibleValues = 1; possibleValues <= n; possibleValues++) {
                if(sudoku[r][col][possibleValues] == val) {
                    sudoku[r][col][possibleValues] = 0;
                }
            }
        }

        // clean in c, row
        for (int c = 0; c < n; c++) {
            for (int possibleValues = 1; possibleValues <= n; possibleValues++) {
                if(sudoku[row][c][possibleValues] == val) {
                    sudoku[row][c][possibleValues] = 0;
                }
            }
        }


        int rowBound = 0;
        int colBound = 0;
        int aux = 0;
        boolean founded = false;
        int boxIndexR = 0;
        int boxIndexC = 0;

        // each box
        for (int i =0; i < n; i++) {
            if (aux == sqrt){
                rowBound += sqrt;
                aux = 0;
                colBound = 0;
            }

            int itRow = 0;
            for (int j = rowBound; itRow < sqrt; j++, itRow++){
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    if (row == j && col == k){
                        boxIndexR = rowBound;
                        boxIndexC = colBound;
                        founded = true;
                        break;
                    }
                }
                if (founded) {
                    itRow = sqrt; // finish for
                    i = n;
                }
            }

            aux++;
            colBound += sqrt;
        }


        int it = 0;
        int aux2 = boxIndexC;
        for (int r = 0; r < sqrt; r++) {
            for (int c = 0; c < sqrt; c++) {

                for (int possibleValues = 1; possibleValues <= n; possibleValues++) {

                    if (val == sudoku[boxIndexR][boxIndexC][possibleValues]) {
                        sudoku[boxIndexR][boxIndexC][possibleValues] = 0;
                    }
                }

                boxIndexC++;
                it++;
                if (it == sqrt){
                    boxIndexC = aux2;
                    it = 0;
                    boxIndexR++;
                }
            }

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