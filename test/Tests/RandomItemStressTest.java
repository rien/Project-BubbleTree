package Tests;

import Zeepbelboom.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.function.IntFunction;

import static CustomAssert.AssertBool.assertTrue;

/**
 * Created by rien on 10/21/15.
 */
public class RandomItemStressTest {

    private static int TEST_SIZE = 1000000;
    private static long SEED = 698697970;
    private static int MAX_N = 20; //K = 2^N


    Integer[] items;
    List<IntFunction<Zeepbelboom<Integer>>> constructors;

    @Before
    public void  init(){
        //Prepare items
        Random rng = new Random(SEED);
        items = new Integer[TEST_SIZE];
        for (int i = 0; i < items.length; i++) {
            items[i] = rng.nextInt();
        }
        //Prepare constructors
        constructors = new ArrayList<>();
        constructors.add(Zeepbelboom1::new);
    }

    @Test
    public void stressTest(){
        Collection<Integer> boom;
        long tmpTime;
        long testAddTime;
        long testContainsTime;
        long testRemoveTime;
        for (IntFunction<Zeepbelboom<Integer>> zeepBelboomConstructor : constructors){
            for (int i = 1; i < MAX_N; i++) {
                int k = twoPower(i);
                System.out.printf("%nTests for K=%d%n -> Add: ", k);
                tmpTime = System.currentTimeMillis();
                boom = zeepBelboomConstructor.apply(k);


                //Add test
                for (Integer item: items){
                    boom.add(item);
                }
                testAddTime = System.currentTimeMillis() - tmpTime;
                System.out.printf("%d ms%n -> Verifying...", testAddTime);


                //Contains test: does the three contain all items?
                for (Integer item : items) {
                    assertTrue(boom.contains(item));
                }
                System.out.printf(" Done!%n -> Contains: ");


                //Contains test: How fast does the contains method work?
                tmpTime =System.currentTimeMillis();
                for (Integer item : items) {
                    boom.contains(item);
                }
                testContainsTime = System.currentTimeMillis() - tmpTime;
                System.out.printf("%d ms%n -> Remove: ", testContainsTime);

                //Remove items
                tmpTime = System.currentTimeMillis();
                for (Integer item: items){
                    //boom.remove(item);
                }
                testRemoveTime = System.currentTimeMillis() - tmpTime;
                System.out.printf("%d ms%n -> Total: %d ms%n", testRemoveTime, testAddTime + testContainsTime + testRemoveTime);
                //assertTrue(boom.isEmpty());
            }
        }



    }

    static int twoPower(int n){
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= 2;
            if (result < 0){
                throw new ArithmeticException("overflow");
            }
        }
        return result;
    }



}
