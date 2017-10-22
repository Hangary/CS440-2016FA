/**
 * Created by qixinzhu on 11/23/16.
 */
public class DifferentiablePerceptron extends Perceptron {

    public DifferentiablePerceptron(int digit, boolean bias, boolean randomWeight, boolean randomOrder, boolean overlap, int gRow, int gCol) {
        super(digit, bias, randomWeight, randomOrder, overlap, gRow, gCol);
    }

    private double sigmoid(double wx) {
        return 1.0 / (1.0 + Math.exp(0 - wx));
    }

    public double classify(Image img) {
        double result = 0;
        if (bias) result += 1.0 * biasWeight;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int feature = getFeatureValue(img, r, c);
                result += weight[r][c][feature];
            }
        }
        return sigmoid(result);
    }

    public void updateWeight(Image img, double alpha, int y0, int y1, double wx) {
        if (y0 == y1) return;
        if (bias) biasWeight += alpha * (y0 - y1);
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                int feature = super.getFeatureValue(img, r, c);
                weight[r][c][feature] += alpha * (y0 - y1) * sigmoid(wx) * (1 - sigmoid(wx));
            }
        }
    }


}
