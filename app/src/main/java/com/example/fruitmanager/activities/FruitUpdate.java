package com.example.fruitmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import com.example.fruitmanager.services.mocks.FruitService;

public class FruitUpdate extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();
    Fruit fruit;
    EditText fruitNameTextView;
    EditText fruitDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_update);

        fruitNameTextView = findViewById(R.id.etuFruitName);
        fruitDescriptionTextView = findViewById(R.id.etuFruitDescription);

        Intent intent = getIntent();
        if (intent != null) {
            int fruitId = intent.getIntExtra("fruitId", -1);
            if (fruitId != -1) {
                fruit = fruitService.getFruit(fruitId);
                if (fruit != null) populateView();
                else finish();
            } else finish();
        } else finish();

    }

    private void populateView() {
        fruitNameTextView.setText(fruit.name);
        fruitDescriptionTextView.setText(fruit.description);
    }

    public void onUpdateFruitClick(View view) {
        String name = fruitNameTextView.getText().toString();
        String description = fruitDescriptionTextView.getText().toString();

        if (!name.equals("") && !description.equals("")) {
            Fruit updatedFruit = fruitService.updateFruit(fruit.id, new Fruit(-1, name, description));
            if (updatedFruit != null) finish();
            else {
                fruitNameTextView.setText(fruit.name);
                fruitDescriptionTextView.setText(fruit.description);
            }
        }
    }
}
