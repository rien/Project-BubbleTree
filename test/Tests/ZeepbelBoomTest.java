package Tests;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import zeepbelboom.*;

import java.util.*;

import static CustomAssert.AssertBool.assertFalse;
import static CustomAssert.AssertBool.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Rien on 14/11/2015.
 */

public abstract class ZeepbelBoomTest {

    private static final int SEED = 25547878;

    protected final int testSize;

    protected final Zeepbelboom<Integer> zeepbelboom;

    public ZeepbelBoomTest(int testSize, Zeepbelboom<Integer> zeepbelboom){
        this.testSize = testSize;
        this.zeepbelboom = zeepbelboom;
        items = new ArrayList<>();
        for (int i = 0; i < testSize; i++) {
            items.add(i);
        }
        Collections.shuffle(items, new Random(SEED));
        zeepbelboom.addAll(items);
    }

    protected final List<Integer> items;

    @Test
    public void testAdd(){
        assertTrue(zeepbelboom.size() == items.size());
        items.forEach(i -> Assert.assertTrue(String.format("Item %d not in zeepbelboom!", i), zeepbelboom.contains(i)));
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
        zit.forEachRemaining(zb -> Assert.assertTrue("Zeepbelboom has wrong size!", zb.checkBubbleSize() == zb.size()));
    }
}
