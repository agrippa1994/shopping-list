package com.example.shoppinglist.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ShoppingList implements Serializable  {
    private String title;
    private List<ListItem> items = new ArrayList<ListItem>();
    private String key;

    public ShoppingList(String title, List<ListItem> items, String key) {
        this.title = title;
        this.items = items;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ListItem> getItems() {
        return items;
    }

    public void setItems(List<ListItem> items) {
        this.items = items;
    }

    public void addItem(ListItem item) {
        items.add(item);
    }

    public void removeItem(ListItem item) {
        items.remove(item);
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
