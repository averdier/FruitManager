# Initiation Android

Durée de formation : 3h
Objectifs : Créer une petite application permettant un CRUD
Points traités :
- Lister des éléments
- Afficher les détails d'un élement
- Créer un élément à l'aide d'un formulaire basique
- Créer un mock pour une API

Nous allons prendre le cas d'une API permettant la de gestion de fruits


## Préparation du projet

Nous allons simuler le fait qu'on utilise une API, nous allons créer un mock, le but est de ne pas aborder la partie API dans le tutoriel (en plus d'être utile pour le testing). Ici pas d'Android, juste du Java.

### Création du projet

Créer un projet à partir d'une activité vide, et nommer le projet `Fruit Manager`

### Modèle de données.

Un fruit est définie par le schéma suivant :
```json
{
    "id": 1,
    "name": "Abricot",
    "description"
}
```

Nous allons donc créer une classe représentant notre fruit :

```java
package com.example.fruitmanager.models;

public class Fruit {
    public final int id;
    public final String name;
    public final String description;

    public Fruit(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
```

### Préparation de création du service

Afin de pouvoir facilement réutiliser notre code, nous allons créer une interface qui décrira ce qu'on attend de notre service spécialisé, cela fait un petit peu de code en plus, mais facilitera grandement le passage à l'utilisation d'une vraie API.

Dans notre service, nous avons besoin de :
- Récupérer la liste des fruits
- Récupérer les détails d'un fruit
- Ajouter un fruit
- Modifier un fruit
- Supprimer un fruit

Ce qui nous donne l'interface suivante :
```java
package com.example.fruitmanager.services.interfaces;

import com.example.fruitmanager.models.Fruit;
import java.util.List;

public interface FruitServiceInterface {
    public List<Fruit> getFruits ();
    public Fruit getFruit (int fruitId);
    public Fruit addFruit (Fruit fruit);
    public boolean deleteFruit (int fruitId);
}
```

### Création du service

Nous pouvons maintenant créer notre service (ici un mock), pour cela nous allons utiliser le pattern Singleton afin que notre service soit accessible dans l'ensemble de notre application.

```java
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

```

A partir d'ici, nous avons de quoi commencer notre application Android

## Afficher la liste des fruits

### Préparation de la vue

La première étape consiste à préparer notre vue, nous allons utiliser un `LinearLayout` vertical et le composant `ListView` pour afficher nos fruits

`activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <ListView
        android:id="@+id/fruitsListView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</LinearLayout>
```

> Nous avons donner un id à notre listview afin de pouvoir y accéder dans notre code

### Préparation de l'affichage d'un fruit

La seconde étape consiste à définir la manière dont seront affichés les fruits dans la liste et le moyen de le faire.

Pour cela nous allons créer :
- Une template de fruit
- Un `ArrayAdapter` qui indiquera à Android comment afficher nos fruits

#### Template

Pour la template, nous allons rester dans quelque chose de très simple, nous afficherons:
- L'identifiant du fruit
- Le nom du fruit

`item_fruit.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/fruitId"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="ID"/>

    <TextView
        android:id="@+id/fruitName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Name"/>

</LinearLayout>
```

#### ArrayAdapter

Maintenant nous allons créer un `ÀrrayAdapter` afin d'indiquer à Android comment rendre une liste de fruits.

Pour cela nous héritons d'un `ArrayAdapter`
```java
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

```

### Logique applicative

Nous pouvons maintenant implémenter notre logique applicative, à la création de notre vue nous souhaitons :
- Charger la liste de fruits
- Insérer les fruits dans notre `ListView`

Ce qui nous donne le code suivant :
`MainActivity.java`
```java
package com.example.fruitmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import com.example.fruitmanager.adapters.FruitAdapter;
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
        FruitAdapter adapter = new FruitAdapter(this, fruitService.getFruits());

        fruitsListView.setAdapter(adapter);
    }

}
```

## Afficher les détails d'un fruit

Maintenant nous souhaitons afficher les détails d'un fruit en cliquant dessus, pour cela nous allons lancer une nouvelle activité.

### Préparation de l'activité :

#### Préparation de la vue

Nous allons rester sur un layout très simple pour afficher nos informations, à savoir :
- L'identifiant du fruit
- Le nom du fruit
- La description du fruit

`activity_fruit_details.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".activities.FruitDetails"
    android:orientation="vertical">

    <TextView
        android:id="@+id/tvFruitId"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="ID"/>

    <TextView
        android:id="@+id/tvFruitName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Name"/>

    <TextView
        android:id="@+id/tvFruitDescription"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Description"/>

</LinearLayout>
```

#### Logique applicative

Quand nous lancerons l'activité, nous lui fournirons en paramètre l'identifiant du fruit que l'on souhaite afficher, nous pourrons ensuite aller le chercher dans notre service de fruits.

