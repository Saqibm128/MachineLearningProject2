import dist.Distribution;
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
public class abigailver2 {

    static final int NUM_OF_SIMULATIONS = 1;
    static final int[] networkStructure = new int[] { 16, 20, 10};
    static final int[] num_iter = {1, 2, 5, 10, 100, 500, 1000};

    public static void main(String[] args) {
        System.out.println("Start reading in data now");
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
                data.get(i).setLabel(new Instance(data.get(i).getData().get(data.get(i).size()-1)));
                data.get(i).setData(data.get(i).getData().remove(data.get(i).size()-1));
            }
            results.add("simulation number, RHC, MIMIC, SA, GA");
            for (int sims = 0; sims < num_iter.length; sims++) {
                // Start timer
                long start = System.nanoTime();
                System.out.println("evaluating RHC");
                FeedForwardNetwork nnRHC = runRHC(data, num_iter[sims]);
                System.out.println("mimic");
                FeedForwardNetwork nnMimic = runMIMIC(data, 1, nnRHC.getWeights().length);
                System.out.println("SA");
                FeedForwardNetwork nnSA = runSA(data, num_iter[sims]);
                System.out.println("GA");
                FeedForwardNetwork nnGA = runGA(data, num_iter[sims]);

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
                tempResults.append("Run on " + num_iter[sims]);

                // End timer
                long end = System.nanoTime();

                // Print out results
                //System.out.println("----");
                System.out.println("Time taken: " + (end - start) / Math.pow(10, 9) + " seconds.");
                System.out.println(tempResults.toString());
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
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure);
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
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure);
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
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure);
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
        FeedForwardNetwork network = factory.createClassificationNetwork(networkStructure);
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
            double correctLabel = data.get(i).getLabel().getDiscrete();
            double proposedLabel = network.getOutputValues().get(0);
//            for (int j = 0; j < network.getOutputValues().size(); j++) {
//                System.out.print(network.getOutputValues().get(j));
//            }
//            System.out.println();
            //System.out.println("Correct label: " + correctLabel);
            //System.out.println("Proposed label: " + proposedLabel);

            if (Math.abs(correctLabel - proposedLabel) <= 0.4)
                correct += 1;
        }

        return correct * 1.0 / data.size();
    }

}









































