package zeepbelboom;

import java.util.*;

/**
 * Created by Rien on 13/11/2015.
 */
public class TreeBuilder<E extends Comparable<E>> {

    private List<Node<E>> nodes;
    private Node<E> root;
    private List<Node<E>> leaves;

    public TreeBuilder(List<Node<E>> nodes){
        this.nodes = nodes;
        leaves = new ArrayList<>();
        build();
    }

    private Node<E> build(){
        root = listToTree(nodes);
        return root;
    }

    public void toBubble(Zeepbel<E> zb){
        zb.clear();
        zb.setRoot(root);
        nodes.forEach(t -> t.setZeepbel(zb));
    }

    public Node<E> getRoot(){
        assert root != null : "Tree root accessed before it was built";
        return root;
    }

    public void attachChildren(List<Node<E>> children){
        assert children.size() == nodes.size() + 1;
        int i = 0;
        for (Node<E> t : leaves) {
            if (!t.hasLeft()) t.setLeftChild(children.get(i++));
            if (!t.hasRight()) t.setRightChild(children.get(i++));
        }
    }

    /**
     * Recursieve methode van een gesorteerde lijst van toppen
     * een zo goed mogelijk gebalanceerde binaire boom opsteld.
     *
     * @param list van toppen die in gesorteerde volorde zitten.
     * @return de <tt>Node</tt> die de wortel is van de gebalanceerde binaire boom.
     */
    private Node<E> listToTree(List<Node<E>> list){
        Node<E> root;
        if (list.isEmpty()){
            return null;
        } else if (list.size() == 1){
            root = list.get(0);
            root.setLeftChild(null);
            root.setRightChild(null);
        } else {
            int mid = (list.size()/2);
            root = list.get(mid);
            root.setLeftChild(listToTree(list.subList(0,mid)));
            root.setRightChild(listToTree(list.subList(mid+1,list.size())));
        }
        if (root.hasNull()){
            leaves.add(root);
        }
        return root;
    }

    /**
     * Iteratieve implementatie van de <tt>listToTree</tt> methode. Helaas blijkt uit testen dat
     * deze trager is dan de recursieve implementatie.
     * @param list van toppen die in gesorteerde volorde zitten.
     * @return de <tt>Node</tt> die de wortel is van de gebalanceerde binaire boom.
     */
    private Node<E> listToTreeIterative(List<Node<E>> list){
        Deque<Item> stack = new ArrayDeque<>();
        int mid = list.size()/2;
        Node<E> root = list.get(mid);
        stack.push(new Item(root,list.subList(0,mid),true));
        stack.push(new Item(root,list.subList(mid+1,list.size()),false));
        Item item;
        while (!stack.isEmpty()){
            item = stack.pop();
            if (item.list.isEmpty()){
                item.connectChild(null);
            } else if (item.list.size() == 1){
                item.list.get(0).clearChildren();
                item.connectChild(item.list.get(0));
            } else {
                mid = item.list.size()/2;
                item.connectChild(item.list.get(mid));
                stack.push(new Item(item.list.get(mid),item.list.subList(0,mid),true));
                //Hergebruik van item
                item.parent = item.list.get(mid);
                item.list = item.list.subList(mid+1, item.list.size());
                item.isLeft = false;
                stack.push(item);
            }
        }

        return root;
    }


    /**
     * Containter die wordt gebruikt door de methode <tt>listToTreeIterative</tt>.
     */
    private class Item{

        private List<Node<E>> list;
        private Node<E> parent;
        private boolean isLeft;


        Item(Node<E> parent, List<Node<E>> list, boolean isLeft){
            this.list = list;
            this.parent = parent;
            this.isLeft = isLeft;
        }

        private void connectChild(Node<E> child){
            if (isLeft){
                parent.setLeftChild(child);
            } else {
                parent.setRightChild(child);
            }
        }
    }


}
