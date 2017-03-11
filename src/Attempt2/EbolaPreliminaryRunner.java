package Attempt2;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.*;
import opt.ga.*;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.Instance;
import util.linalg.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mohammed on 3/10/2017.
 */
public class EbolaPreliminaryRunner extends BaseTest{

    public static final int n = 100;
    public static List<Integer> infected;
    public static List<Integer> dead;
    public static void main(String[] args) {

        infected = new ArrayList<>();
        dead = new ArrayList<>();
        try {
            Scanner ebolaDataScan = new Scanner(new File("C:\\Users\\Mohammed\\Documents\\CS4641\\project2/ABAGAIL/src/Attempt2/EbolaOptimization.csv"));
            ebolaDataScan.useDelimiter("[,\\s]");
            int i = 0;
            while (ebolaDataScan.hasNext()) {
                if (i%2 == 0) {
                    infected.add(Integer.parseInt(ebolaDataScan.next().trim()));
                } else {
                    dead.add(Integer.parseInt(ebolaDataScan.next()));
                }
                i++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(infected.get(0));
        System.out.println(dead.get(0));
        System.out.println(infected.size());
        System.out.println(dead.size());
        try {
            new EbolaPreliminaryRunner().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() throws IOException {
        Random random = new Random();
        // create the random points
        double[][] points = new double[n][1];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextDouble();
        }
        EvaluationFunction ef = new EbolaEvalFunct();
        Distribution odd = new DiscretePermutationDistribution(n);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new EbolaCrossOverFunct();
        final HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        final GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);

        int iterations = 10;
        System.out.println("Randomized Hill Climbing");
        runOne(new OptimizationAlgorithmFactory() {
            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new RandomizedHillClimbing(hcp);
            }
        }, ef, 200000, iterations);

        System.out.println("Simulated Annealing");
        runOne(new OptimizationAlgorithmFactory() {

            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new SimulatedAnnealing(1E12, .95, hcp);
            }
        }, ef, 200000, iterations);

        System.out.println("Genetic Algorithms");
        runOne(new OptimizationAlgorithmFactory() {

            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new StandardGeneticAlgorithm(200, 150, 20, gap);
            }
        }, ef, 1000, iterations);
        System.out.println("MIMIC");


        // for mimic we use a sort encoding
        ef = new EbolaEvalFunct();
        int[] ranges = new int[n];
        Arrays.fill(ranges, n);
        odd = new DiscreteUniformDistribution(ranges);
        Distribution df = new DiscreteDependencyTree(.1, ranges);
        final ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        runOne(new OptimizationAlgorithmFactory() {

            @Override
            public OptimizationAlgorithm newOptimizationAlgorithm() {
                return new MIMIC(200, 100, pop);
            }
        }, ef, 1000, iterations);
    }

    public class EbolaEvalFunct implements EvaluationFunction {
        @Override
        public double value(Instance instance) {
            double sumSquaredError = 0;
            for (int i = 0; i < dead.size() - 1; i++) {
                double infectedSquaredError = infected.get(i) - infected.get(i + 1) + infected.get(i) * instance.getContinuous(0);
                infectedSquaredError = Math.pow(infectedSquaredError, 2);
                sumSquaredError += infectedSquaredError;
            }
            return -1 * sumSquaredError;
        }
    }

    public class EbolaCrossOverFunct implements CrossoverFunction {
        @Override
        public Instance mate(Instance a, Instance b) {
            double average = a.getContinuous(0) + b.getContinuous(0);
            return new Instance(average/2);
        }
    }
}

