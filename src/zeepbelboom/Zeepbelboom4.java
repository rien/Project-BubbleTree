package zeepbelboom;

import java.util.Collection;

/**
 * @author Rien Maertens
 *
 * Implementatie van Semi-splay Zeepbelbomen
 */

public class Zeepbelboom4<E extends Comparable<E>> extends Zeepbelboom<E>{

    Zeepbelboom4(int k) {
        super(k);
    }

    /**
     * Methode die moet worden opgeropen wanneer een zeepbel zijn maximale overschrijd en moet verkleind worden.
     *
     * @param bubble die moet verkleind worden.
     */
    @Override
    protected void shrinkBubble(Zeepbel<E> bubble) {

    }

    @Override
    public String toString() {
        return null;
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
