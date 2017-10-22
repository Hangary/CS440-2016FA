import java.io.FileNotFoundException;

/**
 * Created by qixinzhu on 11/5/16.
 */
public class MultinomialData extends Data {
    public MultinomialData(int labelNum, String trainFile) throws FileNotFoundException {
        super(labelNum, trainFile);
    }

    int getWordCount(String count) {
        return Integer.parseInt(count);
    }

    double getWordLikelihood(int label, String word) {
        int V = uniqueWords.size();
        double total = totalWordCount[label] + smooth_k * 1.0 * V;

        if (!dictionary[label].containsKey(word)) {
            return smooth_k / total;
        }

        double likelihood = (dictionary[label].get(word) * 1.0 + smooth_k) / total;
        return likelihood;
    }

    double getLogPosterior(int label, String document) {
        String[] doc = document.trim().split(" ");
        double logPosterior = Math.log(this.getPrior(label));

        for (int i = 0; i < doc.length; i++) {
            String word = doc[i].split(":")[0];
            int count = getWordCount(doc[i].split(":")[1]);
            logPosterior += count * Math.log(this.getWordLikelihood(label, word));
        }
        return logPosterior;
    }
}
