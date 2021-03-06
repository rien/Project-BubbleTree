package tests;

import zeepbelboom.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.function.Supplier;

import static customassert.AssertBool.assertFalse;
import static customassert.AssertBool.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Test die de gebalanceerde zeepbelbomen toetst of alle ze correct werken.
 * @author Rien Maertens
 */

@RunWith(Parameterized.class)
public class TestShrinkingZeepbelBoom extends AbstractZeepbelBoomTest {

    @Parameterized.Parameters
    public static Collection<Object[]> configs(){
        return Arrays.asList(new Object[][]{

                // testsize, zeepbelboom constructor
                {10,(Supplier<Zeepbelboom<Integer>>)() -> new Zeepbelboom1<>(2)},
                {10,(Supplier<Zeepbelboom<Integer>>)() -> new Zeepbelboom1<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom1<>(2)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom1<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() -> new Zeepbelboom1<>(20)},
                {10,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom2<>(2)},
                {10,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom2<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom2<>(2)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom2<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom2<>(20)},
                {10,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(2)},
                {10,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(2)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(5)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(20)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(20, true, 5, false)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(20, true, 2, false)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(20, false, -1, true)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(20, true, 5, true)},
                {100000,(Supplier<Zeepbelboom<Integer>>)() ->  new Zeepbelboom3<>(20, false, -1, false)},
        });
    }


    public TestShrinkingZeepbelBoom(int testSize, Supplier<Zeepbelboom<Integer>> constructor) {
        super(testSize, constructor);
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
            assertEquals("Het aantal zeepbellen tussen de bladzeepbellen en de wortel is niet gelijk!",heights.get(i), heights.get(i+1));
        }
    }

    @Test
    public void testRemove(){
        List<Integer> randomRemove = items.subList(0,items.size()/10);
        Set<Integer> remaining = new HashSet<>(items.subList(items.size()/10,items.size()));
        Collections.shuffle(randomRemove);

        randomRemove.forEach(zeepbelboom::remove);
        randomRemove.forEach(t -> assertFalse(zeepbelboom.contains(t)));
        randomRemove.forEach(t -> assertFalse(zeepbelboom.remove(t)));
        zeepbelboom.iterator().forEachRemaining(i -> assertTrue(remaining.contains(i)));

        //Voeg de verwijderde elementen opnieuw toe
        zeepbelboom.addAll(randomRemove);
        randomRemove.forEach(t -> assertTrue(zeepbelboom.contains(t)));
        items.forEach(zeepbelboom::remove);
        assertTrue("Zeepbelboom was not empty!", zeepbelboom.isEmpty());
        zeepbelboom.iterator().forEachRemaining(i -> assertFalse(items.contains(i)));
    }

}
