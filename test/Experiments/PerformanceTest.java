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

    public static void main(String[] args) throws Exception{
        PerformanceTest pt = new PerformanceTest();
        //List<TestResult> tests = pt.testAll();
        //tests.sort(TestResult.byTotalTime().reversed());
        //tests.forEach(System.out::println);
        System.out.println(pt.testAdd(new Zeepbelboom1<>(3)));
    }

    public static final int TEST_SIZE = 1000000;
    private static final long SEED = 698697970;
    private static final int MAX_K = 10;


    private Integer[] items;
    private List<IntFunction<Zeepbelboom<Integer>>> constructors;

    public PerformanceTest(){
        //Prepare items
        Random rng = new Random(SEED);
        items = new Integer[TEST_SIZE];
        for (int i = 0; i < items.length; i++) {
            items[i] = rng.nextInt();
        }
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

    public List<TestResult> testAll(){
        List<TestResult> testResults = new ArrayList<>();
        List<List<TestResult>> nested = testPerBubble();
        nested.forEach(testResults::addAll);
        return testResults;
    }

    public List<List<TestResult>> testPerBubble(){
        List<List<TestResult>> testResults = new ArrayList<>();
        for (int i = 0; i < constructors.size(); i++) {
            testResults.add(new ArrayList<>());
        }
        for (int k = 0; k < MAX_K; k+=5) {
            for (int j = 0; j < constructors.size(); j++){
                int i = k < 2 ? 2 : k;
                IntFunction<Zeepbelboom<Integer>> constructor = constructors.get(j);
                testResults.get(j).add(testBoom(
                        () -> constructor.apply(i)
                ));
            }
        }

        return testResults;
    }

    private TestResult testBoom(Supplier<Zeepbelboom<Integer>> constructor){
        Zeepbelboom<Integer> boom = constructor.get();
        System.out.println("Testing " + boom.toString());
        return new TestResult(
                boom.shortName(),
                boom.getBubbleMaxSize(),
                testAdd(boom),
                testContains(boom),
                testRemove(boom)
        );
    }

    private long testAdd(Zeepbelboom<Integer> boom){
        long tmpTime = System.currentTimeMillis();
        //Add test
        Collections.addAll(boom, items);
        return System.currentTimeMillis() - tmpTime;
    }

    private long testContains(Zeepbelboom<Integer> boom){
        //Contains test: How fast does the contains method work?
        long tmpTime = System.currentTimeMillis();
        for (Integer item : items) {
            boom.contains(item);
        }
        return System.currentTimeMillis() - tmpTime;
    }

    private long testRemove(Zeepbelboom<Integer> boom){
        //Remove items
        long tmpTime = System.currentTimeMillis();
        if (boom.supportsDeletion()){
            for (Integer item: items){
                boom.remove(item);
            }
        }
        return System.currentTimeMillis() - tmpTime;
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
