package Attempt2;

import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.example.NeuralNetworkOptimizationProblem;
import shared.runner.MultiRunner;

import java.io.File;

public class RHCRunner extends ABaseNeuralNetworkRunner {

    public static void main(String[] args) throws Exception {
        String iterationsList = args[1];
        String[] parts = iterationsList.split(",");
        int[] iterArray = new int[parts.length];
        for (int ii = 0; ii < parts.length; ii++) {
            iterArray[ii] = Integer.valueOf(parts[ii]);
        }
        
        String splitList = args[2];
        parts = splitList.split(",");
        int[] pctTrainArray = new int[parts.length];
        for (int ii = 0; ii < parts.length; ii++) {
            pctTrainArray[ii] = Integer.valueOf(parts[ii]);
        }

        MultiRunner mrunner = new MultiRunner(new RHCRunner(args[0]), iterArray, pctTrainArray);
        if (args.length >= 4) {
            mrunner.setOutputFolder(new File(args[3]));
        }
        mrunner.runAll();
    }

    public RHCRunner(String dataFilePath) {
        super(dataFilePath);
    }

    @Override
    protected OptimizationAlgorithm newAlgorithmInstance(
            NeuralNetworkOptimizationProblem nno) {
        return new RandomizedHillClimbing(nno);
    }
}
