package zeepbelboom;

import java.util.*;
import java.util.function.Consumer;

/**
 * Implementatie van Semi-splay Zeepbelbomen
 * @author Rien Maertens
 */

public class Zeepbelboom4<E extends Comparable<E>> extends Zeepbelboom<E>{

    public Zeepbelboom4(int k) {
        super(k);
    }

    protected void addToParent(Node<E> parent, E item){
        Node<E> child = new Node<>(item);
        Zeepbel<E> zb = parent.getZeepbel();
        parent.setChild(child);
        if (zb.isFull()){
            new Zeepbel<>(this,child);
        } else {
            child.setZeepbel(zb);
            if (zb.isFull()){
                zb.balanceBubble();
            }
        }
        size++;
    }

    /**
     * Deze methode roept de find-methode van zijn superklasse aan maar gaat na iedere zoekactie
     * nog eens splayen.
     *
     * @param o object die moet gevonden worden.
     * @param found als het object gevonden werd wordt het aan deze consumer meegegeven.
     * @param closest als het object niet in de zeepbelboom zit wordt de top aan deze consumer weergegeven
     *                die normaal de parent van de top zou zijn die het item bevat, moest het item zich
     *                in de zeepbelboom bevinden.
     * @param tombStone als het object in de zeepbelboom zit maar een grafsteen is wordt de top aan deze
     *
     * @return <tt>true</tt> wanneer het gezochte object in de zeepbelboom zit en niet is verwijderd.
     */
    @Override
    protected boolean find(Object o, Consumer<Node<E>> found, Consumer<Node<E>> closest, Consumer<Node<E>> tombStone) {
        return super.find(o,
                t -> {
                    found.accept(t);
                    semiSplay(t);
                },
                t -> {
                    closest.accept(t);
                    semiSplay(t);
                },
                tombStone //Geen grafstenen in deze boom
                );
    }

    /**
     * De semi-splay operatie die de zeepbel, zijn ouderzeepbel en zijn grootouderzeepbel gaat splayen.
     * @param node van waaruit de semisplay vertrekt.
     */
    private void semiSplay(Node<E> node){
        Zeepbel<E> bubble = node.getZeepbel();


        if (bubble != rootBubble) {
            if (!bubble.isFull()) {
                bubble = bubble.getParentBubble();
            }
            Zeepbel<E> parentBubble;
            Zeepbel<E> grandParentBubble;
            List<Node<E>> nodes = new ArrayList<>(bubbleMaxSize * 3);
            List<Node<E>> children = new ArrayList<>((bubbleMaxSize * 3) + 1);

            //Om vlug te kunnen bepalen of een node in een van de drie zeepbellen zit
            BubbleContainer bubbles = new BubbleContainer();

            while (
                    bubble != rootBubble &&
                    bubble.getParentBubble() != rootBubble
                    ) {
                parentBubble = bubble.getParentBubble();
                grandParentBubble = parentBubble.getParentBubble();
                Node<E> parent = grandParentBubble.getRoot().getParent();

                bubbles.add(bubble, grandParentBubble, parentBubble);

                grandParentBubble.getRoot().traverseAndAdd(
                        nodes,
                        children,
                        t -> bubbles.contains(t.getZeepbel())
                );
                assert nodes.size() == 3 * bubbleMaxSize : "Wrong amount of nodes!";
                assert children.size() == 3 * bubbleMaxSize + 1 : "Wrong amount of children!";
                TreeBuilder<E> treeLeft = new TreeBuilder<>(nodes.subList(0, bubbleMaxSize));
                TreeBuilder<E> treeMid = new TreeBuilder<>(nodes.subList(bubbleMaxSize, 2 * bubbleMaxSize));
                TreeBuilder<E> treeRight = new TreeBuilder<>(nodes.subList(2 * bubbleMaxSize, 3 * bubbleMaxSize));

                //Hergebruik de zeepbellen
                treeLeft.toBubble(bubble);
                treeMid.toBubble(parentBubble);
                treeRight.toBubble(grandParentBubble);

                //We zetten de parent van de root goed en maken die eventueel wortelzeepbel
                treeMid.getRoot().removeParent();
                if (parent == null) {
                    setRootBubble(treeMid.getRoot().getZeepbel()); //treeMid
                } else {
                    parent.setChild(treeMid.getRoot());
                }


                treeLeft.attachChildren(children.subList(0, bubbleMaxSize + 1));
                treeRight.attachChildren(children.subList(2 * bubbleMaxSize, 3 * bubbleMaxSize + 1));

                //We hebben de elementen op bubbleMaxSize en 2*bubbleMaxSize al eens toegevoegd
                //We kunnen deze dus overschrijven om gemakkelijk attachChildren() te kunnen gebruiken
                children.set(bubbleMaxSize, treeLeft.getRoot());
                children.set(2 * bubbleMaxSize, treeRight.getRoot());

                treeMid.attachChildren(children.subList(bubbleMaxSize, 2 * bubbleMaxSize + 1));

                //Zet de attributen klaar voor de volgende iteratie
                bubble = treeMid.getRoot().getZeepbel();
                nodes.clear();
                children.clear();
            }
        }
    }


    /**
     * De volgende verwijdermethodes zijn niet gedefinieerd.
     */

    @Override
    public boolean supportsDeletion() {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }


    /**
     * Private klasse die door de semisplay methode wordt gebruikt.
     */
    private class BubbleContainer{

        private Zeepbel<E> zb1;
        private Zeepbel<E> zb2;
        private Zeepbel<E> zb3;

        public void add(Zeepbel<E> zb1, Zeepbel<E> zb2, Zeepbel<E> zb3){
            this.zb1 = zb1;
            this.zb2 = zb2;
            this.zb3 = zb3;
        }

        /**
         * Bepaald in constante tijd of de gegeven zeepbel in deze container zit.
         * @param zb
         * @return <tt>true</tt> wanneer de zeepbel aanwezig is.
         */
        public boolean contains(Zeepbel<E> zb){
            return zb == zb1 || zb == zb2 || zb == zb3;
        }
    }
}
