package com.example.shoppinglist.ui.model;

import java.io.Serializable;

public class ListItem implements Serializable {
    private String title;
    private String quantity;
    private boolean checked;
    private ShoppingList list;
    private int id;

    public ListItem(ShoppingList list, int id, String title, boolean checked, String quantity) {
        this.list = list;
        this.id = id;
        this.title = title;
        this.quantity = quantity;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public ShoppingList getList() {
        return list;
    }

    public int getId() {
        return id;
    }

}
