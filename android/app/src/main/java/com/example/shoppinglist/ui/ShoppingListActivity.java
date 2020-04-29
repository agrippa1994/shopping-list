package com.example.shoppinglist.ui;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.shoppinglist.AddItemMutation;
import com.example.shoppinglist.GetShoppingListQuery;
import com.example.shoppinglist.ListUpdatedSubscription;
import com.example.shoppinglist.R;
import com.example.shoppinglist.ui.data_access.DataAccess;
import com.example.shoppinglist.ui.list_items.ShoppingItemFragment;
import com.example.shoppinglist.ui.model.ListItem;
import com.example.shoppinglist.ui.model.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class ShoppingListActivity extends AppCompatActivity {

    ShoppingItemFragment shoppingListFragment;
    ShoppingList shoppingList;
    DataAccess dataAccess;
    private ApolloSubscriptionCall<ListUpdatedSubscription.Data> subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataAccess = new DataAccess();
        shoppingList = (ShoppingList) getIntent().getSerializableExtra("ShoppingList");

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
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (subscription != null && !subscription.isCanceled()) {
            subscription.cancel();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        this.pullDataFromServer();
        this.handleUpdates();
    }

    private void pullDataFromServer() {
        dataAccess.getShoppingListForId(shoppingList.getKey())
                .enqueue(new ApolloCall.Callback<GetShoppingListQuery.Data>() {
                             private void handleFailure() {
                                 runOnUiThread(() -> {
                                     navigateBack();
                                     Toast.makeText(getApplicationContext(), "Could not load shopping list", Toast.LENGTH_LONG).show();
                                 });
                             }

                             @Override
                             public void onResponse(@NotNull Response<GetShoppingListQuery.Data> response) {
                                 if (response.getData() == null || response.getErrors() != null) {
                                     handleFailure();
                                     return;
                                 }

                                 List<com.example.shoppinglist.fragment.ShoppingList.Item> receivedItems = response.getData()
                                         .shoppingList()
                                         .fragments()
                                         .shoppingList()
                                         .items();


                                 List<ListItem> items = new ArrayList<>();
                                 for (com.example.shoppinglist.fragment.ShoppingList.Item item : receivedItems) {
                                     items.add(new ListItem(shoppingList, item.id(), item.name(), item.checked(), item.quantity()));
                                 }

                                 runOnUiThread(() -> {
                                     replaceFragments(items);
                                 });

                             }

                             @Override
                             public void onFailure(@NotNull ApolloException e) {
                                 handleFailure();
                             }
                         }
                );
    }

    public void updateFragments(List<ListItem> shoppingList) {
        shoppingListFragment.update(shoppingList);
    }

    public void replaceFragments(List<ListItem> shoppingList) {
        shoppingListFragment.replace(shoppingList);
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
            case R.id.copy_to_clipboard:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Key", shoppingList.getKey());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getApplicationContext(), "Key copied to Clipboard", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void disconnectFromList() {
        Intent intent = new Intent(ShoppingListActivity.this, MainActivity.class);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        prefs.edit().remove(shoppingList.getKey()).apply();
        startActivity(intent);
    }

    private void navigateBack() {
        Intent intent = new Intent(ShoppingListActivity.this, MainActivity.class);
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
                        ListItem item = new ListItem(shoppingList, 0, "" + input.getText(), false, Integer.parseInt(input2.getText().toString()));

                        dataAccess.insertShoppingListItem(item).enqueue(new ApolloCall.Callback<AddItemMutation.Data>() {
                            private void handleError() {
                                runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Could not add item", Toast.LENGTH_LONG);
                                });
                            }

                            @Override
                            public void onResponse(@NotNull Response<AddItemMutation.Data> response) {
                                if (response.getData() == null || response.getErrors() != null) {
                                    handleError();
                                    return;
                                }
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                handleError();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration mAppBarConfiguration = null;
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    public void addItemToList(MenuItem item) {
        addItemToList();
    }

    private void handleUpdates() {
        if (subscription != null && !subscription.isCanceled()) {
            subscription.cancel();
        }

        subscription = this.dataAccess.subscribeListChanges(shoppingList.getKey());
        subscription.execute(new ApolloSubscriptionCall.Callback<ListUpdatedSubscription.Data>() {

            public void reconnect() {
                // maybe add some sleep here?
                handleUpdates();
                pullDataFromServer();
            }

            @Override
            public void onResponse(@NotNull Response<ListUpdatedSubscription.Data> response) {
                if (response.getData() == null) {
                    return;
                }

                List<com.example.shoppinglist.fragment.ShoppingList.Item> receivedItems = response.getData()
                        .shoppingListUpdated()
                        .fragments()
                        .shoppingList()
                        .items();


                List<ListItem> items = new ArrayList<>();
                for (com.example.shoppinglist.fragment.ShoppingList.Item item : receivedItems) {
                    items.add(new ListItem(shoppingList, item.id(), item.name(), item.checked(), item.quantity()));
                }

                runOnUiThread(() -> {
                    replaceFragments(items);
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e("SUBSCRIPTION", "Connection failed - reconnect", e);
                reconnect();
            }

            @Override
            public void onCompleted() {
                Log.i("SUBSCRIPTION", "Connection completed - reconnect");
                reconnect();
            }

            @Override
            public void onTerminated() {
                Log.i("SUBSCRIPTION", "Connection terminated - reconnect");
                reconnect();
            }

            @Override
            public void onConnected() {
                Log.i("SUBSCRIPTION", "Connection established");
            }
        });
    }
}
