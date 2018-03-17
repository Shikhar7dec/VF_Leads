package com.leads.vf.vf_leads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.leads.vf.vf_leads.utils.HttpUtils;
import com.leads.vf.vf_leads.utils.ValidationUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity implements View.OnClickListener{
   private int backButtonCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        String lastLogin = sharedpreferences.getString("loggedInDealerCode","");
        Log.d("DEB","last login is>>>"+lastLogin);
        if(null!=lastLogin&&!"".equals(lastLogin)){
            Intent i = new Intent(getApplicationContext(),WelcomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else{
            setContentView(R.layout.activity_login);
            findViewById(R.id.signInButton).setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.signInButton:
        prepareLoginRestCall();
                break;

        }

    }

    private void prepareLoginRestCall() {
        EditText dealerCodeText = (EditText)findViewById(R.id.loginDealerCodeText);
        EditText passwordText = (EditText)findViewById(R.id.loginDealerIDPassword);

        boolean areAllFieldsEmpty = ValidationUtil.isEmptyTextField(dealerCodeText)||
                                    ValidationUtil.isEmptyTextField(passwordText);

        if(!areAllFieldsEmpty){
            RequestParams rp = new RequestParams();
            rp.add("appId","vf_leads_API1");
            rp.add("pwd","vf_leads_API1");
            rp.add("dealer_code",dealerCodeText.getText().toString());
            rp.add("password",passwordText.getText().toString());
            String phpName = "loginUser.php";
            Log.d("req","Request is>>>"+rp);
            makeLoginRestCall(rp,phpName);


        }
        else{
            showValidationMsg("Please enter all the fields");
        }
    }

    private void makeLoginRestCall(RequestParams rp, String phpName) {
        HttpUtils.post(phpName,rp, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
                Log.d("res","---------------response from host>>> "+res+"Status is"+statusCode);
                try{

                    String respMsg = (String)res.getString("res");
                    int errCode = (Integer)res.getInt("error_code");
                    //String userID = (String)res.getString("dealer_code");
                    Log.d("res","Response message is>>>>"+respMsg);
                    showLoginResp(res,errCode);
                    //showValidationMsg(respMsg);

                }
                catch (Exception e){
                    e.printStackTrace();
                    showValidationMsg("Please check your internet and try again");
                    /*RelativeLayout relLayoutProgress = (RelativeLayout) findViewById(R.id.progressBarLayout);
                    relLayoutProgress.setVisibility(View.GONE);*/
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                Log.d("log","Status code is>>"+statusCode+"Response code>>>"+responseString);
                showValidationMsg("Some Error occurred please try again");
                /*RelativeLayout relLayoutProgress = (RelativeLayout) findViewById(R.id.progressBarLayout);
                relLayoutProgress.setVisibility(View.GONE);*/
            }
        });
    }

    private void showLoginResp(JSONObject res, int errCode) {
        try {
            if (0 == errCode) {
                String dealer_code = res.getString("dealer_code");
                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("loggedInDealerCode",dealer_code);
                editor.commit();
                Intent i = new Intent(getApplicationContext(),WelcomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                showValidationMsg(res.getString("res"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
            showValidationMsg("Some error occurred");
        }
    }

    private void showValidationMsg(String message) {
        Toast.makeText(getApplicationContext(),message,
                Toast.LENGTH_LONG)
                .show();
    }

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



