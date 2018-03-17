package com.leads.vf.vf_leads;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.leads.vf.vf_leads.utils.HttpUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AllLeads extends AppCompatActivity implements View.OnClickListener{
    private String dealerCode=null;
    private String[] leadIdArr=null;
    private String[] leadDateArr;
    private String[] isApprovedLeadArr;
    private String[] leadsMobileNumArr;
    private String[] subscriberNamesArr;
    private String[] managerNamesArr;
    private String selectedIsApproved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_leads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fetchAllLeadsDetails();

    }

    @Override
    public void onClick(View v){
        if(null!=leadIdArr){
            for(int itr=0;itr<leadIdArr.length;itr++){
                if(v.getId()==Integer.parseInt(leadIdArr[itr])){
                    selectedIsApproved=isApprovedLeadArr[itr];
                    fetchLeadDetails(leadIdArr[itr]);
                    break;
                }
            }

          }
    }

    private void fetchLeadDetails(String selectedLeadID) {
        RequestParams rp = new RequestParams();
        rp.add("appId","vf_leads_API2");
        rp.add("pwd","vf_leads_API2");
        rp.add("dealer_code",dealerCode);
        rp.add("lead_id",selectedLeadID);
        String phpName = "viewLeads.php";
        Log.d("req","Request is>>>"+rp);
        makeViewLeadsRestCall(rp,phpName);
    }

    private void makeViewLeadsRestCall(RequestParams rp, String phpName) {
        HttpUtils.post(phpName,rp, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
                Log.d("res","---------------response from host>>> "+res+"Status is"+statusCode);
                try{

                    String respMsg = res.getString("res");
                    int errCode = res.getInt("error_code");
                    Log.d("res","Response message is>>>>"+respMsg);
                    if(errCode==0)
                        showViewLeadsResp(res);
                    else
                        showValidationMsg(respMsg);

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
    private void showViewLeadsResp(JSONObject res) {
        try{
            Intent i = new Intent(getApplicationContext(),ViewLeads.class);
            i.putExtra("lead_id",res.getString("lead_id"));
            i.putExtra("dealer_code",res.getString("dealer_code"));
            i.putExtra("lead_date",res.getString("lead_date"));
            i.putExtra("cat",res.getString("cat"));
            i.putExtra("zone",res.getString("zone"));
            i.putExtra("manager_name",res.getString("manager_name"));
            i.putExtra("dist",res.getString("dist"));
            i.putExtra("dl_name",res.getString("dl_name"));
            i.putExtra("touch_point",res.getString("touch_point"));
            i.putExtra("subscriber_name",res.getString("subscriber_name"));
            i.putExtra("mobile_number",res.getString("mobile_number"));
            i.putExtra("count",res.getString("count"));
            i.putExtra("alt_number",res.getString("alt_number"));
            i.putExtra("plan",res.getString("plan"));
            i.putExtra("security_deposit",res.getString("security_deposit"));
            i.putExtra("capture_address_by_telecaller",res.getString("capture_address_by_telecaller"));
            i.putExtra("isApproved",selectedIsApproved);
            i.putExtra("remark",res.getString("remark"));
            i.putExtra("zone_feedback",res.getString("zone_feedback"));
            startActivity(i);
        }
        catch (Exception e){
            e.printStackTrace();
            showValidationMsg("Some error occurred");
        }
    }

    private void fetchAllLeadsDetails() {
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        dealerCode = sharedpreferences.getString("loggedInDealerCode","");
        RequestParams requestParams =new RequestParams();
        requestParams.add("appId", "vf_leads_API4");
        requestParams.add("pwd", "vf_leads_API4");
        requestParams.add("dealer_code",dealerCode);
        String phpName = "getAllLeads.php";
        Log.d("REQ","Request parameter are>>>>"+requestParams+">>"+phpName);
        makeFetchLeadsRestCall(requestParams,phpName);
    }

    private void makeFetchLeadsRestCall(RequestParams requestParams, String phpName) {

        HttpUtils.post(phpName, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject res) {
                Log.d("res", "---------------response from host>>> " + res + "Status is" + statusCode);
                try {

                    String respMsg = res.getString("res");
                    int errCode = res.getInt("error_code");
                    if(errCode==0){
                        String leadIds = (String)res.getString("lead_id");
                        String leadDates = (String)res.getString("lead_date");
                        String isApprovedLeads = (String)res.getString("is_approved");
                        String mobileNumbers= res.getString("mobile_number");
                        String subscriberNames= res.getString("subscriber_name");
                        String managerNames= res.getString("manager_name");
                        Log.d("res", "Response message is>>>>" + respMsg+"CatNames>>"+leadDates);
                        fetchingDetailsIntoTable(respMsg,errCode,leadIds,leadDates,isApprovedLeads
                                ,mobileNumbers,subscriberNames,managerNames);
                    }
                    else
                        showValidationMsg(respMsg);

                } catch (Exception e) {
                    e.printStackTrace();
                    showValidationMsg("Please check your internet and try again");
                        /*RelativeLayout relLayoutProgress = (RelativeLayout) findViewById(R.id.progressBarLayout);
                        relLayoutProgress.setVisibility(View.GONE);*/
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                Log.d("log", "Status code is>>" + statusCode + "Response code>>>" + responseString);
                showValidationMsg("Some Error occurred please try again");
                    /*RelativeLayout relLayoutProgress = (RelativeLayout) findViewById(R.id.progressBarLayout);
                    relLayoutProgress.setVisibility(View.GONE);*/
            }
        });

    }

    private void fetchingDetailsIntoTable(String respMsg, int errCode, String leadIds,
                                          String leadDates, String isApprovedLeads,
                                          String mobileNumbers, String subscriberNames,
                                          String managerNames) {
        leadIdArr=(leadIds).split("#");
        leadDateArr=(leadDates).split("#");
        isApprovedLeadArr=(isApprovedLeads).split("#");
        leadsMobileNumArr=(mobileNumbers).split("#");
        subscriberNamesArr=(subscriberNames).split("#");
        managerNamesArr=(managerNames).split("#");

        TableLayout tableLayout  = (TableLayout)findViewById(R.id.tableLayoutLeadsList);
        tableLayout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) AllLeads.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int itr=0;itr<leadIdArr.length;itr++){
            TableRow tableRow = new TableRow(AllLeads.this);
            View viewForTableRow = inflater.inflate(R.layout.lead_layout_for_table, null,false);
            TextView leadDateTV=(TextView)viewForTableRow.findViewById(R.id.leadDateTV);
            TextView leadMobileTV=(TextView)viewForTableRow.findViewById(R.id.leadMobileTV);
            TextView managerNameTV=(TextView)viewForTableRow.findViewById(R.id.managerNameTV);
            TextView subscriberNameTV=(TextView)viewForTableRow.findViewById(R.id.subscriberNameTV);
            leadDateTV.setText(leadDateArr[itr]);
            leadMobileTV.setText(leadsMobileNumArr[itr]);
            managerNameTV.setText(managerNamesArr[itr]);
            subscriberNameTV.setText(subscriberNamesArr[itr]);
            tableRow.addView(viewForTableRow);
            tableRow.setId(Integer.parseInt(leadIdArr[itr]));
            //findViewById(Integer.parseInt(leadIdArr[itr])).setOnClickListener(this);
            TableLayout.LayoutParams tableRowParams=
                    new TableLayout.LayoutParams
                            (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
            int leftMargin=10;
            int topMargin=2;
            int rightMargin=10;
            int bottomMargin=5;
            tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            tableRow.setLayoutParams(tableRowParams);
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL);
            tableLayout.addView(tableRow);
        }
        makeRowsClickable();

    }

    private void makeRowsClickable() {
        for(String leadID:leadIdArr){
            findViewById(Integer.parseInt(leadID)).setOnClickListener(this);
        }
    }

    private void showValidationMsg(String message) {
        Toast.makeText(getApplicationContext(),message,
                Toast.LENGTH_LONG)
                .show();
    }

}
