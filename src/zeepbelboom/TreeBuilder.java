package zeepbelboom;

import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Rien on 13/11/2015.
 */
public class TreeBuilder<E extends Comparable<E>> {

    private List<Top<E>> nodes;
    private Top<E> root;
    private List<Top<E>> leaves;

    public TreeBuilder(List<Top<E>> nodes){
        this.nodes = nodes;
        build();
    }

    private Top<E> build(){
        root = listToTree(nodes, 0, nodes.size() - 1);
        leaves = new ArrayList<>();
        root.traverseInorder(
                t->{if(!t.hasLeft() || !t.hasRight()){
                        leaves.add(t);
                }},
                t -> true);
        return root;
    }

    public void toBubble(Zeepbel<E> zb){
        zb.clear();
        zb.setRoot(root);
        nodes.forEach(t->t.setZeepbel(zb));
    }

    public Top<E> getRoot(){
        assert root != null : "Tree root accessed before it was built";
        return root;
    }

    public Iterable<Top<E>> leaves(){
        return leaves;
    }

    public void attachChildren(List<Top<E>> children){
        assert children.size() == nodes.size() + 1;
        int i = 0;
        for (Top<E> t : leaves) {
            if (!t.hasLeft()) t.setLeftChild(children.get(i++));
            if (!t.hasRight()) t.setRightChild(children.get(i++));
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
