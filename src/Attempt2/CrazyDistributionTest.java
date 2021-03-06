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

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Use a random function to see how evaluation functions work out
 * no clue what will happen
 * Pretty similar to Pixel Test
 * Created by Mohammed on 3/10/2017.
 */
public class CrazyDistributionTest extends BaseTest{
    public static final int n = 100;

    public static void main(String[] args) {
        try {
            new PixelTest().go();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void go() throws IOException {
        Random random = new Random();
        // create the random points
        int[][] points = new int[n][3];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = random.nextInt();
            points[i][1] = random.nextInt();
            points[i][2] = random.nextInt();
        }
        EvaluationFunction ef = new CrazyEvalFunct();
        Distribution odd = new DiscretePermutationDistribution(n);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new CrazyCrossOverFunct();
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
        ef = new CrazyEvalFunct();
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

    public class CrazyEvalFunct implements EvaluationFunction {
        @Override
        public double value(Instance instance) {
            Vector data = instance.getData();
            double crazyNum = Math.pow(data.get(0),2) * Math.pow(data.get(1), 1/3);
            crazyNum -= Math.cos(data.get(2));
            crazyNum *= Math.sin(data.get(2));
            return Math.abs(crazyNum);
        }
    }

    public class CrazyCrossOverFunct implements CrossoverFunction {
        @Override
        public Instance mate(Instance a, Instance b) {
            int crossOverChoice = (int)(Math.random() * 3);
            Vector data = a.getData();
            data.set(crossOverChoice, b.getData().get(crossOverChoice));
            return new Instance(data);
        }
    }
}


