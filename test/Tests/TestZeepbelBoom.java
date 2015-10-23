package Tests;

import Zeepbelboom.Zeepbelboom;
import Zeepbelboom.Zeepbel;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;

import static CustomAssert.AssertBool.assertTrue;

/**
 * Created by rien on 10/21/15.
 */

@Ignore
public class TestZeepbelBoom {

    Zeepbelboom<Integer> zeepbelboom;

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

}
