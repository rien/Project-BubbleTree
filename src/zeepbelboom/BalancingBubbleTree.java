package zeepbelboom;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rien Maertens
 *
 * Abstracte superklasse voor zeepbelbomen die gebruikmaken van een balanceringsmethode in hun bubbels.
 */
public abstract class BalancingBubbleTree<E extends Comparable<E>> extends Zeepbelboom<E> {

    public BalancingBubbleTree(int k) {
        super(k);
    }


    /**
     * Methde die een zeepbel balanceerd door eerst alle toppen van de zeepbel in een lijst te steken.
     * Omdat de toppen in inorde in de lijst worden gestoken is deze lijst gesorteerd en kan een nieuwe
     * gebalanceerde binaire boom recursief worden opgebouwd. Deze methode werkt in lineaire tijd, waar we
     * het aantal toppen van de te balanceren zeepbel als inputgrootte beschouwen.
     *
     * @param zeepbel die moet gebalanceerd worden.
     */
    protected void balanceBubble(Zeepbel<E> zeepbel){
        List<Top<E>> nodes = new ArrayList<>();
        List<Top<E>> children = new ArrayList<>();
        zeepbel.getRoot().traverseInorder(t -> {
                    nodes.add(t);
                    if (!t.hasLeft() || t.getLeftChild().getZeepbel() != zeepbel) {
                        children.add(t.getLeftChild());
                    }
                    if (!t.hasRight() || t.getRightChild().getZeepbel() != zeepbel) {
                        children.add(t.getRightChild());
                    }
                },
                t -> t.getZeepbel() == zeepbel
        );
        Top<E> newRoot = listToTree(nodes,0,nodes.size()-1);
        assert newRoot != null;
        newRoot.removeParent();
        zeepbel.setRoot(newRoot);
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

    /**
     * Recursieve methode van een gesorteerde lijst van toppen
     * een zo goed mogelijk gebalanceerde binaire boom opsteld.
     *
     * @param list van toppen die in gesorteerde volorde zitten.
     * @param start index vanaf waar de boom moet worden opgebouwd.
     * @param end index tot waar de moet moet worden opgebouwd.
     * @return de <tt>Top</tt> van de gebalanceerde binaire boom.
     */
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
