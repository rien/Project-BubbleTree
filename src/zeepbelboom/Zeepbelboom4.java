package zeepbelboom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Rien Maertens
 *
 * Implementatie van Semi-splay Zeepbelbomen
 */

public class Zeepbelboom4<E extends Comparable<E>> extends Zeepbelboom<E>{

    Zeepbelboom4(int k) {
        super(k);
    }

    protected void addToParent(Top<E> parent, E item){
        Top<E> child = new Top<>(item);
        Zeepbel<E> zb = parent.getZeepbel();
        parent.setChild(child);

        if (zb.isFull()){
            child.setZeepbel(new Zeepbel<>(this,child));
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
        Zeepbel<E> zb1 = top.getZeepbel();
        ArrayList<Top<E>> nodes = new ArrayList<>(bubbleMaxSize*3);
        ArrayList<Top<E>> children = new ArrayList<>((bubbleMaxSize*3)+1);
        while (zb1 == getRoot().getZeepbel() || zb1.getParentBubble() == getRoot().getZeepbel()){
            Zeepbel<E> zb2 = zb1.getParentBubble();
            Zeepbel<E> zb3 = zb2.getParentBubble();
            zb3.getRoot().traverseAndAdd(
                    nodes,
                    children,
                    t -> t.getZeepbel() == zb1 || t.getZeepbel() == zb2 || t.getZeepbel() == zb3
            );

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
