package com.example.shoppinglist.ui.data_access;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloMutationCall;
import com.apollographql.apollo.ApolloQueryCall;
import com.example.shoppinglist.CreateShoppingListMutation;
import com.example.shoppinglist.GetShoppingListQuery;

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
}
