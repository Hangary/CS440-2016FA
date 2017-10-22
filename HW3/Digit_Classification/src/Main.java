import java.io.*;

/**
 * Created by qixinzhu on 11/2/16.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        //Part_1_1();

        //Part_1_2();

        //Part_1_3();

        Part_1_4();
    }

    public static void Part_1_1() throws IOException {
        Data trainingData = DataParser.parseData("./Digit_Classification/digitdata/trainingimages", "./Digit_Classification/digitdata/traininglabels");
        Solution sol = new Solution("./Digit_Classification/digitdata/testimages", "./Digit_Classification/digitdata/testlabels");

        //double bestK = trySmooth(sol, trainingData, 0.1, 10, 0.1, "./Digit_Classification/smoothPlot_1_1.txt");
        //trainingData.setSmooth(bestK);

        long train_start = System.currentTimeMillis();
        /**
         * larger smoothing value gives slightly less classifcation accuracy
         *      but much nicer Odds Ratio plot
         */
        trainingData.setSmooth(10);
        trainingData.calculateProbability();
        long train_end = System.currentTimeMillis();
        long test_start = System.currentTimeMillis();
        sol.maxPosterior(trainingData);
        sol.evaluate();
        long test_end = System.currentTimeMillis();
        sol.printSolution("./Digit_Classification/Solution_1_1.txt", train_end - train_start, test_end - test_start);

        /**
         * four pairs of digits that have the highest confusion rates according to the confusion matrix
         *
         * 4 -> 9: 17.76%
         * 5 -> 3: 17.39%
         * 7 -> 9: 14.15%
         * 8 -> 3: 12.62%
         */
        trainingData.printOddsImage(1, 8);

        trainingData.printOddsImage(4, 9);
        trainingData.printOddsImage(5, 3);
        trainingData.printOddsImage(7, 9);
        trainingData.printOddsImage(8, 3);
    }

    public static double trySmooth(Solution sol, Data trainingData, double start, double end, double interval, String outputFile) throws FileNotFoundException {
        double bestK = start;
        double bestRate = 0;
        PrintWriter out = new PrintWriter(outputFile);
        while (start <= end) {
            trainingData.setSmooth(start);
            trainingData.calculateProbability();

            //trainingData.printProbData();
            sol.maxPosterior(trainingData);
            sol.evaluate();
            double rate = sol.getAccuracyRate();
            System.out.printf("%.2f\t%.2f%%\n", start, rate * 100);
            out.printf("%.2f\t%.2f%%\n", start, rate * 100);
            if (rate > bestRate) {
                bestRate = rate;
                bestK = start;
            }
            start += interval;
        }

        out.close();
        return bestK;
    }

    public static void Part_1_2() throws IOException {
        boolean[] GroupType = {false, true};
        int[][] GroupSize = {{2, 2}, {2, 4}, {4, 2}, {4, 4}, {2, 3}, {3, 2}, {3, 3}};
        for (boolean overlap : GroupType) {
            int caseNum = overlap ? 7 : 4;
            for (int i = 0; i < caseNum; i++) {
                int[] gSize = GroupSize[i];
                int gRow = gSize[0];
                int gCol = gSize[1];
                System.out.printf("\nCurrect Case: %s %dX%d\n", overlap ? "Overlap" : "Disjoint", gRow, gCol);

                long train_start = System.currentTimeMillis();
                Data trainingData = DataParser.parseData(gRow, gCol, overlap, "./Digit_Classification/digitdata/trainingimages", "./Digit_Classification/digitdata/traininglabels");
                Solution sol = new Solution("./Digit_Classification/digitdata/testimages", "./Digit_Classification/digitdata/testlabels");
                //System.out.printf("%d, %d, %d, %d\n",trainingData.getGroupRow(), trainingData.getGroupCol(), trainingData.getLikeliRow(), trainingData.getLikeliCol());

                /**
                 * smoothing k-value
                 */
                //trainingData.useDefaultSmooth();
                trainingData.setSmooth(0.01);
                trainingData.calculateProbability();
                long train_end = System.currentTimeMillis();

                long test_start = System.currentTimeMillis();
                sol.maxPosterior(trainingData);
                sol.evaluate();
                long test_end = System.currentTimeMillis();
                sol.printSolution(String.format("./Digit_Classification/Solution_1_2_%s_%dX%d.txt",
                        overlap ? "Overlap" : "Disjoint", gRow, gCol), train_end - train_start, test_end - test_start);
            }
        }
    }

    public static void Part_1_3() throws IOException {
        Data trainingData = DataParser.parseData(0, 0, false, "./Digit_Classification/digitdata/trainingimages", "./Digit_Classification/digitdata/traininglabels");
        Solution sol = new Solution("./Digit_Classification/digitdata/testimages", "./Digit_Classification/digitdata/testlabels");

        long train_start = System.currentTimeMillis();
        //trainingData.useDefaultSmooth();
        trainingData.setSmooth(0.05);
        trainingData.calculateProbability();
        long train_end = System.currentTimeMillis();
        long test_start = System.currentTimeMillis();
        sol.maxPosterior(trainingData);
        sol.evaluate();
        long test_end = System.currentTimeMillis();
        sol.printSolution("./Digit_Classification/Solution_1_3.txt", train_end - train_start, test_end - test_start);
    }

    public static void Part_1_4() throws IOException {
        Data trainingData = DataParser.parseFaceData(1, 1, false, "./Digit_Classification/facedata/facedatatrain", "./Digit_Classification/facedata/facedatatrainlabels");
        Solution sol = new Solution("./Digit_Classification/facedata/facedatatest", "./Digit_Classification/facedata/facedatatestlabels");

        //double bestK = trySmooth(sol, trainingData, 0.001, 0.1, 0.001, "./Digit_Classification/smoothPlot_1_4.txt");
        //trainingData.setSmooth(bestK);

        long train_start = System.currentTimeMillis();
        //trainingData.setSmooth(2);
        //trainingData.setSmooth(0.001);
        //trainingData.useDefaultSmooth();
        trainingData.setSmooth(100);
        trainingData.calculateProbability();
        long train_end = System.currentTimeMillis();
        long test_start = System.currentTimeMillis();
        sol.maxPosterior(trainingData);
        sol.evaluate();
        long test_end = System.currentTimeMillis();
        sol.printSolution(String.format("./Digit_Classification/Solution_1_4_%d.txt", (int)trainingData.smooth_k), train_end - train_start, test_end - test_start);
    }
}

