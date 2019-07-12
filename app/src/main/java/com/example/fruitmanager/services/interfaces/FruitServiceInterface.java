package com.example.fruitmanager.services.interfaces;

import com.example.fruitmanager.models.Fruit;
import java.util.List;

public interface FruitServiceInterface {
    public List<Fruit> getFruits ();
    public Fruit getFruit (int fruitId);
    public Fruit addFruit (Fruit fruit);
    public Fruit updateFruit (int fruitId, Fruit fruit);
    public boolean deleteFruit (int fruitId);
}
