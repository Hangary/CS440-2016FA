import sun.awt.image.codec.JPEGImageEncoderImpl;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by qixinzhu on 11/21/16.
 */
public class Perceptron {
    public static Random rmd = new Random(1);

    /**
     * instance variables
     */
    boolean bias, randomOrder, overlap;
    double biasWeight;
    double[][][] weight;
    int targetDigit;
    int row, col, gRow, gCol, featureNum;
    int trainedTimes;

    public Perceptron(int digit, boolean bias, boolean randomWeight, boolean randomOrder, boolean overlap, int gRow, int gCol) {
        targetDigit = digit;
        this.bias = bias;
        this.randomOrder = randomOrder;
        this.overlap = overlap;
        this.gRow = gRow;
        this.gCol = gCol;
        trainedTimes = 0;
        featureNum = 1 << (gRow * gCol);
        if (overlap) {
            row = DataParser.dataRow + 1 - gRow;
            col = DataParser.dataCol + 1 - gCol;
        } else {
            row = DataParser.dataRow / gRow;
            col = DataParser.dataCol / gCol;
        }
        weight = new double[row][col][featureNum];
        if (randomWeight) randomInitWeight();
    }

    private void randomInitWeight() {
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                for (int f = 0; f < featureNum; f++) {
                    weight[r][c][f] = rmd.nextDouble() * 1 - 0.5;
                }
            }
        }
    }
/*
    public void train(List<Image> imageList, int epoch) {
        for (int iter = 0; iter < epoch; iter++) {
            trainedTimes++;
            if (randomOrder) Collections.shuffle(imageList, rmd);
            double alpha = EPOCH_CONSTANT * 1.0 / (EPOCH_CONSTANT + trainedTimes);
            for (Image img : imageList) {
                int y0 = targetDigit == img.getTrueLabel() ? 1 : -1;
                int y1 = classify(img) > 0 ? 1 : -1;
                updateWeight(img, alpha, y0, y1);
            }
        }
    }
*/
    protected int getFeatureValue(Image img, int r, int c) {
        if (overlap) {
            return img.getFeatureValue(r, c, gRow, gCol);
        } else {
            return img.getFeatureValue(r * gRow, c * gCol, gRow, gCol);
        }
    }

    public double classify(Image img) {
        double result = 0;
        if (bias) result += 1.0 * biasWeight;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int feature = getFeatureValue(img, r, c);
                result += weight[r][c][feature];
            }
        }
        return result;
    }

    public void updateWeight(Image img, double alpha, int y0, int y1) {
        if (y0 == y1) return;
        if (bias) biasWeight += alpha * (y0 - y1);
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int feature = getFeatureValue(img, r, c);
                weight[r][c][feature] += alpha * (y0 - y1);
            }
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < row; r++) {
            sb.append("[");
            for (int c = 0; c < col; c++) {
                sb.append(String.format("%.3f", weight[r][c][0]));
                for (int f = 1; f < featureNum; f++) {
                    sb.append(String.format("/%.3f", weight[r][c][f]));
                }
                sb.append("\t");
            }
            sb.append("]\n");
        }
        return sb.toString();
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

    /**
     * This function ONLY works for binary classification!!!!
     *
     * @throws IOException
     */
    public void printImage() throws IOException {
        assertEquals(2, featureNum);
        String fileName1 = "./Digit/images/digit_" + targetDigit + "_weight.jpg";

        double maxW = Integer.MIN_VALUE;
        double minW = Integer.MAX_VALUE;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                double w = weight[r][c][1];// - weight[r][c][0];
                maxW = Math.max(maxW, w);
                minW = Math.min(minW, w);
            }
        }
        System.out.printf("Max = %.3f; Min = %.3f\n", maxW, minW);

        int pixelSizeV = 10;
        int pixelSizeH = 10;

        int pixelNum = row;
        BufferedImage img1 = new BufferedImage(pixelSizeH * pixelNum, pixelSizeV * pixelNum, BufferedImage.TYPE_INT_RGB);
        Graphics g1 = img1.getGraphics();
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                double w = weight[r][c][1];// - weight[r][c][0];
                g1.setColor(getColor(minW * 0.5, maxW * 0.5, w));
                //g1.setColor(getColor(-100, 100, w));
                g1.fillRect(c * pixelSizeH, r * pixelSizeV, pixelSizeH, pixelSizeV);
            }
        }
        FileOutputStream fos1 = new FileOutputStream(fileName1);
        JPEGImageEncoderImpl j1 = new JPEGImageEncoderImpl(fos1);
        j1.encode(img1);
        fos1.close();
    }

    public static void main(String[] args) throws IOException {
        /*
        List<Image> imageList = DataParser.parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
        Perceptron p = new Perceptron(1, true, false, true, false, 1, 1);
        p.train(imageList, 20);
        System.out.println(p.biasWeight);
        System.out.println(p);
        p.printImage();
        */
    }
}
