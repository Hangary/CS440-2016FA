import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by qixinzhu on 11/2/16.
 */
public class Solution {
    protected int testNum;
    protected List<Integer> testLabels;
    protected List<Integer> trueLabels;
    protected String testImageFile;
    protected String testLabelFile;
    protected double[][] confusionMatrix;
    protected double[] bestMatchProb;
    protected double[] worstMatchProb;
    protected Image[] bestMatch;
    protected Image[] worstMatch;
    protected double smooth_k;
    protected double overallAccuracy;

    public Solution(String imageFile, String labelFile) throws FileNotFoundException {
        testImageFile = imageFile;
        testLabelFile = labelFile;
        trueLabels = DataParser.readTestLabels(testLabelFile);
        testNum = trueLabels.size();
    }

    public void maxPosterior(Data trainingData) throws FileNotFoundException {
        smooth_k = trainingData.smooth_k;

        Scanner images = new Scanner(new File(testImageFile));

        int row = trainingData.getLikeliRow();
        int col = trainingData.getLikeliCol();
        int gRow = trainingData.getGroupRow();
        int gCol = trainingData.getGroupCol();
        int labelNum = trainingData.getClassNum();

        int imageRow, imageCol;
        if (trainingData.overlap) {
            imageRow = row - 1 + gRow;
            imageCol = col - 1 + gCol;
        } else {
            imageRow = row * gRow;
            imageCol = col * gCol;
        }

        initSolution(labelNum);

        for (int i = 0; i < testNum; i++) {
            // read test image data
            Image image = new Image(imageRow, imageCol);
            for (int r = 0; r < imageRow; r++) {
                String line = images.nextLine();
                image.setImage(r, line);
            }
            // judging which label this image belongs to
            for (int label = 0; label < labelNum; label++) {
                double prob = Math.log(trainingData.getPrior(label));
                for (int r = 0; r < row; r++) {
                    for (int c = 0; c < col; c++) {
                        int feature;
                        if (trainingData.overlap) {
                            feature = image.getFeatureValue(r, c, gRow, gCol);
                        } else if (trainingData.isTernary) {    // ternary case
                            feature = image.getFeatureValueTernary(r, c);
                        } else {
                            feature = image.getFeatureValue(r * gRow, c * gCol, gRow, gCol);
                        }

                        prob += Math.log(trainingData.getLikelihood(label, r, c, feature));
                        /*
                        if (ch == '+' || ch == '#')
                            prob += Math.log(trainingData.getLikelihood(label, r, c, 1));
                        else
                            prob += Math.log(trainingData.getLikelihood(label, r, c, 0));
                        */
                    }
                }
                if (prob > image.maxPosterior) {
                    image.maxPosterior = prob;
                    image.lable = label;
                }
            }
            // add to list and record best/worst match
            addClassification(image);
        }

        images.close();
    }

    private void addClassification(Image image) {
        testLabels.add(image.lable);

        if (image.maxPosterior > bestMatchProb[image.lable]) {
            bestMatchProb[image.lable] = image.maxPosterior;
            bestMatch[image.lable] = image;
        }
        if (image.maxPosterior < worstMatchProb[image.lable]) {
            worstMatchProb[image.lable] = image.maxPosterior;
            worstMatch[image.lable] = image;
        }
    }

    private void initSolution(int labelNum) {
        testLabels = new ArrayList<>(testNum);
        bestMatchProb = new double[labelNum];
        Arrays.fill(bestMatchProb, Integer.MIN_VALUE);
        worstMatchProb = new double[labelNum];
        Arrays.fill(worstMatchProb, Integer.MAX_VALUE);
        bestMatch = new Image[labelNum];
        worstMatch = new Image[labelNum];
        confusionMatrix = new double[labelNum][labelNum];
        overallAccuracy = 0;
        System.gc();
    }

    public void evaluate() {
        int labelNum = confusionMatrix.length;
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

    public void printSolution(String outputFile, long trainTime, long testTime) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(outputFile);
        out.printf("Using Smoothing K-value = %.3f, Overall Accuracy = %.2f%%\n", smooth_k, overallAccuracy * 100);
        out.printf("Training Time = %dms, Testing Time = %dms\n", trainTime, testTime);

        System.out.printf("Using Smoothing K-value = %.3f, Overall Accuracy = %.2f%%\n", smooth_k, overallAccuracy * 100);
        System.out.printf("Training Time = %dms, Testing Time = %dms\n", trainTime, testTime);

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

        for (int i = 0; i < bestMatch.length; i++) {
            out.printf("\nThe MOST prototypical image for digit\"%d\":\n", i);
            out.print(bestMatch[i].toString());
            out.printf("\nThe LEAST prototypical image for digit\"%d\":\n", i);
            out.print(worstMatch[i].toString());
        }

        out.close();
    }

}
