import sun.awt.image.codec.JPEGImageEncoderImpl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by qixinzhu on 11/4/16.
 */
public class Solution {
    protected Data trainingData;
    protected List<Integer> testLabels;
    protected List<Integer> trueLabels;
    protected double[][] confusionMatrix;
    protected double overallAccuracy;

    public Solution(Data trainData, String testFile) throws FileNotFoundException {
        trainingData = trainData;
        trueLabels = new ArrayList<>();
        testLabels = new ArrayList<>();
        overallAccuracy = 0;

        Scanner in = new Scanner(new File(testFile));
        int temp = 0;
        long start_time = System.currentTimeMillis();

        while (in.hasNextLine()) {
            temp++;
            int truelabel = in.nextInt();
            trueLabels.add(truelabel < 0 ? 0 : truelabel);
            int testlabel = -1;
            double maxPosterior = Integer.MIN_VALUE;
            String document = in.nextLine();
            for (int label = 0; label < trainingData.getLabelNum(); label++) {
                double posterior = trainingData.getLogPosterior(label, document);
                if (posterior > maxPosterior) {
                    maxPosterior = posterior;
                    testlabel = label;
                }
            }
            testLabels.add(testlabel);
            if (temp % 50 == 0) {
                System.out.printf("Finish %d tests in %d[ms]\n", temp, System.currentTimeMillis() - start_time);
                start_time = System.currentTimeMillis();
            }
        }

        confusionMatrix = new double[trainingData.getLabelNum()][trainingData.getLabelNum()];
        in.close();
    }

    public void evaluate() {
        int labelNum = trainingData.getLabelNum();
        int[] trueCount = new int[labelNum];
        for (int i = 0; i < trueLabels.size(); i++) {
            confusionMatrix[trueLabels.get(i)][testLabels.get(i)]++;
            trueCount[trueLabels.get(i)]++;
            if (trueLabels.get(i) == testLabels.get(i))
                overallAccuracy += 1.0;
        }
        for (int i = 0; i < labelNum; i++) {
            for (int j = 0; j < labelNum; j++) {
                confusionMatrix[i][j] = confusionMatrix[i][j] * 1.0 / trueCount[i];
            }
        }
        overallAccuracy /= trueLabels.size();
    }

    public double getAccuracyRate() {
        return overallAccuracy;
    }

    private class MyWord implements Comparable<MyWord> {
        String word;
        double value;

        private MyWord(String w, double v) {
            word = w;
            value = v;
        }

        public int compareTo(MyWord other) {
            if (this.value < other.value) return -1;
            else if (this.value > other.value) return 1;
            else return 0;
        }
    }

    /**
     * Top 10 words with highest likelihood
     * Top 10 words with highest odds ratio
     */
    private Queue<MyWord>[] topLikelihood, topOddsRatio;

    public void updateTopTen() {
        assertTrue(trainingData.getLabelNum() == 2);
        topLikelihood = new Queue[trainingData.getLabelNum()];
        topOddsRatio = new Queue[trainingData.getLabelNum()];
        for (int label = 0; label < trainingData.getLabelNum(); label++) {
            topLikelihood[label] = new PriorityQueue<>(10);
            topOddsRatio[label] = new PriorityQueue<>(10);
            for (String word : trainingData.dictionary[label].keySet()) {
                double likelihood = trainingData.getWordLikelihood(label, word);
                if (topLikelihood[label].size() < 10 || topLikelihood[label].peek().value < likelihood) {
                    topLikelihood[label].add(new MyWord(word, likelihood));
                    if (topLikelihood[label].size() > 10) topLikelihood[label].poll();
                }
                //if (!trainingData.containsWord(1 - label, word)) continue;
                double oddsRatio = likelihood / trainingData.getWordLikelihood(1 - label, word);
                if (topOddsRatio[label].size() < 10 || topOddsRatio[label].peek().value < oddsRatio) {
                    topOddsRatio[label].add(new MyWord(word, oddsRatio));
                    if (topOddsRatio[label].size() > 10) topOddsRatio[label].poll();
                }
            }
        }
    }


