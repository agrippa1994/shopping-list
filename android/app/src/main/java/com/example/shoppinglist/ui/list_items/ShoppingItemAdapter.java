package com.example.shoppinglist.ui.list_items;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppinglist.R;
import com.example.shoppinglist.ui.ShoppingListActivity;
import com.example.shoppinglist.ui.list_overview.ShoppingListsAdapter;
import com.example.shoppinglist.ui.model.ListItem;
import com.example.shoppinglist.ui.model.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingListViewHolder> {

    private List<ListItem> item_list;
    private ShoppingItemAdapter.ShoppingItemClickListener itemClickListener;

    public ShoppingItemAdapter(List<ListItem> item_list, ShoppingItemAdapter.ShoppingItemClickListener listener) {
        this.item_list = item_list;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ShoppingItemAdapter.ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ShoppingItemAdapter.ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemAdapter.ShoppingListViewHolder holder, int position) {
        holder.bindItem(item_list.get(position));
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public void addToList(List<ListItem> list) {
        this.item_list.addAll(list);
    }

    public void removeFromList(ListItem item) {
        this.item_list.remove(item);
    }

    class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTitle;
        private TextView itemQuantity;
        private View rootView;
        private LinearLayout item_wrapper;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            rootView = itemView.findViewById(R.id.cvItem);
            item_wrapper = itemView.findViewById(R.id.item_wrapper);
        }

        public void bindItem(ListItem item) {
            itemTitle.setText(item.getTitle());
            itemQuantity.setText(item.getQuantity());
            item_wrapper.setBackgroundResource((item.isChecked() ? R.color.colorItemChecked : R.color.colorItemUnchecked));


            rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.onShoppingItemClicked(item);
                    AlertDialog.Builder alert = new AlertDialog.Builder(ShoppingListViewHolder.super.itemView.getContext());

                    LinearLayout layout = new LinearLayout(ShoppingListViewHolder.super.itemView.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText input = new EditText(ShoppingListViewHolder.super.itemView.getContext());
                    input.setHint(R.string.item);
                    input.setText(item.getTitle());

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    layout.addView(input);
                    input.setPadding(50, 30, 50, 30);
                    final EditText input2 = new EditText(ShoppingListViewHolder.super.itemView.getContext());
                    input2.setHint(R.string.quantity);
                    input2.setText(item.getQuantity());

                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input2.setLayoutParams(lp2);
                    input2.setPadding(50, 30, 50, 30);
                    layout.addView(input2);
                    alert.setTitle("Editing a item ...")
                            .setView(layout)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    itemTitle.setText(input.getText());
                                    itemQuantity.setText(input2.getText());
                                    item.setTitle(""+input.getText());
                                    item.setQuantity(""+input2.getText());
                                    syncItem(item);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    itemClickListener.onShoppingItemLongClicked(item);
                                    syncItem(item);
                                }
                            })
                            .show();
                    return false;
                }


            });
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout item_wrapper = view.findViewById(R.id.item_wrapper);
                    boolean checked = !item.isChecked();
                    item.setChecked(checked);
                    item_wrapper.setBackgroundResource((checked ? R.color.colorItemChecked : R.color.colorItemUnchecked));
                    syncItem(item);
                }

            });
        }
    }

    private void syncItem(ListItem item){
        // TODO (mani) sync Item to server with id
    }

    public interface ShoppingItemClickListener {
        void onShoppingItemClicked(ListItem item);
        void onShoppingItemLongClicked(ListItem item);
    }

}