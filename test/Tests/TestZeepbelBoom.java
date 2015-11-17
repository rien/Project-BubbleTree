package Tests;

import zeepbelboom.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.function.IntFunction;

import static CustomAssert.AssertBool.assertFalse;
import static CustomAssert.AssertBool.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author Rien Maertens
 * Test die een zeepbelboom test of alle methodes correct werken.
 */

@RunWith(Parameterized.class)
public class TestZeepbelBoom extends ZeepbelBoomTest {

    private static final int SEED = 25547878;

    @Parameterized.Parameters
    public static Collection<Object[]> configs(){
        return Arrays.asList(new Object[][]{

                // testsize, zeepbelboom
                {10, new Zeepbelboom1<Integer>(2)},
                {10, new Zeepbelboom1<Integer>(5)},
                {100000, new Zeepbelboom1<Integer>(2)},
                {100000, new Zeepbelboom1<Integer>(5)},
                {100000, new Zeepbelboom1<Integer>(20)},
                {10, new Zeepbelboom2<Integer>(2)},
                {10, new Zeepbelboom2<Integer>(5)},
                {100000, new Zeepbelboom2<Integer>(2)},
                {100000, new Zeepbelboom2<Integer>(5)},
                {100000, new Zeepbelboom2<Integer>(20)},
                {10, new Zeepbelboom3<Integer>(2)},
                {10, new Zeepbelboom3<Integer>(5)},
                {100000, new Zeepbelboom3<Integer>(2)},
                {100000, new Zeepbelboom3<Integer>(5)},
                {100000, new Zeepbelboom3<Integer>(20)},
                {100000, new Zeepbelboom3<Integer>(20, true, 5, false)},
                {100000, new Zeepbelboom3<Integer>(20, true, 2, false)},
                {100000, new Zeepbelboom3<Integer>(20, false, -1, true)},
                {100000, new Zeepbelboom3<Integer>(20, true, 5, true)},
                {100000, new Zeepbelboom3<Integer>(20, false, -1, false)},
        });
    }


    public TestZeepbelBoom(int testSize, Zeepbelboom<Integer> zeepbelboom) {
        super(testSize, zeepbelboom);
    }

    @Test
    public void testAdd(){
        assertTrue(zeepbelboom.size() == items.size());
        items.forEach(i -> assertTrue(String.format("Item %d not in zeepbelboom!", i), zeepbelboom.contains(i)));
    }

    @Test
    public void testZeepbelHoogte(){
        List<Node<Integer>> leaves = new ArrayList<>();
        List<Integer> heights = new ArrayList<>();
        zeepbelboom.getRoot().traverseInorder(
                t -> {
                    if ((!t.hasLeft() && t.hasRight())){
                        leaves.add(t);
                    }
                }, t->true);
        for (Node<Integer> t: leaves) {
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
        List<Integer> randomRemove = items.subList(0,items.size()/10);
        Collections.shuffle(randomRemove);
        randomRemove.forEach(zeepbelboom::remove);
        randomRemove.forEach(t -> assertFalse(zeepbelboom.contains(t)));
        randomRemove.forEach(t -> assertFalse(zeepbelboom.remove(t)));
        zeepbelboom.addAll(randomRemove);
        randomRemove.forEach(t -> assertTrue(zeepbelboom.contains(t)));
        items.forEach(zeepbelboom::remove);
        assertTrue("Zeepbelboom was not empty!",zeepbelboom.isEmpty());
    }

}
