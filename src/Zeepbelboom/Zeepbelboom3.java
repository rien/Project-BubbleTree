package Zeepbelboom;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 *
 */
public class Zeepbelboom3<E extends Comparable<E>> extends BalancingBubbleTree<E> {

    public Zeepbelboom3(int k) {
        super(k);
    }

    @Override
    protected void shrinkBubble(Zeepbel<E> bubble) {
        Top<E> parent = bubble.getRoot().getParent();
        balanceBubble(bubble);
        Top<E> root = bubble.getRoot();

        if (parent == null){
            //We zitten aan de wortel, dus we moeten een nieuwe aanmaken.
            split(root, root.getZeepbel());
            createNewRootBubble(root);
        } else {
            //Duw net genoeg toppen omhoog tot de parent vol zit.
            int amountToPush = getBubbleMaxSize() - parent.getZeepbel().size();
            if (amountToPush <= 1){
                splitAndPushUp(parent, root, root.getZeepbel());
            } else {
                List<Top<E>> topsToPush = new ArrayList<>(amountToPush);
                Queue<Top<E>> children = new ArrayDeque<>();

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



}
