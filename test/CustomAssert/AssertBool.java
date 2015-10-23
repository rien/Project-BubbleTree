package CustomAssert;

import org.junit.Assert;

/**
 * Created by rien on 10/20/15.
 */
public class AssertBool extends Assert{

    public static void assertTrue(boolean condition) {
        assertTrue("Expected true but was false.", condition);
    }

    public static void assertFalse(boolean condition) {
        assertFalse("Expected false but was true.", condition);
    }


}
