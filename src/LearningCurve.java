/**
 * Created by Mohammed on 3/6/2017.
 */
public class LearningCurve implements LearningCurveInterface {
    private double[] trainErrors;
    private double[] testErrors;
    public LearningCurve(){};

    @Override
    public double getTrainAccuracy(int iteration) {
        return trainErrors[iteration];
    }

    @Override
    public double getTestAccuracy(int iteration) {
        return 0;
    }

    @Override
    public double[] getTrainAccuracies() {
        return new double[0];
    }

    @Override
    public double[] getTestAccuracies() {
        return new double[0];
    }
}
