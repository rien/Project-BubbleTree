package zeepbelboom;

import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Rien Maertens
 */
public class Top<E extends Comparable<E>> implements Comparable<E> {


    private E item;

    private Zeepbel<E> zeepbel;

    private boolean removed;

    private Top<E> parent;
    private Top<E> leftChild;
    private Top<E> rightChild;

    public Top(E item){
        this.item = item;
    }

    public Top<E> getLeftChild() {
        return leftChild;
    }

    public Top<E> getParent() {
        return parent;
    }

    private void setParent(Top<E> parent) {
        this.parent = parent;
    }

    public void removeParent(){
        this.parent = null;
    }

    public void setLeftChild(Top<E> leftChild) {
        this.leftChild = leftChild;
        if (leftChild != null){
            leftChild.setParent(this);
        }
    }

    public Top<E> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Top<E> rightChild) {
        this.rightChild = rightChild;
        if (rightChild != null){
            rightChild.setParent(this);
        }
    }

    public void setChild(Top<E> child){
        if (compareTo(child.getItem()) < 0){
            setRightChild(child);
        } else {
            setLeftChild(child);
        }
    }

    public boolean isRemoved() {
        return removed;
    }

    public void remove(){
        assert !removed;
        this.removed = true;
    }

    public void unRemove(){
        assert removed;
        this.removed = false;
    }

    public E getItem() {
        return item;
    }

    public boolean hasLeft(){
        return leftChild != null;
    }

    public boolean hasRight(){
        return rightChild != null;
    }

    public boolean leftChildInSameBubble(){
        return leftChild.getZeepbel() == this.getZeepbel();
    }

    public boolean rightChildInSameBubble(){
        return rightChild.getZeepbel() == this.getZeepbel();
    }

    public Zeepbel<E> getZeepbel() {
        return zeepbel;
    }

    public boolean isBubbleRoot(){
        return zeepbel.getRoot() == this;
    }

//    public void removeFromParent(){
//        parent.removeChild(this);
//    }
//
//    private void removeChild(Top<E> top){
//        if (leftChild == top){
//            leftChild = null;
//        } else if (rightChild == top){
//            rightChild = null;
//        } else {
//            throw new IllegalArgumentException("The given node is not a child of this node!");
//        }
//        zeepbel.topsRemoved(1);
//    }

    public Top<E> getSibling(){
        if (parent.leftChild == this){
            return parent.rightChild;
        } else if (parent.rightChild == this){
            return parent.leftChild;
        } else {
            throw new IllegalStateException("Parent has wrong link to children!");
        }
    }

    /**
     * @return de top met de dichtste waarde bij de huidige top.
     */
    public Top<E> findClosestChild(){
        Top<E> top;
        if (hasRight()){
            top = rightChild;
            while (top.hasLeft()){
                top = top.getLeftChild();
            }
        } else if(hasLeft()) {
            top = leftChild;
            while (top.hasRight()){
                top = top.getRightChild();
            }
        } else {
            top = this;
        }
        return top;
    }
    /*
    Alternatieve swapmethode die alle verwijzingen aanpast

    public void swap(Top<E> other){
        Top<E> tempParent = this.parent;
        Top<E> tempRight = this.rightChild;
        Top<E> tempLeft = this.leftChild;
        Zeepbel<E> tempBubble = this.zeepbel;

        this.parent = other.parent;
        this.leftChild = other.leftChild;
        this.rightChild = other.rightChild;
        this.zeepbel = other.zeepbel;
        other.parent = tempParent;
        other.leftChild = tempLeft;
        other.rightChild = tempRight;
        other.zeepbel = tempBubble;

        if (other.zeepbel.getRoot() == other){
            other.zeepbel.setRoot(this);
        }
        if (tempBubble.getRoot() == this){
            tempBubble.setRoot(other);
        }
    }*/

    public void swapItems(Top<E> other){
        E temp = this.item;
        this.item = other.item;
        other.item = temp;
    }

    /**
     * Voegt de huide top toe aan de zeepbel.
     * @param zeepbel waaraan de top moet teoegevoegd worden.
     * @return <tt>true</tt> wanneer door deze toevoeging de zeepbel moet gesplitst worden.
     */
    public boolean setZeepbel(Zeepbel<E> zeepbel){
        this.zeepbel = zeepbel;
        return zeepbel.topAdded();
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

    /**
     * Methode om in inorde te itereren over de kinderen van de huidige top.
     *
     * @param consumer lambda die de volgende top behandeld.
     * @param predicate waaraan een top moet voldoen om te kunnen toegevoegd worden.
     */
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

    @Override
    public String toString() {
        return "Top: " + item.toString();
    }
}
