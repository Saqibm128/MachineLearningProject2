package Attempt2;

/**
 * Holds parameters for the running of the testRunners
 * Created by Mohammed on 3/6/2017.
 */
public class RunnerParams {
    public String fileName;
    public int[] numIters;
    public int[] pctTrain;
    public String destFolder;


    public RunnerParams(String fileName, int[] numIters,  int[] pctTrain) {
        this.fileName = fileName;
        this.numIters = numIters;
        this.pctTrain = pctTrain;
    }

    public RunnerParams(String arffFileName, int[] numIters, int[] pctTrain, String destFolder) {
        this(arffFileName,numIters, pctTrain);
        this.destFolder = destFolder;
    }

}
