package zeepbelboom;

import java.util.*;

/**
 * @author Rien Maertens
 *
 * TODO Description
 */
public abstract class ShrinkingBubbleTree<E extends Comparable<E>> extends Zeepbelboom<E> {

    ShrinkingBubbleTree(int k) {
        super(k);
    }

    /**
     * Voeg een nieuwe item toe aan een top die al in de zeepbelboom zit.
     * @param item toe te voegen item.
     * @param parent ouder waaraan het kind moet toegevoegd worden.
     */
    protected void addToParent(Node<E> parent, E item){
        Zeepbel<E> zb = parent.getZeepbel();
        Node<E> child = new Node<>(item);
        parent.setChild(child);
        child.setZeepbel(zb);
        if(zb.hasToBurst()){
            //De zeepbel zit overvol en moet verkleind worden.
            shrinkBubble(zb);
        }
        size++;
    }

    protected abstract void shrinkBubble(Zeepbel<E> zb);

    /**
     * Splitst een zeepbel in twee en voeg de oude root toe aan de bovenliggende zeepbel.
     *
     * @param parent ouder waaraan de huidige root van de zeepbel zal worden toegevoegd aan zijn zeepbel.
     * @param root top die als kinderen de wortels bevat van de nieuwe zeepbellen, die na de operatie wordt toegevoegd
     *             aan de bovenliggende zeepbel.
     * @param bubble die in twee gesplitst moet worden.
     */
    void splitAndPushUp(Node<E> parent, Node<E> root, Zeepbel<E> bubble){
        // Deel de zeepbel in twee
        split(root, bubble);
        // Duw de huidige root omhoog
        pushRootUp(parent, root);
    }

    /**
     * Splits een zeepbel in twee.
     *
     * Enkel de toppen die del uitmaken van de opgegeven zeepbel worden overlopen en
     * aangezien een zeepbel een maximaal aantal toppen heeft (k) werkt deze methode in constante tijd.
     *
     * @param root top die als kinderen de wortels bevat van de nieuwe zeepbellen.
     * @param bubble die in twee gesplitst moet worden.
     */
    void split(Node<E> root, Zeepbel<E> bubble){
        Node<E> rightRoot = root.getRightChild();
        Node<E> leftRoot = root.getLeftChild();
        // Zet de rechterwortel als wortel van de rechterzeepbel
        bubble.setRoot(rightRoot);
        // Vorm een nieuwe zeepbel
        Zeepbel<E> newBubble = new Zeepbel<E>(this);
        // Alle kinderen van de nieuwe zeepbelwortel die nog in de oude zeepbel zitten worden lid van de nieuwe zeepbel.
        leftRoot.traverseInorder(t -> t.setZeepbel(newBubble), t -> t.getZeepbel() == bubble);
        // Maak de linkerwortel de wortel van de linkerzeepbel
        newBubble.setRoot(leftRoot);
        // De huidige zeepbel bevat de toppen uit de nieuwe zeepbel niet meer en ook niet de opgeborrelde top.
        bubble.topsRemoved(newBubble.size() + 1);
    }

    /**
     * Voeg de oude wortel van een zojuist gesplitste zeepbel toe aan de ouderzeepbel.
     * Indien nodig zal de ouderzeepbel ook splitsen.
     *
     * @param parent ouder van de node.
     * @param node die toegevoegd moet worden aan de bovenliggende zeepbel.
     */
    private void pushRootUp(Node<E> parent, Node<E> node){
        //Laat nu de gekozen node 'opborrelen';
        if(parent == null){
            //We zitten bij de root en moeten een nieuwe bubbel aanmaken
            createNewRootBubble(node);
        } else {
            parent.setChild(node);
            Zeepbel<E> parentBubble = parent.getZeepbel();
            node.setZeepbel(parentBubble);
            if (parentBubble.hasToBurst()) {
                shrinkBubble(parentBubble);
            }
        }
    }

    /**
     * Maak een nieuwe zeepbel aan in de wortel.
     *
     * @param node die de wortel van de nieuwe wortelzeepbel moet worden.
     */
    void createNewRootBubble(Node<E> node){
        node.removeParent();
        Zeepbel<E> rootBubble = new Zeepbel<E>(this, node);
        setRootBubble(rootBubble);
    }


