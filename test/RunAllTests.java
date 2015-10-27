import Tests.RandomItemStressTest;
import Tests.TestZeepbelBoom;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by rien on 10/20/15.
 */
public class RunAllTests {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestZeepbelBoom.class, RandomItemStressTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.printf("%s: %s%n", failure.getTestHeader(), failure.getMessage());
            System.out.println(failure.getTrace());
        }

        if (result.wasSuccessful()){
            System.out.println("All tests passed!");
        } else {
            System.out.printf("%n%n%d of the %d tests failed.%n", result.getFailureCount(), result.getRunCount());
        }
        System.out.printf("Runtime: %d ms%n", result.getRunTime());
    }
}
