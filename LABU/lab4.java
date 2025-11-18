
import java.util.ArrayList;
import java.util.List;
class CageFullException extends Exception {
    public CageFullException(String message) { super(message); }
}
class AnimalNotFoundException extends Exception {
    public AnimalNotFoundException(String message) { super(message); }
}
abstract class Animal {
    private String name;
    public Animal(String name) { this.name = name; }
    public String getName() { return name; }
    @Override public String toString() { return name; }
}

abstract class Mammal extends Animal {
    public Mammal(String name) { super(name); }
}

abstract class Bird extends Animal {
    public Bird(String name) { super(name); }
}

class Lion extends Mammal {
    public Lion(String name) { super(name); }
}

abstract class Ungulate extends Mammal { // Копитні
    public Ungulate(String name) { super(name); }
}

class Zebra extends Ungulate {
    public Zebra(String name) { super(name); }
}

class Giraffe extends Ungulate {
    public Giraffe(String name) { super(name); }
}

class Eagle extends Bird {
    public Eagle(String name) { super(name); }
}
abstract class Cage<T extends Animal> {
    private int capacity;
    private List<T> animals;

    public Cage(int capacity) {
        this.capacity = capacity;
        this.animals = new ArrayList<>();
    }

    public void addAnimal(T animal) throws CageFullException {
        if (animals.size() >= capacity) {
            throw new CageFullException("Вольєр переповнений! Неможливо додати: " + animal.getName());
        }
        animals.add(animal);
    }
    public void removeAnimal(T animal) throws AnimalNotFoundException {
        if (!animals.contains(animal)) {
            throw new AnimalNotFoundException("Тварину " + animal.getName() + " не знайдено у вольєрі.");
        }
        animals.remove(animal);
    }
    public int getOccupiedPlaces() {
        return animals.size();
    }
    public int getMaxCapacity() {
        return capacity;
    }
}

class LionCage extends Cage<Lion> {
    public LionCage(int capacity) { super(capacity); }
}
class UngulateCage extends Cage<Ungulate> {
    public UngulateCage(int capacity) { super(capacity); }
}
class BirdCage extends Cage<Bird> {
    public BirdCage(int capacity) { super(capacity); }
}
class Zoo {
    private List<Cage<? extends Animal>> cages = new ArrayList<>();

    public void addCage(Cage<? extends Animal> cage) {
        cages.add(cage);
    }

    public int getCountOfAnimals() {
        int total = 0;
        for (Cage<? extends Animal> cage : cages) {
            total += cage.getOccupiedPlaces();
        }
        return total;
    }

    public int getCountOfCages() {
        return cages.size();
    }
}
