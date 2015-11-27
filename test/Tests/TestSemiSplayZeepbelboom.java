package Tests;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import zeepbelboom.Zeepbelboom;
import zeepbelboom.Zeepbelboom4;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by Rien on 14/11/2015.
 */
@RunWith(Parameterized.class)
public class TestSemiSplayZeepbelboom extends AbstractZeepbelBoomTest {

    @Parameterized.Parameters
    public static Collection<Object[]> configs(){
        return Arrays.asList(new Object[][]{

                // testsize, zeepbelboom constructor
                {10,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom4<>(2)},
                {10,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom4<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom4<>(2)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom4<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom4<>(2000)},
        });
    }

    public TestSemiSplayZeepbelboom(int testSize,Supplier<Zeepbelboom<Integer>> constructor) {
        super(testSize, constructor);
    }
}
