import java.io.*;
import java.util.*;

/**
 * Created by qixinzhu on 11/21/16.
 */
public class KNearest {

    private class Neighbor implements Comparable<Neighbor> {
        Image img;
        double similarity;

        protected Neighbor(Image i, double s) {
            img = i;
            similarity = s;
        }

        public int compareTo(Neighbor other) {
            if (this.similarity < other.similarity) return -1;
            else if (this.similarity > other.similarity) return 1;
            else return 0;
        }
    }

    /**
     * static variable
     */
    public static int MAX_K = 200;

    /**
     * instance variables
     */

    List<Image> trainImages;
    int k, labelNum;
    int row, col;
    protected double[][] confusionMatrix;
    protected double overallAccuracy;

    public KNearest(List<Image> images, int k, int l) {
        trainImages = images;
        this.k = k;
        row = DataParser.dataRow;
        col = DataParser.dataCol;
        labelNum = l;
        confusionMatrix = new double[labelNum][labelNum];
    }

    public double getAccuracyRate() {
        return overallAccuracy;
    }

    private double getManhattanDistance(Image img1, Image img2) {
        double dist = 0;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                if (img1.getFeatureValue(r, c, 1, 1) != img2.getFeatureValue(r, c, 1, 1))
                    dist++;
            }
        }
        return dist;
    }

    private double getDotProduct(Image img1, Image img2) {
        double sim = 0;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int di = img1.getFeatureValue(r, c, 1, 1);
                int dj = img2.getFeatureValue(r, c, 1, 1);
                sim += di * dj;
            }
        }
        return sim;
    }

    private double getCosineSimilarity(Image img1, Image img2) {
        double d1 = 0, d2 = 0;
        double cos = 0;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int di = img1.getFeatureValue(r, c, 1, 1);
                int dj = img2.getFeatureValue(r, c, 1, 1);
                cos += di * dj;
                d1 += di * di;
                d2 += dj * dj;
            }
        }
        d1 = Math.sqrt(d1);
        d2 = Math.sqrt(d2);
        return cos / (d1 * d2);
    }

    public int classify(Image image) {
        Queue<Neighbor> kNearest = new PriorityQueue<>(k);
        for (Image img : trainImages) {

            double similarity = getCosineSimilarity(img, image);
            //double similarity = 1.0 / getManhattanDistance(img, image);
            //double similarity = getDotProduct(img, image);

            if (kNearest.size() < k || similarity > kNearest.peek().similarity) {
                kNearest.add(new Neighbor(img, similarity));
            }
            if (kNearest.size() > k)
                kNearest.poll();
        }
        int[] labels = new int[10];
        int maxCount = 0, testLabel = -1;
        for (Neighbor nb : kNearest) {
            int nbLabel = nb.img.getTrueLabel();
            labels[nbLabel]++;
            if (labels[nbLabel] > maxCount) {
                testLabel = nbLabel;
                maxCount = labels[nbLabel];
            }
        }
        return testLabel;
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

    public void printSolution(String outputFile, long testTime) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFile);
        out.printf("%d-Nearest Neighbor, Overall Accuracy = %.2f%%\n", k, overallAccuracy * 100);
        out.printf("Testing Time = %dms\n", testTime);

        System.out.printf("%d-Nearest Neighbor, Overall Accuracy = %.2f%%\n", k, overallAccuracy * 100);
        System.out.printf("Testing Time = %dms\n", testTime);

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
}
