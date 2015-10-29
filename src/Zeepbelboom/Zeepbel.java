package Zeepbelboom;

import java.lang.IllegalArgumentException;


import java.util.*;

/**
 * @author Rien Maertens
 */
public class Zeepbel<E extends Comparable<E>> implements Iterable<E>{

    private int size;
    private final int maxSize;
    private Top<E> root;

    public Zeepbel(Zeepbelboom<E> tree){
        this.maxSize = tree.getBubbleMaxSize();
        this.size = 0;
    }

    /**
     * Alternate constructor which accepts a Top to be set as roo. Automatically sets the bubble of this
     * top to the newly made bubble.
     *
     * @param tree of which this bubble is a part of.
     * @param root which has to be the root of this bubble.
     */
    public Zeepbel(Zeepbelboom<E> tree, Top<E> root){
        this(tree);
        this.root = root;
        root.setZeepbel(this);
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
     * @return het aatal toppen in deze zeepbel.
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
     * @return true if you are not allowed to add another item to this bubble.
     */
    public boolean isFull(){
        return size >= maxSize;
    }

    /**
     * Methode die aan de zeepbel verteld dat er zojuist een top is toegevoegd.
     *
     * @return <tt>true</tt> wanneer de zeepbel moet gebalanceerd worden.
     */
    public boolean topAdded(){
        assert size <= maxSize : "Bubble is too big!";

        size++;
        return size == maxSize + 1;
    }

    /**
     * Verteld een zeepbel hoeveel items er zojuist zijn verwijderd.
     *
     * @param amount aantal items die verwijderd zijn.
     */
    public void topsRemoved(int amount){
        size -= amount;
        assert size > 0;
    }


    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<E> iterator() {
        ArrayList<E> items = new ArrayList<>(size);
        root.traverseInorder(t -> items.add(t.getItem()), t -> t.getZeepbel() == this);
        return items.iterator();
    }

    /**
     * @return the size of the current bubble, for debugging purposes.
     */
    public int checkBubbleSize(){
        ArrayList<E> items = new ArrayList<>(size);
        root.traverseInorder(t -> items.add(t.getItem()), t -> t.getZeepbel() == this);
        return items.size();
    }
}
