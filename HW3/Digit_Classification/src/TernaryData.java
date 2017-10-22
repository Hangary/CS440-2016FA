import java.io.FileNotFoundException;

/**
 * Dose not work with grouped classification
 * If tried, may have OutOfMemory Error
 */
public class TernaryData extends Data {
    public TernaryData(int pNum, int pixelRow, int pixelCol) {
        super(pNum, pixelRow, pixelCol);
        featureNum = 3;
        likelihoodCount = new short[pNum][likeliRow][likeliCol][featureNum];
        likelihood = new double[pNum][likeliRow][likeliCol][featureNum];
        isTernary = true;
    }



    public static void main(String[] args) throws FileNotFoundException {
        /**
         * test TernaryData
         */
        Data test = DataParser.parseData(0, 0, false, "./Digit_Classification/digitdata/trainingimages", "./Digit_Classification/digitdata/traininglabels");
        System.out.println(test.featureNum);
        test.calculateProbability();
        //test.printProbData();
    }
}
