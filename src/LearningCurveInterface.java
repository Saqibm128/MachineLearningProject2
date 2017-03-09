/**
 * An abstract object service handler whose job is to keep track of accuracies over iterations
 * Created by Mohammed on 3/6/2017.
 */
public interface LearningCurveInterface {
    /**
     * Gets a single train accuracy
     * @param iteration Which iteration to get accuracy from
     * @return double which represents percent accuracy (aka 1 if all correct, 0 if all incorrect)
     */
    double getTrainAccuracy(int iteration);
    /**
     * Gets a single test accuracy
     * @param iteration Which iteration to get accuracy from
     * @return double which represents percent accuracy (aka 1 if all correct, 0 if all incorrect)
     */
    double getTestAccuracy(int iteration);

    /**
     * gets all train accuracies
     * @return array where 1 if all correct at specific iteration, 0 if all incorrect
     */
    double[] getTrainAccuracies();
    /**
     * gets all test accuracies
     * @return array where 1 if all correct at specific iteration, 0 if all incorrect
     */
    double[] getTestAccuracies();
}
