
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ZooTest {

    @Test
    void testLionCageRestrictions() throws CageFullException {
        // Створюємо вольєр для левів на 2 місця
        LionCage lionCage = new LionCage(2);
        Lion simba = new Lion("Simba");
        Lion nala = new Lion("Nala");
        lionCage.addAnimal(simba);
        lionCage.addAnimal(nala);
        assertEquals(2, lionCage.getOccupiedPlaces());

        assertThrows(CageFullException.class, () -> {
            lionCage.addAnimal(new Lion("Scar"));
        });
    }

    @Test
    void testZooStatistics() throws CageFullException {
        Zoo zoo = new Zoo();

        LionCage lCage = new LionCage(3);
        lCage.addAnimal(new Lion("Alex"));

        UngulateCage uCage = new UngulateCage(4);
        uCage.addAnimal(new Zebra("Marty"));
        uCage.addAnimal(new Giraffe("Melman"));

        zoo.addCage(lCage);
        zoo.addCage(uCage);

        assertEquals(3, zoo.getCountOfAnimals());
    }
}