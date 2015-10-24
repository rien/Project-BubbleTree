package Zeepbelboom;

import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by rien on 10/15/15.
 */
public class Top<E extends Comparable<E>> implements Comparable<E> {


    private E item;

    private Zeepbel<E> zeepbel;

    private  Top<E> parent;
    private Top<E> leftChild;
    private Top<E> rightChild;

    public Top(E item){
        this.item = item;
    }

    public Top(E item, Zeepbel<E> zeepbel){
        this.item = item;
        this.zeepbel = zeepbel;
    }

    public Top<E> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Top<E> leftChild) {
        this.leftChild = leftChild;
    }

    public Top<E> getParent() {
        return parent;
    }

    public void setParent(Top<E> parent) {
        this.parent = parent;
    }

    public Top<E> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Top<E> rightChild) {
        this.rightChild = rightChild;
    }

    public E getItem() {
        return item;
    }

    public void setItem(E item) {
        this.item = item;
    }

    public boolean hasLeft(){
        return leftChild != null;
    }

    public boolean hasRight(){
        return rightChild != null;
    }

    public Zeepbel<E> getZeepbel() {
        return zeepbel;
    }

    public void setZeepbel(Zeepbel<E> zeepbel){
        this.zeepbel = zeepbel;
    }

    public boolean add(E o){
        int comp = item.compareTo(o);
        if (comp == 0){
            return false;
        }
        Top<E> child = comp < 0 ? leftChild : rightChild;
        if (child == null){
            child = new Top<E>(o);
            child.setParent(this);
            return true;
        } else {
            return child.add(o);
        }
    }

    public Top<E> find(Object o){
        @SuppressWarnings("unchecked")
        E item = (E) o;
        int comp = item.compareTo(item);
        if (comp == 0){
            return this;
        }
        Top<E> child = comp < 0 ? leftChild : rightChild;
        if (child == null){
            return null;
        } else {
            return child.find(item);
        }

    }



    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Top && ((Top) obj).getItem().equals(this.item);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(E o) {
        return item.compareTo(o);
    }


    public void traverseInorder(Consumer<Top<E>> consumer, Predicate<Top<E>> predicate){
        Top<E> t = this;
        Stack<Top<E>> s = new Stack<>();
        //Stop alle linkerkinderen in de stack
        while (t != null && predicate.test(t)){
            s.push(t);
            t = t.getLeftChild();
        }
        //Haal de toppen één voor één uit de stack
        while (!s.isEmpty()) {
            t = s.pop();

            //Behandel de top.
            consumer.accept(t);


            //Voeg de rechterkinderen toe
            if (t.hasRight()){
                t = t.getRightChild();
                while (t != null && predicate.test(t)){
                    s.push(t);
                    t = t.getLeftChild();
                }
            }
        }


    }
}
