package Tests;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import zeepbelboom.Zeepbelboom;
import zeepbelboom.Zeepbelboom4;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Rien on 14/11/2015.
 */
@RunWith(Parameterized.class)
public class TestSemiSplayZeepbelboom extends ZeepbelBoomTest {



    @Parameterized.Parameters
    public static Collection<Object[]> configs(){
        return Arrays.asList(new Object[][]{

                // testsize, zeepbelboom
                {10, new Zeepbelboom4<Integer>(2)},
                {10, new Zeepbelboom4<Integer>(5)},
                {100000, new Zeepbelboom4<Integer>(2)},
                {100000, new Zeepbelboom4<Integer>(5)},
                {100000, new Zeepbelboom4<Integer>(20)},
        });
    }

    public TestSemiSplayZeepbelboom(int testSize, Zeepbelboom<Integer> zeepbelboom) {
        super(testSize, zeepbelboom);
    }
}
