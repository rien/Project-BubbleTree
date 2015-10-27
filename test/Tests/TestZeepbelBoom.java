package Tests;

import Zeepbelboom.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.IntFunction;

import static CustomAssert.AssertBool.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by rien on 10/21/15.
 */

@RunWith(Parameterized.class)
public class TestZeepbelBoom {

    private static int SEED = 25547878;

    private int testSize;

    private Zeepbelboom<Integer> zeepbelboom;

    @Parameterized.Parameters
    public static Collection<Object[]> configs(){
        return Arrays.asList(new Object[][]{
                {10, new Zeepbelboom1<Integer>(2)},
                {10, new Zeepbelboom1<Integer>(5)},
                {10000, new Zeepbelboom1<Integer>(2)},
                {10000, new Zeepbelboom1<Integer>(5)},
                {10000, new Zeepbelboom1<Integer>(20)},
                {10, new Zeepbelboom2<Integer>(2)},
                {10, new Zeepbelboom2<Integer>(5)},
                {10000, new Zeepbelboom2<Integer>(2)},
                {10000, new Zeepbelboom2<Integer>(5)},
                {10000, new Zeepbelboom1<Integer>(20)},
        });
    }

    public TestZeepbelBoom(int testSize, Zeepbelboom<Integer> zeepbelboom){
        this.testSize = testSize;
        this.zeepbelboom = zeepbelboom;
        items = new ArrayList<>();
        for (int i = 0; i < testSize; i++) {
            items.add(i);
        }
        Collections.shuffle(items, new Random(SEED));
        zeepbelboom.addAll(items);
    }



    private List<Integer> items;

    @Test
    public void testAdd(){
        assertTrue(zeepbelboom.size() == items.size());
        items.forEach(i -> assertTrue(String.format("Item %d not in zeepbelboom!", i), zeepbelboom.contains(i)));
    }

    @Test
    public void testZeepbelIterator(){
        Iterator<Zeepbel<Integer>> it = zeepbelboom.zeepbelIterator();
        Integer prev = null;
        Integer currentItem;
        Zeepbel<Integer> currentZeepbel;
        while (it.hasNext()){
            currentZeepbel = it.next();
            currentItem = currentZeepbel.getWortelSleutel();
            if (prev != null){
                assertTrue(prev < currentItem);
            }
            prev = currentItem;
        }
    }

    @Test
    public void testZeepbelSize(){
        Iterator<Zeepbel<Integer>> zit = zeepbelboom.zeepbelIterator();
        zit.forEachRemaining(zb -> assertTrue("Zeepbelboom has wrong size!", zb.checkBubbleSize() == zb.size()));
    }

    @Test
    public void testZeepbelHoogte(){
        List<Top<Integer>> leaves = new ArrayList<>();
        List<Integer> heights = new ArrayList<>();
        zeepbelboom.getRoot().traverseInorder(
                t -> {
                    if ((!t.hasLeft() && t.hasRight())){
                        leaves.add(t);
                    }
                }, t->true);
        for (Top<Integer> t: leaves) {
            int height = 0;
            while (t != null){
                if (t.getZeepbel().getRoot() == t){
                    height++;
                }
                t = t.getParent();
            }
            heights.add(height);
        }
        for (int i = 0; i < heights.size() - 1; i++) {
            assertEquals("Het aantal zeepbellen tussen de bladzeepbellen en de wortel s niet gelijk!",heights.get(i), heights.get(i+1));
        }
    }

    @Test
    public void testRemove(){
        items.forEach(i -> assertTrue("Item was not found!",zeepbelboom.remove(i)));
        assertTrue("Zeepbelboom was not empty!",zeepbelboom.isEmpty());
    }

}
