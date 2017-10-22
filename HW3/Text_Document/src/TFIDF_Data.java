import java.io.*;
import java.util.*;

/**
 * Created by qixinzhu on 11/7/16.
 */
public class TFIDF_Data extends MultinomialData {
    public TFIDF_Data(int labelNum, String trainFile) throws FileNotFoundException {
        super(labelNum, trainFile);
        for (int i = 0; i < labelNum; i++) dictionary[i] = new HashMap<>();
        Scanner in = new Scanner(new File(trainFile));

        while (in.hasNextLine()) {
            int label = in.nextInt();
            if (label < 0) label = 0;
            String[] wordAndCount = in.nextLine().trim().split(" ");
            String[] words = new String[wordAndCount.length];
            int[] counts = new int[wordAndCount.length];
            int totalCount = 0;
            double maxFreq = 0;
            for (int i = 0; i < wordAndCount.length; i++) {
                words[i] = wordAndCount[i].split(":")[0];
                counts[i] = getWordCount(wordAndCount[i].split(":")[1]);
                totalCount += counts[i];
                maxFreq = Math.max(maxFreq, counts[i]);
            }
            maxFreq /= totalCount;

            for (int i = 0; i < wordAndCount.length; i++) {
                if (!uniqueWords.containsKey(words[i])) continue;
                double f = counts[i] * 1.0 / totalCount;
                double tf = 0.5 + 0.5 * f / maxFreq;
                double idf = Math.log(totalDocCount * 1.0 / uniqueWords.get(words[i]));
                double tfidf = tf * idf;
                totalWordCount[label] += tfidf;
                if (!dictionary[label].containsKey(words[i])) dictionary[label].put(words[i], tfidf);
                else dictionary[label].put(words[i], dictionary[label].get(words[i]) + tfidf);
            }
        }
        in.close();
    }
}
