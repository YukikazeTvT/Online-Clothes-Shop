package com.he172006.onlineclothesshop.component;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;




import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.he172006.onlineclothesshop.HomeActivity;
import com.he172006.onlineclothesshop.R;




public class HeaderComponent {




    public static void setupHeader(Activity activity) {
        ImageView iconSearch = activity.findViewById(R.id.iconSearch);
        ImageView iconProfile = activity.findViewById(R.id.iconProfile);
        ImageView iconMenu = activity.findViewById(R.id.iconMenu);
        ImageView logo = activity.findViewById(R.id.logoImage);


        iconProfile.setOnClickListener(v -> {
            Toast.makeText(activity, "Profile clicked!", Toast.LENGTH_SHORT).show();
        });


        logo.setOnClickListener(v -> {
            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });
        iconMenu.setOnClickListener(v -> showPopupMenu(activity, v));
        iconSearch.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            View fragmentContainer = activity.findViewById(R.id.fragment_container);
            View homeContent = activity.findViewById(R.id.homeContent);


            if (fragmentContainer.getVisibility() == View.VISIBLE) {
                fragmentManager.popBackStack();
                fragmentContainer.setVisibility(View.GONE);
                homeContent.setVisibility(View.VISIBLE);
            } else {
                homeContent.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);


                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new Search());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });




    }




    private static void showPopupMenu(Activity activity, View view) {
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());




        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_sort) {
                showSortMenu(activity, view);
                return true;
            } else if (id == R.id.menu_logout) {
                Toast.makeText(activity, "Logout Clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });




        popup.show();
    }




    private static void showSortMenu(Activity activity, View view) {
        PopupMenu popup = new PopupMenu(activity, view);
        popup.getMenuInflater().inflate(R.menu.menu_sort, popup.getMenu());




        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();




            if (activity instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) activity;
                if (id == R.id.sort_price_low_high) {
                    homeActivity.sortProducts(true);
                    return true;
                } else if (id == R.id.sort_price_high_low) {
                    homeActivity.sortProducts(false);
                    return true;
                }
            }
            return false;
        });




        popup.show();
    }




}








