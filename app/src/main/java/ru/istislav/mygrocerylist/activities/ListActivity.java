package ru.istislav.mygrocerylist.activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.istislav.mygrocerylist.R;
import ru.istislav.mygrocerylist.data.DatabaseHandler;
import ru.istislav.mygrocerylist.model.Grocery;
import ru.istislav.mygrocerylist.ui.RecyclerViewAdapter;

public class ListActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private RecyclerView recyclerView;
    private List<Grocery> groceryList;
    private ArrayList<Grocery> listItems;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        db = new DatabaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        groceryList = db.getAllGroceries();
        for(Grocery c : groceryList) {
            // ни черта не понятно, зачем создавать копию объекта, и потом, неужели, нельзя проще это сделать?
            Grocery grocery = new Grocery();
            grocery.setName(c.getName());
            grocery.setQuantity("Qty: " + c.getQuantity());
            grocery.setId(c.getId());
            grocery.setDateItemAdded("Added at: " + c.getDateItemAdded());

            listItems.add(grocery);
        }
        recyclerViewAdapter = new RecyclerViewAdapter(this, listItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}