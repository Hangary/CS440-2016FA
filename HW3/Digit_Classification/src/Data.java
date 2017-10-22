import sun.awt.image.codec.JPEGImageEncoderImpl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.junit.Assert.*;


/**
 * Created by qixinzhu on 11/2/16.
 */
public class Data {
    // Raw data: counts
    protected int totalCount;
    protected int[] priorCount;
    protected short[][][][] likelihoodCount;
    protected int likeliRow;
    protected int likeliCol;
    protected int groupRow;
    protected int groupCol;
    protected boolean overlap;
    protected boolean isTernary;

    // Probability data
    protected double smooth_k;
    protected int featureNum;
    protected double[][][][] likelihood;

    public Data(int pNum, int pixelRow, int pixelCol) {
        this(pNum, pixelRow, pixelCol, 1, 1, false);
    }

    public Data(int pNum, int pixelRow, int pixelCol, int sizeR, int sizeC, boolean overlap) {
        if (!overlap) {
            assertTrue("Invalid group row size", pixelRow % sizeR == 0);
            assertTrue("Invalid group column size", pixelCol % sizeC == 0);
        }
        totalCount = 0;
        priorCount = new int[pNum];
        smooth_k = 1.0;
        featureNum = 1 << (sizeR * sizeC);
        groupRow = sizeR;
        groupCol = sizeC;
        this.overlap = overlap;
        isTernary = false;

        if (overlap) {
            likeliRow = pixelRow + 1 - groupRow;
            likeliCol = pixelCol + 1 - groupCol;
        } else {
            likeliRow = pixelRow / groupRow;
            likeliCol = pixelCol / groupCol;
        }

        likelihoodCount = new short[pNum][likeliRow][likeliCol][featureNum];
        likelihood = new double[pNum][likeliRow][likeliCol][featureNum];
    }

    public void increaseLikelihoodCount(int pNum, int row, int col, int feature) {
        likelihoodCount[pNum][row][col][feature]++;
    }

    public void increaseClassCount(int pNum) {
        totalCount++;
        priorCount[pNum]++;
    }

    /**
     * default smooth-k pretends to have 10% data for each class on average
     */
    public void useDefaultSmooth() {
        int V = likeliRow * likeliCol;
        this.setSmooth((totalCount / this.getClassNum()) * 0.1 / V);
    }

    public void calculateProbability() {
        //int V = likeliRow * likeliCol;
        int V = featureNum;
        for (int i = 0; i < priorCount.length; i++) {
            double total = priorCount[i] + smooth_k * V;
            //double total = priorCount[i] + smooth_k * featureNum;
            //System.out.println(total);
            for (int r = 0; r < likeliRow; r++) {
                for (int c = 0; c < likeliCol; c++) {
                    for (int f = 0; f < featureNum; f++) {
                        likelihood[i][r][c][f] = (likelihoodCount[i][r][c][f] + smooth_k) / total;
                    }
                }
            }
        }
    }

    public void setSmooth(double k) {
        smooth_k = k;
    }

    public double getLikelihood(int label, int row, int col, int feature) {
        return likelihood[label][row][col][feature];
    }

    public double getPrior(int label) {
        return priorCount[label] * 1.0 / totalCount;
    }

    public int getClassNum() {
        return priorCount.length;
    }

    public int getLikeliRow() {
        return likeliRow;
    }

    public int getLikeliCol() {
        return likeliCol;
    }

    public int getGroupRow() {
        return groupRow;
    }

    public int getGroupCol() {
        return groupCol;
    }

    /*
    public void printCountData() {
        System.out.printf("Total # of training data: %d\n", totalCount);
        System.out.println("# of training data for each label:\n");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i : priorCount) {
            sb.append(i);
            sb.append(" ");
        }
        System.out.printf("%s]\n\n", sb.toString().trim());

        for (int i = 0; i < priorCount.length; i++) {
            for (int r = 0; r < likelihoodCount[i].length; r++) {
                sb = new StringBuilder();
                sb.append("[");
                for (int c = 0; c < likelihoodCount[i][r].length; c++) {
                    sb.append(likelihoodCount[i][r][c]);
                    sb.append("\t");
                }
                System.out.printf("%s]\n", sb.toString());
            }
            System.out.println();
        }
    }
*/

    public void printProbData() {
        assertEquals(2, featureNum);
        System.out.printf("Total # of training data: %d\n", totalCount);
        System.out.println("# of training data for each label:\n");
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i : priorCount) {
            sb.append(i);
            sb.append(" ");
        }
        System.out.printf("%s]\n\n", sb.toString().trim());

        for (int i = 0; i < priorCount.length; i++) {
            for (int r = 0; r < likelihood[i].length; r++) {
                sb = new StringBuilder();
                sb.append("[");
                for (int c = 0; c < likelihood[i][r].length; c++) {
                    sb.append(String.format("%.4f", likelihood[i][r][c][1]));
                    sb.append("\t");
                }
                System.out.printf("%s]\n", sb.toString());
            }
            System.out.println();
        }
    }

