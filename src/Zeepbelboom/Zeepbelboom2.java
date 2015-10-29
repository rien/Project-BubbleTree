package Zeepbelboom;

/**
 * @author Rien Maertens
 *
 * Zeepbelboom die overvolle zeepbellen gaat verkleinen door ze eerst zo goed mogelijk te balanceren
 * om vervolgens de zeepbel in twee te splitsen en de wortel omhoog te duwen.
 */
public class Zeepbelboom2<E extends Comparable<E>> extends BalancingBubbleTree<E> {

    public Zeepbelboom2(int k) {
        super(k);
    }


    @Override
    protected void shrinkBubble(Zeepbel<E> bubble) {
        Top<E> parent = bubble.getRoot().getParent();
        balanceBubble(bubble);
        Top<E> root = bubble.getRoot();
        splitAndPushUp(parent, root, bubble);
    }




}
