package com.example.shoppinglist.ui.list_items;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.shoppinglist.DeleteItemMutation;
import com.example.shoppinglist.R;
import com.example.shoppinglist.UpdateItemMutation;
import com.example.shoppinglist.ui.data_access.DataAccess;
import com.example.shoppinglist.ui.model.ListItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShoppingItemFragment  extends Fragment implements ShoppingItemAdapter.ShoppingItemProcessingListener {
    private RecyclerView rvShoppingItems;
    private ShoppingItemAdapter shoppingItemAdapter;
    private DataAccess dataAccess;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shopping_list, container, false);
        rvShoppingItems = view.findViewById(R.id.rvShoppingList);
        rvShoppingItems.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ListItem> sl = new ArrayList<>();
        shoppingItemAdapter = new ShoppingItemAdapter(sl, this);
        dataAccess = new DataAccess();
        rvShoppingItems.setAdapter(shoppingItemAdapter);
        return view;
    }

    public void merge(List<ListItem> lists) {
        shoppingItemAdapter.mergeList(lists);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("LIFECYCLE", "onPause location");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("LIFECYCLE", "onStop location");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("LIFECYCLE", "onDestroy location");

    }

    @Override
    public void onShoppingItemCheckedStateChanged(ListItem item) {
        this.dataAccess.updateShoppingListItem(item).enqueue(
                new ApolloCall.Callback<UpdateItemMutation.Data>() {

                    private void handleError() {
                        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(@NotNull Response<UpdateItemMutation.Data> response) {
                        if (response.getErrors() != null) {
                            handleError();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        handleError();
                    }
                }
        );

    }

    @Override
    public void onShoppingItemDeleted(ListItem item) {
        this.dataAccess.deleteShoppingListItem(item).enqueue(
                new ApolloCall.Callback<DeleteItemMutation.Data>() {

                    private void handleError() {
                        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(@NotNull Response<DeleteItemMutation.Data> response) {
                        if (response.getErrors() != null) {
                            handleError();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        handleError();
                    }
                }
        );
    }

    @Override
    public void onShoppingItemUpdated(ListItem item) {
        this.dataAccess.updateShoppingListItem(item).enqueue(
                new ApolloCall.Callback<UpdateItemMutation.Data>() {

                    private void handleError() {
                        Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onResponse(@NotNull Response<UpdateItemMutation.Data> response) {
                        if (response.getErrors() != null) {
                            handleError();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        handleError();
                    }
                }
        );
    }


}
