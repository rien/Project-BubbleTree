package Experiments;

import zeepbelboom.*;

import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static CustomAssert.AssertBool.assertTrue;

/**
 * @author Rien Maertens
 *
 * Test die voor iedere zeepbelboom de basisbewerkingen gaat uitvoeren op een groot aantal
 * willekeurige elementen. Deze test wordt ook meerdere keer herhaald voor verschillende k-waardes.
 */
public class PerformanceTest {

    private static final long SEED = 698697970;
    private List<IntFunction<Zeepbelboom<Integer>>> constructors;

    public PerformanceTest(){
        //Prepare constructors
        constructors = new ArrayList<>();
        constructors.add(Zeepbelboom1::new);
        constructors.add(Zeepbelboom2::new);
        constructors.add(Zeepbelboom3::new);
        constructors.add(k -> new Zeepbelboom3<>(k,false,-1,true));
        constructors.add(k -> new Zeepbelboom3<>(k,true,5,true));
        constructors.add(k -> new Zeepbelboom3<>(k,true,5,false));
        constructors.add(Zeepbelboom4::new);
    }

    private Integer[] generateItems(int size){
        Integer[] items;
        Random rng = new Random(SEED);
        items = new Integer[size];
        for (int i = 0; i < items.length; i++) {
            items[i] = rng.nextInt();
        }
        Collections.shuffle(Arrays.asList(items), rng);
        return items;
    }

    public List<List<TestResult>> testPerN(int maxSize, int k){
        List<List<TestResult>> testResults = new ArrayList<>();
        for (int i = 0; i < constructors.size(); i++) {
            testResults.add(new ArrayList<>());
        }
        for (int n = 10000; n <= maxSize; n += 10000) {
            for (int j = 0; j < constructors.size(); j++){
                IntFunction<Zeepbelboom<Integer>> constructor = constructors.get(j);
                testResults.get(j).add(testBoom(
                        () -> constructor.apply(k),
                        generateItems(n)
                ));
                Runtime.getRuntime().gc();
            }
        }
        return testResults;
    }

    public List<List<TestResult>> testPerK(int testSize, int maxK){
        Integer[] items = generateItems(testSize);
        List<List<TestResult>> testResults = new ArrayList<>();
        for (int i = 0; i < constructors.size(); i++) {
            testResults.add(new ArrayList<>());
        }
        for (int k = 2; k <= maxK; k += 3) {
            for (int j = 0; j < constructors.size(); j++){
                int n = k;
                IntFunction<Zeepbelboom<Integer>> constructor = constructors.get(j);
                testResults.get(j).add(testBoom(
                        () -> constructor.apply(n),
                        items
                ));
                Runtime.getRuntime().gc();
            }
        }

        return testResults;
    }

    private TestResult testBoom(Supplier<Zeepbelboom<Integer>> constructor, Integer[] items){
        long add = 0, contains = 0, remove = 0;
        Zeepbelboom<Integer> boom = constructor.get();

        //Opwarmen
        testAdd(boom,items);
        testContains(boom,items);
        testRemove(boom,items);
        boom.clear();

        System.out.print("\nTesting " + boom.toString() + " Size: " + items.length);
        for (int i = 0; i < 10; i++) {
            System.out.print(".");
            add += testAdd(boom,items);
            contains += testContains(boom,items);
            remove += testRemove(boom,items);
            boom.clear();
        }
        return new TestResult(
                boom.shortName(),
                boom.getBubbleMaxSize(),
                items.length,
                add,
                contains,
                remove
        );
    }

    private long testAdd(Zeepbelboom<Integer> boom, Integer[] items){
        long tmpTime = System.currentTimeMillis();
        //Add test
        for (Integer item : items){
            boom.add(item);
        }
        return System.currentTimeMillis() - tmpTime;

    }

    private long testContains(Zeepbelboom<Integer> boom, Integer[] items){
        //Contains test: How fast does the contains method work?
        long tmpTime = System.currentTimeMillis();
        for (Integer item : items) {
            boom.contains(item);
        }
        return System.currentTimeMillis() - tmpTime;
    }

    private long testRemove(Zeepbelboom<Integer> boom, Integer[] items){
        //Remove items
        long tmpTime = System.currentTimeMillis();
        if (boom.supportsDeletion()){
            for (Integer item: items){
                boom.remove(item);
            }
        }
        return System.currentTimeMillis() - tmpTime;
    }
}
