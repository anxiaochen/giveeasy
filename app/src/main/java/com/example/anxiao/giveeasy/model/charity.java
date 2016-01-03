package com.example.anxiao.giveeasy.model;

import java.util.ArrayList;

/**
 * Created by anxiao on 12/29/15.
 */


public class charity implements Comparable{

    private String name, desc, thumbnailUrl, twitter, facebook, email, phone;
    private String currency;
    private int charityID;




    public charity(){

    }

    public charity(String name, String thumlUrl, String description, String curr, String twitter, String fb, String mail, String telphone, int id) {
        this.name = name;
        this.thumbnailUrl = thumlUrl;
        this.desc = description;
        this.currency = curr;
        this.charityID = id;
        this.twitter = twitter;
        this.facebook = fb;
        this.email = mail;
        this.phone = telphone;
    }

    public String getTitle() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String DesInfo) {
        this.desc = DesInfo;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getlCurrency() {
        return currency;
    }

    public void setlCurrency(String currency1) {
        this.currency = currency1;
    }

    public int getCharityID(){
        return charityID;
    }

    public String getTwitter(){
        return twitter;
    }

    public void setTwitter(String twitter1){
        this.twitter = twitter1;
    }

    public String getFacebook(){
        return facebook;
    }

    public void setFacebook(String facebook1){
        this.facebook = facebook1;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email1){
        this.email = email1;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone1){
        this.phone = phone1;
    }


    @Override
    public int compareTo(Object o) {
        charity f = (charity)o;
        return f.getTitle().compareToIgnoreCase(((charity) o).getTitle());
    }

}
