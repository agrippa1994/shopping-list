package com.example.shoppinglist.ui.list_items;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppinglist.R;
import com.example.shoppinglist.ui.data_access.LoadImageTask;
import com.example.shoppinglist.ui.model.ListItem;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingListViewHolder> {

    private List<ListItem> itemList;
    private ShoppingItemAdapter.ShoppingItemProcessingListener itemProcessingListener;

    public ShoppingItemAdapter(List<ListItem> itemList, ShoppingItemAdapter.ShoppingItemProcessingListener listener) {
        this.itemList = itemList;
        this.itemProcessingListener = listener;
    }

    @NonNull
    @Override
    public ShoppingItemAdapter.ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ShoppingItemAdapter.ShoppingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemAdapter.ShoppingListViewHolder holder, int position) {
        holder.bindItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Merges the given list with the current content and notifies the adapter about its changes
     * @param incomingList the list that contains the changes
     */
    public void mergeList(@NotNull  List<ListItem> incomingList) {
        int index = 0;
        for(ListItem newItem : incomingList) {
            boolean exists = false;
            index = 0;
            for(ListItem existingItem : this.itemList) {

                if(existingItem.getId() == newItem.getId()) {
                    exists = true;

                    // update items
                    if (!existingItem.equals(newItem)) {
                        existingItem.setChecked(newItem.isChecked());
                        existingItem.setTitle(newItem.getTitle());
                        existingItem.setQuantity(newItem.getQuantity());
                        notifyItemChanged(index);
                    }

                    break;
                }
                index++;
            }

            // create item if it does not exist
            if (!exists) {
                itemList.add(newItem);
                notifyItemInserted(itemList.size() - 1);
            }
        }

        // remove all items from existing list that are not part of the update
        if(itemList.size() > 0) {
            for(int i = itemList.size() - 1; i >= 0; --i) {
                boolean exists = false;
                for(ListItem newItem : incomingList) {
                    if(newItem.getId() == itemList.get(i).getId()) {
                        exists = true;
                        break;
                    }
                }

                if(!exists) {
                    itemList.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    class ShoppingListViewHolder extends RecyclerView.ViewHolder {
        private TextView itemTitle;
        private TextView itemQuantity;
        private View rootView;
        private LinearLayout item_wrapper;
        private ImageView itemPicture;
        private CheckBox itemCheckbox;

        public ShoppingListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            itemPicture = itemView.findViewById(R.id.itemPicture);
            rootView = itemView.findViewById(R.id.cvItem);
            itemCheckbox = itemView.findViewById(R.id.itemChecked);
            item_wrapper = itemView.findViewById(R.id.item_wrapper);
        }

        /**
         * Loads the picture asynchronously
         * @param item
         */
        private void loadImage(ListItem item) {
            new LoadImageTask(new LoadImageTask.Listener() {
                @Override
                public void onImageLoaded(Bitmap bitmap) {
                    itemPicture.setImageBitmap(bitmap);
                    Log.i("IMAGE", "Image loaded");
                }

                @Override
                public void onError() {
                    Log.e("IMAGE", "Error encountered");
                    itemPicture.setImageResource(android.R.color.transparent);
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "https://shopping.mani94.space/pictures/" + item.getTitle());
        }

        public void bindItem(ListItem item) {
            loadImage(item);
            itemTitle.setText(item.getTitle());
            itemQuantity.setText(item.getQuantity());
            itemCheckbox.setChecked(item.isChecked());

            itemCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setChecked(isChecked);
                itemProcessingListener.onShoppingItemCheckedStateChanged(item);
            });

            rootView.setOnLongClickListener(v -> {
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
                                itemProcessingListener.onShoppingItemUpdated(item);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                itemProcessingListener.onShoppingItemDeleted(item);
                            }
                        })
                        .show();
                return false;
            });
            rootView.setOnClickListener(view -> {
                boolean checked = !item.isChecked();
                itemCheckbox.setChecked(checked);
                item.setChecked(checked);
                itemProcessingListener.onShoppingItemCheckedStateChanged(item);
            });
        }
    }

    public interface ShoppingItemProcessingListener {
        void onShoppingItemCheckedStateChanged(ListItem item);
        void onShoppingItemDeleted(ListItem item);
        void onShoppingItemUpdated(ListItem item);
    }

}