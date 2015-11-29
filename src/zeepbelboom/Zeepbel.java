package zeepbelboom;


import java.util.*;

/**
 * Klasse die een zeepbel voorstelt.
 * @author Rien Maertens
 */
public class Zeepbel<E extends Comparable<E>> implements Iterable<E>{

    private int size;
    private final int maxSize;
    private Node<E> root;

    public Zeepbel(Zeepbelboom<E> tree){
        this.maxSize = tree.getBubbleMaxSize();
        this.size = 0;
    }

    /**
     * Alternatieve constructor waaraan een top kan meegegeven worden die onmiddelijk
     * als wortel kan gezet worden.
     *
     * @param tree zeepbelboom waar deze zeepbel in zit.
     * @param root wortel van deze zeepbel.
     */
    public Zeepbel(Zeepbelboom<E> tree, Node<E> root){
        this(tree);
        this.root = root;
        root.setZeepbel(this);
    }

    public Node<E> getRoot(){
        return root;
    }

    public void setRoot(Node<E> node){
        this.root = node;
    }

    public E getWortelSleutel(){
        return root.getItem();
    }

    public int getMaxSize() {
        return maxSize;
    }

    /**
     * @return het aantal toppen in deze zeepbel.
     */
    public int size() {
        return size;
    }

    /**
     * @return <tt>true</tt> als de zeepbel leeg is.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return <tt>true</tt> als de zeepbel vol zit.
     */
    public boolean isFull(){
        return size >= maxSize;
    }

    /**
     * Methode die aan de zeepbel verteld dat er zojuist een top is toegevoegd.
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

    /**
     * @deprecated
     * @return een tweeling van deze zeepbel
     */
    public Zeepbel<E> getSiblingbubble(){
        Zeepbel<E> parentBubble = getRoot().getParent().getZeepbel();
        Node<E> node = getRoot().getSibling();
        boolean right = getRoot().compareTo(node.getItem()) < 0;
        while (node.getZeepbel() == parentBubble){
            node = right ? node.getRightChild() : node.getLeftChild();
        }
        //Node zit nu in een andere zeepbel
        return node.getZeepbel();
    }

    public Zeepbel<E> getParentBubble(){
        return root.getParent() == null ? null : root.getParent().getZeepbel();
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
        if (!isEmpty()){
            root.traverseInorder(
                    t ->{
                        if(!t.isRemoved()) items.add(t.getItem());
                    },
                    t -> t.getZeepbel() == this);
        }
        return items.iterator();
    }

//    public Iterator<Node<E>> topIterator(){
//        ArrayList<Node<E>> toppen = new ArrayList<>(size);
//        if (!isEmpty()){
//            root.traverseInorder(toppen::add, t->t.getZeepbel() == this);
//        }
//        return toppen.iterator();
//    }

    /**
     * @return de huidige grootte van de zeepbel. Handig om te debuggen.
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
        if (size > 2){
            Node<E> parent = root.getParent();
            List<Node<E>> nodes = new ArrayList<>();
            List<Node<E>> children = new ArrayList<>();
            getRoot().traverseAndAdd(nodes,children,t -> t.getZeepbel() == this);
            TreeBuilder<E> builder = new TreeBuilder<>(nodes);
            Node<E> newRoot = builder.getRoot();
            builder.attachChildren(children);
            setRoot(newRoot);
            newRoot.removeParent();
            if (parent != null){
                parent.setChild(newRoot);
            }
       }
    }

    public void clear(){
        this.size = 0;
        this.root = null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
