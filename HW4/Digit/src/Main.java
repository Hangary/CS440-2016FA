import java.io.*;
import java.util.*;

/**
 * Created by qixinzhu on 11/2/16.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Part_2_1();

        //Part_2_2();

        //Part_2_3();

        //Part_2_4();

        Part_2_5();
    }

    public static void Part_2_1() throws IOException {
        Solution.EPOCH_CONSTANT = 5;
        int limit = 100;

        List<Image> trainImages = DataParser.parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
        List<Image> testImages = DataParser.parseData("./Digit/digitdata/testimages", "./Digit/digitdata/testlabels");

        PrintWriter out = new PrintWriter(new File(String.format("./Digit/results/Part_2_1_curve_%d.txt", (int) Solution.EPOCH_CONSTANT)));
        out.println("epoch\taccuracy");
        Solution s = new Solution(10, -1, trainImages, true, false, true, false, 1, 1);
        int bestEpoch = 1;
        double bestAcc = 0;
        long train_time = 0, test_time = 0;
        long start;
        for (int epoch = 1; epoch <= limit; epoch++) {
            start = System.currentTimeMillis();
            s.train(1);
            train_time += System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            s.test(testImages);
            test_time += System.currentTimeMillis() - start;
            System.out.printf("[%dms] Epoch = %d, Accuracy = %.3f\n", train_time + test_time, epoch, s.getAccuracyRate());
            out.printf(" %d\t\t%.2f%%\n", epoch, s.getAccuracyRate() * 100);
            if (s.getAccuracyRate() > bestAcc) {
                bestAcc = s.getAccuracyRate();
                bestEpoch = epoch;
            }
            s.printSolution(String.format("./Digit/results/Part_2_1_%d.txt", epoch), train_time, test_time);
        }
        out.close();
        for (int i = 1; i <= limit; i++) {
            if (i != bestEpoch) {
                File toDelete = new File(String.format("./Digit/results/Part_2_1_%d.txt", i));
                toDelete.delete();
            }
        }
    }

    /**
     * Discrete & overlap grouped pixels
     */
    public static void Part_2_3() throws IOException {
        Solution.EPOCH_CONSTANT = 5;
        boolean[] GroupType = {false, true};
        int[][] GroupSize = {{2, 2}, {2, 4}, {4, 2}, {4, 4}, {2, 3}, {3, 2}, {3, 3}};
        for (boolean overlap : GroupType) {
            int caseNum = overlap ? 7 : 4;
            for (int i = 0; i < caseNum; i++) {
                int[] gSize = GroupSize[i];
                int gRow = gSize[0];
                int gCol = gSize[1];
                System.out.printf("\nCurrect Case: %s %dX%d\n", overlap ? "Overlap" : "Disjoint", gRow, gCol);
                List<Image> trainImages = DataParser.parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
                List<Image> testImages = DataParser.parseData("./Digit/digitdata/testimages", "./Digit/digitdata/testlabels");
                long train_start = System.currentTimeMillis();
                Solution s = new Solution(10, 40, trainImages, true, false, true, overlap, gRow, gCol);
                s.train();
                long train_end = System.currentTimeMillis();
                long test_start = System.currentTimeMillis();
                s.test(testImages);
                long test_end = System.currentTimeMillis();
                s.printSolution(String.format("./Digit/results/Solution_2_3_%s_%dX%d.txt",
                        overlap ? "Overlap" : "Disjoint", gRow, gCol), train_end - train_start, test_end - test_start);
                System.out.printf("[%dms] Accuracy = %.3f\n", test_end - train_start, s.getAccuracyRate());
            }
        }
    }

    /**
     * Visualize the learned perceptron weights for each class as an image
     */

    public static void Part_2_4() throws IOException {
        List<Image> trainImages = DataParser.parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
        Solution s = new Solution(10, 100, trainImages, true, false, true, false, 1, 1);
        s.train();
        s.visualizeWeights();
    }

    /**
     * k-nearest neighbor
     */
    public static void Part_2_2() throws FileNotFoundException {
        List<Image> trainImages = DataParser.parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
        List<Image> testImages = DataParser.parseData("./Digit/digitdata/testimages", "./Digit/digitdata/testlabels");

        //PrintWriter out = new PrintWriter(new File("./Digit/results/Part_2_2_curve.txt"));
        //out.println("k\taccuracy\ttest_time");
        int bestK = 1, limit = 11;
        double bestAcc = 0;
        int k;
        for (k = 11; k <= limit; k++) {
            KNearest kn = new KNearest(trainImages, k, 10);
            long test_start = System.currentTimeMillis();
            kn.test(testImages);
            long test_end = System.currentTimeMillis();
            //out.printf("%d\t%.2f%%\t[%dms]\n", k, kn.getAccuracyRate() * 100, test_end - test_start);
            if (kn.getAccuracyRate() > bestAcc) {
                bestAcc = kn.getAccuracyRate();
                bestK = k;
            }
            kn.printSolution(String.format("./Digit/results/Part_2_2_%d-Nearest.txt", k), test_end - test_start);
        }
        //out.close();
        for (int i = 2; i < limit; i++) {
            if (i != bestK) {
                File toDelete = new File(String.format("./Digit/results/Part_2_2_%d-Nearest.txt", i));
                toDelete.delete();
            }
        }
    }

    /**
     * Differentiable Perceptron
     */
    public static void Part_2_5() throws IOException {
        Solution.EPOCH_CONSTANT = 5;
        int limit = 100;

        List<Image> trainImages = DataParser.parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
        List<Image> testImages = DataParser.parseData("./Digit/digitdata/testimages", "./Digit/digitdata/testlabels");
        PrintWriter out = new PrintWriter(new File(String.format("./Digit/results/Part_2_5_curve_%d.txt", (int) Solution.EPOCH_CONSTANT)));
        out.println("epoch\taccuracy");

        Solution s = new SolutionDiff(10, -1, trainImages, true, false, true, true);

        int bestEpoch = 1;
        double bestAcc = 0;
        long train_time = 0, test_time = 0;
        long start;
        for (int epoch = 1; epoch <= limit; epoch++) {
            start = System.currentTimeMillis();
            s.train(1);
            train_time += System.currentTimeMillis() - start;
            start = System.currentTimeMillis();
            s.test(testImages);
            test_time += System.currentTimeMillis() - start;
            System.out.printf("[%dms] Epoch = %d, Accuracy = %.3f\n", train_time + test_time, epoch, s.getAccuracyRate());
            if (s.getAccuracyRate() > bestAcc) {
                bestAcc = s.getAccuracyRate();
                bestEpoch = epoch;
            }
            out.printf(" %d\t\t%.2f%%\n", epoch, s.getAccuracyRate() * 100);
            s.printSolution(String.format("./Digit/results/Part_2_5_%d.txt", epoch), train_time, test_time);
        }
        for (int i = 1; i <= limit; i++) {
            if (i != bestEpoch) {
                File toDelete = new File(String.format("./Digit/results/Part_2_5_%d.txt", i));
                toDelete.delete();
            }
        }
        out.close();
    }
}