    /**
     * Voegt meerdere toppen toe aan de zeepbel van parent en splitst deze zeepbel indien nodig.
     *
     * @param parent waaraan de eerste van de toppen het kind van is.
     * @param nodes lijst van toppen die moeten toegevoegd worden aan de zeepbel van de ouder.
     */
    void pushMultipleUp(Node<E> parent, List<Node<E>> nodes){
        assert parent != null : "De parent mag niet null zijn! Je zit waarschijnlijk in de root.";
        Zeepbel<E> parentBubble = parent.getZeepbel();
        boolean balanceAfter;
        int after = nodes.size() + parentBubble.size();
        if (after <= bubbleMaxSize){
            balanceAfter = false;
        } else if (after == bubbleMaxSize + 1){
            balanceAfter = true;
        } else {
            throw new IllegalArgumentException("Teveel toppen om toe te voegen!");
        }

        // De eerste top moet eerst toegevoegd worden als kind aan de parent.
        parent.setChild(nodes.get(0));

        //Voeg iedere top toe aan de parentBubble;
        nodes.forEach(t -> t.setZeepbel(parentBubble));
        //Verklein de zeepbel indien nodig
        if (balanceAfter){
            shrinkBubble(parentBubble);
        }
    }


    /**
     * Probeer een object uit de zeepbelboom te verwijderen door een grafsteen te plaatsen.
     *
     * Het verwijderen van dit object gebeurt in logaritmische tijd want er moet enkel gezocht worden naar dit
     * object en een grafsteen geplaatst worden.
     *
     * Wanneer de zeepbelboom uit een bepaald percentage grafstenen bestaat wordt de boom echter in lineaire
     * tijd opnieuw opgebouwd.
     *
     * @param o het te verwijderen object.
     * @return <tt>true</tt> als het element zich in de zeepbelboom bevond en verwijderd werd.
     * @throws ClassCastException            als het type van het te verwijderen object
     *                                       incompatibel is met de huidige zeepbelboom.
     * @throws NullPointerException          als het te verwijderen object <tt>null</tt> is.
     */
    @Override
    public boolean remove(Object o) {
        return find(o, this::removeTop,t->{},t->{});
    }


    public void removeTop(Node<E> node){
        node.remove();
        size--;
        if (size == 0){
            clear();
        } else if ((tombStones*100)/size >= maxTombstoneRatio){
            rebuildTree();
        }
    }


    private void rebuildTree(){
        //Rebuild tree
        List<E> items = new ArrayList<>();
        Queue<Node<E>> q = new ArrayDeque<>();
        q.add(getRoot());
        Node<E> t;
        //Stop alle items in BFS-volgorde in de lijst.
        while (!q.isEmpty()){
            t = q.remove();
            if (!t.isRemoved()){
                items.add(t.getItem());
            }
            if (t.hasLeft()){
                q.add(t.getLeftChild());
            }
            if (t.hasRight()){
                q.add(t.getRightChild());
            }
        }
        assert items.size() == size;
        clear();
        addAll(items);
    }

