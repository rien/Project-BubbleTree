package Zeepbelboom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rien on 10/27/15.
 */
public abstract class BalancingBubbleTree<E extends Comparable<E>> extends Zeepbelboom<E> {

    public BalancingBubbleTree(int k) {
        super(k);
    }


    protected void balanceBubble(Zeepbel<E> zb){
        List<Top<E>> nodes = new ArrayList<>();
        List<Top<E>> children = new ArrayList<>();
        zb.getRoot().traverseInorder(t -> {
                    nodes.add(t);
                    if (!t.hasLeft() || t.getLeftChild().getZeepbel() != zb) {
                        children.add(t.getLeftChild());
                    }
                    if (!t.hasRight() || t.getRightChild().getZeepbel() != zb) {
                        children.add(t.getRightChild());
                    }
                },
                t -> t.getZeepbel() == zb
        );
        //TODO: Midden op de juiste manier?
        Top<E> newRoot = listToTree(nodes,0,nodes.size()-1);
        assert newRoot != null;
        newRoot.removeParent();
        zb.setRoot(newRoot);
        int nextChild = 0;
        for (Top<E> t : nodes) {
            if (!t.hasLeft()){
                t.setLeftChild(children.get(nextChild++));
            }
            if (!t.hasRight()){
                t.setRightChild(children.get(nextChild++));
            }
        }
    }

    private Top<E> listToTree(List<Top<E>> list, int start, int end){
        if (end < start){
            return null;
        } else {
            int mid = start + ((end - start)/2);
            Top<E> root = list.get(mid);
            root.setLeftChild(listToTree(list, start, mid - 1));
            root.setRightChild(listToTree(list, mid + 1, end));
            return root;
        }
    }
}
