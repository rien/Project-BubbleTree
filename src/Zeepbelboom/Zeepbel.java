package Zeepbelboom;

import java.lang.IllegalArgumentException;


import java.util.*;

/**
 * Created by rien on 10/9/15.
 */
public class Zeepbel<E extends Comparable<E>> implements Iterable<E>{

    private int size;
    private final int maxSize;

    private Top<E> root;
    private Zeepbelboom<E> tree;

    public Zeepbel(int maxSize, Zeepbelboom<E> tree){
        if (maxSize < 2){
            throw new IllegalArgumentException(String.format("Zeepbelboom heeft een" +
                    "minimale maxSize-waarde van 2, maar kreeg  %d %n", maxSize));
        }
        this.maxSize = maxSize;
        this.tree = tree;
        this.size = 0;
    }

    public Zeepbel(int maxSize, Zeepbelboom<E> tree, Top<E> root){
        this(maxSize, tree);
        size = 1;
        this.root = root;
    }

    public Top<E> getRoot(){
        return root;
    }

    public void setRoot(Top<E> top){
        this.root = top;
    }

    public E getWortelSleutel(){
        return root.getItem();
    }


    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this collection
     */
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this collection contains no elements.
     *
     * @return <tt>true</tt> if this collection contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<E> iterator() {
        @SuppressWarnings("unchecked")
        ArrayList<E> items = new ArrayList<>(size);
        Stack<Top<E>> s = new Stack<>();
        Top<E> t = root;
        //Stop alle linkerkinderen in de stack
        while (t != null && t.getZeepbel() == this){
            s.push(t);
            t = t.getLeftChild();
        }
        //Haal de toppen één voor één uit de stack
        while (s.size() > 0) {
            t = s.pop();

            //Behandel de top.
            items.add(t.getItem());


            //Voeg de rechterkinderen toe
            if (t.hasRight()){
                t = t.getRightChild();
                while (t != null && t.getZeepbel() == this){
                    s.push(t);
                    t = t.getLeftChild();
                }
            }
        }
        return items.iterator();
    }



    public void clear() {

    }
}
