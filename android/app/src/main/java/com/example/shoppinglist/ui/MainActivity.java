package com.example.shoppinglist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.shoppinglist.CreateShoppingListMutation;
import com.example.shoppinglist.GetShoppingListQuery;
import com.example.shoppinglist.R;
import com.example.shoppinglist.ui.data_access.DataAccess;
import com.example.shoppinglist.ui.list_overview.ShoppingListsFragment;
import com.example.shoppinglist.ui.model.ListItem;
import com.example.shoppinglist.ui.model.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ShoppingListsFragment shoppingListFragment;
    SharedPreferences prefs;
    DataAccess dataAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataAccess = new DataAccess();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.ic_shopping_cart_small);
        }
        if (getIntent().getData() != null) {
            String key = getIntent().getData().getQueryParameter("key");
            if(key != null) {
                getListFromServer(key);
            }
            else{
                System.out.println("intent data");
                System.out.println(getIntent().getData());
                Toast.makeText(getApplicationContext(), "Could not load the list! Enter the key manually", Toast.LENGTH_LONG).show();
            }
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                chooseAddNewListAction(null);
            }
        });
        /*DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/
        shoppingListFragment = new ShoppingListsFragment(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragmentContainer, shoppingListFragment);
        transaction.commit();

        new Thread(() -> {
            List<ShoppingList> shoppingLists = new ArrayList<>();
            Map<String, ?> allEntries = prefs.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                ShoppingList sl = new ShoppingList(entry.getValue().toString(), new ArrayList<ListItem>(), entry.getKey());
                shoppingLists.add(sl);
            }

            runOnUiThread(() -> {
                updateFragments(shoppingLists);
            });
        }).start();
    }


    public void chooseAddNewListAction(MenuItem item) {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("To want to create a new list or add an existing one?")
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        createNewList();
                    }
                })
                .setNeutralButton(R.string.add, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addNewList();
                    }
                })
                .show();
    }

    private void updateFragments(List<ShoppingList> shoppingLists) {
        shoppingListFragment.update(shoppingLists);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("item.getItemId()");
        System.out.println(item.getItemId());
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_item:
                addNewList();
                return true;
            case R.id.create_list:
                createNewList();
                return true;
            case R.id.disconnect_all:
                shoppingListFragment.removeAllLists();
                prefs.edit().clear().apply();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void createNewList() {
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setPadding(50, 30, 50, 30);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enter the title of a new shopping list ...")
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dataAccess.createShoppingList(input.getText().toString())
                                .enqueue(new ApolloCall.Callback<CreateShoppingListMutation.Data>() {
                                    @Override
                                    public void onResponse(@NotNull Response<CreateShoppingListMutation.Data> response) {
                                        List<ShoppingList> shoppingLists = new ArrayList<>();
                                        List<ListItem> items = new ArrayList<>();

                                        ShoppingList list = new ShoppingList(
                                                response.getData().createShoppingList().fragments().shoppingList().name(),
                                                items,
                                                response.getData().createShoppingList().fragments().shoppingList().id()
                                        );
                                        shoppingLists.add(list);
                                        prefs.edit().putString(
                                                response.getData().createShoppingList().fragments().shoppingList().id(),
                                                response.getData().createShoppingList().fragments().shoppingList().name()).apply();

                                        runOnUiThread(() -> {
                                            updateFragments(shoppingLists);
                                            Toast.makeText(getApplicationContext(), "List created", Toast.LENGTH_LONG).show();
                                        });
                                    }

                                    @Override
                                    public void onFailure(@NotNull ApolloException e) {
                                        runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Could not create item", Toast.LENGTH_LONG).show();
                                        });
                                    }
                                });
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void addNewList() {
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setPadding(50, 30, 50, 30);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enter the key of a shopping list ...")
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getListFromServer("" + input.getText());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void getListFromServer(String key) {
        System.out.println("trying to retrieve data from server by key:" + key);

        dataAccess.getShoppingListForId(key).enqueue(
                new ApolloCall.Callback<GetShoppingListQuery.Data>() {

                    public void handleError() {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(1000);
                        }
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Could not find list", Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Response<GetShoppingListQuery.Data> response) {
                        List<ShoppingList> shoppingLists = new ArrayList<>();

                        if (response.getErrors() != null || response.getData() == null) {
                            handleError();
                            return;
                        }

                        ShoppingList list = new ShoppingList(
                                response.getData().shoppingList().fragments().shoppingList().name(),
                                new ArrayList<>(),
                                response.getData().shoppingList().fragments().shoppingList().id()
                        );
                        shoppingLists.add(list);
                        prefs.edit().putString(
                                response.getData().shoppingList().fragments().shoppingList().id(),
                                response.getData().shoppingList().fragments().shoppingList().name()).apply();

                        runOnUiThread(() -> {
                            updateFragments(shoppingLists);
                        });

                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(300);
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