`FruitDetails.java`
```java
package com.example.fruitmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import com.example.fruitmanager.services.mocks.FruitService;

public class FruitDetails extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_details);

        Intent intent = getIntent();
        if (intent != null) {
            int fruitId = intent.getIntExtra("fruitId", -1);
            if (fruitId != -1) {
                Fruit fruit = fruitService.getFruit(fruitId);
                if (fruit != null) populateView(fruit);
                else finish();
            } else finish();
        } else finish();
    }

    private void populateView(Fruit fruit) {
        TextView fruitId = findViewById(R.id.tvFruitId);
        TextView fruitName = findViewById(R.id.tvFruitName);
        TextView fruitDescription = findViewById(R.id.tvFruitDescription);

        fruitId.setText(String.format("#%d", fruit.id));
        fruitName.setText(fruit.name);
        fruitDescription.setText(fruit.description);
    }
}
```

### Mettre en place la navigation

Pour pouvoir afficher les détails d'un fruit, nous devons :
- Réagir lors d'un clic sur la `ListView`
- Lancer une activité

Pour cela il suffit de rajouter un petit listener

`MainActivity.java`
```java
package com.example.fruitmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
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
        FruitAdapter adapter = new FruitAdapter(this, fruitService.getFruits());

        fruitsListView.setAdapter(adapter);

        fruitsListView.setOnItemClickListener((parent, view, position, id) -> {
            Fruit fruit = (Fruit) fruitsListView.getItemAtPosition(position);

            Intent intent = new Intent(this, FruitDetails.class);
            intent.putExtra("fruitId", fruit.id);

            startActivity(intent);
        });
    }

}
```

## Ajouter un fruit

Maintenant nous allons ajouter la possibilité d'ajouter un fruit à l'aide de notre service.

### Préparation de la vue

Pour la vue, nous avons besoin de :
- Un champ pour le nom
- Un champ pour la description
- Un bouton pour ajouter le fruit

Ce qui nous donne le code suivant :

`activity_fruit_add.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".activities.FruitAdd"
    android:orientation="vertical">

    <EditText
        android:id="@+id/etFruitName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <EditText
        android:id="@+id/etFruitDescription"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Add fruit"
        android:onClick="onAddFruitClick"/>

</LinearLayout>
```

### Logique applicative

Nous pouvons désormais ajouter notre logique, il s'agit simplement de lire les valeurs du formulaire et d'envoyer les données à notre service.

Ce qui donne le code suivant :

`FruitAdd.java`
```java
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
```

### Mettre en place la navigation

Pour faire simple, nous allons juste ajouter un bouton au dessus de notre ListView et réagir lors du click

Ce qui nous donne :

`activity_main.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:orientation="vertical">

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Add fruit"
        android:onClick="onAddFruitClick"/>

    <ListView
        android:id="@+id/fruitsListView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</LinearLayout>
```


`MainActivity.java`
```java
package com.example.fruitmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        FruitAdapter adapter = new FruitAdapter(this, fruitService.getFruits());

        fruitsListView.setAdapter(adapter);

        fruitsListView.setOnItemClickListener((parent, view, position, id) -> {
            Fruit fruit = (Fruit) fruitsListView.getItemAtPosition(position);

            Intent intent = new Intent(this, FruitDetails.class);
            intent.putExtra("fruitId", fruit.id);

            startActivity(intent);
        });
    }

    public void onAddFruitClick(View view) {
        startActivity(new Intent(this, FruitAdd.class));
    }

}
```

## Supprimer un fruit

Nous avançons dans notre CRUD, il nous reste à pouvoir modifier et supprimer un fruit.

Penchons nous sur le fait de supprimer un fruit, pour cela, nous allons juste ajouter un Button en dessous des TextView, réagir au click et appeler notre service.

Ce qui donne le code suivant :

`activity_fruit_details.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".activities.FruitDetails"
    android:orientation="vertical">

    <TextView
        android:id="@+id/tvFruitId"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="ID"/>

    <TextView
        android:id="@+id/tvFruitName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Name"/>

    <TextView
        android:id="@+id/tvFruitDescription"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Description"/>

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Delete"
        android:onClick="onDeleteFruitClick"/>

</LinearLayout>
```

`FruitDetails.java`
```java
package com.example.fruitmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import com.example.fruitmanager.services.mocks.FruitService;

public class FruitDetails extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();
    Fruit fruit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_details);

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
        TextView fruitId = findViewById(R.id.tvFruitId);
        TextView fruitName = findViewById(R.id.tvFruitName);
        TextView fruitDescription = findViewById(R.id.tvFruitDescription);

        fruitId.setText(String.format("#%d", fruit.id));
        fruitName.setText(fruit.name);
        fruitDescription.setText(fruit.description);
    }

    public void onDeleteFruitClick(View view) {
        fruitService.deleteFruit(fruit.id);
        finish();
    }
}
```

## Mettre à jour un fruit

Dernière étape de notre CRUD, modifier un fruit

### Préparation de l'activité

