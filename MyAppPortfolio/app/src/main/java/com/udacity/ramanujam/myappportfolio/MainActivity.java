package com.udacity.ramanujam.myappportfolio;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getPopularMovies(View view) {
        showToast("This button will launch my popular movies!");
    }

    public void getStockHawk(View view) {
        showToast("This button will launch the stock hawk!");
    }

    public void buildItBigger(View view) {
        showToast("This button will build it bigger!");
    }

    public void makeYourAppMaterial(View view) {
        showToast("This button will make it material!");
    }

    public void goUbiquitous(View view) {
        showToast("This button will go ubiquitous!");
    }

    public void getCapstone(View view) {
        showToast("This button will launch Capstone");
    }

    private void showToast(String text) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
