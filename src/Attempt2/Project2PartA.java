package Attempt2;

/**
 * EDIT THIS FILE
 * Terribly ugly name, but cant think of anything better
 * TODO: rename this class
 * Created by Mohammed on 3/6/2017.
 */
public class Project2PartA {
    int[] numIters = {100, 250, 500, 1000};
    int[] pctSplit = {25, 50, 75, 90};
    RunnerParams runnerParams = new RunnerParams("pendigits", numIters, pctSplit, "results/mohammedSaqib");
    //Because i give up on runnerParams
    static String[] replacementArgs = {"C:/Users/Mohammed/Documents/CS4641/project2/ABagail/nominalPenDigits.arff", "100,500,5000", "25,50,70,90,100"};

    public static void main(String[] args) throws Exception {
        args = replacementArgs;
        System.out.println("beginning RHCRunner");
        RHCRunner.main(args);
        System.out.println("beginning SimulatedAnnealingRunner");
        SimulatedAnnealingRunner.main(args);
        System.out.println("beginning GeneticRunner");
        String[] mimicArgs = {"C:/Users/Mohammed/Documents/CS4641/project2/ABagail/nominalPenDigits.arff", "5000,10000", "25,50,90"};
        GeneticRunner.main(args);
    }

}
