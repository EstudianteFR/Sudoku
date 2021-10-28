package cl.ucn.disc.hpc.sudoku;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class Main {

    private static int[][][] sudoku;
    private static int[][][] solution;
    private static int n;
    private static int sqrt;
    private static boolean solved = false;
    private static boolean anyChange = false;

    public static void main(String[] args) throws InterruptedException {

        solution = readFile("src/main/resources/9x9-solved-normal.txt");
        sudoku = readFile("src/main/resources/9x9-unsolved-normal.txt");

        solving();
        //gemelos();

    }

    private static void solving() throws InterruptedException {

        // the watch
        StopWatch sw = StopWatch.createStarted();

        fillOptions();

        while (!solved()){

            anyChange = false;

            elimination();

            if (!anyChange) loneRangerBox();

            if (!anyChange) loneRangerCol();

            if (!anyChange) loneRangerRow();

            //if (!anyChange) gemelos();

        }

        printMatrix();
        percentageSolved();
    }

    private static void fillOptions() {

        fillBox();
        fillRow();
        fillCol();
    }

    private static void gemelos() {

        for (int row = 0; row < n; row++) {

            //log.debug("row: {}", row);

            for (int col = 0; col < n -1; col++) {

                List<Integer> possibleValues = new ArrayList<Integer>(n);
                List<Integer> twinPossibleValues = new ArrayList<Integer>();
                //log.debug("     col: {}", col);

                for (int i = 1; i <= n; i++) {
                    int v = sudoku[row][col][i];
                    if (v > 0) {
                        possibleValues.add(v);
                    }
                }

                int counterTwins = 0;
                int colTwin = -1;
                for (int col2 = col + 1; col2 < n; col2++) {
                    List<Integer> possibleValues2 = new ArrayList<Integer>(n);
                    possibleValues2 = new ArrayList<Integer>(n);
                    //log.debug("         col2: {}", col2);

                    for (int i = 1; i <= n; i++) {
                        int v = sudoku[row][col2][i];
                        if (v > 0) {
                            possibleValues2.add(v);
                        }
                    }

                    if (
                            (possibleValues.size() == 2 && possibleValues2.size() == 3) ||
                            (possibleValues.size() == 3 && possibleValues2.size() == 2) ||
                            (possibleValues.size() == 3 && possibleValues2.size() == 3)
                    ) {
                        int counter = 0;
                        for (int v : possibleValues) {
                            for (int v2 : possibleValues2) {
                                if (v == v2) {
                                    counter++;
                                }
                            }
                        }

                        if (counter == 2) {
                            counterTwins++;
                            log.debug("counterTwins++");
                            colTwin = col2;
                            twinPossibleValues = new ArrayList<Integer>(possibleValues2);
                            if (counterTwins > 1){
                                log.debug("if (counterTwins > 1)");
                                return;
                            }
                        }
                    }

                }

                if (counterTwins == 1) {

                    // Si las listas son de tamanio 2 no nos sirven



                    // Ahora debemos asegurarnos que no compartan valores posibles

                    List<Integer> allPossibleValues = new ArrayList<Integer>(n);
                    for (int col3 = 0; col3 < n; col3++) {

                        if (col3 != col && col3 != colTwin) {
                            for (int i = 1; i <= n; i++) {
                                allPossibleValues.add(sudoku[row][col3][i]);
                            }
                        }
                    }

                    allPossibleValues.removeAll(Collections.singleton(0));

                    boolean twins = false;

                    int aux = 0;
                    for (int v : possibleValues) {
                        for (int vt : twinPossibleValues) {
                            for (int a : allPossibleValues) {
                                if (v == vt && v == a) {
                                    log.debug("aux++");
                                    aux++;
                                }
                            }
                        }
                    }

                    // TODO: Check method, this situation is not checkable
                    log.debug("HOLA");
                    if (aux == 0) {
                        twins = true;
                    }

                }

            }



        }

    }

    private static void twins() {

        for (int row = 0; row < n; row++) {

            // list to count quantity of each possible values
            int list[][] = new int[n][3];
            for (int i = 0; i < n; i++) {
                list[i][0] = i + 1;
                list[i][1] = 0;
            }

            for (int col = 0; col < n; col++) {

                for (int indexPosVal = 1; indexPosVal <= n; indexPosVal++) {

                    // the cell have not value
                    if (sudoku[row][col][0] == 0){
                        int index = sudoku[row][col][indexPosVal];
                        if (index > 0) {
                            list[index - 1][1] += 1;
                        }
                    }

                }
            }

            int countListRep = 0;
            // search for values with 2 repetitions
            for (int i = 0; i < n; i++) {
                if (list[i][1] == 2){
                    list[i][2] += 1;
                    countListRep++;
                }
            }

            if (countListRep > 1) {



                // for each value with 2 repetitions
                for (int i = 0; i < n; i++) {
                    if (list[i][2] == 1){

                        // value with 2 repetitions
                        int value2 = list[i][0];
                        int twinCol1 = -1;
                        int twinCol2 = -1;

                        // find the column that have a repetition
                        for (int col = 0; col < n; col++) {


                            if (sudoku[row][col][0] == 0) {
                                for (int indexPosVal = 1; indexPosVal <= n; indexPosVal++) {
                                    if (sudoku[row][col][indexPosVal] == value2){
                                        if (twinCol1 == -1) {
                                            twinCol1 = col;
                                        }else{
                                            twinCol2 = col;
                                        }
                                    }
                                }

                            }


                        }

                        if (twinCol1 > 0 && twinCol2 >0){

                            int quantityPosValTwin1 = 0;
                            int quantityPosValTwin2 = 0;

                            for (int indexPosVal = 1; indexPosVal <= n; indexPosVal++) {
                                if (sudoku[row][twinCol1][indexPosVal]> 0){
                                    quantityPosValTwin1 += 1;
                                }if (sudoku[row][twinCol2][indexPosVal]> 0){
                                    quantityPosValTwin2 += 1;
                                }

                            }

                            int realTwins = 0;
                            if (quantityPosValTwin1 == 2 || quantityPosValTwin1 == 3){
                                if (quantityPosValTwin2 == 2 || quantityPosValTwin2 == 3){
                                    if (quantityPosValTwin1 != quantityPosValTwin2){
                                        for (int j = 1; j <= n; j++) {
                                            for (int k = 1; k <= n; k++) {

                                                if (sudoku[row][twinCol1][j] == sudoku[row][twinCol2][k]){


                                                    if (sudoku[row][twinCol1][j]  != 0 &&  list[sudoku[row][twinCol1][j] -  1][2] == 1){
                                                        realTwins++;
                                                    }
                                                }
                                            }


                                        }

                                    }
                                }
                            }
                            if (realTwins == 2) {
                                log.debug("Twins by {}", value2);

                            }

                        }
                    }
                }
            }

        }

    }
    
    private static void triplets() {
        
        int tripletCol1 = 0;
        int tripletCol2 = 0;
        int tripletCol3 = 0;
        
        int triplets = 0;

        for (int row = 0; row < n; row++) {

            // the coincidences
            List<Integer> coincidencesList = coincidencesList = new ArrayList<Integer>(n);

            for (int col = 0; col < n; col++) {
                triplets = 0;
                // search for possibles values
                List<Integer> list = new ArrayList<Integer>(n);
                for (int possibleValue = 1; possibleValue <= n; possibleValue++) {
                    if ( sudoku[row][col][0] == 0 && sudoku[row][col][possibleValue] > 0) {
                        list.add(sudoku[row][col][possibleValue]);

                    }
                }

                for (int col2 = col + 1; col2 < n - 1; col2++) {

                    int col3 = col2 + 1;
                    
                    List<Integer> list2 = new ArrayList<Integer>(n);
                    List<Integer> list3 = new ArrayList<Integer>(n);
                    
                    for (int possibleValue = 1; possibleValue <= n; possibleValue++) {
                        if ( sudoku[row][col2][0] == 0 && sudoku[row][col2][possibleValue] > 0) {
                            list2.add(sudoku[row][col2][possibleValue]);

                        }
                    }

                    for (int possibleValue = 1; possibleValue <= n; possibleValue++) {
                        if ( sudoku[row][col3][0] == 0 && sudoku[row][col3][possibleValue] > 0) {
                            list3.add(sudoku[row][col3][possibleValue]);

                        }
                    }


                    // order list
                    Set<Integer> set = new HashSet<>(list2);
                    list2.clear();
                    list2.addAll(set);
                    Collections.sort(list2);
                    
                    // order list
                    Set<Integer> set2 = new HashSet<>(list3);
                    list3.clear();
                    list3.addAll(set);
                    Collections.sort(list3);

                    // order list
                    set = new HashSet<>(list);
                    list.clear();
                    list.addAll(set);
                    Collections.sort(list);

                    // get list's size
                    int maxL = list.size();
                    int maxL2 = list2.size();
                    int maxL3 = list3.size();

                    // number of coincidences
                    int coincidence= 0;

                    // for each value in list
                    for (int value : list){

                        // if both have same possible values save that coincidence
                        if(list2.contains(value) && list3.contains(value)){
                            coincidence ++;
                            coincidencesList.add(value);
                        }
                    }

                    if (coincidence == 3) {

                        // maybe twins but needs to check all cells
                        triplets++;

                        if (triplets == 1) {
                            tripletCol1 = col;
                            tripletCol2 = col2;
                            tripletCol3 = col3;

                        }else {
                            // cheack another row
                            col = n;
                            col2 = n;
                            triplets = 0;
                        }
                    }


                }
                
            }

            // only there triplets
            if (triplets == 2) {
                for (int possibleValuesIndex = 1; possibleValuesIndex <= n; possibleValuesIndex++) {
                    sudoku[row][tripletCol1][possibleValuesIndex] = 0;
                    sudoku[row][tripletCol2][possibleValuesIndex] = 0;
                    sudoku[row][tripletCol3][possibleValuesIndex] = 0;
                }

                int it = 1;
                for (int coincidence : coincidencesList){
                    sudoku[row][tripletCol1][it] = coincidence;
                    sudoku[row][tripletCol2][it] = coincidence;
                    sudoku[row][tripletCol3][it] = coincidence;
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
        List<Integer> possibleValuesWithoutRepetitions;

        for (int i =0; i < n; i++) {
            possibleValues = new ArrayList<Integer>(n * n);
            possibleValuesWithoutRepetitions = new ArrayList<Integer>(n * n);
            if (aux == sqrt) {
                rowBound += sqrt;
                aux = 0;
                colBound = 0;
            }

            int itRow = 0;

            for (int j = rowBound; itRow < sqrt; j++, itRow++) {
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    int value = sudoku[j][k][0];
                    if (value == 0) {
                        for (int l = 1; l <= n; l++) {
                            int possibleValue = sudoku[j][k][l];
                            if (possibleValue > 0) {
                                possibleValues.add(possibleValue);
                                possibleValuesWithoutRepetitions.add(possibleValue);
                            }
                        }
                    }
                }
            }
            Set<Integer> set = new HashSet<Integer>(possibleValues);
            possibleValues.clear();
            possibleValues.addAll(set);

            // Store the unique value
            int unique = -1;

            // Count that help to look a unique value
            int counter = 0;

            // For each possible value
            for (int value : possibleValues) {
                counter = 0;

                // For each possible value without repetition
                for (int v : possibleValuesWithoutRepetitions) {

                    if (value == v) {
                        counter++;
                    }

                }

                // There are only one repetition
                if (counter == 1) {
                    unique = value;
                    break;
                }
            }

            itRow = 0;

            for (int j = rowBound; itRow < sqrt; j++, itRow++) {
                int itCol = 0;
                for (int k = colBound; itCol < sqrt; k++, itCol++) {
                    int value = sudoku[j][k][0];
                    if (value == 0) {
                        for (int l = 1; l <= n; l++) {

                            if(sudoku[j][k][l] == unique) {
                                sudoku[j][k][0] = unique;
                                cleanPossibleValues(j,k);
                                log.debug("Cambio por LoneRanger BOX");
                                return;
                            }

                        }
                    }
                }
            }

        }

    }

    private static void loneRangerRow() {

        List<Integer> possibleValues;
        List<Integer> possibleValuesWithoutRepetitions;

        // for each row
        for (int row = 0; row < n; row++) {
            possibleValues = new ArrayList<Integer>(n * n);
            possibleValuesWithoutRepetitions = new ArrayList<Integer>(n * n);

            // for each col
            for (int col = 0; col < n; col++) {

                // search for possible values for sudoku[row][col]
                for (int indexPossibleValue = 1; indexPossibleValue <= n; indexPossibleValue++) {
                    int possibleValue = sudoku[row][col][indexPossibleValue];
                    if (possibleValue > 0) {
                        possibleValues.add(possibleValue);
                        possibleValuesWithoutRepetitions.add(possibleValue);
                    }
                }


            }

            // now I have all possible values of all row

            // remove duplicates
            Set<Integer> set = new HashSet<Integer>(possibleValues);
            possibleValues.clear();
            possibleValues.addAll(set);

            // Store the unique value
            int unique = -1;

            // Count that help to look a unique value
            int counter = 0;

            // For each possible value
            for (int value : possibleValues) {
                counter = 0;

                // For each possible value without repetition
                for (int v : possibleValuesWithoutRepetitions) {

                    if (value == v) {
                        counter++;
                    }

                }

                // There are only one repetition
                if (counter == 1) {
                    unique = value;
                    break;
                }
            }

            // If there are only one repetition
            if (counter == 1) {

                // for each column
                for (int col = 0; col < n; col++) {

                    // For each possible value in [row][column]
                    for (int i = 1; i <= n; i++) {

                        // The unique value belongs to [row][col][i]
                        if (unique == sudoku[row][col][i]) {

                            sudoku[row][col][0] = unique;
                            cleanPossibleValues(row,col);
                            log.debug("Cambio por LoneRanger ROW");
                            return;
                        }
                    }
                }
            }

        }

    }

    private static void loneRangerCol() {

        List<Integer> possibleValues;
        List<Integer> possibleValuesWithoutRepetitions;

        // for each col
        for (int col = 0; col < n; col++) {
            possibleValues = new ArrayList<Integer>(n * n);
            possibleValuesWithoutRepetitions = new ArrayList<Integer>(n * n);

            // for each col
            for (int row = 0; row < n; row++) {

                // search for possible values for sudoku[col][row]
                for (int indexPossibleValue = 1; indexPossibleValue <= n; indexPossibleValue++) {
                    int possibleValue = sudoku[row][col][indexPossibleValue];
                    if (possibleValue > 0) {
                        possibleValues.add(possibleValue);
                        possibleValuesWithoutRepetitions.add(possibleValue);
                    }
                }


            }

            // now I have all possible values of all col

            // remove duplicates
            Set<Integer> set = new HashSet<Integer>(possibleValues);
            possibleValues.clear();
            possibleValues.addAll(set);

            // Store the unique value
            int unique = -1;

            // Count that help to look a unique value
            int counter = 0;

            // For each possible value
            for (int value : possibleValues) {
                counter = 0;

                // For each possible value without repetition
                for (int v : possibleValuesWithoutRepetitions) {

                    if (value == v) {
                        counter++;
                    }

                }

                // There are only one repetition
                if (counter == 1) {
                    unique = value;
                    break;
                }
            }

            // If there are only one repetition
            if (counter == 1) {

                // for each row
                for (int row = 0; row < n; row++) {

                    // For each possible value in [col][column]
                    for (int i = 1; i <= n; i++) {

                        // The unique value belongs to [col][row][i]
                        if (unique == sudoku[row][col][i]) {

                            sudoku[row][col][0] = unique;
                            cleanPossibleValues(row,col);
                            log.debug("Cambio por LoneRanger COL");
                            return;
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
                        log.debug("Cambio por ELIMINATION");
                        return;
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

    /**
     * Read a text file and populate the board
     * @param path of file
     * @return populated board
     */
    private static int[][][] readFile(String path) {

        int[][][] board = new int[0][][];
        try {

            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            n = Integer.parseInt(myReader.nextLine());
            sqrt = (int) Math.sqrt(n);
            board = new int[n][n][n+1];

            for(int i =0; i < n; i++) {
                String line = myReader.nextLine();
                int size = line.length();
                String numStr = "";
                int col = 0;
                for(int j = 0; j < size; j++){
                    if (line.charAt(j) != ','){
                        numStr += line.charAt(j);
                    }else{
                        board[i][col][0] = Integer.parseInt(numStr);
                        col++;
                        numStr = "";
                    }
                }

                board[i][col][0] = Integer.parseInt(numStr);

            }} catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        return board;
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

        // clean the own possible values
        for (int possibleValues = 1; possibleValues <= n; possibleValues++) {
            sudoku[row][col][possibleValues] = 0;
        }

        // clean in r,col
        for (int r = 0; r < n; r++) {
            for (int possibleValues = 1; possibleValues <= n; possibleValues++) {
                if (val == sudoku[r][col][possibleValues]) {
                    sudoku[r][col][possibleValues] = 0;
                }
            }
        }

        // clean in c, row
        for (int c = 0; c < n; c++) {
            for (int possibleValues = 1; possibleValues <= n; possibleValues++) {
                if (val == sudoku[row][c][possibleValues]) {
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
        int aux3 = boxIndexR;
        for (int r = 0; r < sqrt; r++) {
            for (int c = 0; c < sqrt; c++) {

                //log.debug("Row: {}    Col: {}", boxIndexR, boxIndexC);

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
        anyChange = true;
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

    private static void percentageSolved() {

        int pot = n * n;
        int counter = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                if (sudoku[row][col][0] == solution[row][col][0]){
                    counter++;
                }
            }
        }

        double percentage = 100 * counter / pot;
        log.debug("Counter: {}", counter);
        log.debug("The percentage solved is: {}", percentage );

    }

    private static boolean solve(int[][][] board) {
        for (int row = 0; row < n; row++) {
            for (int column = 0; column < n; column++) {
                if (board[row][column][0] == 0) {
                    for (int k = 1; k <= n; k++) {
                        board[row][column][0] = k;
                        if (isValid(board, row, column) && solve(board)) {
                            return true;
                        }
                        board[row][column][0] = 0;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isValid(int[][][] board, int row, int column) {
        return (rowConstraint(board, row)
                && columnConstraint(board, column)
                && subsectionConstraint(board, row, column));
    }

    private static boolean rowConstraint(int[][][] board, int row) {
        boolean[] constraint = new boolean[n];
        return IntStream.range(0, n)
                .allMatch(column -> checkConstraint(board, row, constraint, column));
    }

    private static boolean columnConstraint(int[][][] board, int column) {
        boolean[] constraint = new boolean[n];
        return IntStream.range(0, n)
                .allMatch(row -> checkConstraint(board, row, constraint, column));
    }

    private static boolean subsectionConstraint(int[][][] board, int row, int column) {
        boolean[] constraint = new boolean[n];
        int subsectionRowStart = (row / sqrt) * sqrt;
        int subsectionRowEnd = subsectionRowStart + sqrt;

        int subsectionColumnStart = (column / sqrt) * sqrt;
        int subsectionColumnEnd = subsectionColumnStart + sqrt;

        for (int r = subsectionRowStart; r < subsectionRowEnd; r++) {
            for (int c = subsectionColumnStart; c < subsectionColumnEnd; c++) {
                if (!checkConstraint(board, r, constraint, c)) return false;
            }
        }
        return true;
    }

    private static boolean checkConstraint(
            int[][][] board,
            int row,
            boolean[] constraint,
            int column) {
        if (board[row][column][0] != 0) {
            if (!constraint[board[row][column][0] - 1]) {
                constraint[board[row][column][0] - 1] = true;
            } else {
                return false;
            }
        }
        return true;
    }

}