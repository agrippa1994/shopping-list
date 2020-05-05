package com.example.shoppinglist.ui.list_overview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppinglist.R;
import com.example.shoppinglist.ui.MainActivity;
import com.example.shoppinglist.ui.model.ListItem;
import com.example.shoppinglist.ui.model.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ShoppingListViewHolder> {

    private List<ShoppingList> lists;
    private ShoppingListClickListener itemClickListener;
    private MainActivity main;

    public ShoppingListsAdapter(List<ShoppingList> lists, ShoppingListClickListener listener, MainActivity main) {
        this.lists = lists;
        this.main = main;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_list_item, parent, false);
        return new ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListViewHolder holder, int position) {
        holder.bindItem(lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void addToList(List<ShoppingList> lists) {
        this.lists.addAll(lists);
    }

    public void removeFromList(ShoppingList list) {
        this.lists.remove(list);
    }

    public void removeAllLists() {
        this.lists = new ArrayList<>();
    }

    class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        private TextView listTitle;
        private TextView listKey;
        private View rootView;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);

            listTitle = itemView.findViewById(R.id.listTitle);
            listKey = itemView.findViewById(R.id.listKey);
            rootView = itemView.findViewById(R.id.cvShoppingList);

        }

        public void bindItem(ShoppingList list) {
            listTitle.setText(list.getTitle());
            listKey.setText(list.getKey());

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onShoppingListItemClicked(list);
                }
            });
            rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final EditText input = new EditText(ShoppingListViewHolder.super.itemView.getContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    input.setText(list.getTitle());
                    input.setPadding(50,30,50,30);
                    new AlertDialog.Builder(ShoppingListViewHolder.super.itemView.getContext())
                            .setTitle("Enter the title of a new shopping list ...")
                            .setView(input)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                  list.setTitle(""+input.getText());
                                  listTitle.setText(input.getText());
                                  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(main);
                                  prefs.edit().putString(list.getKey(), list.getTitle()).apply();
                                }
                            })
                            .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    itemClickListener.onShoppingListItemLongClicked(list);
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(main);
                                    prefs.edit().remove(list.getKey()).apply();
                                }
                            })
                            .setNeutralButton(R.string.share, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDialog.Builder(main)
                                            .setTitle("How do you want to share the list?")
                                            .setPositiveButton(R.string.copy_to_clipboard, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ClipboardManager clipboard = (ClipboardManager) main.getSystemService(Context.CLIPBOARD_SERVICE);
                                                    ClipData clip = ClipData.newPlainText("Key", list.getKey());
                                                    if (clipboard != null) {
                                                        clipboard.setPrimaryClip(clip);
                                                        Toast.makeText(main.getApplicationContext(), "Key copied to Clipboard", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            })
                                            .setNeutralButton(R.string.send, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent sendIntent = new Intent();
                                                    sendIntent.setAction(Intent.ACTION_SEND);
                                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Come and join my shopping list! Just enter the key: "+list.getKey()+" or go to www.shopping-list-test.com?key="+list.getKey());
                                                    sendIntent.setType("text/plain");
                                                    ShoppingListViewHolder.super.itemView.getContext().startActivity(sendIntent);
                                                }
                                            })
                                            .show();
                                }
                            })
                            .show();
                    return false;
                }
            });

        }
    }

    public interface ShoppingListClickListener {
        void onShoppingListItemClicked(ShoppingList list);
        void onShoppingListItemLongClicked(ShoppingList list);
    }

}
