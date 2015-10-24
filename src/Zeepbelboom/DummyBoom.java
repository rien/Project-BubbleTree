package Zeepbelboom;

/**
 * Created by rien on 10/22/15.
 */
public class DummyBoom<E extends Comparable<E>> extends Zeepbelboom<E> {

    public DummyBoom(int k) {
        super(k);
    }
    }

    @Override
    public void balanceer() {

    }

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns <tt>true</tt> if this collection changed as a
     * result of the call.  (Returns <tt>false</tt> if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     * <p>
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add <tt>null</tt> elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     * <p>
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning <tt>false</tt>).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     *
     * @param o element whose presence in this collection is to be ensured
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *                                       is not supported by this collection
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this collection
     * @throws NullPointerException          if the specified element is null and this
     *                                       collection does not permit null elements
     * @throws IllegalArgumentException      if some property of the element
     *                                       prevents it from being added to this collection
     * @throws IllegalStateException         if the element cannot be added at this
     *                                       time due to insertion restrictions
     */
    @Override
    public boolean add(E o) {
        if (rootBubble == null){
            this.rootBubble = new Zeepbel<E>(Integer.MAX_VALUE, this, new Top<E>(o));
            size++;
            return true;
        } else {
            if(rootBubble.getRoot().add(o)){
                size++;
                return true;
            } else {
                return false;
            }
        }
    }


    public boolean remove(E o) {

        Top<E> top = rootBubble.getRoot().find(o);
        if (top == null){
            return false;
        } else {

        }
        return true;
    }
}
