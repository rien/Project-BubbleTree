package Zeepbelboom;

/**
 * Die probeert de onderliggende boom zo intact mogelijk te houden bij het toevoegen.
 *
 * Algoritme voor toeveogen:
 * - Voeg item toe aan de boom zoals normaal en voeg de top toe aan de zeepbel van de parent
 * - Als de zeepbel te vol zit:
 *      - Als de root maar één kind (in dezelfde zeepbel) bevat: roteer de eerste 3
 *      - Duw de root omhoog en maak van de linker- en rechterdeelboom een nieuwe zeepbel.
 *      - Pas de bovenliggende zeepbel aan, indien nodig
 *
 * Algoritme voor verwijderen:
 * - Stel X het te verwijderen item met ouder P. P heeft een nu leeg kind.
 * - Neem K het ander kind van K. Neem K2 het grootse/kleinste kind binnen dezelfde zeepbel.
 * - Verwijder dit kind en plaats het op de plaats van P en plaats P de plaats van X.
 * Stel: ander kind van K2  (NU) is leeg:
 *      Duw K2 bij P.
 *      Doe nu alsof K2 werd verwijderd en do zo verder.
 */
public class Zeepbelboom1<E extends Comparable<E>> extends Zeepbelboom<E> {


    public Zeepbelboom1(int k) {
        super(k);
    }

    @Override
    public boolean add(E e) {
        //Als de boom leeg is maken we de eerste zeepbel aan.
        if (isEmpty() || getRoot() == null){
            Zeepbel<E> rootBubble = new Zeepbel<E>(this, new Top<E>(e));

            setRootBubble(rootBubble);
            size++;
            return true;
        }

        //Zoek de parent van de toe te voegen top.
        Top<E> top = getRoot();
        Top<E> parent = null;
        int comp;
        while (top != null){
            comp = top.compareTo(e);
            parent = top;
            if (comp < 0){
                top = parent.getLeftChild();
            } else if (comp > 0){
                top = parent.getRightChild();
            } else {
                // e is al in de zeepbelboom.
                return false;
            }
        }

        //Nu is parent de Top waaraan het nieuwe item e moet toegevoegd worden.
        Zeepbel<E> zb = parent.getZeepbel();
        Top<E> child = new Top<E>(e);
        parent.setChild(child);
        if(child.setZeepbel(zb)){ //The bubble is full and this has to be solved.
            splitBubble(zb);
        }
        size++;
        return true;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void splitBubble(Zeepbel<E> bubble){
        Top<E> a = bubble.getRoot();
        Top<E> p = a.getParent();
        Top<E> up; //Top die 'opborrelt';
        Top<E> newBubbleRoot; //Top die de root vormt van de nieuwe zeepbel
        if (!(a.hasLeft() && a.leftChildInSameBubble())){
            //Roteer de eerste drie toppen in de zeepbel:
            Top<E> b = a.getRightChild();
            newBubbleRoot = a;
            if (b.hasRight() && b.rightChildInSameBubble()){
                /* P\                P\
                 *   A                 B
                 * W/ \B      ->    A/   \C
                 *   X/ \C         W/\X Y/\Z
                 *      Y/\Z
                 */
                Top<E> c = b.getRightChild();
                Top<E> x = a.getRightChild();
                a.setRightChild(x);
                b.setLeftChild(a);
                bubble.setRoot(c);
                up = b;
            } else {
                /* P\               P\
                 *   A                C
                 * W/ \B     ->    A/   \B
                 *   C/ \Z        W/\X Y/\Z
                 *  X/\Y
                 */
                Top<E> c = b.getLeftChild();
                Top<E> x = c.getRightChild();
                Top<E> y = c.getLeftChild();
                a.setRightChild(x);
                b.setLeftChild(y);
                c.setLeftChild(a);
                c.setRightChild(b);
                bubble.setRoot(b);
                up = c;
            }
        } else if (!(a.hasRight() && a.rightChildInSameBubble())){
            Top<E> b = a.getLeftChild();
            newBubbleRoot = a;
            if (b.hasLeft() && b.leftChildInSameBubble()){
                /*     P\           P\
                 *      A            B
                 *    B/ \Z  ->    C/  \A
                 *  C/ \Y        W/\X Y/\Z
                 * W/\X
                 */
                Top<E> c = b.getLeftChild();
                Top<E> y = b.getRightChild();
                a.setLeftChild(y);
                b.setRightChild(a);
                bubble.setRoot(c);
                up = b;
            } else {
                /*     P\          P\
                 *      A            C
                 *   B/   \Z  ->   B/  \A
                 *  W/ \C         W/\X Y/\Z
                 *    X/\Y
                 */
                Top<E> c = b.getRightChild();
                Top<E> x = c.getLeftChild();
                Top<E> y = c.getRightChild();
                a.setLeftChild(y);
                b.setRightChild(x);
                c.setLeftChild(b);
                c.setRightChild(a);
                bubble.setRoot(b);
                up = c;
            }
        } else {
            /*   P\
             *     A
             *   B/ \C
             *
             * Hier zitten A, B, en C in dezelfde zeepbel. Er moet dus niet geroteerd worden, enkel gesplitst.
             */
            Top<E> b = a.getLeftChild();
            Top<E> c = a.getRightChild();
            newBubbleRoot = b;
            bubble.setRoot(c);
            up = a;
        }

        //Vorm een nieuwe zeepbel
        Zeepbel<E> newBubble = new Zeepbel<E>(this, newBubbleRoot);
        //Alle kinderen van de nieuwe zeepbelwortel die nog in de oude zeepbel zitten worden lid van de nieuwe zeepbel.
        newBubbleRoot.traverseInorder(t -> t.setZeepbel(newBubble),t -> t.getZeepbel() == bubble);
        //De huidige zeepbel bevat de toppen uit de nieuwe zeepbel niet meer en ook niet de opgeborrelde top.
        bubble.topsRemoved(newBubble.size() - 1);

        //Laat nu de gekozen top 'opborrelen';
        if(p == null){
            //We zitten bij de root en moeten een nieuwe bubbel aanmaken
            Zeepbel<E> rootBubble = new Zeepbel<E>(this, up);
            up.setZeepbel(rootBubble);
            setRootBubble(rootBubble);
        } else {
            Zeepbel<E> parentBubble = p.getZeepbel();
            if (up.setZeepbel(parentBubble)) {
                splitBubble(parentBubble);
            }
        }
    }


    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }
}
