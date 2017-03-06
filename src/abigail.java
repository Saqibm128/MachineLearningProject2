import dist.Distribution;
import func.nn.activation.ActivationFunction;
import func.nn.activation.DifferentiableActivationFunction;
import func.nn.activation.LogisticSigmoid;
import func.nn.feedfwd.FeedForwardNetwork;
import func.nn.feedfwd.FeedForwardNeuralNetworkFactory;
import opt.EvaluationFunction;
import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.example.NeuralNetworkWeightDistribution;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.*;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;
import util.ABAGAILArrays;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by JasonGibson on 3/3/17.
 */
public class abigail {
    static final int NUM_ITER = 10;
    static final int NUM_OF_SIMULATIONS = 1;
    static final int[] networkStructure = new int[] { 39, 20, 20, 20, 20, 1};
    static final DifferentiableActivationFunction sigmoidFunction = new LogisticSigmoid();

    public static void main(String[] args) {
        DataSetReader dsr = new ArffDataSetReader(new File("").getAbsolutePath() + "/nominalPenDigits.arff");
        // read in the raw data
        DataSet data = null;
        try {
            data = dsr.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String> results = new ArrayList<>();
        if(data != null) {
            for (int i = 0; i < data.size(); i++) {
                int lastIndexOfData = data.get(i).getData().size() - 1;
                data.get(i).setLabel(new Instance(data.get(i).getData().get(lastIndexOfData)));
            }
            results.add("simulation number, RHC, MIMIC, SA, GA");
            for (int sims = 0; sims <= NUM_OF_SIMULATIONS - 1; sims++) {
                // Start timer
                long start = System.nanoTime();
                System.out.println("simulation " + sims);
                System.out.println("running RHC");
                FeedForwardNetwork nnRHC = runRHC(data, 1000);
                System.out.println("running MIMIC");
                FeedForwardNetwork nnMimic = runMIMIC(data, 1000, nnRHC.getWeights().length);
                System.out.println("running SA");
                FeedForwardNetwork nnSA = runSA(data, 1000);
                System.out.println("running GA");
                FeedForwardNetwork nnGA = runGA(data, 1000);

                System.out.println("Simulation " + sims + "...");
                StringBuffer tempResults = new StringBuffer();
                tempResults.append(String.format("%d,", sims));
                tempResults.append(String.valueOf(fitness(nnRHC, data)));
                tempResults.append(",");
                tempResults.append(String.valueOf(fitness(nnMimic, data)));
                tempResults.append(",");
                tempResults.append(String.valueOf(fitness(nnSA, data)));
                tempResults.append(",");
                tempResults.append(String.valueOf(fitness(nnGA, data)));

                // End timer
                long end = System.nanoTime();

                // Print out results
                //System.out.println("----");
                System.out.println("Time taken: " + (end - start) / Math.pow(10, 9) + " seconds.");
                results.add(tempResults.toString());
            }
        }
        try{
            PrintWriter writer = new PrintWriter("results.csv", "UTF-8");
            for (String current: results) {
                writer.write((current + "\n").toCharArray());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FeedForwardNetwork runSA(DataSet data, int numIter) {
        FeedForwardNeuralNetworkFactory factory = new FeedForwardNeuralNetworkFactory();
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure, sigmoidFunction);
        ErrorMeasure measure = new SumOfSquaresError();
        NeuralNetworkOptimizationProblem nno = new NeuralNetworkOptimizationProblem(data, network, measure);
        OptimizationAlgorithm o = new SimulatedAnnealing(10, 0.99, nno);
        FixedIterationTrainer fit = new FixedIterationTrainer(o, numIter);
        fit.train();
        network.setWeights(o.getOptimal().getData());
        return network;
    }

    public static FeedForwardNetwork runGA(DataSet data, int numIter) {
        FeedForwardNeuralNetworkFactory factory = new FeedForwardNeuralNetworkFactory();
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure, sigmoidFunction);
        ErrorMeasure measure = new SumOfSquaresError();
        NeuralNetworkOptimizationProblem nno = new NeuralNetworkOptimizationProblem(data, network, measure);
        OptimizationAlgorithm o = new StandardGeneticAlgorithm(500, 300, 50, nno);
        FixedIterationTrainer fit = new FixedIterationTrainer(o, numIter);
        fit.train();
        network.setWeights(o.getOptimal().getData());
        return network;
    }

    public static FeedForwardNetwork runRHC(DataSet data, int numIter) {
        FeedForwardNeuralNetworkFactory factory = new FeedForwardNeuralNetworkFactory();
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure, sigmoidFunction);
        ErrorMeasure measure = new SumOfSquaresError();
        NeuralNetworkOptimizationProblem nno = new NeuralNetworkOptimizationProblem(data, network, measure);
        OptimizationAlgorithm o = new RandomizedHillClimbing(nno);
        FixedIterationTrainer fit = new FixedIterationTrainer(o, numIter);
        fit.train();
        network.setWeights(o.getOptimal().getData());
        return network;
    }

    public static FeedForwardNetwork runMIMIC(DataSet data, int numIter, int length) {
        FeedForwardNeuralNetworkFactory factory = new FeedForwardNeuralNetworkFactory();
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure, sigmoidFunction);
        NeuralNetworkWeightDistribution dist1 = new NeuralNetworkWeightDistribution(length);
        NeuralNetworkWeightDistribution dist2 = new NeuralNetworkWeightDistribution(length);
        dist1.estimate(data);
        EvaluationFunction measure = new LastIndexEvaluationFunction();
        GenericProbabilisticOptimizationProblem nno = new GenericProbabilisticOptimizationProblem(measure, dist1, dist2);
        OptimizationAlgorithm o = new MIMIC(1000, 300, nno);
        FixedIterationTrainer fit = new FixedIterationTrainer(o, numIter);
        fit.train();
        //train(nno);
        network.setWeights(o.getOptimal().getData());
        return network;
    }

    public static double train(ProbabilisticOptimizationProblem op) {
        Distribution distribution = op.getDistribution();
        int samples = 20;
        int tokeep = 10;
        Instance[] data = new Instance[samples];
        for (int i = 0; i < data.length; i++) {
            data[i] = distribution.sample(null);
        }
        double[] values = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            values[i] = op.value(data[i]);
        }
        double[] temp = new double[values.length];
        System.arraycopy(values, 0, temp, 0, temp.length);
        double cutoff = ABAGAILArrays.randomizedSelect(temp, temp.length - tokeep);
        int j = 0;
        Instance[] kept = new Instance[tokeep];
        for (int i = 0; i < data.length && j < kept.length; i++) {
            if (values[i] >= cutoff) {
                kept[j] = data[i];
                j++;
            }
        }
        distribution.estimate(new DataSet(kept));
        return cutoff;
    }


    /**
     * Fitness.
     *
     * @return the double
     */
    public static double fitness(FeedForwardNetwork network, DataSet data) {
        int correct = 0;
        Instance[] patterns = data.getInstances();
        for (int i = 0; i < patterns.length; i++) {
            network.setInputValues(patterns[i].getData());
            network.run();
            double correctLabel = data.get(i).getLabel().getContinuous();
            double proposedLabel = network.getOutputValues().get(0);

            //System.out.println("Correct label: " + correctLabel);
            //System.out.println("Proposed label: " + proposedLabel);

            if (Math.abs(correctLabel - proposedLabel) <= 0.4)
                correct += 1;
        }

        return correct * 1.0;
    }

}
