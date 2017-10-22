import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by qixinzhu on 11/2/16.
 */
public class Image {
    protected char[][] image;
    protected int trueLabel, testLabel;
    protected double maxChance;

    public Image(int row, int col, int label) {
        image = new char[row][col];
        trueLabel = label;
        maxChance = 0;
    }

    public void setImage(int r, String str) {
        image[r] = str.toCharArray();
    }

    public int getTrueLabel() {
        return trueLabel;
    }

    public char getChar(int r, int c) {
        return image[r][c];
    }

    public int getFeatureValue(int row, int col, int gRow, int gCol) {
        int feature = 0;
        char c;
        for (int i = 0; i < gRow; i++) {
            for (int j = 0; j < gCol; j++) {
                if (row + i >= image.length || col + j >= image[0].length) {
                    System.out.println("Index out of bound!\n");
                }
                c = getChar(row + i, col + j);
                if (c == '+' || c == '#') feature += (1 << (i * gCol + j));
            }
        }
        return feature;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < image.length; i++) {
            sb.append(image[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Testing Image class methods
     *
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
        Scanner images = new Scanner(new File("./Digit_Classification/digitdata/trainingimages"));
        int digitRow = 28;
        int digitCol = 28;
        Image image = new Image(digitRow, digitCol, 5);
        for (int r = 0; r < digitRow; r++) {
            String line = images.nextLine();
            image.setImage(r, line);
        }
        System.out.println(image);
        System.out.println(image.getFeatureValue(0, 0, 1, 1)); // 0
        System.out.println(image.getFeatureValue(0, 0, 2, 2)); // 0
        System.out.println(image.getFeatureValue(9, 9, 1, 1)); // 1
        System.out.println(image.getFeatureValue(9, 9, 2, 2)); // 3
        System.out.println(image.getFeatureValue(9, 9, 2, 3)); // 39
        images.close();
    }
}
