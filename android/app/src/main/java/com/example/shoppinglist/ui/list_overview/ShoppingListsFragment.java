package com.example.shoppinglist.ui.list_overview;

import android.content.Intent;
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

import com.example.shoppinglist.R;
import com.example.shoppinglist.ui.MainActivity;
import com.example.shoppinglist.ui.ShoppingListActivity;
import com.example.shoppinglist.ui.SplashActivity;
import com.example.shoppinglist.ui.model.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListsFragment extends Fragment implements ShoppingListsAdapter.ShoppingListClickListener{
    private RecyclerView rvShoppingLists;
    private ShoppingListsAdapter shoppingListAdapter;
    private MainActivity main;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shopping_list_list, container, false);
        rvShoppingLists = view.findViewById(R.id.rvShoppingLists);
        rvShoppingLists.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ShoppingList> sl = new ArrayList<>();
        shoppingListAdapter = new ShoppingListsAdapter(sl, this, main);
        return view;
    }
    public ShoppingListsFragment(MainActivity main){
        this.main = main;
    }

    public void update(List<ShoppingList> lists) {
        shoppingListAdapter.addToList(lists);
        rvShoppingLists.setAdapter(shoppingListAdapter);
    }
    public void removeList(ShoppingList list) {
        shoppingListAdapter.removeFromList(list);
        rvShoppingLists.setAdapter(shoppingListAdapter);
    }
    public void removeAllLists() {
        shoppingListAdapter.removeAllLists();
        rvShoppingLists.setAdapter(shoppingListAdapter);
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
    public void onShoppingListItemClicked(ShoppingList list) {
        //Toast.makeText(getContext(), list.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(super.getContext(), ShoppingListActivity.class);
        intent.putExtra("ShoppingList", list);
        startActivity(intent);
    }

    @Override
    public void onShoppingListItemLongClicked(ShoppingList list) {
        removeList(list);
    }
}
