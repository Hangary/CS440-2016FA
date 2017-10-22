import java.io.*;
import java.util.*;

/**
 * Created by qixinzhu on 11/2/16.
 */
public class DataParser {
    private static int labelNum = 10;
    public static int dataRow = 28;
    public static int dataCol = 28;

    public static List<Image> parseFaceData(String trainingImages, String trainingLables) throws FileNotFoundException {
        labelNum = 2;
        dataRow = 70;
        dataCol = 60;
        return parseData(trainingImages, trainingLables);
    }

    public static List<Image> parseData(String trainingImages, String trainingLables) throws FileNotFoundException {
        Scanner images = new Scanner(new File(trainingImages));
        Scanner labels = new Scanner(new File(trainingLables));

        List<Image> imageList = new ArrayList<>();
        while (labels.hasNextInt()) {
            int label = labels.nextInt();
            // read test image data
            Image image = new Image(dataRow, dataCol, label);
            for (int r = 0; r < dataRow; r++) {
                String line = images.nextLine();
                image.setImage(r, line);
            }
            imageList.add(image);
        }
        images.close();
        labels.close();
        return imageList;
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
        List<Image> imageList = parseData("./Digit/digitdata/trainingimages", "./Digit/digitdata/traininglabels");
        System.out.println(imageList.size());
        System.out.println(imageList.get(0));
    }
}
