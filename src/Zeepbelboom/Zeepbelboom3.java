package Zeepbelboom;

/**
 * Je neemt niet de middelste sleutel van een zeepbel, maar de 3 middelste.
 * Die bestaan altijd want een overvolle zeepbel bevat altijd minstens 3 toppen.
 * Die 3 middelste toppen herbalanceer je tot een binaire boom.
 * Dus de 2e middelste in het midden en de 1e en 3e middelste respectievelijk links en rechts van de 2e middelste.
 * Daarna herbalanceer je de rest van de toppen als binaire bomen die respectievelijk aan de 1e middelste en aan de 2e middelste hangen.
 * Dan maak je van die rest van die toppen nieuwe zeepbellen en duw je de 3 middelste toppen naar boven
 */
public class Zeepbelboom3<E extends Comparable<E>> extends Zeepbelboom<E> {

    public Zeepbelboom3(int k) {
        super(k);
    }

    @Override
    protected void shrinkBubble(Zeepbel<E> bubble) {

    }

}
