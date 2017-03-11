package Attempt2;

/**
 * Created by Mohammed on 3/10/2017.
 */
public class Project2DPartA {
    static String[] replacementArgs = {"C:/Users/Mohammed/Documents/CS4641/project2/ABagail/nominalPenDigits.arff", "10000", "25,50,90"};
    public static void main(String[] args) throws Exception {
        args = replacementArgs;
        System.out.println("beginning GeneticRunner");
        String[] mimicArgs = {"C:/Users/Mohammed/Documents/CS4641/project2/ABagail/nominalPenDigits.arff", "5000,10000", "25,50,90"};
        GeneticRunner.main(args);
    }
}
