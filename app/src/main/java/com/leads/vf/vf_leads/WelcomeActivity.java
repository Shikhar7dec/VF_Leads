package com.leads.vf.vf_leads;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{
    private int backButtonCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setWelcomeText();
        findViewById(R.id.viewLeadsButton).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_logout){
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.viewLeadsButton:
                toAllLeadsActivity();
               break;
        }
    }

    private void toAllLeadsActivity() {
        Intent i = new Intent(getApplicationContext(),AllLeads.class);
        startActivity(i);
    }

    private void setWelcomeText() {
        TextView welcomeText =(TextView)findViewById(R.id.welcomeTextView);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String dealerCode = sharedpreferences.getString("loggedInDealerCode","");
        welcomeText.setText(String.format("Welcome %s", dealerCode));

    }
    /*private void showValidationMsg(String message) {
        Toast.makeText(getApplicationContext(),message,
                Toast.LENGTH_LONG)
                .show();
    }*/

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

}
