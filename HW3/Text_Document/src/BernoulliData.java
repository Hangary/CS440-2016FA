import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by qixinzhu on 11/5/16.
 */
public class BernoulliData extends Data {
    public BernoulliData(int labelNum, String trainFile) throws FileNotFoundException {
        super(labelNum, trainFile);
    }

    int getWordCount(String count) {
        return 1;
    }

    double getWordLikelihood(int label, String word) {
        int V = 2;
        double total = priorCount[label] + smooth_k * 1.0 * V;

        double likelihood;
        if (dictionary[label].containsKey(word)) {
            likelihood = (dictionary[label].get(word) * 1.0 + smooth_k) / total;
        } else {
            likelihood = smooth_k / total;
        }
        return likelihood;
    }

    /*
    double getWordLikelihood(int label, String word) {
        int V = uniqueWords.size();
        double total = totalWordCount[label] + smooth_k * 1.0 * V;

        if (!dictionary[label].containsKey(word)) {
            return smooth_k / total;
        }

        double likelihood = (dictionary[label].get(word) * 1.0 + smooth_k) / total;
        return likelihood;
    }
    */

    double getLogPosterior(int label, String document) {
        String[] doc = document.trim().split(" ");
        Set<String> docWords = new HashSet<>();
        for (String wordAndCount : doc) {
            String word = wordAndCount.split(":")[0];
            docWords.add(word);
        }
        double logPosterior = Math.log(this.getPrior(label));

        for (String word : uniqueWords.keySet()) {
            if (docWords.size() > 0 && docWords.contains(word)) {
                logPosterior += Math.log(this.getWordLikelihood(label, word));
                docWords.remove(word);
            }
            else {
                logPosterior += Math.log(1 - this.getWordLikelihood(label, word));
            }
        }
        return logPosterior;
    }
}
