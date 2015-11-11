package zeepbelboom;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Rien Maertens
 *
 * Abstracte superklasse voor zeepbelbomen.
 */
public abstract class Zeepbelboom<E extends Comparable<E>> implements Collection<E> {

    protected int size;
    protected int bubbleMaxSize;


    protected int tombStones;
    protected final int maxTombstoneRatio = 50; //Maximaal % aan tombstones.

    private Zeepbel<E> rootBubble;

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
     * @return een iterator van alle zeepbellen die in inorde gesorteerd zijn.
     */
    public Iterator<Zeepbel<E>> zeepbelIterator() {
        List<Zeepbel<E>> list = new ArrayList<>();
        rootBubble.getRoot().traverseInorder(t -> {
            Zeepbel<E> zb = t.getZeepbel();
            if (zb.getRoot() == t) {
                list.add(zb);
            }
        }, t -> true);
        return list.iterator();
    }

    protected void setRootBubble(Zeepbel<E> rootBubble){
        this.rootBubble = rootBubble;
    }


    public Top<E> getRoot(){
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
        if (isEmpty() || getRoot() == null){
            Zeepbel<E> rootBubble = new Zeepbel<E>(this, new Top<E>(e));

            setRootBubble(rootBubble);
            size++;
            return true;
        }

        //Wanneer het item gevonden werd en al in de zeepbel zit moet false teruggegeven worden.
        return !find(
                e,
                t->{},
                t->addToParent(new Top<>(e), t),
                Top::unRemove
        );
    }

    /**
     * Voeg een nieuwe top toe aan een top die al in de zeepbelboom zit.
     * @param child toe te voegen top.
     * @param parent ouder waaraan het kind moet toegevoegd worden.
     */
    private void addToParent(Top<E> child, Top<E> parent){
        Zeepbel<E> zb = parent.getZeepbel();
        parent.setChild(child);
        if(child.setZeepbel(zb)){
            //De zeepbel zit overvol en moet verkleind worden.
            shrinkBubble(zb);
        }
        size++;
    }

    /**
     * Methode die moet worden opgeropen wanneer een zeepbel zijn maximale overschrijd en moet verkleind worden.
     *
     * @param bubble die moet verkleind worden.
     */
    protected abstract void shrinkBubble(Zeepbel<E> bubble);

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
    protected boolean find(Object o, Consumer<Top<E>> found, Consumer<Top<E>> closest, Consumer<Top<E>> tombStone){
        E item = castToType(o);
        if (size == 0) return false;
        Top<E> top = getRoot();
        Top<E> parent = null;
        int comp;
        while (top != null){
            parent = top;
            comp = top.compareTo(item);
            if (comp < 0){
                top = parent.getRightChild();
            } else  if (comp > 0){
                top = parent.getLeftChild();
            } else {
                //Comp == 0, dus we hebben o gevonden.
                if (top.isRemoved()){
                    tombStone.accept(top);
                    return false;
                } else {
                    found.accept(top);
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
        rootBubble.getRoot().traverseInorder(t -> {
            if(!t.isRemoved()) {
                items.add(t.getItem());
            }}, t -> true);
        return items.iterator();
    }

    /**
     * @return een array met alle elementen van de zeepbelboom, in volgorde.
     */
    @Override
    public Object[] toArray() {
        Object[] a = new Object[size];
        Iterator<E> it = iterator();
        int i = 0;
        while (it.hasNext()){
            a[i] = it.next();
        }
        return a;
    }

    /**
     * Plaatst alle elementen van de zeepbelboom in de opgegeven array wanneer deze groot genoeg is.
     *
     * @param a array waar alle elementen van de zeepbelboom in geplaatst moeten worden. Indien a groot genoeg is
     *          wordt deze ook teruggegeven, als a te klein is wordt er een nieuwe array aangemaakt en teruggeven die
     *          wel groot genoeg is.
     * @return een array waar alle elementen van de zeepbelboom inzitten.
     * @throws ArrayStoreException  als het type van de array niet compatibel is.
     * @throws NullPointerException als de opgegeven array <tt>null</tt> is.
     */
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size){
            a = castToType(new Object[size]);
        }
        int i = 0;
        Iterator<E> it= iterator();
        while (i < a.length){
            a[i++] = it.hasNext() ? castToType(it.next()) : null;
        }
        return a;
    }

    /**
     * Kijk of alle elementen van een collection ook te vinden zijn in deze zeepbelboom.
     *
     * @param c de collection die moet doorzocht worden.
     * @return <tt>true</tt> als deze zeepbelboom alle elementen uit de opgegeven collectie bevat.
     * @throws ClassCastException   als de type van één of meer elementen uit de collectie niet
     *                              compatibel is met deze zeepbelboom.
     * @throws NullPointerException als een element uit de collectie null is.
     * @see #contains(Object)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o: c){
            if (!contains(o)){
                return false;
            }
        }
        return true;
    }

    /**
     * Voeg alle elementen uit een collectie toe aan de zeepbelboom.
     *
     * @param c de collectie waarvan alle elementen moeten toegevoegd worden.
     * @return <tt>true</tt> als de huidige zeepbelboom veranderd is door deze operatie.
     * @throws ClassCastException   als de type van één of meer elementen uit de collectie niet
     *                              compatibel is met deze zeepbelboom.
     * @throws NullPointerException als een element uit de collectie null is.l
     * @see #add(Object)
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e: c){
            if (add(e)){
                changed = true;
            }
        }
        return changed;
    }



    /**
     * Houd enkel de elementen in deze zeepbel over die ook te vinden zijn in de opgegeven colelctie.
     *
     * @param c collectie met elementen die moeten overgehouden worden.
     * @return <tt>true</tt> als de huidige zeepbelboom veranderd is door deze operatie.
     * @throws ClassCastException   als de type van één of meer elementen uit de collectie niet
     *                              compatibel is met deze zeepbelboom.
     * @throws NullPointerException als een element uit de collectie null is.
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Iterator<E> it = iterator();
        E item;
        boolean changed = false;
        while (it.hasNext()){
            item = it.next();
            if (!c.contains(item)){
                remove(item);
                changed = true;
            }
        }
        return changed;
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
