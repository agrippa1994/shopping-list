package com.example.shoppinglist.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.shoppinglist.R;
import com.example.shoppinglist.ui.list_items.ShoppingItemFragment;
import com.example.shoppinglist.ui.model.ListItem;
import com.example.shoppinglist.ui.model.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ShoppingItemFragment shoppingListFragment;
    String list_title, key;
    ShoppingList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        list = (ShoppingList) getIntent().getSerializableExtra("ShoppingList");
        list_title = list.getTitle();
        key = list.getKey();

        // TODO (mani) remove the line below and replace with getting the data from the server with the key
        List<ListItem> items = new ArrayList<>(list.getItems());


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.ic_shopping_cart_small);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemToList();
            }
        });

        shoppingListFragment = new ShoppingItemFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragmentContainer, shoppingListFragment);
        transaction.commit();

        new Thread(() -> {
            runOnUiThread(() -> {
                updateFragments(items);
            });
        }).start();
    }

    public void updateFragments(List<ListItem> shoppingList) {
        shoppingListFragment.update(shoppingList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shopping_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("item.getItemId()");
        System.out.println(item.getItemId());
        // Handle item selection


        switch (item.getItemId()) {
            case R.id.add_item:
                addItemToList();
                return true;
            case R.id.return_home:
                Intent intent = new Intent(ShoppingListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.disconnect_list:
                disconnectFromList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void disconnectFromList() {
        Intent intent = new Intent(ShoppingListActivity.this, MainActivity.class);
        intent.putExtra("ShoppingListToRemove", list);
        startActivity(intent);
    }

    private void addItemToList() {


        AlertDialog.Builder alert = new AlertDialog.Builder(ShoppingListActivity.this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText input = new EditText(ShoppingListActivity.this);
        input.setHint(R.string.item);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        layout.addView(input);
        input.setPadding(50, 30, 50, 30);
        final EditText input2 = new EditText(ShoppingListActivity.this);
        input2.setHint(R.string.quantity);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input2.setLayoutParams(lp2);
        input2.setPadding(50, 30, 50, 30);
        layout.addView(input2);
        alert.setTitle("Adding a new item ...")
                .setView(layout)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        List<ListItem> shoppingList = new ArrayList<>();
                        ListItem li1 = new ListItem("" + input.getText(), "" + input2.getText(), false);
                        shoppingList.add(li1);
                        runOnUiThread(() -> {
                            updateFragments(shoppingList);
                        });
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void addItemToList(MenuItem item) {
        addItemToList();
    }
}
