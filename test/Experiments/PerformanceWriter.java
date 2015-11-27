package Experiments;

import java.io.PrintWriter;
import java.util.List;

/**
 * @author Rien Maertens
 *         TODO: Description
 */
public class PerformanceWriter {

    //Parameters
    private static final int maxK = 500;
    private static final int defaultSize = 100000;
    private static final int defaultK = 20;
    private static final int maxSize = 1000000;


    private PerformanceTest pt;

    public static void main(String[] args) {
        PerformanceWriter pw = new PerformanceWriter();
        pw.run();
    }

    PerformanceWriter(){
        this.pt = new PerformanceTest();
    }

    public void run(){
        List<List<TestResult>> results = pt.testPerK(defaultSize, maxK);
        for (List<TestResult> result : results) {
            write(result.get(result.size()-1).getName() + "_Ktest.dat", toDAT(result));
        }
        results.clear();
        results = pt.testPerN(maxSize,defaultK);
        for (List<TestResult> result : results) {
            write(result.get(result.size()-1).getName() + "_SizeTest.dat", toDAT(result));
        }
    }


    public String toDAT(List<TestResult> results){
        String csv = "k size add contains remove\n";
        for (TestResult result : results) {
            csv += String.format("%d %d %d %d %d%n",
                    result.getK(),
                    result.getSize(),
                    result.getAddTime(),
                    result.getContainsTime(),
                    result.getRemoveTime());
        }
        return csv;
    }

    public void write(String filename, String content){
        System.out.println("\n!!!!! WRITING !!!!!!");
        try(PrintWriter pw = new PrintWriter(filename,"UTF-8")) {
            pw.println(content);
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Done");

    }

}
