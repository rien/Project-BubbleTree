package Tests;

import Zeepbelboom.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

import static CustomAssert.AssertBool.assertTrue;

/**
 * Created by rien on 10/21/15.
 */

@Ignore
public class TestZeepbelBoom {

    private static int K = 7;
    private static int TEST_SIZE = 10000;
    private static int SEED = 587884422;

    Zeepbelboom<Integer> zeepbelboom;
    ArrayList<Integer> items;

    @Before
    public void init(){
        items = new ArrayList<>();
        zeepbelboom = new Zeepbelboom2<Integer>(K);
        for (int i = 0; i < TEST_SIZE; i++) {
            items.add(i);
        }
        Collections.shuffle(items, new Random(SEED));
        assertTrue(zeepbelboom.addAll(items));
        Top<Integer> root = zeepbelboom.getRoot();
        assertTrue(zeepbelboom.size() == items.size());
    }

    @Test
    public void testAdd(){
        items.forEach(i -> assertTrue(String.format("Item %d not in zeepbelboom!", i),zeepbelboom.contains(i)));
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

}
