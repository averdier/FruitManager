package com.example.fruitmanager.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import com.example.fruitmanager.services.mocks.FruitService;

public class FruitAdd extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_add);
    }

    public void onAddFruitClick(View view) {
        EditText etName = findViewById(R.id.etFruitName);
        EditText etDescription = findViewById(R.id.etFruitDescription);

        String name = etName.getText().toString();
        String description = etDescription.getText().toString();

        if (!name.equals("") && !description.equals("")) {
            fruitService.addFruit(new Fruit(-1, name, description));
            finish();
        }
    }
}