    /**
     * Verwijder alle elementen uit de zeepbelboom die in de gegeven collectie zitten.
     * Gaat enkel controleren of de boom opnieuw moet opbouwt worden na de laatste verwijderactie.
     *
     * @param c collectie met de te verwijderen elementen.
     * @return <tt>true</tt> als de huidige zeepbelboom veranderd is door deze operatie.
     * @throws ClassCastException   als de type van één of meer elementen uit de collectie niet
     *                              compatibel is met deze zeepbelboom.
     * @throws NullPointerException als een element uit de collectie null is.
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean deleted;
        boolean changed = false;
        for(Object o: c){
            deleted = find(o,t->{
                t.remove();
                size--;
                if (size == 0){
                    clear();
                }
            }, t->{}, t->{});
            if (deleted){
                changed = true;
            }
        }
        if ((tombStones*100)/size >= maxTombstoneRatio){
            rebuildTree();
        }
        return changed;
    }


    /**
     * Implementatie van remove() die niet werkt. Deze implementatie werkt met het effectief verwijderen van een top
     * in plaats van gebruik te maken van grafstenen. Helaas had ik geen tijd meer om deze methode te debuggen
     * en heb ik een remove() geïmplementeerd die met grafstenen werkt.
     */


//     /**
//     * Probeer een object uit de zeepbelboom te verwijderen.
//     *
//     * @param o het te verwijderen object.
//     * @return <tt>true</tt> als het element zich in de zeepbelboom bevond en verwijderd werd.
//     * @throws ClassCastException            als het type van het te verwijderen object
//     *                                       incompatibel is met de huidige zeepbelboom.
//     * @throws NullPointerException          als het te verwijderen object <tt>null</tt> is.
//     */
//    @Override
//    public boolean remove(Object o) {
//        return find(o, this::removeTop, t->{});
//    }
//
//    /**
//     * Verwijder een top uit de zeepbelboom en balanceert indien nodig.
//     *
//     * @param toRemove uit de zeepbelboom die moet verwijderd worden.
//     */
//    private void removeTop(Node<E> toRemove){
//        if (toRemove.hasRight() && toRemove.hasLeft()){
//            //We zitten met een interne top, dus wisselen deze van plaats met de kleinste top uit de rechterdeelboom.
//            Node<E> closest = toRemove.findClosestChild();
//            toRemove.swapItems(closest);
//            toRemove = closest;
//        }
//        // De top is een blad.
//
//        Zeepbel<E> zb = toRemove.getZeepbel();
//        if (zb.size() > 1){
//            //De top zit niet alleen in de zeepbel, er moet dus niets op zeepbelniveau veranderd worden.
//            removeLeaf(toRemove);
//
//        } else {
//            //Speciaal geval: door het verwijderen van de top komen we een lege zeepbel uit
//            Zeepbel<E> siblingZeepbel = toRemove.getZeepbel().getSiblingbubble();
//            Node<E> parent = toRemove.getParent();
//            toRemove.swapItems(parent);
//            toRemove = parent;
//            parent = toRemove.getParent();
//
//            if (siblingZeepbel.size() > 1){
//                //We kunnen een top van de tweelingzeepbel gebruiken
//                Node<E> top  = siblingZeepbel.getRoot().findClosestChild();
//                toRemove.swapItems(top);
//                toRemove = top;
//                //toRemove zit nu in een zeepbel met  > 1 items en kan dus verwijder worden.
//                removeLeaf(toRemove);
//            } else if (toRemove.getZeepbel().size() > 1){
//                //We kunnen twee zeepbellen met 1 item mergen
//                Node<E> left = toRemove.getLeftChild();
//                toRemove.getRightChild().setRightChild(left);
//                toRemove.getParent().setChild(mergeZeepbelRight(toRemove));
//            } else {
//                Node<E> top;
//                while (toRemove.getParent() != null && toRemove.getZeepbel() != toRemove.getParent().getZeepbel()){
//                    siblingZeepbel = toRemove.getZeepbel().getSiblingbubble();
//                    toRemove.swapItems(parent);
//                    top = toRemove;
//                    toRemove = parent;
//                    boolean right = top.compareTo(parent.getItem()) < 0;
//                    if (right){
//                        top.setLeftChild(mergeZeepbelLeft(top));
//                        top.setRightChild(siblingZeepbel.getRoot());
//                    } else {
//                        top.setRightChild(mergeZeepbelRight(top));
//                        top.setLeftChild(siblingZeepbel.getRoot());
//                    }
//                    siblingZeepbel.moveAllChildrenTo(top.getZeepbel());
//                }
//                if (toRemove.getParent() == null){
//                    setRootBubble(mergeZeepbelLeft(toRemove).getZeepbel());
//                }
//            }
//
//        }
//        size--;
//    }
//
//
//    private Node<E> mergeZeepbelRight(Node<E> top){
//        Node<E> left = top.getLeftChild();
//        top.getRightChild().setLeftChild(left);
//        left.setZeepbel(top.getLeftChild().getZeepbel());
//        return top.getRightChild();
//    }
//
//    private Node<E> mergeZeepbelLeft(Node<E> top){
//        Node<E> right = top.getRightChild();
//        top.getLeftChild().setRightChild(right);
//        right.setZeepbel(top.getRightChild().getZeepbel());
//        return top.getLeftChild();
//    }
//
//
//     /**
//     *  Verwijder een blad uit de zeepbelboom.
//     *
//     * @param toRemove blad van de zeepbelboom
//     */
//    private void removeLeaf(Node<E> toRemove){
//        Zeepbel<E> zb = toRemove.getZeepbel();
//        if (!toRemove.hasLeft() && !toRemove.hasRight()){
//            //De top heeft zelf geen kinderen (en is dus ook geen root), dus kunnen we de gewoon verwijderen.
//            toRemove.removeFromParent();
//        } else if (toRemove.hasLeft()){
//            //De top heeft enkel een linkerkind
//            toRemove.getParent().setChild(toRemove.getLeftChild());
//            zb.topsRemoved(1);
//        } else {
//            //De top heeft enkel een richterkind
//            toRemove.getParent().setChild(toRemove.getRightChild());
//            zb.topsRemoved(1);
//        }
//    }

}
