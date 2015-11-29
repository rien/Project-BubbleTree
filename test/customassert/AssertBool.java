package customassert;

import org.junit.Assert;

/**
 * Eigen assertTrue en assertFalse die een standaardtekstje gaan teruggeven wanneer ze geïnitialiseerd zijn
 * zonder message. De assertTrue van JUnit zelf geeft dan altijd "null" als bericht.
 * @author Rien Maertens
 */
public class AssertBool extends Assert{

    public static void assertTrue(boolean condition) {
        assertTrue("Expected true but was false.", condition);
    }

    public static void assertFalse(boolean condition) {
        assertFalse("Expected false but was true.", condition);
    }


}