/*
    public void printOdds(int digit1, int digit2) throws FileNotFoundException {
        String fileName = "./Digit_Classification/odds_" + digit1 + "->" + digit2 + ".txt";
        PrintWriter out = new PrintWriter(fileName);
        StringBuilder sb1, sb2, sb3;
        // print digit1 likelihood
        sb1 = new StringBuilder(String.format("\nLikelihood of digit %d\n", digit1));
        sb2 = new StringBuilder(String.format("\nLikelihood of digit %d\n", digit2));
        sb3 = new StringBuilder(String.format("\nOdds ratio of %d -> %d\n", digit1, digit2));
        for (int r = 0; r < likelihood[digit1].length; r++) {
            for (int c = 0; c < likelihood[digit1][r].length; c++) {
                // digit1
                double prob1 = Math.log(likelihood[digit1][r][c]);

                sb1.append(String.format("%.4f\t", prob1));
                // digit2
                double prob2 = Math.log(likelihood[digit2][r][c]);

                sb2.append(String.format("%.4f\t", prob2));

                sb3.append(String.format("%.4f\t", prob1 - prob2));
            }
            sb1.append("\n");
            sb2.append("\n");
            sb3.append("\n");
        }
        out.print(sb1.toString());
        out.print(sb2.toString());
        out.print(sb3.toString());

        out.close();
    }
*/

    /**
     * This function ONLY works for binary classification!!!!
     *
     * @param digit1
     * @param digit2
     * @throws IOException
     */
    public void printOddsImage(int digit1, int digit2) throws IOException {
        assertEquals(2, featureNum);
        String fileName1 = "./Digit_Classification/images/likelihood_" + digit1 + ".jpg";
        String fileName2 = "./Digit_Classification/images/likelihood_" + digit2 + ".jpg";
        String fileName3 = "./Digit_Classification/images/odds_" + digit1 + "->" + digit2 + ".jpg";

        double maxProb1 = -100;
        double minProb1 = 1;
        double maxProb2 = -100;
        double minProb2 = 1;
        double maxOdds = -100;
        double minOdds = 1;
        for (int r = 0; r < likelihood[digit1].length; r++) {
            for (int c = 0; c < likelihood[digit1][r].length; c++) {
                // digit1
                double prob1 = Math.log(likelihood[digit1][r][c][1]);
                maxProb1 = Math.max(maxProb1, prob1);
                minProb1 = Math.min(minProb1, prob1);
                // digit2
                double prob2 = Math.log(likelihood[digit2][r][c][1]);
                maxProb2 = Math.max(maxProb2, prob2);
                minProb2 = Math.min(minProb2, prob2);
                // odds ratio
                double odds = prob1 - prob2;
                maxOdds = Math.max(maxOdds, odds);
                minOdds = Math.min(minOdds, odds);
            }
        }
        System.out.printf("Max: (%.3f, %.3f, %.3f)\n", maxProb1, maxProb2, maxOdds);
        System.out.printf("Min: (%.3f, %.3f, %.3f)\n", minProb1, minProb2, minOdds);

        int pixelSizeV = 10;
        int pixelSizeH = 10;

        int pixelNum = likelihood[digit1].length;
        BufferedImage img1 = new BufferedImage(pixelSizeH * pixelNum, pixelSizeV * pixelNum, BufferedImage.TYPE_INT_RGB);
        Graphics g1 = img1.getGraphics();
        BufferedImage img2 = new BufferedImage(pixelSizeH * pixelNum, pixelSizeV * pixelNum, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = img2.getGraphics();
        BufferedImage img3 = new BufferedImage(pixelSizeH * pixelNum, pixelSizeV * pixelNum, BufferedImage.TYPE_INT_RGB);
        Graphics g3 = img3.getGraphics();
        for (int r = 0; r < likelihood[digit1].length; r++) {
            for (int c = 0; c < likelihood[digit1][r].length; c++) {
                // digit1
                double prob1 = Math.log(likelihood[digit1][r][c][1]);
                g1.setColor(getColor(minProb1, maxProb1, prob1));
                g1.fillRect(c * pixelSizeH, r * pixelSizeV, pixelSizeH, pixelSizeV);
                // digit2
                double prob2 = Math.log(likelihood[digit2][r][c][1]);
                g2.setColor(getColor(minProb2, maxProb2, prob2));
                g2.fillRect(c * pixelSizeH, r * pixelSizeV, pixelSizeH, pixelSizeV);
                // odds ratio
                double odds = prob1 - prob2;
                g3.setColor(getColor(minOdds*0.85, maxOdds, odds));
                g3.fillRect(c * pixelSizeH, r * pixelSizeV, pixelSizeH, pixelSizeV);
                //System.out.printf("(%.3f, %.3f, %.3f)\n",prob1, prob2, odds);
            }
        }
        FileOutputStream fos1 = new FileOutputStream(fileName1);
        JPEGImageEncoderImpl j1 = new JPEGImageEncoderImpl(fos1);
        j1.encode(img1);
        fos1.close();
        FileOutputStream fos2 = new FileOutputStream(fileName2);
        JPEGImageEncoderImpl j2 = new JPEGImageEncoderImpl(fos2);
        j2.encode(img2);
        fos2.close();
        FileOutputStream fos3 = new FileOutputStream(fileName3);
        JPEGImageEncoderImpl j3 = new JPEGImageEncoderImpl(fos3);
        j3.encode(img3);
        fos3.close();
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

}
