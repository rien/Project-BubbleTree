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
    private int start, end;
    private Top<E> root;
    private List<Top<E>> leaves;

    private TreeBuilder(List<Top<E>> nodes, int start, int end){
        this.nodes = nodes;
        this.start = start;
        this.end = end;
        build();
    }

    public static <E extends Comparable<E>> TreeBuilder<E> fromList(List<Top<E>> nodes, int start, int end){
        return new TreeBuilder<>(nodes, start, end);
    }

    public static <E extends Comparable<E>> TreeBuilder<E> fromList(List<Top<E>> nodes){
        return new TreeBuilder<>(nodes, 0, nodes.size()-1);
    }

    public static <E extends Comparable<E>> TreeBuilder<E> fromBubble(Zeepbel<E> zb){
        List<Top<E>> nodes = new ArrayList<>();
        zb.getRoot().traverseInorder(nodes::add, t -> t.getZeepbel() == zb);
        return fromList(nodes);
    }

    private Top<E> build(){
        root = listToTree(nodes, start, end);
        leaves = new ArrayList<>();
        root.traverseInorder(
                t->{if(!t.hasLeft() || !t.hasRight()){
                        leaves.add(t);
                }},
                t -> true);
        return root;
    }

    public Top<E> getRoot(){
        assert root != null : "Tree root accessed before it was built";
        return root;
    }

    public Iterable<Top<E>> leaves(){
        return leaves;
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
