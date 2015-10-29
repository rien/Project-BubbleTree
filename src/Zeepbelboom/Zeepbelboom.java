package Zeepbelboom;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Rien Maertens
 *
 * Abstracte superklasse voor zeepbelbomen.
 */
public abstract class Zeepbelboom<E extends Comparable<E>> implements Collection<E> {

    private int size;
    private final int bubbleMaxSize;
    private int aantalZeepbellen;

    private Zeepbel<E> rootBubble;

    Zeepbelboom(int k) {
        if (k < 2){
            throw new IllegalArgumentException(String.format("Zeepbelboom heeft een" +
                    "minimale k-waarde van 2, maar kreeg  %d %n", k));
        }
        size = 0;
        aantalZeepbellen = 0;
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
        List<Zeepbel<E>> list = new ArrayList<>(aantalZeepbellen);
        rootBubble.getRoot().traverseInorder(t -> {
            Zeepbel<E> zb = t.getZeepbel();
            if (zb.getRoot() == t) {
                list.add(zb);
            }
        }, t -> true);
        return list.iterator();
    }

    private void setRootBubble(Zeepbel<E> rootBubble){
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
     * Splitst een zeepbel in twee en voeg de oude root toe aan de bovenliggende zeepbel.
     *
     * @param parent ouder waaraan de huidige root van de zeepbel zal worden toegevoegd aan zijn zeepbel.
     * @param root top die als kinderen de wortels bevat van de nieuwe zeepbellen, die na de operatie wordt toegevoegd
     *             aan de bovenliggende zeepbel.
     * @param bubble die in twee gesplitst moet worden.
     */
    void splitAndPushUp(Top<E> parent, Top<E> root, Zeepbel<E> bubble){
        // Deel de zeepbel in twee
        split(root, bubble);
        // Duw de huidige root omhoog
        pushRootUp(parent, root);
    }

    /**
     * Splits een zeepbel in twee.
     *
     * Enkel de toppen die del uitmaken van de opgegeven zeepbel worden overlopen en
     * aangezien een zeepbel een maximaal aantal toppen heeft (k) werkt deze methode in constante tijd.
     *
     * @param root top die als kinderen de wortels bevat van de nieuwe zeepbellen.
     * @param bubble die in twee gesplitst moet worden.
     */
    void split(Top<E> root, Zeepbel<E> bubble){
        Top<E> rightRoot = root.getRightChild();
        Top<E> leftRoot = root.getLeftChild();
        // Zet de rechterwortel als wortel van de rechterzeepbel
        bubble.setRoot(rightRoot);
        // Vorm een nieuwe zeepbel
        Zeepbel<E> newBubble = new Zeepbel<E>(this);
        // Alle kinderen van de nieuwe zeepbelwortel die nog in de oude zeepbel zitten worden lid van de nieuwe zeepbel.
        leftRoot.traverseInorder(t -> t.setZeepbel(newBubble), t -> t.getZeepbel() == bubble);
        // Maak de linkerwortel de wortel van de linkerzeepbel
        newBubble.setRoot(leftRoot);
        // De huidige zeepbel bevat de toppen uit de nieuwe zeepbel niet meer en ook niet de opgeborrelde top.
        bubble.topsRemoved(newBubble.size() + 1);
    }

    /**
     * Voeg de oude wortel van een zojuist gesplitste zeepbel toe aan de ouderzeepbel.
     * Indien nodig zal de ouderzeepbel ook splitsen.
     *
     * @param parent ouder van de top.
     * @param top die toegevoegd moet worden aan de bovenliggende zeepbel.
     */
    private void pushRootUp(Top<E> parent, Top<E> top){
        //Laat nu de gekozen top 'opborrelen';
        if(parent == null){
            //We zitten bij de root en moeten een nieuwe bubbel aanmaken
            createNewRootBubble(top);
        } else {
            parent.setChild(top);
            Zeepbel<E> parentBubble = parent.getZeepbel();
            if (top.setZeepbel(parentBubble)) {
                shrinkBubble(parentBubble);
            }
        }
    }

    /**
     * Maak een nieuwe zeepbel aan in de wortel.
     *
     * @param top die de wortel van de nieuwe wortelzeepbel moet worden.
     */
    void createNewRootBubble(Top<E> top){
        top.removeParent();
        Zeepbel<E> rootBubble = new Zeepbel<E>(this, top);
        setRootBubble(rootBubble);
    }


    /**
     * Voegt meerdere toppen toe aan de zeepbel van parent en splitst deze zeepbel indien nodig.
     *
     * @param parent waaraan de eerste van de toppen het kind van is.
     * @param tops lijst van toppen die moeten toegevoegd worden aan de zeepbel van de ouder.
     */
    void pushMultipleUp(Top<E> parent, List<Top<E>> tops){
        assert parent != null : "De parent mag niet null zijn! Je zit waarschijnlijk in de root.";
        Zeepbel<E> parentBubble = parent.getZeepbel();
        boolean balanceAfter;
        int after = tops.size() + parentBubble.size();
        if (after <= bubbleMaxSize){
            balanceAfter = false;
        } else if (after == bubbleMaxSize + 1){
            balanceAfter = true;
        } else {
            throw new IllegalArgumentException("Teveel toppen om toe te voegen!");
        }

        // De eerste top moet eerst toegevoegd worden als kind aan de parent.
        parent.setChild(tops.get(0));

        //Voeg iedere top toe aan de parentBubble;
        tops.forEach(t -> t.setZeepbel(parentBubble));
        //Verklein de zeepbel indien nodig
        if (balanceAfter){
            shrinkBubble(parentBubble);
        }
    }

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
                t->{/*De top werd gevonden en moet niet meer toegevoegd worden*/},
                t->addToParent(new Top<E>(e), t)
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
        return find(o, t -> {}, t->{});
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
     *
     * @return <tt>true</tt> wanneer het object gevonden werd.
     * @throws ClassCastException           wanneer het type van o niet overeenkomt
     *                                      met het type van de boom.
     * @throws NullPointerException         wanneer o <tt>null</tt> is.
     */
    private boolean find(Object o, Consumer<Top<E>> found, Consumer<Top<E>> closest){
        @SuppressWarnings("unchecked")
        E item = (E) o;
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
                found.accept(top);
                return true;
            }
        }
        closest.accept(parent);
       return false;
    }


    /**
     * Probeer een object uit de zeepbelboom te verwijderen.
     *
     * @param o het te verwijderen object.
     * @return <tt>true</tt> als het element zich in de zeepbelboom bevond en verwijderd werd.
     * @throws ClassCastException            als het type van het te verwijderen object
     *                                       incompatibel is met de huidige zeepbelboom.
     * @throws NullPointerException          als het te verwijderen object <tt>null</tt> is.
     */
    @Override
    public boolean remove(Object o) {
        return find(o, this::removeTop, t->{});
    }

    /**
     * Verwijder een top uit de zeepbelboom en balanceert indien nodig.
     *
     * @param toRemove uit de zeepbelboom die moet verwijderd worden.
     */
    private void removeTop(Top<E> toRemove){
        if (toRemove.hasRight() && toRemove.hasLeft()){
            //We zitten met een interne top, dus wisselen deze van plaats met de kleinste top uit de rechterdeelboom.
            Top<E> closest = toRemove.findClosestChild();
            toRemove.swapItems(closest);
            toRemove = closest;
        }
        // De top is een blad.

        Zeepbel<E> zb = toRemove.getZeepbel();
        if (zb.size() > 1){
            //De top zit niet alleen in de zeepbel, er moet dus niets op zeepbelniveau veranderd worden.
            removeLeaf(toRemove);

        } else {
            //Speciaal geval: door het verwijderen van de top komen we een lege zeepbel uit
            Top<E> sibling = toRemove.getSibling();
            Top<E> parent = toRemove.getParent();

            if (sibling.getZeepbel().size() > 1){
                //We kunnen een top van de tweelingzeepbel gebruiken
                if (sibling.isBubbleRoot()){
                    Top<E> closest = sibling.findClosestChild();
                    sibling.swapItems(closest);
                }

                //TODO


            } else{

            }

        }
    }

    /**
     *  Verwijder een blad uit de zeepbelboom.
     *
     * @param toRemove blad van de zeepbelboom
     */
    private void removeLeaf(Top<E> toRemove){
        Zeepbel<E> zb = toRemove.getZeepbel();
        if (!toRemove.hasLeft() && !toRemove.hasRight()){
            //De top heeft zelf geen kinderen (en is dus ook geen root), dus kunnen we de gewoon verwijderen.
            toRemove.removeFromParent();
        } else if (toRemove.hasLeft()){
            //De top heeft enkel een linkerkind
            toRemove.getParent().setChild(toRemove.getLeftChild());
            zb.topsRemoved(1);
        } else {
            //De top heeft enkel een richterkind
            toRemove.getParent().setChild(toRemove.getRightChild());
            zb.topsRemoved(1);
        }
    }

    /**

     * @return Een <tt>Iterator</tt> over alle elementen van de zeepbelboom.
     */
    @Override
    public Iterator<E> iterator() {
        ArrayList<E> items = new ArrayList<>(size);
        rootBubble.getRoot().traverseInorder(t -> items.add(t.getItem()), t -> true);
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
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size){
            a = ((T[]) new Object[size]);
        }
        int i = 0;
        Iterator<E> it= iterator();
        while (i < a.length){
            a[i++] = it.hasNext() ? (T)it.next() : null;
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
     * Verwijder alle elementen uit de zeepbelboom die in de gegeven collectie zitten.
     *
     * @param c collectie met de te verwijderen elementen.
     * @return <tt>true</tt> als de huidige zeepbelboom veranderd is door deze operatie.
     * @throws ClassCastException   als de type van één of meer elementen uit de collectie niet
     *                              compatibel is met deze zeepbelboom.
     * @throws NullPointerException als een element uit de collectie null is.
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for(Object o: c){
            if (remove(o)){
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
        rootBubble = null;
        aantalZeepbellen = 0;
    }
}
