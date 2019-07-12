package com.example.fruitmanager.services.mocks;

import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FruitService implements FruitServiceInterface {

    private static FruitService instance = new FruitService();
    private List<Fruit> fruits = new ArrayList<>();
    private int idx;

    private FruitService() {
        fruits.add(new Fruit(0, "Abricot", "C'est un fruit charnu, une drupe, de forme arrondie, possédant un noyau dur contenant une seule grosse graine, ou amande."));
        fruits.add(new Fruit(1, "Groseille", "La groseille rouge est le fruit du groseillier rouge, arbrisseau d’un mètre et demi à port décombant."));
        fruits.add(new Fruit(2, "Melon", "Le Melon (Cucumis melo) est une plante herbacée annuelle originaire d'Afrique intertropicale"));
        fruits.add(new Fruit(3, "Pomme", "La pomme est un fruit comestible à pépins d'un goût sucré et acidulé et à la propriété plus ou moins astringente selon les variétés."));
        idx = fruits.size();
    }

    public static FruitService getInstance() { return instance; }


    @Override
    public List<Fruit> getFruits() {
        return fruits;
    }

    @Override
    public Fruit getFruit(int fruitId) {
        return fruits.stream()
                .filter(fruit -> fruit.id == fruitId).
                        findFirst().orElse(null);
    }

    @Override
    public Fruit addFruit(Fruit fruit) {
        fruits.add(new Fruit(idx++, fruit.name, fruit.description));

        return new Fruit(idx, fruit.name, fruit.description);
    }

    @Override
    public Fruit updateFruit(int fruitId, Fruit fruit) {
        Fruit found = fruits.stream()
                .filter(e -> e.id == fruitId)
                .findFirst().orElse(null);

        if (found != null) {
            Fruit newFruit = new Fruit(found.id, fruit.name, fruit.description);
            Collections.replaceAll(fruits, found, newFruit);

            return newFruit;
        }

        return null;
    }

    @Override
    public boolean deleteFruit(final int fruitId) {
        Fruit found = fruits.stream()
                .filter(fruit -> fruit.id == fruitId)
                .findFirst().orElse(null);

        if (found != null) {
            fruits.remove(found);

            return true;
        }
        return false;
    }
}
