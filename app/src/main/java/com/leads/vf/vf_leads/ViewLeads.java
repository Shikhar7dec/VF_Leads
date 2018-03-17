package com.leads.vf.vf_leads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.leads.vf.vf_leads.utils.HttpUtils;
import com.leads.vf.vf_leads.utils.ValidationUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class ViewLeads extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    private static String[]  isApprovedArr ={"Select Status","Closed","Not Closed"};
    private static String[]  remarkArr ={"Select Remark","Postponed","Ringing","Not Interested"};
    private String leadID;
    private String dealerCode;
    private Spinner remarkSpinner;
    Spinner isApprovedSpinner;
    private String selectedStatus="";
    private String selectedRemark="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_leads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(null!=getSupportActionBar())
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        puttingValuesInTextField(extras);
        findViewById(R.id.updateBtn).setOnClickListener(this);
        findViewById(R.id.resetBtn).setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.updateBtn:
                prepareDataForUpdate();
                break;
            case R.id.resetBtn:
                resetFields();
                break;
        }
    }

    private void resetFields() {
        EditText zoneFeedbackEditText = (EditText)findViewById(R.id.zoneFeedbackEditText);
        zoneFeedbackEditText.setText("");
        remarkSpinner=(Spinner)findViewById(R.id.remarkSpinner);
        isApprovedSpinner=(Spinner)findViewById(R.id.isApprovedSpinner);
        isApprovedSpinner.setSelection(0);
        remarkSpinner.setSelection(0);
        remarkSpinner.setEnabled(false);
        selectedStatus="";
        selectedRemark="";

    }

    private void prepareDataForUpdate() {
        EditText zoneFeedbackEditText = (EditText)findViewById(R.id.zoneFeedbackEditText);

        boolean areAllFieldsEmpty = ValidationUtil.isEmptyTextField(zoneFeedbackEditText);
        if(!areAllFieldsEmpty){
            if(!"".equalsIgnoreCase(selectedStatus)){
                RequestParams rp = new RequestParams();
                rp.add("appId","vf_leads_API3");
                rp.add("pwd","vf_leads_API3");
                rp.add("dealer_code",dealerCode);
                rp.add("lead_id",leadID);
                rp.add("is_approved",selectedStatus);
                rp.add("remark",selectedRemark);
                rp.add("zone_feedback",zoneFeedbackEditText.getText().toString());
                String phpName = "updateLeads.php";
                Log.d("req","Request is>>>"+rp);
                makeUpdateRestCall(rp,phpName);
            }
            else{
                showValidationMsg("Please Select Status");
            }
        }
        else{
            showValidationMsg("Please enter Zone feedback the fields");
        }

    }

    private void makeUpdateRestCall(RequestParams rp, String phpName) {
        HttpUtils.post(phpName,rp, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
                Log.d("res","---------------response from host>>> "+res+"Status is"+statusCode);
                try{

                    String respMsg = res.getString("res");
                    int errCode = res.getInt("error_code");
                    //String userID = (String)res.getString("dealer_code");
                    Log.d("res","Response message is>>>>"+respMsg);
                    showValidationMsg(respMsg);
                    if(errCode==0)
                        showUpdateResp();
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

    private void showUpdateResp() {
        Intent i = new Intent(this,WelcomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void showValidationMsg(String message) {
        Toast.makeText(getApplicationContext(),message,
                Toast.LENGTH_LONG)
                .show();
    }

    private void puttingValuesInTextField(Bundle extras) {
        if(extras!=null){
            String leadDate;
            String cat;
            String zone;
            String managerName;
            String dist;
            String dlName;
            String touchPoint;
            String subscriberName;
            String mobNumber;
            String count;
            String altNumber;
            String plan;
            String secDeposit;
            String cABTC;
            String isApproved;
            String remark;
            String zoneFeedback;

            leadID = extras.getString("lead_id");
            dealerCode = extras.getString("dealer_code");
            leadDate = extras.getString("lead_date");
            cat = extras.getString("cat");
            zone = extras.getString("zone");
            managerName = extras.getString("manager_name");
            dist = extras.getString("dist");
            dlName = extras.getString("dl_name");
            touchPoint = extras.getString("touch_point");
            subscriberName = extras.getString("subscriber_name");
            mobNumber = extras.getString("mobile_number");
            count = extras.getString("count");
            altNumber = extras.getString("alt_number");
            plan = extras.getString("plan");
            secDeposit = extras.getString("security_deposit");
            cABTC = extras.getString("capture_address_by_telecaller");
            isApproved=extras.getString("isApproved");
            remark = extras.getString("remark");
            zoneFeedback=extras.getString("zone_feedback");
            TextView dealerCodeText = (TextView)findViewById(R.id.dealerCodeText);
            TextView leadDateText = (TextView)findViewById(R.id.leadDateText);
            TextView categoryText = (TextView)findViewById(R.id.categoryText);
            TextView zoneText = (TextView)findViewById(R.id.zoneText);
            TextView mangerText = (TextView)findViewById(R.id.mangerText);
            TextView distText = (TextView)findViewById(R.id.distText);
            TextView dlNameText = (TextView)findViewById(R.id.dlNameText);
            TextView touchPointText = (TextView)findViewById(R.id.touchPointText);
            TextView subscriberNameText = (TextView)findViewById(R.id.subscriberNameText);
            TextView mobileNumberText = (TextView)findViewById(R.id.mobileNumberText);
            TextView countText = (TextView)findViewById(R.id.countText);
            TextView altNumberText = (TextView)findViewById(R.id.altNumberText);
            TextView planText = (TextView)findViewById(R.id.planText);
            TextView secDepositText = (TextView)findViewById(R.id.secDepositText);
            TextView cABTCText = (TextView)findViewById(R.id.cABTCText);
            EditText isApprovedEditText = (EditText)findViewById(R.id.isApprovedEditText);
            EditText remarkEditText = (EditText)findViewById(R.id.remarkEditText);
            EditText zoneFeedbackEditText = (EditText)findViewById(R.id.zoneFeedbackEditText);
            Button updateButton = (Button)findViewById(R.id.updateBtn);
            Button resetButton = (Button)findViewById(R.id.resetBtn);
            dealerCodeText.setText(dealerCode);
            leadDateText.setText(leadDate);
            categoryText.setText(cat);
            zoneText.setText(zone);
            mangerText.setText(managerName);
            distText.setText(dist);
            dlNameText.setText(dlName);
            touchPointText.setText(touchPoint);
            subscriberNameText.setText(subscriberName);
            mobileNumberText.setText(mobNumber);
            countText.setText(count);
            altNumberText.setText(altNumber);
            planText.setText(plan);
            secDepositText.setText(secDeposit);
            cABTCText.setText(cABTC);
            remarkSpinner=(Spinner)findViewById(R.id.remarkSpinner);
            isApprovedSpinner=(Spinner)findViewById(R.id.isApprovedSpinner);
            if(isApproved!=null&&!"none".equalsIgnoreCase(isApproved)){
                isApprovedEditText.setText(isApproved);
                remarkEditText.setText(remark);
                zoneFeedbackEditText.setText(zoneFeedback);
                isApprovedEditText.setEnabled(false);
                remarkEditText.setEnabled(false);
                zoneFeedbackEditText.setEnabled(false);
                updateButton.setVisibility(View.GONE);
                resetButton.setVisibility(View.GONE);
                isApprovedSpinner.setVisibility(View.GONE);
                remarkSpinner.setVisibility(View.GONE);
            }
            else{
                isApprovedEditText.setVisibility(View.GONE);
                remarkEditText.setVisibility(View.GONE);
                isApprovedSpinner.setVisibility(View.VISIBLE);
                remarkSpinner.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewLeads.this,
                        R.layout.spinner_item,isApprovedArr);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                isApprovedSpinner.setAdapter(adapter);
                isApprovedSpinner.setOnItemSelectedListener(this);
                ArrayAdapter<String> remarkAdapter=new ArrayAdapter<String>(ViewLeads.this,
                        R.layout.spinner_item,remarkArr);
                remarkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                remarkSpinner.setAdapter(remarkAdapter);
                remarkSpinner.setOnItemSelectedListener(this);
                isApprovedSpinner.setSelection(0);
                remarkSpinner.setSelection(0);
                remarkSpinner.setEnabled(false);
            }


        }
    }
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if(0!=position){
            Spinner s = (Spinner)parent;
            if(s.getId()==R.id.isApprovedSpinner){
                remarkSpinner=(Spinner)findViewById(R.id.remarkSpinner);
                selectedStatus=isApprovedArr[position];
                    if(position==2){
                        remarkSpinner.setEnabled(true);
                    }
                    if(position==1){
                        selectedRemark="";
                        remarkSpinner.setEnabled(false);
                        remarkSpinner.setSelection(0);
                    }
            }
            if(s.getId()==R.id.remarkSpinner){
                selectedRemark=remarkArr[position];
            }

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
