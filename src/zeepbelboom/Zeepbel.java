package zeepbelboom;


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
     */
    public void topAdded(){
        assert size <= maxSize : "Bubble is too big!";
        size++;
    }

    /**
     * @return <tt>true</tt> wanneer de zeepbel overvol zit en moet splitsen.
     */
    public boolean hasToBurst(){
        return size > maxSize;
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


    public Zeepbel<E> getSiblingZeepbel(){
        Zeepbel<E> parentBubble = getRoot().getParent().getZeepbel();
        Top<E> top = getRoot().getSibling();
        boolean right = getRoot().compareTo(top.getItem()) < 0;
        while (top.getZeepbel() == parentBubble){
            top = right ? top.getRightChild() : top.getLeftChild();
        }
        //Top zit nu in een andere zeepbel
        return top.getZeepbel();
    }

    public void moveAllChildrenTo(Zeepbel<E> other){
        root.traverseInorder(t->t.setZeepbel(other),t->t.getZeepbel() == this);
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

    public Iterator<Top<E>> topIterator(){
        ArrayList<Top<E>> toppen = new ArrayList<>(size);
        root.traverseInorder(toppen::add, t->t.getZeepbel() == this);
        return toppen.iterator();
    }

    /**
     * @return the size of the current bubble, for debugging purposes.
     */
    public int checkBubbleSize(){
        ArrayList<E> items = new ArrayList<>(size);
        root.traverseInorder(t -> items.add(t.getItem()), t -> t.getZeepbel() == this);
        return items.size();
    }

    @Override
    public String toString() {
        return "Zeepbel {root " + root.getItem().toString() + "}";
    }


    /**
     * Balanceer de zeepbel door eerst alle toppen van de zeepbel in een lijst te steken.
     * Omdat de toppen in inorde in de lijst worden gestoken is deze lijst gesorteerd en kan een nieuwe
     * gebalanceerde binaire boom recursief worden opgebouwd. Deze methode werkt in lineaire tijd, waar we
     * het aantal toppen van de te balanceren zeepbel als inputgrootte beschouwen.
     */
    public void balanceBubble(){
        List<Top<E>> nodes = new ArrayList<>();
        List<Top<E>> children = new ArrayList<>();
        getRoot().traverseInorder(t -> {
                    nodes.add(t);
                    if (!t.hasLeft() || t.getLeftChild().getZeepbel() != this) {
                        children.add(t.getLeftChild());
                    }
                    if (!t.hasRight() || t.getRightChild().getZeepbel() != this) {
                        children.add(t.getRightChild());
                    }
                },
                t -> t.getZeepbel() == this
        );
        Top<E> newRoot = listToTree(nodes,0,nodes.size()-1);
        assert newRoot != null;
        newRoot.removeParent();
        setRoot(newRoot);
        int nextChild = 0;
        for (Top<E> t : nodes) {
            if (!t.hasLeft()){
                t.setLeftChild(children.get(nextChild++));
            }
            if (!t.hasRight()){
                t.setRightChild(children.get(nextChild++));
            }
        }
    }

    /**
     * Recursieve methode van een gesorteerde lijst van toppen
     * een zo goed mogelijk gebalanceerde binaire boom opsteld.
     *
     * @param list van toppen die in gesorteerde volorde zitten.
     * @param start index vanaf waar de boom moet worden opgebouwd.
     * @param end index tot waar de moet moet worden opgebouwd.
     * @return de <tt>Top</tt> van de gebalanceerde binaire boom.
     */
    private static  <E extends Comparable<E>> Top<E> listToTree(List<Top<E>> list, int start, int end){
        if (end < start){
            return null;
        } else {
            int mid = start + ((end - start)/2);
            Top<E> root = list.get(mid);
            root.setLeftChild(listToTree(list, start, mid - 1));
            root.setRightChild(listToTree(list, mid + 1, end));
            return root;
        }
    }
}
