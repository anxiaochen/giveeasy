package com.example.anxiao.giveeasy.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.anxiao.giveeasy.R;
import com.example.anxiao.giveeasy.adapter.CustomListAdapter;
import com.example.anxiao.giveeasy.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.anxiao.giveeasy.model.charity;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivity extends Activity implements SearchView.OnQueryTextListener,AdapterView.OnItemClickListener, View.OnClickListener {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "https://edheroz.com/api/v2/charities.json";
    private ProgressDialog pDialog;
    private List<charity> charityList = new ArrayList<charity>();
    private ListView listView;
    private CustomListAdapter adapter;
    TextView logouttext;
    TextView usernametext;

    JSONArray charitiesArray = null;
    JsonObjectRequest charityReq;


    private SearchView mSearchView;

    int total=0;
    String firstValue;
    String secondValue;
    String thirdValue;
    ArrayList<String> checkoutlist = new ArrayList<String>();

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_charities_list);



        FacebookSdk.sdkInitialize(this.getApplicationContext());

        listView = (ListView) findViewById(R.id.charity_list);
        listView.setTextFilterEnabled(true);
        adapter = new CustomListAdapter(this, charityList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        final Button paybtn = (Button) findViewById(R.id.paybtn);
        final Button button = (Button) findViewById(R.id.sortbtn);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        //show username
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            username = profile.getName();
        }
        usernametext = (TextView) findViewById(R.id.usernmelabel);
        usernametext.setText("Welcome: "+username);

        //logout text click listener
        logouttext = (TextView)findViewById(R.id.logout);
        logouttext.setOnClickListener(this);

        // Creating volley request obj
        charityReq = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    // Getting JSON Array node
                    charitiesArray = response.getJSONArray("charities");

                    //parent node
                    for (int i = 0; i < charitiesArray.length(); i++) {


                        JSONObject obj = charitiesArray.getJSONObject(i);

                        charity c = new charity();
                        c.setTitle(obj.getString("name"));
                        c.setDesc(obj.getString("description"));
                        //c.setThumbnailUrl(obj.getString("logo_url")); //image access denied
                        c.setThumbnailUrl("http://wp.givingtuesday.ca/app/uploads/2014/11/give-icon-yellow.png");
                        //contact information
                        c.setTwitter(obj.getString("twitter_url"));
                        c.setFacebook(obj.getString("facebook_url"));
                        c.setEmail(obj.getString("public_email"));
                        c.setPhone(obj.getString("phone"));


                        JSONObject currencyobj = obj.getJSONObject("currency");
                        String Isocode = currencyobj.getString("iso_code");
                        String Isoname = currencyobj.getString("name");
                        String Isosymbol = currencyobj.getString("symbol");
                        c.setlCurrency(Isocode+" "+Isoname+" "+Isosymbol);

                        // / adding all data to charity array
                        charityList.add(c);
                        adapter.notifyDataSetChanged();
                    }
                    hidePDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }//end response

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hidePDialog();
            }
        });

        //Set a retry policy in case of SocketTimeout & ConnectionTimeout Exceptions.
        //Volley does retry for you if you have specified the policy.
        charityReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(charityReq);



        //Restore sharedpreferences charity data
        SharedPreferences sp = getSharedPreferences("easyGivePreferences", Context.MODE_PRIVATE);
        Set<String> myStrings = sp.getStringSet("myStrings", new HashSet<String>());

         final List<String> stringsList = new ArrayList<>(myStrings);

         for (String src : stringsList) {
            String[] parts = src.split(",");
            if (parts.length == 3) {
                firstValue = parts[0]; //name
                secondValue = parts[1]; //sum
                thirdValue = parts[2]; //frequency
                total += Integer.parseInt(secondValue);

                checkoutlist.add("Charity name: "+firstValue+"\n "+ " your selected donated: "+secondValue+"\n "+"your selected frequency: "+thirdValue+"/monthly");

            }
        }



        //sort by name
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Collections.sort(charityList, new Comparator<charity>() {

                    @Override
                    public int compare(final charity object1, final charity object2) {
                        return object1.getTitle().compareToIgnoreCase(object2.getTitle());
                    }
                });
                adapter = new CustomListAdapter(MainActivity.this, charityList);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

        //check is checkout image or empty image
        if(checkoutlist.size()>0 && total>=0) {
        paybtn.setBackgroundResource(R.drawable.full_cart);
        }else{
            paybtn.setBackgroundResource(R.drawable.empty_cart);
        }

        //checkout button listener
        paybtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("sum", total);
                bundle.putStringArrayList("orderlist", checkoutlist);


                if(checkoutlist.size()>0 && total>=0) {
                    Intent checkoutactivity = new Intent(MainActivity.this, checkout_activity.class);
                    checkoutactivity.putExtras(bundle);
                    startActivity(checkoutactivity);
                }else{
                    Toast.makeText(MainActivity.this, "You don't select any charity!", Toast.LENGTH_LONG).show();
                }
            }
        });


    }//oncreate()


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {

            charity selectedCharity = (charity)parent.getAdapter().getItem(position);

            Intent intent = new Intent(view.getContext(), selectedCharity_activity.class);

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putString("name", selectedCharity.getTitle());
            bundle.putString("description", selectedCharity.getDesc());
            bundle.putString("currency", selectedCharity.getlCurrency());
            bundle.putString("twitter_url", selectedCharity.getTwitter());
            bundle.putString("facebook_url", selectedCharity.getFacebook());
            bundle.putString("public_email", selectedCharity.getEmail());
            bundle.putString("phone", selectedCharity.getPhone());


            intent.putExtras(bundle);
            startActivity(intent);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    @Override
    public boolean onQueryTextChange(String newText)
    {

        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }


    @Override
    public void onClick(View v){
        switch(v.getId()){
            //logout
            case R.id.logout:
                LoginManager.getInstance().logOut();
                Intent backtologin=new Intent(MainActivity.this, Facebook_Login.class);
                startActivity(backtologin);

            default:
                break;
        }


    }



    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}
