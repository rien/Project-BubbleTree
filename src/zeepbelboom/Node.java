package zeepbelboom;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * De toppen van een zeepbelboom.
 * @author Rien Maertens
 */
public class Node<E extends Comparable<E>> implements Comparable<E> {


    private E item;

    private Zeepbel<E> zeepbel;

    private boolean removed;

    private Node<E> parent;
    private Node<E> leftChild;
    private Node<E> rightChild;

    public Node(E item){
        this.item = item;
    }

    public Node<E> getLeftChild() {
        return leftChild;
    }

    public Node<E> getParent() {
        return parent;
    }

    private void setParent(Node<E> parent) {
        this.parent = parent;
    }

    public void removeParent(){
        this.parent = null;
    }

    public void setLeftChild(Node<E> leftChild) {
        this.leftChild = leftChild;
        if (leftChild != null){
            leftChild.setParent(this);
        }
    }

    public Node<E> getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node<E> rightChild) {
        this.rightChild = rightChild;
        if (rightChild != null){
            rightChild.setParent(this);
        }
    }

    public void setChild(Node<E> child){
        if (compareTo(child.getItem()) < 0){
            setRightChild(child);
        } else {
            setLeftChild(child);
        }
    }

    public void clearChildren(){
        rightChild = null;
        leftChild = null;
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

    public boolean hasNull(){
        return leftChild == null || rightChild == null;
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
//    private void removeChild(Node<E> top){
//        if (leftChild == top){
//            leftChild = null;
//        } else if (rightChild == top){
//            rightChild = null;
//        } else {
//            throw new IllegalArgumentException("The given node is not a child of this node!");
//        }
//        zeepbel.topsRemoved(1);
//    }

    /**
     * @return de andere top met dezelfde ouder.
     */
    public Node<E> getSibling(){
        if (parent.leftChild == this){
            return parent.rightChild;
        } else if (parent.rightChild == this){
            return parent.leftChild;
        } else {
            throw new IllegalStateException("Parent has wrong link to children!");
        }
    }

    /**
     * @deprecated
     * @return de top met de dichtste waarde bij de huidige top.
     */
    @Deprecated
    public Node<E> findClosestChild(){
        Node<E> node;
        if (hasRight()){
            node = rightChild;
            while (node.hasLeft()){
                node = node.getLeftChild();
            }
        } else if(hasLeft()) {
            node = leftChild;
            while (node.hasRight()){
                node = node.getRightChild();
            }
        } else {
            node = this;
        }
        return node;
    }
    /*
    Alternatieve swapmethode die alle verwijzingen aanpast

    public void swap(Node<E> other){
        Node<E> tempParent = this.parent;
        Node<E> tempRight = this.rightChild;
        Node<E> tempLeft = this.leftChild;
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

    public void swapItems(Node<E> other){
        E temp = this.item;
        this.item = other.item;
        other.item = temp;
    }

    /**
     * Voegt de huide top toe aan de zeepbel.
     * @param zeepbel waaraan de top moet toegevoegd worden.
     */
    public void setZeepbel(Zeepbel<E> zeepbel){
        this.zeepbel = zeepbel;
        zeepbel.topAdded();
    }

    /**
     * @param o het object dat moet vergeleken worden.
     * @return een negatief getal, nul of een positief getal als het item in deze top
     * kleiner dan, gelijk aan of groter dan het gegeven object is.
     * @throws NullPointerException als het gegeven object null is.
     * @throws ClassCastException   als het gegeven object niet kan vergeleken worden omdat
     *                              die van een niet-compatibele klasse is.
     */
    @Override
    public int compareTo(E o) {
        return item.compareTo(o);
    }

    /**
     * Methode om in inorde te itereren over de kinderen van de huidige top.
     * Heeft een tijdscomplexiteit die lineair is met het aantal toppen die moet bezocht worden.
     *
     * @param consumer lambda die de volgende top behandeld.
     * @param predicate waaraan een top moet voldoen om te kunnen toegevoegd worden.
     */
    public void traverseInorder(Consumer<Node<E>> consumer, Predicate<Node<E>> predicate){
        Node<E> t = this;
        Deque<Node<E>> s = new ArrayDeque<>();
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

    /**
     * Overloop alle kinderen van deze top en voeg te toe aan de juiste collection.
     *
     * @param nodes kinderen van deze top die voldoen aan de voorwaarde.
     * @param children kinderen van de toppen uit <tt>nodes</tt> die niet meer voldoen aan de voorwaarde.
     * @param predicate voorwaarde waaraan een top moet voldoen om aan <tt>nodes</tt> toe gevoegd te worden.
     */
    public void traverseAndAdd(Collection<Node<E>> nodes, Collection<Node<E>> children, Predicate<Node<E>> predicate){
        traverseInorder(
                t->{
                    nodes.add(t);
                    if (!t.hasLeft() || !predicate.test(t.getLeftChild())){
                        children.add(t.getLeftChild());
                    }
                    if(!t.hasRight() || !predicate.test(t.getRightChild())){
                        children.add(t.getRightChild());
                    }
                },
                predicate
        );

    }


    @Override
    public String toString() {
        String str = "N: " + item.toString();
        if (zeepbel != null && zeepbel.getRoot() == this){
            str = "|" + str + "|";
        }
        return str;
    }
}
