package ru.istislav.mygrocerylist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ru.istislav.mygrocerylist.R;
import ru.istislav.mygrocerylist.activities.DetailsActivity;
import ru.istislav.mygrocerylist.data.DatabaseHandler;
import ru.istislav.mygrocerylist.model.Grocery;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Grocery> groceryItems;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Grocery grocery = groceryItems.get(position);
        holder.groceryItemName.setText(grocery.getName());
        holder.quantity.setText(grocery.getQuantity());
        holder.dateAdded.setText(grocery.getDateItemAdded());
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView groceryItemName;
        public TextView quantity;
        public TextView dateAdded;
        public Button editButton;
        public Button deleteButton;

        public int id;

        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;

            groceryItemName = (TextView) view.findViewById(R.id.name);
            quantity = (TextView) view.findViewById(R.id.quantity);
            dateAdded = (TextView) view.findViewById(R.id.dateAdded);

            editButton = (Button) view.findViewById(R.id.editButton);
            deleteButton = (Button) view.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Grocery grocery =  groceryItems.get(position);
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity", grocery.getQuantity());
                    intent.putExtra("id", grocery.getId());
                    intent.putExtra("date", grocery.getDateItemAdded());

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position;
            Grocery grocery;

            switch (v.getId()) {
                case R.id.editButton:
                    position = getAdapterPosition();
                    grocery = groceryItems.get(position);

                    editItem(grocery);
                    break;
                case R.id.deleteButton:
                    position = getAdapterPosition();
                    grocery = groceryItems.get(position);

                    deleteItem(grocery.getId());
                    break;
            }
        }

        public void deleteItem(final int id) {
            // create AlertDialog
            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmation_dialog, null);

            Button noButton = (Button) view.findViewById(R.id.noButton);
            Button yesButton = (Button) view.findViewById(R.id.yesButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // deleting the item
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteGrocery(id);
                    groceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    dialog.dismiss();
                }
            });
        }

        public void editItem(final Grocery grocery) {
            final DatabaseHandler db = new DatabaseHandler(context);

            int grId = grocery.getId();
            final Grocery dbGrocery = db.getGrocery(grId);

            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);
            final View view  = inflater.inflate(R.layout.popup, null);

            final EditText groceryItem = (EditText) view.findViewById(R.id.groceryItem);
            final EditText quantity = (EditText) view.findViewById(R.id.groceryQty);
            final TextView title = (TextView) view.findViewById(R.id.popupLayoutTitle);
            Button saveButton = (Button) view.findViewById(R.id.saveButton);

            groceryItem.setText(dbGrocery.getName());
            quantity.setText(dbGrocery.getQuantity());
            title.setText("Edit grocery");
            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Update item
                    dbGrocery.setName(groceryItem.getText().toString());
                    dbGrocery.setQuantity(quantity.getText().toString());
                    grocery.setName(groceryItem.getText().toString());
                    grocery.setQuantity("Qty: " + quantity.getText().toString());

                    if (!groceryItem.getText().toString().isEmpty()
                            && !quantity.getText().toString().isEmpty()) {
                        db.updateGrocery(dbGrocery);
                        notifyItemChanged(getAdapterPosition(), grocery);
                        dialog.dismiss();
                    } else {
                        Snackbar.make(view, "Add Grocery Name and Quantity",
                                                        Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
