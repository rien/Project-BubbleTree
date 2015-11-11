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
     * Splitst een zeepbel in twee en voeg de oude root toe aan de bovenliggende zeepbel.
     *
     * @param parent ouder waaraan de huidige root van de zeepbel zal worden toegevoegd aan zijn zeepbel.
     * @param root top die als kinderen de wortels bevat van de nieuwe zeepbellen, die na de operatie wordt toegevoegd
     *             aan de bovenliggende zeepbel.
     * @param bubble die in twee gesplitst moet worden.
     */
    void splitAndPushUp(Top<E> parent, Top<E> root, Zeepbel<E> bubble){
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
    void split(Top<E> root, Zeepbel<E> bubble){
        Top<E> rightRoot = root.getRightChild();
        Top<E> leftRoot = root.getLeftChild();
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
     * @param parent ouder van de top.
     * @param top die toegevoegd moet worden aan de bovenliggende zeepbel.
     */
    private void pushRootUp(Top<E> parent, Top<E> top){
        //Laat nu de gekozen top 'opborrelen';
        if(parent == null){
            //We zitten bij de root en moeten een nieuwe bubbel aanmaken
            createNewRootBubble(top);
        } else {
            parent.setChild(top);
            Zeepbel<E> parentBubble = parent.getZeepbel();
            if (top.setZeepbel(parentBubble)) {
                shrinkBubble(parentBubble);
            }
        }
    }

    /**
     * Maak een nieuwe zeepbel aan in de wortel.
     *
     * @param top die de wortel van de nieuwe wortelzeepbel moet worden.
     */
    void createNewRootBubble(Top<E> top){
        top.removeParent();
        Zeepbel<E> rootBubble = new Zeepbel<E>(this, top);
        setRootBubble(rootBubble);
    }


    /**
     * Voegt meerdere toppen toe aan de zeepbel van parent en splitst deze zeepbel indien nodig.
     *
     * @param parent waaraan de eerste van de toppen het kind van is.
     * @param tops lijst van toppen die moeten toegevoegd worden aan de zeepbel van de ouder.
     */
    void pushMultipleUp(Top<E> parent, List<Top<E>> tops){
        assert parent != null : "De parent mag niet null zijn! Je zit waarschijnlijk in de root.";
        Zeepbel<E> parentBubble = parent.getZeepbel();
        boolean balanceAfter;
        int after = tops.size() + parentBubble.size();
        if (after <= bubbleMaxSize){
            balanceAfter = false;
        } else if (after == bubbleMaxSize + 1){
            balanceAfter = true;
        } else {
            throw new IllegalArgumentException("Teveel toppen om toe te voegen!");
        }

        // De eerste top moet eerst toegevoegd worden als kind aan de parent.
        parent.setChild(tops.get(0));

        //Voeg iedere top toe aan de parentBubble;
        tops.forEach(t -> t.setZeepbel(parentBubble));
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


    public void removeTop(Top<E> top){
        top.remove();
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
        Queue<Top<E>> q = new ArrayDeque<>();
        q.add(getRoot());
        Top<E> t;
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
//    private void removeTop(Top<E> toRemove){
//        if (toRemove.hasRight() && toRemove.hasLeft()){
//            //We zitten met een interne top, dus wisselen deze van plaats met de kleinste top uit de rechterdeelboom.
//            Top<E> closest = toRemove.findClosestChild();
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
//            Zeepbel<E> siblingZeepbel = toRemove.getZeepbel().getSiblingZeepbel();
//            Top<E> parent = toRemove.getParent();
//            toRemove.swapItems(parent);
//            toRemove = parent;
//            parent = toRemove.getParent();
//
//            if (siblingZeepbel.size() > 1){
//                //We kunnen een top van de tweelingzeepbel gebruiken
//                Top<E> top  = siblingZeepbel.getRoot().findClosestChild();
//                toRemove.swapItems(top);
//                toRemove = top;
//                //toRemove zit nu in een zeepbel met  > 1 items en kan dus verwijder worden.
//                removeLeaf(toRemove);
//            } else if (toRemove.getZeepbel().size() > 1){
//                //We kunnen twee zeepbellen met 1 item mergen
//                Top<E> left = toRemove.getLeftChild();
//                toRemove.getRightChild().setRightChild(left);
//                toRemove.getParent().setChild(mergeZeepbelRight(toRemove));
//            } else {
//                Top<E> top;
//                while (toRemove.getParent() != null && toRemove.getZeepbel() != toRemove.getParent().getZeepbel()){
//                    siblingZeepbel = toRemove.getZeepbel().getSiblingZeepbel();
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
//    private Top<E> mergeZeepbelRight(Top<E> top){
//        Top<E> left = top.getLeftChild();
//        top.getRightChild().setLeftChild(left);
//        left.setZeepbel(top.getLeftChild().getZeepbel());
//        return top.getRightChild();
//    }
//
//    private Top<E> mergeZeepbelLeft(Top<E> top){
//        Top<E> right = top.getRightChild();
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
//    private void removeLeaf(Top<E> toRemove){
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
