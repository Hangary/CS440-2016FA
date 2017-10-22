import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by qixinzhu on 11/2/16.
 */
public class Solution {

    public static double EPOCH_CONSTANT = 5;

    /**
     * instance variables
     */

    protected int labelNum, epoch;
    protected double[][] confusionMatrix;
    protected double overallAccuracy;
    Perceptron[] classifiers;
    List<Image> trainImages;
    int trainedTimes;

    public Solution(int labelNum, int epoch, List<Image> trainImages, boolean bias, boolean randomWeight, boolean randomOrder, boolean overlap, int gRow, int gCol) throws IOException {
        this.labelNum = labelNum;
        classifiers = new Perceptron[labelNum];
        for (int i = 0; i < labelNum; i++) {
            classifiers[i] = new Perceptron(i, bias, randomWeight, randomOrder, overlap, gRow, gCol);
        }
        this.trainImages = trainImages;
        confusionMatrix = new double[labelNum][labelNum];
        overallAccuracy = 0;
        this.epoch = epoch;
        trainedTimes = 0;
    }

    public void train(int times) {
        for (int i = 0; i < times; i++) {
            double alpha = EPOCH_CONSTANT * 1.0 / (EPOCH_CONSTANT + trainedTimes);
            for (Image img : trainImages) {
                int testLabel = classify(img);
                int trueLabel = img.getTrueLabel();
                int update = (testLabel == trueLabel ? 0 : 1);
                classifiers[trueLabel].updateWeight(img, alpha, update, 0);
                classifiers[testLabel].updateWeight(img, alpha, 0, update);
            }
            trainedTimes++;
        }
    }

    public void train() {
        this.train(epoch);
    }


    public void test(List<Image> testImages) {
        int[] trueCount = new int[labelNum];
        for (Image img : testImages) {
            trueCount[img.getTrueLabel()]++;
            int bestLabel = classify(img);
            img.testLabel = bestLabel;
            confusionMatrix[img.getTrueLabel()][bestLabel]++;
            if (bestLabel == img.getTrueLabel()) overallAccuracy++;
        }

        for (int i = 0; i < labelNum; i++) {
            for (int j = 0; j < labelNum; j++) {
                confusionMatrix[i][j] = confusionMatrix[i][j] * 1.0 / trueCount[i];
            }
        }
        overallAccuracy /= testImages.size();
    }

    protected int classify(Image img) {
        double maxChance = Integer.MIN_VALUE;
        int bestLabel = -1;
        for (int i = 0; i < labelNum; i++) {
            double result = classifiers[i].classify(img);
            if (result > maxChance) {
                maxChance = result;
                bestLabel = i;
            }
        }
        img.maxChance = maxChance;
        return bestLabel;
    }

    public double getAccuracyRate() {
        return overallAccuracy;
    }

    public void printSolution(String outputFile, long trainTime, long testTime) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFile);
        out.printf("Using Epoch = %d, Overall Accuracy = %.2f%%\n", trainedTimes, overallAccuracy * 100);
        out.printf("Training Time = %dms, Testing Time = %dms\n", trainTime, testTime);

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < confusionMatrix.length; i++) {
            sb.append(String.format("%d: %.2f%%,\t", i, confusionMatrix[i][i] * 100));
        }
        out.print("Classification Rate for each digit:\n");
        out.printf("%s]\n\n", sb.toString().trim());

        out.print("Confusion Matrix:\n");
        sb = new StringBuilder();
        for (int i = 0; i < confusionMatrix.length; i++) {
            sb.append(String.format("\t\t%d", i));
        }
        out.println(sb.toString());
        for (int i = 0; i < confusionMatrix.length; i++) {
            sb = new StringBuilder();
            sb.append(String.format("%d\t[", i));
            for (int j = 0; j < confusionMatrix.length; j++) {
                sb.append(String.format("%.2f%%\t", confusionMatrix[i][j] * 100));
            }
            out.printf("%s]\n", sb.toString());
        }

        out.close();
    }

    public void visualizeWeights() throws IOException {
        for (Perceptron p : classifiers) p.printImage();
    }

    public static void main(String[] args) throws IOException {
        int epoch = 20;
        List<Image> trainImages = DataParser.parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
        List<Image> testImages = DataParser.parseData("./Digit/digitdata/testimages", "./Digit/digitdata/testlabels");
        long train_start = System.currentTimeMillis();
        Solution s = new Solution(10, epoch, trainImages, true, false, true, false, 1, 1);
        s.train();
        long train_end = System.currentTimeMillis();
        long test_start = System.currentTimeMillis();
        s.test(testImages);
        long test_end = System.currentTimeMillis();
        s.printSolution(String.format("./Digit/results/Part_2_1_%d.txt", epoch), train_end - train_start, test_end - test_start);
    }

}
