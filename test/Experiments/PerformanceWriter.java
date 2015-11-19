package Experiments;

import java.io.BufferedWriter;
import java.io.IOError;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Rien Maertens
 *         TODO: Description
 */
public class PerformanceWriter {

    private PerformanceTest pt;

    public static void main(String[] args) {
        PerformanceWriter pw = new PerformanceWriter();
        pw.run();
    }

    PerformanceWriter(){
        this.pt = new PerformanceTest();
    }

    public void run(){
        List<List<TestResult>> results = pt.testPerBubble();
        for (List<TestResult> result : results) {
            write(result.get(0).getName() + ".csv", toCSV(result));
        }
    }

    public String toCSV(List<TestResult> results){
        String csv = PerformanceTest.TEST_SIZE + "### k, add, contains, remove### +\n";
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
