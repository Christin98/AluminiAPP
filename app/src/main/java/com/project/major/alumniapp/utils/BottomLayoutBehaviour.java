package com.project.major.alumniapp.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class BottomLayoutBehaviour extends CoordinatorLayout.Behavior<BottomNavigationView> {
   private int height;

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull BottomNavigationView child, int layoutDirection) {
        height = child.getHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (dyConsumed > 0) {
            hideBottomNavigationView(child);
        } else if (dyConsumed < 0) {
            showBottomNavigationView(child);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                                  @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy < 0){
            showBottomNavigationView(child);
        }else if (dy > 0){
            hideBottomNavigationView(child);
        }
    }

    private void hideBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(height).setDuration(200);
    }

    private void showBottomNavigationView(BottomNavigationView view) {
        view.animate().translationY(0).setDuration(200);
    }
}
