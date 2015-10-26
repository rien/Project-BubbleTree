package Tests;

import Zeepbelboom.Top;
import Zeepbelboom.Zeepbel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static CustomAssert.AssertBool.assertTrue;
import static CustomAssert.AssertBool.assertFalse;

/**
 * Created by rien on 10/20/15.
 */
public class TestZeepbel {
    int[] items;
    ArrayList<Top<Integer>> toppen;
    Zeepbel<Integer> zeepbel;
    Set<Integer> zeepbelToppen;



    @Before
    public void init(){

        /** Overzicht huidige zeepbel:
         *               6
         *      1               8
         *   0      3        7     9
         *       2     5
         *            4
         *
         *   Met 7, 8, 9 niet in de zeepbel.
         */
        items = new int[10];
        toppen = new ArrayList<>();
        zeepbel = new Zeepbel<Integer>(null);
        zeepbelToppen = new HashSet<>();
        for (int i = 0; i < items.length; i++) {
            items[i] = i; //i+1 for convenience
            toppen.add(new Top<Integer>(items[i]));

        }
        for (int i = 0; i < 8; i++) {
            toppen.get(i).setZeepbel(zeepbel);
            zeepbelToppen.add(i);
        }
        toppen.get(6).setLeftChild(toppen.get(1));
        toppen.get(6).setRightChild(toppen.get(8));

        toppen.get(1).setLeftChild(toppen.get(0));
        toppen.get(1).setRightChild(toppen.get(3));

        toppen.get(3).setLeftChild(toppen.get(2));
        toppen.get(3).setRightChild(toppen.get(5));

        toppen.get(5).setLeftChild(toppen.get(4));

        toppen.get(8).setLeftChild(toppen.get(7));
        toppen.get(8).setRightChild(toppen.get(9));
        zeepbel.setRoot(toppen.get(6));

    }

    @Test
    public void testZeepbelIterator(){
        Iterator<Integer> it = zeepbel.iterator();
        Integer i = 0;
        while (it.hasNext()){
            Integer item = it.next();
            assertEquals(item, i++);
            assertTrue(zeepbelToppen.contains(item));
        }
    }
}
