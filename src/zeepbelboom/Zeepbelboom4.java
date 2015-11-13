package zeepbelboom;

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
    protected boolean find(Object o, Consumer<Top<E>> found, Consumer<Top<E>> closest, Consumer<Top<E>> tombStone) {
        return super.find(
                o,
                t->{found.accept(t);semiSplay(t);},
                t->{closest.accept(t);semiSplay(t);},
                tombStone
        );
    }

    private void semiSplay(Top<E> top){
        while (getRoot() != top){

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
