package Tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import zeepbelboom.*;

import java.util.*;
import java.util.function.Supplier;

import static CustomAssert.AssertBool.assertFalse;
import static CustomAssert.AssertBool.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Rien on 14/11/2015.
 */

public abstract class AbstractZeepbelBoomTest {

    private static final int SEED = 25547878;

    protected List<Integer> items;

    protected Supplier<Zeepbelboom<Integer>> filledZBB;

    protected Zeepbelboom<Integer> zeepbelboom;

    @Before
    public void prepare(){
        zeepbelboom = filledZBB.get();
    }

    public AbstractZeepbelBoomTest(int testSize, Supplier<Zeepbelboom<Integer>> constructor){
        items = new ArrayList<>();
        for (int i = 0; i < testSize; i++) {
            items.add(i);
        }
        Collections.shuffle(items, new Random(SEED));

        this.filledZBB = () -> {
            Zeepbelboom<Integer> zbb = constructor.get();
            zbb.addAll(items);
            return zbb;
        };

    }


    @Test
    public void testAdd(){
        assertTrue(zeepbelboom.size() == items.size());
        //We voegen alle items nog een keer toe
        assertFalse(zeepbelboom.addAll(items));
        //De boom mag niet veranderd zijn
        assertTrue(zeepbelboom.size() == items.size());
        items.forEach(i -> assertTrue(String.format("Item %d not in zeepbelboom!", i), zeepbelboom.contains(i)));

        List<Integer> sortedItems = new ArrayList<>(items);
        sortedItems.sort(Comparator.<Integer>naturalOrder());

        Iterator<Integer> inZeepbelIterator = zeepbelboom.iterator();
        Iterator<Integer> itemsIterator = sortedItems.iterator();

        inZeepbelIterator.forEachRemaining(i -> assertTrue(i.equals(itemsIterator.next())));
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
