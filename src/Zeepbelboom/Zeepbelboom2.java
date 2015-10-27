package Zeepbelboom;

/**
 * Created by rien on 10/15/15.
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
        splitBubble(parent, root, bubble);
    }




}
