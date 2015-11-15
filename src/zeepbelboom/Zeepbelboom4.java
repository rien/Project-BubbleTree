package zeepbelboom;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Rien Maertens
 *
 * Implementatie van Semi-splay Zeepbelbomen
 */

public class Zeepbelboom4<E extends Comparable<E>> extends Zeepbelboom<E>{

    public Zeepbelboom4(int k) {
        super(k);
    }

    protected void addToParent(Top<E> parent, E item){
        Top<E> child = new Top<>(item);
        Zeepbel<E> zb = parent.getZeepbel();
        parent.setChild(child);
        size++;
        if (zb.isFull()){
            new Zeepbel<>(this,child);
        } else {
            child.setZeepbel(zb);
            if (zb.isFull()){
                zb.balanceBubble();
            }
        }
    }

    @Override
    public boolean contains(Object o) {
        return find(o,this::semiSplay,t->{/* Deze zeepbel is incompleet */},this::semiSplay);
    }

    @Override
    protected boolean find(Object o, Consumer<Top<E>> found, Consumer<Top<E>> closest, Consumer<Top<E>> tombStone) {
        return super.find(
                o,
                t->{found.accept(t);semiSplay(t);},
                t->{closest.accept(t);semiSplay(t);},
                tombStone
        );
    }

    private void semiSplay(Top<E> top){
        Zeepbel<E> bubble = top.getZeepbel();
        Zeepbel<E> parentBubble;
        Zeepbel<E> grandParentBubble;
        List<Top<E>> nodes = new ArrayList<>(bubbleMaxSize*3);
        List<Top<E>> children = new ArrayList<>((bubbleMaxSize*3)+1);

        //Om vlug te kunnen bepalen of een top in een van de drie zeepbellen zit
        Set<Zeepbel<E>> zb = new HashSet<>();

        while (
                bubble.isFull() &&
                bubble != rootBubble &&
                bubble.getParentBubble() != rootBubble
                ){
            parentBubble = bubble.getParentBubble();
            grandParentBubble = parentBubble.getParentBubble();
            Top<E> parent = grandParentBubble.getRoot().getParent();

            zb.add(bubble);
            zb.add(parentBubble);
            zb.add(grandParentBubble);

            grandParentBubble.getRoot().traverseAndAdd(
                    nodes,
                    children,
                    t -> zb.contains(t.getZeepbel())
            );
            assert nodes.size() == 3*bubbleMaxSize : "Wrong amount of nodes!";
            assert children.size() == 3*bubbleMaxSize + 1 : "Wrong amount of children!";
            TreeBuilder<E> treeLeft = new TreeBuilder<>(nodes.subList(0,bubbleMaxSize));
            TreeBuilder<E> treeMid = new TreeBuilder<>(nodes.subList(bubbleMaxSize,2*bubbleMaxSize));
            TreeBuilder<E> treeRight = new TreeBuilder<>(nodes.subList(2*bubbleMaxSize,3*bubbleMaxSize));

            //Hergebruik de zeepbellen
            bubble.clear();
            parentBubble.clear();
            grandParentBubble.clear();
            treeLeft.toBubble(bubble);
            treeMid.toBubble(parentBubble);
            treeRight.toBubble(grandParentBubble);

            //We zetten de parent van de root goed en maken die eventueel wortelzeepbel
            treeMid.getRoot().removeParent();
            if (parent == null){
                setRootBubble(parentBubble); //treeMid
            } else {
                parent.setChild(treeMid.getRoot());
            }


            treeLeft.attachChildren(children.subList(0,bubbleMaxSize+1));
            treeRight.attachChildren(children.subList(2*bubbleMaxSize,3*bubbleMaxSize+1));

            //We hebben de elementen op bubbleMaxSize en 2*bubbleMaxSize al eens toegevoegd
            //We kunnen deze dus overschrijven om gemakkelijk attachChildren() te kunnen gebruiken
            children.set(bubbleMaxSize,treeLeft.getRoot());
            children.set(2*bubbleMaxSize,treeRight.getRoot());

            treeLeft.attachChildren(children.subList(bubbleMaxSize,2*bubbleMaxSize+1));

            //Zet de attributen klaar voor de volgende iteratie
            bubble = treeMid.getRoot().getZeepbel();
            nodes.clear();
            children.clear();
            zb.clear();
        }

    }

    /**
     * De volgende verwijdermethodes zijn niet gedefinieerd.
     */

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }
}
