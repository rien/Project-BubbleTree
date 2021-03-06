package zeepbelboom;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Implementatie van een zeebelboom die bij het splitsen gaat proberen zoveel mogelijk
 * toppen omhoog te duwen.
 *
 * De methode heeft de volgende variabelen die kunnen geconfigureerd worden:
 *
 * - maxPushUp:             Het maximaal aantal toppen die omhoog mag geduwt worden.
 *                          Werkt enkel wanneer maxPushUp <tt>true</tt> is.
 * - maxPushUpEnabled:      Booleaanse waarde die bepaald of er een limiet op het
 *                          Aantal omhoog te duwen toppen staat.
 * - forceParentOverflow:   Wanneer dit <tt>true</tt> is wordt de ouder (indien mogelijk) geforceerd om te splisen.
 *                          Als dit <tt>false</tt> is wordt de ouder volledig vol gestopt zodat die net niet splitst.
 * @author Rien Maertens
 */
public class Zeepbelboom3<E extends Comparable<E>> extends ShrinkingBubbleTree<E> {


    private  int maxPushUp;
    private  boolean maxPushUpEnabled;
    private  boolean forceParentOverflow;

    public Zeepbelboom3(int k) {
        super(k);
        maxPushUp = 5;
        maxPushUpEnabled = false;
        forceParentOverflow = false;
    }

    public Zeepbelboom3(int k, boolean maxPushUpEnabled, int maxPushUp, boolean forceParentOverflow){
        super(k);
        if (maxPushUpEnabled && maxPushUp < 1){
            throw new IllegalArgumentException("De waarde van maxPushUp mag niet negatief zijn als maxPushUp enabled is.");
        }
        this.maxPushUpEnabled = maxPushUpEnabled;
        this.maxPushUp = maxPushUp;
        this.forceParentOverflow = forceParentOverflow;
    }

    /**
     * Verklein een zeepbel door te proberen zoveel mogelijk toppen toe te voegen aan de ouderzeepbel.
     *
     * @param bubble die moet verkleind worden.
     */
    @Override
    protected void shrinkBubble(Zeepbel<E> bubble) {
        Node<E> parent = bubble.getRoot().getParent();
        bubble.balanceBubble();
        Node<E> root = bubble.getRoot();

        if (parent == null){
            //We zitten aan de wortel, dus we moeten een nieuwe aanmaken.
            split(root, root.getZeepbel());
            createNewRootBubble(root);
        } else {
            //Duw net genoeg toppen omhoog tot de parent vol zit.
            int amountToPush = getBubbleMaxSize() - parent.getZeepbel().size();

            if (forceParentOverflow){
                //Forceer het splitsen van de ouder door net één top extra aan de zeepbel toe te voegen.
                amountToPush += 1;
            }
            if (maxPushUpEnabled){
                //Controleer of het maximum aantal omhoog te duwen toppen niet overschreven is
                amountToPush = amountToPush > maxPushUp ? maxPushUp : amountToPush;
            }


            if (amountToPush <= 1){
                //De bovenliggende zeepbel is vol en moet dus op een normale manier geplitst worden.
                splitAndPushUp(parent, root, root.getZeepbel());
            } else {
                List<Node<E>> topsToPush = new ArrayList<>(amountToPush);
                Queue<Node<E>> children = new ArrayDeque<>();

                //Ga op een BFS-manier door de zeepbel en blijf splitsen tot we genoeg toppen hebben
                // of één van de kinderen null is
                while (topsToPush.size() < amountToPush){
                    if (!(root.hasLeft() && root.leftChildInSameBubble()) ||
                        !(root.hasRight() && root.rightChildInSameBubble())){
                        break;
                    }
                    topsToPush.add(root);
                    children.add(root.getLeftChild());
                    children.add(root.getRightChild());
                    split(root, root.getZeepbel());
                    root = children.remove();
                }
                pushMultipleUp(parent, topsToPush);
            }
        }

    }
    @Override
    public String toString() {
        String pushup = maxPushUpEnabled ? "maxPushup = " + String.valueOf(maxPushUp) : "maxPushUp disabled";
        String forceOverflow = forceParentOverflow ? "parentOverflow enabled" : "parentOverflow disabled";
        return super.toString() + ", " + pushup + " and " + forceOverflow + ".";
    }

    @Override
    public String shortName() {
        String pushup = maxPushUpEnabled ? "_maxPushUp" + String.valueOf(maxPushUp) : "";
        String forceOverflow = forceParentOverflow ? "_parentOverflow" : "";
        return super.shortName() + pushup + forceOverflow ;
    }
}
