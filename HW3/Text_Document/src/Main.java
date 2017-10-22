import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by qixinzhu on 11/4/16.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Part_2();

        //Part_2_2();

        //Part_2_TFIDF();
    }

    private static void Part_2() throws IOException {
        boolean[] classifier = {false, true};
        String[] topicFolder = {"fisher_2topic", "movie_review", "fisher_40topic"};
        String[] topicTrain = {"fisher_train_2topic.txt", "rt-train.txt", "fisher_train_40topic.txt"};
        String[] topicTest = {"fisher_test_2topic.txt", "rt-test.txt", "fisher_test_40topic.txt"};
        int[] topicNum = {2, 2, 40};
        double[] kValues = {20, 2, 0.1};    // 20, 2, 0.1


        for (int i = 0; i < 3; i++) {
            String folder = topicFolder[i];
            String train = topicTrain[i];
            String test = topicTest[i];
            for (boolean isBernoulli : classifier) {
                String solFile = isBernoulli ? "Bernoulli" : "Multinomial";

                long train_start, train_end, test_start, test_end;
                train_start = System.currentTimeMillis();
                Data trainingData;
                if (isBernoulli)
                    trainingData = new BernoulliData(topicNum[i], String.format("./Text_Document/%s/%s", folder, train));
                else
                    trainingData = new MultinomialData(topicNum[i], String.format("./Text_Document/%s/%s", folder, train));
                train_end = System.currentTimeMillis();
                System.out.printf("Training is finished in %d[ms]\n", train_end - train_start);
                System.out.println(trainingData.getTotalDocCount());
                System.out.println(trainingData.uniqueWords.size());

                trainingData.setSmooth(kValues[i]);
                //trainingData.useDefaultSmooth();
                test_start = System.currentTimeMillis();
                Solution solution = new Solution(trainingData, String.format("./Text_Document/%s/%s", folder, test));
                solution.evaluate();
                test_end = System.currentTimeMillis();
                solution.printSolution(String.format("./Text_Document/solution_%s_%s.txt", folder, solFile), train_end - train_start, test_end - test_start);

                if (i==2) solution.printMatrix(String.format("./Text_Document/ConfusionMatrix_%s_%s.jpg", folder, solFile));
            }
        }
    }

    private static void Part_2_2() throws IOException {
        boolean[] classifier = {false, true};
        String[] topicFolder = {"fisher_2topic", "movie_review", "fisher_40topic"};
        String[] topicTrain = {"fisher_train_2topic.txt", "rt-train.txt", "fisher_train_40topic.txt"};
        String[] topicTest = {"fisher_test_2topic.txt", "rt-test.txt", "fisher_test_40topic.txt"};
        int[] topicNum = {2, 2, 40};
        double[] kValues = {20, 2, 0.001};    // 20, 2, 0.001


        int i = 2;
        String folder = topicFolder[i];
        String train = topicTrain[i];
        String test = topicTest[i];
        for (boolean isBernoulli : classifier) {
            String solFile = isBernoulli ? "Bernoulli" : "Multinomial";

            long train_start, train_end, test_start, test_end;
            train_start = System.currentTimeMillis();
            Data trainingData;
            if (isBernoulli)
                trainingData = new BernoulliData(topicNum[i], String.format("./Text_Document/%s/%s", folder, train));
            else
                trainingData = new MultinomialData(topicNum[i], String.format("./Text_Document/%s/%s", folder, train));
            train_end = System.currentTimeMillis();
            System.out.printf("Training is finished in %d[ms]\n", train_end - train_start);
            System.out.println(trainingData.getTotalDocCount());
            System.out.println(trainingData.uniqueWords.size());

            trainingData.setSmooth(kValues[i]);
            //trainingData.useDefaultSmooth();
            test_start = System.currentTimeMillis();
            Solution solution = new Solution(trainingData, String.format("./Text_Document/%s/%s", folder, test));
            solution.evaluate();
            test_end = System.currentTimeMillis();
            solution.printSolution(String.format("./Text_Document/solution_%s_%s.txt", folder, solFile), train_end - train_start, test_end - test_start);

            if (i==2) solution.printMatrix(String.format("./Text_Document/ConfusionMatrix_%s_%s.jpg", folder, solFile));
        }

    }

    private static void Part_2_TFIDF() throws IOException {

        String[] topicFolder = {"fisher_2topic", "movie_review", "fisher_40topic"};
        String[] topicTrain = {"fisher_train_2topic.txt", "rt-train.txt", "fisher_train_40topic.txt"};
        String[] topicTest = {"fisher_test_2topic.txt", "rt-test.txt", "fisher_test_40topic.txt"};
        int[] topicNum = {2, 2, 40};

        /**
         * smoothing k-value for TF-IDF is much easier to find
         * because in likelihood calculation: first term in denominator is much larger than doc number
         *      the value is less affected by smoothing V-value (unqiue words number)
         */
        double[] kValues = {20, 11, 2.2};    // 20, 11, 2.2


        for (int i = 0; i < 3; i++) {
            String folder = topicFolder[i];
            String train = topicTrain[i];
            String test = topicTest[i];
            String solFile = "TF-IDF";

            long train_start, train_end, test_start, test_end;
            train_start = System.currentTimeMillis();
            Data trainingData = new TFIDF_Data(topicNum[i], String.format("./Text_Document/%s/%s", folder, train));

            train_end = System.currentTimeMillis();
            System.out.printf("Training is finished in %d[ms]\n", train_end - train_start);
            System.out.println(trainingData.getTotalDocCount());
            System.out.println(trainingData.uniqueWords.size());

            trainingData.setSmooth(kValues[i]);
            //trainingData.useDefaultSmooth();
            test_start = System.currentTimeMillis();
            Solution solution = new Solution(trainingData, String.format("./Text_Document/%s/%s", folder, test));
            solution.evaluate();
            test_end = System.currentTimeMillis();
            solution.printSolution(String.format("./Text_Document/solution_%s_%s.txt", folder, solFile), train_end - train_start, test_end - test_start);

            if (i==2) solution.printMatrix(String.format("./Text_Document/ConfusionMatrix_%s_%s.jpg", folder, solFile));
        }
    }
}
