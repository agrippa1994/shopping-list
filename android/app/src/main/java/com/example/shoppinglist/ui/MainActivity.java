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

import com.example.shoppinglist.R;
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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    ShoppingListsFragment shoppingListFragment;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = findViewById(R.id.fab);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.drawable.ic_shopping_cart_small);
        }
        if(getIntent().getData() != null){
            String key = getIntent().getData().getQueryParameter("key");
            getListFromServer(key);
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
                /*
                List<ListItem> li1 = new ArrayList<>();
                li1.add(new ListItem("Apples", "2x", false));
                li1.add(new ListItem("Meat", "2kg", false));
                li1.add(new ListItem("Fish", "", false));
                ShoppingList l1 = new ShoppingList("TestList1", li1, randomString(8));
                shoppingLists.add(l1);
                List<ListItem> li2 = new ArrayList<>();
                li2.add(new ListItem("Apples", "5kg", false));
                li2.add(new ListItem("Meat", "all of it", false));
                li2.add(new ListItem("Fish", "2 Filets", false));
                ShoppingList l2 = new ShoppingList("TestList2", li2, randomString(8));
                shoppingLists.add(l2);
                List<ListItem> li3 = new ArrayList<>();
                ShoppingList l3 = new ShoppingList("TestList3", li3, randomString(8));
                shoppingLists.add(l3);
                    */
                runOnUiThread(() -> {
                    updateFragments(shoppingLists);
                });
            }).start();


        new Thread(() -> {
            runOnUiThread(() -> {
                ShoppingList list = (ShoppingList) getIntent().getSerializableExtra("ShoppingListToRemove");
                if(list != null){
                    removeShoppingList(list);
                }
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

    private void removeShoppingList(ShoppingList list) {
        shoppingListFragment.removeList(list);
        prefs.edit().remove(list.getKey()).apply();
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
        input.setPadding(50,30,50,30);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enter the title of a new shopping list ...")
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        List<ShoppingList> shoppingLists = new ArrayList<>();
                        List<ListItem> li1 = new ArrayList<>();
                        String randomKey = randomString(8);
                        ShoppingList l1 = new ShoppingList(""+input.getText(), li1 , randomKey);
                        shoppingLists.add(l1);
                        runOnUiThread(() -> {
                            updateFragments(shoppingLists);
                        });
                        prefs.edit().putString(randomKey, input.getText().toString()).apply();
                        //TODO (mani) create new list on server/db
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
        input.setPadding(50,30,50,30);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Enter the key of a shopping list ...")
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                             getListFromServer(""+input.getText());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void getListFromServer(String key) {
        System.out.println("trying to retrieve data from server by key:"+key);
        List<ShoppingList> shoppingLists = new ArrayList<>();
        //TODO (mani) get shopping list from server/db with key
        runOnUiThread(() -> {
            updateFragments(shoppingLists);
        });
        //TODO (mani) if found by key - vibrate & maybe vibrate longer when not found
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(300);
        }
        //TODO (mani) insert key from server
        String title = "";

        prefs.edit().putString(key.toString(), title).apply();


    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    String randomString( int len ){
        //TODO (mani) check with server with the key in unique
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }


}
