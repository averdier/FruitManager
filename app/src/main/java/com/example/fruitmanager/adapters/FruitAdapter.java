package com.example.fruitmanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import java.util.List;

public class FruitAdapter extends ArrayAdapter<Fruit> {

    public FruitAdapter(Context context, List<Fruit> fruits) {
        super(context, 0, fruits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fruit fruit = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_fruit, parent, false);
        }

        TextView fruitId = convertView.findViewById(R.id.fruitId);
        TextView fruitName = convertView.findViewById(R.id.fruitName);

        fruitId.setText(String.format("#%d", fruit.id));
        fruitName.setText(fruit.name);

        return convertView;
    }
}
