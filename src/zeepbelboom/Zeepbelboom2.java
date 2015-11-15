package zeepbelboom;

/**
 * @author Rien Maertens
 *
 * Implementatie van een zeepbelboom die overvolle zeepbellen gaat verkleinen door ze eerst zo goed
 * mogelijk te balanceren om vervolgens de zeepbel in twee te splitsen en de wortel omhoog te duwen.
 */
public class Zeepbelboom2<E extends Comparable<E>> extends ShrinkingBubbleTree<E> {

    public Zeepbelboom2(int k) {
        super(k);
    }


    /**
     * Splits een zeepbel door deze eerst te balanceren,
     * daarna de wortel omhoog te duwen en twee nieuwe zeepbellen te maken.
     *
     * @param bubble die moet verkleind worden.
     */
    @Override
    protected void shrinkBubble(Zeepbel<E> bubble) {
        Node<E> parent = bubble.getRoot().getParent();
        bubble.balanceBubble();
        Node<E> root = bubble.getRoot();
        splitAndPushUp(parent, root, bubble);
    }



}
