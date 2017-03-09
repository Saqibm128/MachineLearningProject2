package Attempt2;

import opt.OptimizationAlgorithm;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.ga.StandardGeneticAlgorithm;
import shared.runner.MultiRunner;

import java.io.File;

public class GeneticRunner extends ABaseNeuralNetworkRunner {

    /**
     *
     * @param args
     * @throws Exception
     */
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

        MultiRunner mrunner = new MultiRunner(new GeneticRunner(args[0]), iterArray, pctTrainArray);
        if (args.length >= 4) {
            mrunner.setOutputFolder(new File(args[3]));
        }
        mrunner.runAll();
    }

    public GeneticRunner(String dataFilePath) {
        super(dataFilePath);
    }

    @Override
    protected OptimizationAlgorithm newAlgorithmInstance(
            NeuralNetworkOptimizationProblem nno) {
        // Genetic Algorithm is used with a population of 60 and mating/mutating 2 per generation.
        return new StandardGeneticAlgorithm(60, 2, 2, nno);
    }
}
