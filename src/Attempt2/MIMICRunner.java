package Attempt2;//import opt.EvaluationFunction;
//import opt.OptimizationAlgorithm;
//import opt.example.NeuralNetworkOptimizationProblem;
//import opt.example.NeuralNetworkWeightDistribution;
//import opt.ga.StandardGeneticAlgorithm;
//import opt.prob.GenericProbabilisticOptimizationProblem;
//import opt.prob.MIMIC;
//import shared.runner.MultiRunner;
//
//import java.io.File;
//
///**
// * Created by Mohammed on 3/8/2017.
// */
//public class MIMICRunner extends ABaseNeuralNetworkRunner{
//
//    public static final int NUM_SAMPLES = 200;
//    public static final int NUM_SAMPLES_TO_KEEP = 100;
//    public static int lengthOfNetworkDist = 0;
//   /**
//     *
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        String iterationsList = args[1];
//        String[] parts = iterationsList.split(",");
//        int[] iterArray = new int[parts.length];
//        for (int ii = 0; ii < parts.length; ii++) {
//            iterArray[ii] = Integer.valueOf(parts[ii]);
//        }
//
//        String splitList = args[2];
//        parts = splitList.split(",");
//        int[] pctTrainArray = new int[parts.length];
//        for (int ii = 0; ii < parts.length; ii++) {
//            pctTrainArray[ii] = Integer.valueOf(parts[ii]);
//        }
//
//        MultiRunner mrunner = new MultiRunner(new MIMICRunner(args[0]), iterArray, pctTrainArray);
//        if (args.length >= 4) {
//            mrunner.setOutputFolder(new File(args[3]));
//        }
//        mrunner.runAll();
//    }
//
//    public MIMICRunner(String dataFilePath) {
//        super(dataFilePath);
//    }
//
//    @Override
//    protected OptimizationAlgorithm newAlgorithmInstance(
//            NeuralNetworkOptimizationProblem nno) {
//
//        NeuralNetworkWeightDistribution dist1 = new NeuralNetworkWeightDistribution();
//        NeuralNetworkWeightDistribution dist2 = new NeuralNetworkWeightDistribution();
//        EvaluationFunction measure = new LastIndexEvaluationFunction();
//        GenericProbabilisticOptimizationProblem nno = new GenericProbabilisticOptimizationProblem(measure, dist1, dist2);
//        return new MIMIC(1000, 300, nno);
//    }
//
//
//}
