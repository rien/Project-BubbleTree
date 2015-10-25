package Zeepbelboom;

/**
 * Die probeert de onderliggende boom zo intact mogelijk te houden bij het toevoegen.
 *
 * Algoritme voor toeveogen:
 * - Voeg item toe aan de boom zoals normaal en voeg de top toe aan de zeepbel van de parent
 * - Als de zeepbel te vol zit:
 *      - Als de root maar één kind (in dezelfde zeepbel) bevat: roteer de eerste 3
 *      - Duw de root omhoog en maak van de linker- en rechterdeelboom een nieuwe zeepbel.
 *      - Pas de bovenliggende zeepbel aan, indien nodig
 *
 * Algoritme voor verwijderen:
 * - Stel X het te verwijderen item met ouder P. P heeft een nu leeg kind.
 * - Neem K het ander kind van K. Neem K2 het grootse/kleinste kind binnen dezelfde zeepbel.
 * - Verwijder dit kind en plaats het op de plaats van P en plaats P de plaats van X.
 * Stel: ander kind van K2  (NU) is leeg:
 *      Duw K2 bij P.
 *      Doe nu alsof K2 werd verwijderd en do zo verder.
 */
public class Zeepbelboom1<E extends Comparable<E>> extends Zeepbelboom<E> {


    public Zeepbelboom1(int k) {
        super(k);
    }

    @Override
    public void balanceer() {

    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }
}
