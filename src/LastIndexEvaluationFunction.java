import opt.EvaluationFunction;
import shared.Instance;

public class LastIndexEvaluationFunction implements EvaluationFunction {
    public double value(Instance instance) {
        int lastValIndex = instance.getData().size() - 1;
        return instance.getData().get(lastValIndex);
    }
}
