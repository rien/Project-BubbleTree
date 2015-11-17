package zeepbelboom;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Rien Maertens
 *
 * Abstracte superklasse voor zeepbelbomen.
 */
public abstract class Zeepbelboom<E extends Comparable<E>> extends  AbstractCollection<E> {

    protected int size;
    protected int bubbleMaxSize;


    protected int tombStones;
    protected final int maxTombstoneRatio = 50; //Maximaal % aan tombstones.

    protected Zeepbel<E> rootBubble;

    Zeepbelboom(int k) {
        if (k < 2){
            throw new IllegalArgumentException(String.format("Zeepbelboom heeft een" +
                    "minimale k-waarde van 2, maar kreeg  %d %n", k));
        }
        size = 0;
        bubbleMaxSize = k;
    }

    /**
     * @return de sleutel van de wortel van de boom.
     */
    public E getWortelSleutel() {
        return rootBubble.getWortelSleutel();
    }

    /**
     * Voeg een item toe aan de zeepbelboom. Dit gebeurt in logaritmische tijd.
     *
     * @param e item die moet toegevoegd worden aan de zeepbelboom.
     * @return <tt>true</tt> als het item nog niet in de zeepbelboom zat en is toegevoegd.
     *
     * @throws ClassCastException            als de klasse van het meegegeven item niet
     *                                       compatibel is met deze zeepbelboom en dus
     *                                       niet kon toegevoegd worden.
     * @throws NullPointerException          als het toe te voegen element <tt>null</tt> is.
     */
    @Override
    public boolean add(E e) {
        //Als de boom leeg is maken we de eerste zeepbel aan.
        if (isEmpty()){
            rootBubble = new Zeepbel<>(this, new Node<>(e));
            size++;
            return true;
        }

        //Wanneer het item gevonden werd en al in de zeepbel zit moet false teruggegeven worden.
        return !find(
                e,
                t->{},
                t->addToParent(t, e),
                Node::unRemove
        );
    }

    protected abstract void addToParent(Node<E> parent, E item);

    /**
     * @return een iterator van alle zeepbellen die in inorde gesorteerd zijn.
     */
    public Iterator<Zeepbel<E>> zeepbelIterator() {
        List<Zeepbel<E>> list = new ArrayList<>();
        if (!isEmpty()){
            rootBubble.getRoot().traverseInorder(t -> {
                Zeepbel<E> zb = t.getZeepbel();
                if (zb.getRoot() == t) {
                    list.add(zb);
                }
            }, t -> true);
        }
        return list.iterator();
    }

    protected void setRootBubble(Zeepbel<E> rootBubble){
        this.rootBubble = rootBubble;
    }


    public Node<E> getRoot(){
        return rootBubble.getRoot();
    }

    /**
     * @return maximum grootte van een zeepbel (k).
     */
    public int getBubbleMaxSize(){return bubbleMaxSize;}


    /**
     * @return het aantal elementen in de zeepbelboom.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * @return <tt>true</tt> als deze zeepbelboom leeg is.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Kijk of het gegeven object in de zeepbelboom zit.
     *
     * @param o object die gezocht wordt.
     * @return <tt>true</tt> als het object zich in de zeepbelboom bevind.
     * @throws NullPointerException         wanneer o <tt>null</tt> is.
     */
    @Override
    public boolean contains(Object o) {
        return find(o,t->{}, t->{},t->{});
    }

    /**
     * Vind een object in de zeepbelboom.
     * Dit gebeurt in logaritmische tijd in relatie met het aantal elementen in de zeepbelboom.
     *
     * @param o object die moet gevonden worden.
     * @param found als het object gevonden werd wordt het aan deze consumer meegegeven.
     * @param closest als het object niet in de zeepbelboom zit wordt de top aan deze consumer weergegeven
     *                die normaal de parent van de top zou zijn die het item bevat, moest het item zich
     *                in de zeepbelboom bevinden.
     * @param tombStone als het object in de zeepbelboom zit maar een grafsteen is wordt de top aan deze
     *
     *
     *
     * @return <tt>true</tt> wanneer het object gevonden werd.
     * @throws ClassCastException           wanneer het type van o niet overeenkomt
     *                                      met het type van de boom.
     * @throws NullPointerException         wanneer o <tt>null</tt> is.
     */
    protected boolean find(Object o, Consumer<Node<E>> found, Consumer<Node<E>> closest, Consumer<Node<E>> tombStone){
        E item = castToType(o);
        if (size == 0) return false;
        Node<E> node = getRoot();
        Node<E> parent = null;
        int comp;
        while (node != null){
            parent = node;
            comp = node.compareTo(item);
            if (comp < 0){
                node = parent.getRightChild();
            } else  if (comp > 0){
                node = parent.getLeftChild();
            } else {
                //Comp == 0, dus we hebben o gevonden.
                if (node.isRemoved()){
                    tombStone.accept(node);
                    return false;
                } else {
                    found.accept(node);
                    return true;
                }
            }
        }
        closest.accept(parent);
       return false;
    }

    /**

     * @return Een <tt>Iterator</tt> over alle elementen van de zeepbelboom.
     */
    @Override
    public Iterator<E> iterator() {
        ArrayList<E> items = new ArrayList<>(size);
        if (!isEmpty()){
            rootBubble.getRoot().traverseInorder(t -> {
                if(!t.isRemoved())
                    items.add(t.getItem());
                }, t -> true);
        }
        return items.iterator();
    }

    /**
     * Verwijder alle elementen uit deze zeepbel.
     */
    @Override
    public void clear() {
        size = 0;
        tombStones = 0;
        rootBubble = null;
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + " with K=" + String.valueOf(getBubbleMaxSize());
    }

    @SuppressWarnings("unchecked")
    private <T> T castToType(Object obj){
        return (T) obj;
    }
}