Nous avons besoin des mêmes choses que pour le formulaire d'ajout d'un fruit, les seules vraies différences sont :
- Les id des EditText
- Le texte du boutton
- Le nom de la fonction appelée

Ce qui nous donne le code suivant :

`activity_fruit_update.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".activities.FruitUpdate"
    android:orientation="vertical">

    <EditText
        android:id="@+id/etuFruitName"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <EditText
        android:id="@+id/etuFruitDescription"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Update"
        android:onClick="onUpdateFruitClick"/>

</LinearLayout>
```

### Logique applicative

Comme pour l'affichage des détails d'un fruit, nous allons attendre en paramètre l'identifiant du fruit et le récupérer à l'aide de notre service. Nous pourrons ensuite pré-remplir les EditText.

Ce qui nous donne le code suivant :

`FruitUpdata.java`
```java
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
```


### Mettre en place la navigation

Il ne nous reste plus qu'à ajouter un bouton sur l'activité qui affiche les détails d'un fruit et réagir lors du click.

Ce qui nous donne le code suivant :

`activity_fruit_details.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".activities.FruitDetails"
    android:orientation="vertical">

    <TextView
        android:id="@+id/tvFruitId"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="ID"/>

    <TextView
        android:id="@+id/tvFruitName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Name"/>

    <TextView
        android:id="@+id/tvFruitDescription"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Description"/>

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Delete"
        android:onClick="onDeleteFruitClick"/>

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Update"
        android:onClick="onUpdateFruitClick"/>

</LinearLayout>
```

`FruitDetails.java`
```java
package com.example.fruitmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import com.example.fruitmanager.services.mocks.FruitService;

public class FruitDetails extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();
    Fruit fruit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_details);

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
        TextView fruitId = findViewById(R.id.tvFruitId);
        TextView fruitName = findViewById(R.id.tvFruitName);
        TextView fruitDescription = findViewById(R.id.tvFruitDescription);

        fruitId.setText(String.format("#%d", fruit.id));
        fruitName.setText(fruit.name);
        fruitDescription.setText(fruit.description);
    }

    public void onDeleteFruitClick(View view) {
        fruitService.deleteFruit(fruit.id);
        finish();
    }

    public void onUpdateFruitClick(View view) {
        Intent intent = new Intent(this, FruitUpdate.class);
        intent.putExtra("fruitId", fruit.id);

        startActivity(intent);
    }
}
```


## Initiation au cyle de vie

Après l'implémentation de la modification d'un fruit, nous avons un problème, le fruit n'est pas mis à jour dans les différentes activités.

Pour rappel, nous avons utilisé la méthode `onCreate` pour initialiser nos informations, hors quand on navigue, l'activité n'est pas forcément tuée.

Pour pallier cela, nous allons charger nos informations quand nous naviguons vers la vue, que ce soit pour y aller ou pour y revenir

### Détails d'un fruit

Nous allons commencer par l'activité affichant les détails d'un fruit, implémentons la méthode `onResume`

Ce qui nous donne le code suivant :
`FruitDetails.java`
```java
package com.example.fruitmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import com.example.fruitmanager.services.mocks.FruitService;

public class FruitDetails extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();
    private int fruitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_details);

        Intent intent = getIntent();
        if (intent == null) finish();
        fruitId = intent.getIntExtra("fruitId", -1);
        populateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateView();
    }

    private void populateView() {
        if (fruitId == -1) finish();
        else {
            Fruit fruit = fruitService.getFruit(fruitId);
            if (fruit == null) finish();
            else {
                setTextViewsText(fruit);
            }
        }
    }

    private void setTextViewsText (Fruit fruit) {
        TextView fruitId = findViewById(R.id.tvFruitId);
        TextView fruitName = findViewById(R.id.tvFruitName);
        TextView fruitDescription = findViewById(R.id.tvFruitDescription);

        fruitId.setText(String.format("#%d", fruit.id));
        fruitName.setText(fruit.name);
        fruitDescription.setText(fruit.description);
    }

    public void onDeleteFruitClick(View view) {
        fruitService.deleteFruit(fruitId);
        finish();
    }

    public void onUpdateFruitClick(View view) {
        Intent intent = new Intent(this, FruitUpdate.class);
        intent.putExtra("fruitId", fruitId);

        startActivity(intent);
    }
}
```

### Liste de fruits

Faisons de même avec l'activité qui liste les fruits, implémentons la méthode `onResume`

Ce qui nous donne le code suivant :

`MainActivity.java`
```java
package com.example.fruitmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
```

## Fin

Et voilà nous avons implémenter un CRUD certes basique mais fonctionnel, les points d'améliorations possibles sont :
- Ajouter des informations aux fruits (icone, couleur, origine, etc)
- Utiliser une vraie API
- Utilser des actions asynchrones pour une meilleure expérience utilisateur
- Mettre en place un système de validation de formulaire
- Améliorer le layout