package Tests;

import org.junit.Before;
import org.junit.Test;
import zeepbelboom.Zeepbelboom;
import zeepbelboom.Zeepbelboom1;
import zeepbelboom.Zeepbelboom2;
import zeepbelboom.Zeepbelboom3;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import static CustomAssert.AssertBool.assertTrue;

/**
 * @author Rien Maertens
 *
 * Performance test die een aantal willekeurige operaties gaat uitvoeren op een reeds gevulde boom.
 */
public class RandomOperationPerformanceTest {


    private static final int TEST_SIZE = 10000000;
    private static final int AMOUNT_OPERATIONS = 1000000;
    private static final long SEED = 698697970;
    private static final int MAX_N = 10; //K = 2^N


    private List<Integer> items;
    private List<Integer> itemsShuffled;
    private List<IntFunction<Zeepbelboom<Integer>>> constructors;
    private List<Integer> operations;



    @Before
    public void  init(){
        //Prepare items
        Random rng = new Random(SEED);
        System.out.printf("Preparing %d items.%n", TEST_SIZE);
        items = new ArrayList<>();
        for (int i = 0; i < TEST_SIZE; i++) {
            items.add(i);
        }
        System.out.println("Shuffling items.");
        Collections.shuffle(items, rng);

        System.out.println("Preparing second list of items.");
        itemsShuffled = items.subList(0,AMOUNT_OPERATIONS);

        System.out.println("Shuffling the second item list.");
        Collections.shuffle(itemsShuffled);

        operations = new ArrayList<>();
        System.out.println("Preparing operations");;
        for (int i = 0; i < AMOUNT_OPERATIONS; i++) {
            operations.add(rng.nextInt(3));
        }
        System.out.println("Preparation done!");

        //Prepare constructors
        constructors = new ArrayList<>();
        constructors.add(Zeepbelboom1::new);
        constructors.add(Zeepbelboom2::new);
        constructors.add(Zeepbelboom3::new);
        constructors.add(k -> new Zeepbelboom3<Integer>(k,false,-1,true));
        constructors.add(k -> new Zeepbelboom3<Integer>(k,true,5,true));
        constructors.add(k -> new Zeepbelboom3<Integer>(k,true,5,false));

    }

    @Test
    public void stressTest(){
        Collection<Integer> boom;
        long tmpTime;
        long testTime;
        for (int i = 1; i < MAX_N; i++) {
            for (IntFunction<Zeepbelboom<Integer>> zeepBelboomConstructor : constructors){
                int k = twoPower(i);
                boom = zeepBelboomConstructor.apply(k);
                System.out.printf("%nTests for %s%n",boom.toString() );
                boom.addAll(items);

                tmpTime = System.currentTimeMillis();

                for (int j = 0; j < operations.size(); j++) {
                    switch (operations.get(j)){
                        case 0: boom.add(itemsShuffled.get(j));
                            break;
                        case 1: boom.contains(itemsShuffled.get(j));
                            break;
                        case 2: boom.remove(itemsShuffled.get(j));
                            break;
                    }

                }
                testTime = System.currentTimeMillis() - tmpTime;
                System.out.printf(" -> Time: %d ms%n", testTime);
            }
        }
    }

    private static int twoPower(int n){
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
