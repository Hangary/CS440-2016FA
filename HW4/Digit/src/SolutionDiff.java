import java.io.IOException;
import java.util.List;

/**
 * Created by qixinzhu on 11/23/16.
 */
public class SolutionDiff extends Solution {
    public SolutionDiff(int labelNum, int epoch, List<Image> trainImages, boolean bias, boolean randomWeight, boolean randomOrder, boolean differentiable) throws IOException {
        super(labelNum,epoch,trainImages,bias,randomWeight,randomOrder,false,1,1);
        for (int i = 0; i < labelNum; i++) {
            classifiers[i] = new DifferentiablePerceptron(i, bias, randomWeight, randomOrder, false, 1, 1);
        }
    }

    public void train(int times) {
        for (int i = 0; i < times; i++) {
            double alpha = EPOCH_CONSTANT * 1.0 / (EPOCH_CONSTANT + trainedTimes);
            for (Image img : trainImages) {
                int testLabel = classify(img);
                int trueLabel = img.getTrueLabel();
                int update = (testLabel == trueLabel ? 0 : 1);
                ((DifferentiablePerceptron)classifiers[trueLabel]).updateWeight(img, alpha, update, 0, img.maxChance);
                ((DifferentiablePerceptron)classifiers[testLabel]).updateWeight(img, alpha, 0, update, img.maxChance);
            }
            trainedTimes++;
        }
    }
}