    public void printSolution(String outputFile, long trainTime, long testTime) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFile);
        out.printf("Using Smoothing K-value = %.4f, Overall Accuracy = %.1f%%\n", trainingData.smooth_k, overallAccuracy * 100);
        out.printf("Training Time = %dms, Testing Time = %dms\n", trainTime, testTime);

        System.out.printf("\n\t%s\n",outputFile);
        System.out.printf("Using Smoothing K-value = %.4f, Overall Accuracy = %.1f%%\n", trainingData.smooth_k, overallAccuracy * 100);
        System.out.printf("Training Time = %dms, Testing Time = %dms\n", trainTime, testTime);
        System.out.println(trainingData.getTotalDocCount());
        System.out.println(trainingData.uniqueWords.size());

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < trainingData.getLabelNum(); i++) {
            sb.append(String.format("%d: %.2f%%,\t", (trainingData.getLabelNum() == 2 && i == 0) ? -1 : i, confusionMatrix[i][i] * 100));
        }
        out.print("Classification Rate for each topic:\n");
        out.printf("%s]\n\n", sb.toString().trim());

        out.print("Confusion Matrix:\n");
        sb = new StringBuilder();
        for (int i = 0; i < trainingData.getLabelNum(); i++) {
            sb.append(String.format("\t\t%d", (trainingData.getLabelNum() == 2 && i == 0) ? -1 : i));
        }
        out.println(sb.toString());
        for (int i = 0; i < trainingData.getLabelNum(); i++) {
            sb = new StringBuilder();
            sb.append(String.format("%d\t[", (trainingData.getLabelNum() == 2 && i == 0) ? -1 : i));
            for (int j = 0; j < trainingData.getLabelNum(); j++) {
                sb.append(String.format("%.2f%%\t", confusionMatrix[i][j] * 100));
            }
            out.printf("%s]\n", sb.toString());
        }

        if (trainingData.getLabelNum() == 2) {
            updateTopTen();
            sb = new StringBuilder();
            for (int i = 0; i < trainingData.getLabelNum(); i++) {
                sb.append(String.format("\nTopic [%d] - Top 10 words with the highest Likelihood:\n", i == 0 ? -1 : i));
                Stack<MyWord> tmp = new Stack<>();
                while (!topLikelihood[i].isEmpty()) tmp.add(topLikelihood[i].poll());
                while (!tmp.isEmpty()) {
                    MyWord myWord = tmp.pop();
                    sb.append(String.format("\t%-18s\t%.2f%%\n", myWord.word, myWord.value * 100));
                }

                tmp = new Stack<>();
                while (!topOddsRatio[i].isEmpty()) tmp.add(topOddsRatio[i].poll());
                sb.append(String.format("\nTopic [%d] - Top 10 words with the highest Odds Ratio:\n", i == 0 ? -1 : i));
                while (!tmp.isEmpty()) {
                    MyWord myWord = tmp.pop();
                    sb.append(String.format("\t%-18s\t%.2f\n", myWord.word, myWord.value));
                }

            }
            out.print(sb.toString());
        }

        out.close();
    }


    private Color getColor(double minimum, double maximum, double value) {
        value = Math.min(maximum, value);
        value = Math.max(value, minimum);
        double ratio = (value - minimum) / (maximum - minimum);

        int r, g, b;
        if (ratio > 0.9) {
            r = 255 - (int) ((ratio -0.9) / 0.1 * 100);
            g = 0;
            b = 0;
        } else if (ratio > 0.7) {
            r = 255;
            g = 255 - (int) ((ratio - 0.7) / 0.2 * 255);
            b = 0;
        } else if (ratio > 0.5) {
            r = (int) ((ratio - 0.5) / 0.2 * 255);
            g = 255;
            b = 0;
        } else if (ratio > 0.3) {
            r = 0;
            g = 255;
            b = 255 - (int) ((ratio - 0.3) / 0.2 * 255);
        } else if (ratio > 0.1){
            r = 0;
            g = (int) ((ratio - 0.1) / 0.2 * 255);
            b = 255;
        } else {
            r = 0;
            g = 0;
            b = 155 + (int) (ratio / 0.1 * 100);
        }

        return new Color(r, g, b);
    }

    public void printMatrix(String fileName) throws IOException {
        int pixelSizeV = 10;
        int pixelSizeH = 10;

        int pixelNum = confusionMatrix.length;
        BufferedImage img = new BufferedImage(pixelSizeH * pixelNum, pixelSizeV * pixelNum, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        for (int r = 0; r < confusionMatrix.length; r++) {
            for (int c = 0; c < confusionMatrix[r].length; c++) {
                // digit1
                double prob = confusionMatrix[r][c];
                g.setColor(getColor(0, 1, prob));
                g.fillRect(c * pixelSizeH, r * pixelSizeV, pixelSizeH, pixelSizeV);

            }
        }
        FileOutputStream fos = new FileOutputStream(fileName);
        JPEGImageEncoderImpl j = new JPEGImageEncoderImpl(fos);
        j.encode(img);
        fos.close();
    }
}
