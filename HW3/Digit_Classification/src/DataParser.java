import java.io.*;
import java.util.*;

/**
 * Created by qixinzhu on 11/2/16.
 */
public class DataParser {
    private static int labelNum = 10;
    private static int dataRow = 28;
    private static int dataCol = 28;

    public static Data parseData(String trainingImages, String trainingLables) throws FileNotFoundException {
        return parseData(1, 1, false, trainingImages, trainingLables);
    }

    public static Data parseFaceData(int gRow, int gCol, boolean overlap, String trainingImages, String trainingLables) throws FileNotFoundException {
        labelNum = 2;
        dataRow = 70;
        dataCol = 60;
        return parseData(gRow, gCol, overlap, trainingImages, trainingLables);
    }

    public static Data parseData(int gRow, int gCol, boolean overlap, String trainingImages, String trainingLables) throws FileNotFoundException {
        System.gc();
        Data digitData;
        boolean ternary = true;
        if (gRow > 0 && gCol > 0) ternary = false;
        if (ternary) {
            digitData = new TernaryData(labelNum, dataRow, dataCol);
        } else {
            digitData = new Data(labelNum, dataRow, dataCol, gRow, gCol, overlap);
        }

        Scanner images = new Scanner(new File(trainingImages));
        Scanner labels = new Scanner(new File(trainingLables));

        while (labels.hasNextInt()) {
            int label = labels.nextInt();
            digitData.increaseClassCount(label);
            // read test image data
            Image image = new Image(dataRow, dataCol);
            for (int r = 0; r < dataRow; r++) {
                String line = images.nextLine();
                image.setImage(r, line);
            }

            for (int i = 0; i < digitData.getLikeliRow(); i++) {
                for (int j = 0; j < digitData.getLikeliCol(); j++) {
                    int feature;
                    if (overlap) {
                        feature = image.getFeatureValue(i, j, gRow, gCol);
                    } else if (!ternary) {
                        feature = image.getFeatureValue(i * gRow, j * gCol, gRow, gCol);
                    } else {    // for ternary
                        feature = image.getFeatureValueTernary(i, j);
                    }
                    digitData.increaseLikelihoodCount(label, i, j, feature);
                }
                /*
                char[] line = images.nextLine().toCharArray();
                char c;
                for (int j = 0; j < digitCol; j++) {
                    c = line[j];
                    if (c == '+' || c == '#')
                        digitData.increaseLikelihoodCount(label, i, j);
                }*/
            }
        }
        images.close();
        labels.close();
        return digitData;
    }


    public static List<Integer> readTestLabels(String testLabelsFile) throws FileNotFoundException {
        List<Integer> testLabels = new ArrayList<>();
        Scanner labels = new Scanner(new File(testLabelsFile));
        while (labels.hasNextInt()) {
            testLabels.add(labels.nextInt());
        }
        labels.close();
        return testLabels;
    }

    public static void main(String[] args) throws FileNotFoundException {
        /**
         * test DataParser
         */
        Data test = parseData("./Digit_Classification/digitdata/trainingimages", "./Digit_Classification/digitdata/traininglabels");
        //test.printCountData();
        test.calculateProbability();
        test.printProbData();
    }
}
