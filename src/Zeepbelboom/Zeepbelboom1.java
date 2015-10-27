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
    @SuppressWarnings("SuspiciousNameCombination")
    protected void shrinkBubble(Zeepbel<E> bubble){
        Top<E> a = bubble.getRoot();
        Top<E> parent = a.getParent();
        Top<E> up; //Top die 'opborrelt';
        Top<E> newBubbleRoot; //Top die de root moet worden van de nieuwe zeepbel
        Top<E> oldBubbleRoot; //Top die de root moet worden van de oude zeepbel
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
                Top<E> x = b.getLeftChild();
                a.setRightChild(x);
                b.setLeftChild(a);
                oldBubbleRoot = c;
                up = b;
            } else {
                /* P\               P\
                 *   A                C
                 * W/ \B     ->    A/   \B
                 *   C/ \Z        W/\X Y/\Z
                 *  X/\Y
                 */
                Top<E> c = b.getLeftChild();
                Top<E> x = c.getLeftChild();
                Top<E> y = c.getRightChild();
                a.setRightChild(x);
                b.setLeftChild(y);
                c.setLeftChild(a);
                c.setRightChild(b);
                oldBubbleRoot = b;
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
                oldBubbleRoot = c;
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
                oldBubbleRoot = b;
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
            oldBubbleRoot = c;
            up = a;
        }

        splitBubble(newBubbleRoot,oldBubbleRoot, bubble);
        pushRootUp(parent,up);
    }
}
