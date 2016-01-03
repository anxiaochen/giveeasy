package com.example.anxiao.giveeasy.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.anxiao.giveeasy.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class selectedCharity_activity extends Activity {

    String CharityTitle;
    String CharityDescription;
    String CharityCurrency;
    String Charityfacebook;
    String Charitytwitter;
    String CharityEmail;
    String Charityphone;
    String getSum;
    String getFrequency;
    int total=0;
    String firstValue;
    String secondValue;
    String thirdValue;


    TextView CharityName;
    TextView CharityDes;
    TextView CharityCurrencyView;
    TextView CharityFbtext;
    TextView CharityTwtext;
    TextView CharityEmailtext;
    TextView CharityPhonetext;
    EditText sumEdit;
    EditText frequencyEdit;

    final Context context = this;




    ArrayList<String> checkoutlist = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_charity_activity);



        ///Get the Parameters from Extras/////
        Bundle bundle = getIntent().getExtras();
        CharityTitle = bundle.getString("name");
        CharityDescription = bundle.getString("description");
        CharityCurrency = bundle.getString("currency");
        Charityfacebook = bundle.getString("facebook_url");
        Charitytwitter = bundle.getString("twitter_url");
        CharityEmail = bundle.getString("public_email");
        Charityphone = bundle.getString("phone");

        //set data to views
        CharityName =(TextView) findViewById(R.id.title);
        CharityName.setText(CharityTitle);

        CharityDes =(TextView) findViewById(R.id.description);
        CharityDes.setText(CharityDescription);

        CharityCurrencyView =(TextView) findViewById(R.id.currencyInfo);
        CharityCurrencyView.setText(CharityCurrency);

        CharityFbtext=(TextView) findViewById(R.id.facebooktext);
        CharityFbtext.setMovementMethod(LinkMovementMethod.getInstance());
        CharityFbtext.setText(Html.fromHtml(Charityfacebook));


        CharityTwtext = (TextView) findViewById(R.id.Twittertext);
        CharityTwtext.setText(Charitytwitter);

        CharityEmailtext=(TextView) findViewById(R.id.emailtext);
        CharityEmailtext.setText(CharityEmail);

        CharityPhonetext=(TextView) findViewById(R.id.phonetext);
        CharityPhonetext.setText(Charityphone);

        //get sum and frequency edittext data
        sumEdit=(EditText) findViewById(R.id.sumeditText);
        frequencyEdit=(EditText) findViewById(R.id.frequencyeditText);


        //Add button event
        final Button button = (Button) findViewById(R.id.addbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_view);
                dialog.setTitle("Check out/continue shop");

                //check out button event
                Button okButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSum = sumEdit.getText().toString();
                        getFrequency = frequencyEdit.getText().toString();
                        //for only once checkout
                        checkoutlist.add("Charity name: " + CharityTitle + "\n " + " your selected donated: " + getSum + "\n " + "your selected frequency: " + getFrequency + "/monthly");

                        //for multiple checkout condition:choose few items before and checkout in here.
                        //Restore sharedpreferences data
                        SharedPreferences sp = getSharedPreferences("easyGivePreferences", Context.MODE_PRIVATE);
                        Set<String> mynewStrings = sp.getStringSet("myStrings", new HashSet<String>());
                        final List<String> stringsList = new ArrayList<>(mynewStrings);

                        if (mynewStrings.size()==0) {
                            total = Integer.parseInt(getSum);
                        } else {
                            for (String src : stringsList) {
                                String[] parts = src.split(",");
                                if (parts.length == 3) {
                                    firstValue = parts[0]; //name
                                    secondValue = parts[1]; //sum
                                    thirdValue = parts[2]; //frequency
                                    total += Integer.parseInt(secondValue) + Integer.parseInt(getSum);

                                    checkoutlist.add("Charity name: " + firstValue + "\n " + " your selected donated: " + secondValue + "\n " + "your selected frequency: " + thirdValue + "/monthly");

                                }
                            }
                        }


                        Bundle bundle = new Bundle();
                        bundle.putInt("sum", total);
                        bundle.putStringArrayList("orderlist", checkoutlist);
                        Intent gotoCheckoutList = new Intent(selectedCharity_activity.this, checkout_activity.class);
                        gotoCheckoutList.putExtras(bundle);
                        startActivity(gotoCheckoutList);
                        // if button is clicked, close the custom dialog
                        dialog.dismiss();
                    }
                });

                //continue shop button event,back to charities listview
                Button continueButton = (Button) dialog.findViewById(R.id.dialogButtonContinue);
                continueButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("easyGivePreferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();

                        Set<String> myStrings = settings.getStringSet("myStrings", new HashSet<String>());
                        getSum  = sumEdit.getText().toString();
                        getFrequency  = frequencyEdit.getText().toString();
                        myStrings.add(CharityTitle + "," + getSum + "," + getFrequency);

                        editor.putStringSet("myStrings", myStrings);
                        editor.commit();

                        Intent backtoList= new Intent(selectedCharity_activity.this, MainActivity.class);
                        startActivity(backtoList);
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });
    }



}
