package Experiments;

import java.io.BufferedWriter;
import java.io.IOError;
import java.io.PrintWriter;
import java.util.IntSummaryStatistics;
import java.util.List;

/**
 * @author Rien Maertens
 *         TODO: Description
 */
public class PerformanceWriter {

    private PerformanceTest pt;
    private int testSize;

    public static void main(String[] args) {
        int size = 1000;
        int k = 100;
        if (args.length > 1){
            size = Integer.parseInt(args[0],10);
            k = Integer.parseInt(args[1],10);
        }
        PerformanceWriter pw = new PerformanceWriter(size,k);
        pw.run();
    }

    PerformanceWriter(int size, int maxk){
        this.testSize = size;
        this.pt = new PerformanceTest(size,maxk);
    }

    public void run(){
        List<List<TestResult>> results = pt.testPerBubble();
        for (List<TestResult> result : results) {
            write(result.get(0).getName() + "_" + testSize + ".csv", toCSV(result));
        }
    }


    public String toCSV(List<TestResult> results){
        String csv = "k, add, contains, remove\n";
        for (TestResult result : results) {
            csv += String.format("%d, %d, %d, %d%n",
                    result.getK(),
                    result.getAddTime(),
                    result.getContainsTime(),
                    result.getRemoveTime());
        }
        return csv;
    }

    public void write(String filename, String content){
        try(PrintWriter pw = new PrintWriter(filename,"UTF-8")) {
            pw.println(content);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

}
