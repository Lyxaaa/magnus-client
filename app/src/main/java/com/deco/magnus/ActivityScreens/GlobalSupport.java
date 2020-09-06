package com.deco.magnus.ActivityScreens;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class GlobalSupport {
    boolean active = false;
    public void setSupportBarActive(ActionBar bar, boolean active) {
        this.active = active;
        bar.setDisplayHomeAsUpEnabled(active);
        bar.setDisplayShowHomeEnabled(active);
    }
}
