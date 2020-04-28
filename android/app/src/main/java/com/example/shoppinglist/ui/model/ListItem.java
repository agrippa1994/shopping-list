package com.example.shoppinglist.ui.model;

import java.io.Serializable;

public class ListItem implements Serializable {
    private String title;
    private String quantity;
    private boolean checked;

    public ListItem(String title, String quantity, boolean checked) {
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
}
