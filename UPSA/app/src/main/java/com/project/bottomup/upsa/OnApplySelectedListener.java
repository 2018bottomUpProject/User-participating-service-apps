package com.project.bottomup.upsa;

import android.widget.CheckBox;

import java.util.ArrayList;

public interface OnApplySelectedListener {
    public boolean postCategory(String category);
    public boolean postPlaceInfo(String extraContent);
    public boolean postPlaceCheck(CheckBox[] toilet, CheckBox[] parking);
    public boolean postMenuInfo(ArrayList<MenuInfo> menuInfo);
    public boolean postReview(String content);
}
