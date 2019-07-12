package com.example.fruitmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.fruitmanager.activities.FruitAdd;
import com.example.fruitmanager.activities.FruitDetails;
import com.example.fruitmanager.adapters.FruitAdapter;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.mocks.FruitService;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;

public class MainActivity extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();
    ListView fruitsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fruitsListView = findViewById(R.id.fruitsListView);

        fruitsListView.setOnItemClickListener((parent, view, position, id) -> {
            Fruit fruit = (Fruit) fruitsListView.getItemAtPosition(position);

            Intent intent = new Intent(this, FruitDetails.class);
            intent.putExtra("fruitId", fruit.id);

            startActivity(intent);
        });

        populateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateView();
    }

    private void populateView() {
        FruitAdapter adapter = new FruitAdapter(this, fruitService.getFruits());
        fruitsListView.setAdapter(adapter);
    }

    public void onAddFruitClick(View view) {
        startActivity(new Intent(this, FruitAdd.class));
    }

}
