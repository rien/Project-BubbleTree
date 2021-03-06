package zeepbelboom;

/**
 * * Implementatie van zeepbelboom die probeert de onderliggende boom zo intact mogelijk te houden bij het toevoegen.
 *
 * Algoritme voor toevoegen:
 * - Voeg item toe aan de boom zoals normaal in een binaire boom en voeg de top toe aan de zeepbel van de parent
 * - Als de zeepbel te vol zit:
 *      - Als de root maar één kind (in dezelfde zeepbel) bevat: roteer de eerste 3
 *      - Duw de root omhoog en maak van de linker- en rechterdeelboom een nieuwe zeepbel.
 *      - Pas de bovenliggende zeepbel aan, indien nodig
 * @author Rien Maertens
 *
 */
public class Zeepbelboom1<E extends Comparable<E>> extends ShrinkingBubbleTree<E> {


    public Zeepbelboom1(int k) {
        super(k);
    }

    /**
     * Verklein de huidige zeepbel door ze in twee te splitsen.
     * Wanneer de wortel maar één kind (in dezelfde zeepbel) heeft worden de eerste drie
     * toppen geroteerd zodat de kinderen van de wortel in dezelfde zeepbel zitten en de zeepbel
     * kan gesplitst worden.
     *
     * Dit verkleinen gebeurt in constante tijd, aangezien er telkens maar drie toppen geroteerd worden,
     * onafhankelijk van de inputgrootte.
     *
     * @param bubble die moet verkleind worden.
     */
    @Override
    protected void shrinkBubble(Zeepbel<E> bubble){
        Node<E> a = bubble.getRoot(); //Huidige root
        Node<E> parent = a.getParent(); //Parent van de huidige root die in een bovenliggende zeepbel zit.
        Node<E> up; //Node die aan de bovenliggende zeepbel wordt toegevoegd.
        if (!(a.hasLeft() && a.leftChildInSameBubble())){
            //Roteer de eerste drie toppen in de zeepbel:
            Node<E> b = a.getRightChild();
            if (b.hasRight() && b.rightChildInSameBubble()){
                /* P\                P\
                 *   A                 B
                 * W/ \B      ->    A/   \C
                 *   X/ \C         W/\X Y/\Z
                 *      Y/\Z
                 */
                Node<E> x = b.getLeftChild();
                a.setRightChild(x);
                b.setLeftChild(a);
                up = b;
            } else {
                /* P\               P\
                 *   A                C
                 * W/ \B     ->    A/   \B
                 *   C/ \Z        W/\X Y/\Z
                 *  X/\Y
                 */
                Node<E> c = b.getLeftChild();
                Node<E> x = c.getLeftChild();
                Node<E> y = c.getRightChild();
                a.setRightChild(x);
                b.setLeftChild(y);
                c.setLeftChild(a);
                c.setRightChild(b);
                up = c;
            }
        } else if (!(a.hasRight() && a.rightChildInSameBubble())){
            Node<E> b = a.getLeftChild();
            if (b.hasLeft() && b.leftChildInSameBubble()){
                /*     P\           P\
                 *      A            B
                 *    B/ \Z  ->    C/  \A
                 *  C/ \Y        W/\X Y/\Z
                 * W/\X
                 */
                Node<E> y = b.getRightChild();
                a.setLeftChild(y);
                b.setRightChild(a);
                up = b;
            } else {
                /*     P\          P\
                 *      A            C
                 *   B/   \Z  ->   B/  \A
                 *  W/ \C         W/\X Y/\Z
                 *    X/\Y
                 */
                Node<E> c = b.getRightChild();
                Node<E> x = c.getLeftChild();
                Node<E> y = c.getRightChild();
                a.setLeftChild(y);
                b.setRightChild(x);
                c.setLeftChild(b);
                c.setRightChild(a);
                up = c;
            }
        } else {
            /*   P\
             *     A
             *   B/ \C
             *
             * Hier zitten A, B, en C in dezelfde zeepbel. Er moet dus niet geroteerd worden, enkel gesplitst.
             */
            up = a;
        }
        splitAndPushUp(parent, up, bubble);
    }
}
