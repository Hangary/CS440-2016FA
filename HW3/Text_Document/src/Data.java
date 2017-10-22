import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by qixinzhu on 11/4/16.
 */
public abstract class Data {
    protected int totalDocCount;
    protected int[] priorCount;
    protected double[] totalWordCount; // used in Multinomial likelihood
    Map<String, Double>[] dictionary;  // word -> count
    Map<String, Integer> uniqueWords;        // used in smoothing
    protected double smooth_k;

    public Data(int labelNum, String trainFile) throws FileNotFoundException {
        // initialize instance variables first
        totalDocCount = 0;
        priorCount = new int[labelNum];
        totalWordCount = new double[labelNum];
        dictionary = new Map[labelNum];
        smooth_k = 0.1;
        for (int i = 0; i < labelNum; i++) dictionary[i] = new HashMap<>();
        uniqueWords = new HashMap<>();

        Scanner in = new Scanner(new File(trainFile));

        while (in.hasNextLine()) {
            int label = in.nextInt();
            if (label < 0) label = 0;
            totalDocCount++;
            priorCount[label]++;
            String[] wordAndCount = in.nextLine().trim().split(" ");
            for (String wordCount : wordAndCount) {
                this.addWord(label, wordCount);
            }
        }
        in.close();
    }

    private void addWord(int label, String wordWithCount) {
        assertTrue(wordWithCount.contains(":"));

        String word = wordWithCount.split(":")[0];
        if (uniqueWords.containsKey(word)) uniqueWords.put(word, uniqueWords.get(word) + 1);
        else uniqueWords.put(word, 1);
        int count = getWordCount(wordWithCount.split(":")[1]);
        totalWordCount[label] += count;

        if (!dictionary[label].containsKey(word)) dictionary[label].put(word, count * 1.0);
        else dictionary[label].put(word, dictionary[label].get(word) + count);
    }

    abstract int getWordCount(String count);

    public int getTotalDocCount() {
        return totalDocCount;
    }

    public int getLabelNum() {
        return priorCount.length;
    }

    public int getUniqueWordNum() {
        return uniqueWords.size();
    }


    public double getPrior(int label) {
        return priorCount[label] * 1.0 / totalDocCount;
    }

    abstract double getLogPosterior(int label, String document);

    abstract double getWordLikelihood(int label, String word);

    public boolean containsWord(int label, String word) {
        return dictionary[label].containsKey(word);
    }

    public void setSmooth(double k) {
        smooth_k = k;
    }

    public static void main(String[] args) throws FileNotFoundException {
        /**
         * testing Data Class
         */
        Data trainingData = new TFIDF_Data(2, "./Text_Document/fisher_2topic/fisher_train_2topic.txt");
        //Data trainingData = new TFIDF_Data(2, "./Text_Document/movie_review/rt-train.txt");
        //Data trainingData = new TFIDF_Data(40, "./Text_Document/fisher_40topic/fisher_train_40topic.txt");
        System.out.println(trainingData.getTotalDocCount());
        System.out.println(trainingData.uniqueWords.size());
        for (int i : trainingData.priorCount) System.out.println(i);
        for (double i : trainingData.totalWordCount) System.out.println(i);
    }
}
