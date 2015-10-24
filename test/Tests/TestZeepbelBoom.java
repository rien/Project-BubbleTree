package Tests;

import Zeepbelboom.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

import static CustomAssert.AssertBool.assertTrue;

/**
 * Created by rien on 10/21/15.
 */

@Ignore
public class TestZeepbelBoom {

    Zeepbelboom<Integer> zeepbelboom;

    @Before
    public void init(){
        /** Boom: http://imgur.com/doXiRcc
         *
         *  zb0: 50
         *  zb1 : 25, 20
         *  zb2: 10, 22
         *  zb3: 40
         *  zb4: 60, 55
         *  zb5: 69
         *  zb6: 53
         *  zb7: 57 59
         */


        zeepbelboom = new Zeepbelboom1<Integer>(2);
        ArrayList<Top<Integer>> toppen = new ArrayList<>();
        for (int i = 0; i < 70; i++) {
            toppen.add(new Top<Integer>(i));
        }
        ArrayList<Zeepbel<Integer>> zeepbellen = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            zeepbellen.add(new Zeepbel<Integer>(2, zeepbelboom));
        }
        zeepbelboom.setRootBubble(zeepbellen.get(0));

        //Maak eerst de boom:
        toppen.get(50).setRightChild(toppen.get(60));
        toppen.get(50).setLeftChild(toppen.get(25));

        toppen.get(25).setRightChild(toppen.get(40));
        toppen.get(25).setLeftChild(toppen.get(20));

        toppen.get(20).setRightChild(toppen.get(22));
        toppen.get(20).setLeftChild(toppen.get(10));

        toppen.get(60).setRightChild(toppen.get(69));
        toppen.get(60).setLeftChild(toppen.get(55));

        toppen.get(55).setRightChild(toppen.get(57));
        toppen.get(55).setLeftChild(toppen.get(53));

        toppen.get(57).setRightChild(toppen.get(59));

        //Voeg de toppen toe aan de juiste zeepbel
        zeepbellen.get(0).setRoot(toppen.get(50));
        toppen.get(50).setZeepbel(zeepbellen.get(0));

        zeepbellen.get(1).setRoot(toppen.get(25));
        toppen.get(25).setZeepbel(zeepbellen.get(1));
        toppen.get(20).setZeepbel(zeepbellen.get(1));

        zeepbellen.get(2).setRoot(toppen.get(10));
        toppen.get(10).setZeepbel(zeepbellen.get(2));

        zeepbellen.get(3).setRoot(toppen.get(22));
        toppen.get(22).setZeepbel(zeepbellen.get(3));

        zeepbellen.get(4).setRoot(toppen.get(40));
        toppen.get(40).setZeepbel(zeepbellen.get(4));

        zeepbellen.get(5).setRoot(toppen.get(60));
        toppen.get(60).setZeepbel(zeepbellen.get(5));
        toppen.get(55).setZeepbel(zeepbellen.get(5));

        zeepbellen.get(6).setRoot(toppen.get(69));
        toppen.get(69).setZeepbel(zeepbellen.get(6));

        zeepbellen.get(7).setRoot(toppen.get(53));
        toppen.get(53).setZeepbel(zeepbellen.get(7));

        zeepbellen.get(8).setRoot(toppen.get(57));
        toppen.get(57).setZeepbel(zeepbellen.get(8));
        toppen.get(59).setZeepbel(zeepbellen.get(8));

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

}
