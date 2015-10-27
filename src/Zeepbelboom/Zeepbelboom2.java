package Zeepbelboom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rien on 10/15/15.
 */
public class Zeepbelboom2<E extends Comparable<E>> extends Zeepbelboom<E> {

    public Zeepbelboom2(int k) {
        super(k);
    }

    @Override
    protected void splitBubble(Zeepbel<E> bubble) {
        Top<E> parent = bubble.getRoot().getParent();
        balanceBubble(bubble);
        Top<E> root = bubble.getRoot();
        Top<E> leftRoot = root.getLeftChild();
        Top<E> rightRoot = root.getRightChild();
        bubble.setRoot(rightRoot);

        fixBubble(leftRoot, bubble);
        pushRootUp(parent, root);
    }


    private void balanceBubble(Zeepbel<E> zb){
        List<Top<E>> nodes = new ArrayList<>();
        List<Top<E>> children = new ArrayList<>();
        getRoot().traverseInorder(t -> {
                    nodes.add(t);
                    if (!t.hasLeft() || t.getLeftChild().getZeepbel() != zb){
                        children.add(t.getLeftChild());
                    }
                    if(!t.hasRight() || t.getRightChild().getZeepbel() != zb){
                        children.add(t.getRightChild());
                    }
                },
                t -> t.getZeepbel() == zb);

        Top<E> newRoot = listToTree(nodes,0,nodes.size()-1);
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
        if (start == end){
            Top<E> leaf = list.get(start);
            leaf.setRightChild(null);
            leaf.setLeftChild(null);
            return leaf;
        } else {
            int mid = start + ((end - start)/2);
            Top<E> root = list.get(mid);
            root.setLeftChild(listToTree(list, start, mid - 1));
            root.setRightChild(listToTree(list, mid + 1, end));
            return root;
        }
    }


}
