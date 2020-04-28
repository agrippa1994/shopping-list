package com.example.shoppinglist.ui.data_access;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloMutationCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;
import com.example.shoppinglist.AddItemMutation;
import com.example.shoppinglist.CreateShoppingListMutation;
import com.example.shoppinglist.DeleteItemMutation;
import com.example.shoppinglist.GetShoppingListQuery;
import com.example.shoppinglist.ListUpdatedSubscription;
import com.example.shoppinglist.UpdateItemMutation;
import com.example.shoppinglist.ui.model.ListItem;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class DataAccess {

    private static final String BASE_URL = "https://shopping.mani94.space/graphql";
    private static final String SUBSCRIPTION_BASE_URL = "wss://shopping.mani94.space/graphql";

    private ApolloClient apolloClient;


    public DataAccess() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        this.apolloClient = ApolloClient.builder()
                .okHttpClient(okHttpClient)
                .subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory(SUBSCRIPTION_BASE_URL, okHttpClient))
                .subscriptionHeartbeatTimeout(5, TimeUnit.SECONDS)
                .serverUrl(BASE_URL)
                .build();
    }

    public ApolloQueryCall<GetShoppingListQuery.Data> getShoppingListForId(String id) {
        return this.apolloClient.query(
                GetShoppingListQuery.builder()
                .listId(id)
                .build()
        );
    }

    public ApolloMutationCall<CreateShoppingListMutation.Data> createShoppingList(String name) {
        return this.apolloClient.mutate(
                CreateShoppingListMutation.builder()
                .name(name)
                .build()
        );
    }

    public ApolloSubscriptionCall<ListUpdatedSubscription.Data> subscribeListChanges(String id) {
        return this.apolloClient.subscribe(
                ListUpdatedSubscription.builder()
                .listId(id)
                .build()
        );
    }

    public ApolloMutationCall<UpdateItemMutation.Data> updateShoppingListItem(ListItem item) {
        return this.apolloClient.mutate(
                UpdateItemMutation.builder()
                .checked(item.isChecked())
                .id(item.getId())
                .listId(item.getList().getKey())
                .name(item.getTitle())
                .quantity(item.getQuantity())
                .build()
        );
    }

    public ApolloMutationCall<AddItemMutation.Data> insertShoppingListItem(ListItem item) {
        return this.apolloClient.mutate(
                AddItemMutation.builder()
                .listId(item.getList().getKey())
                .name(item.getTitle())
                .quantity(item.getQuantity())
                .build()
        );
    }

    public ApolloMutationCall<DeleteItemMutation.Data> deleteShoppingListItem(ListItem item) {
        return this.apolloClient.mutate(
                DeleteItemMutation.builder()
                .listId(item.getList().getKey())
                .id(item.getId())
                .build()
        );
    }
}
