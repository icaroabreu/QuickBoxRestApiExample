package com.icaroabreu.quickboxrestapiexample.webservice.model;

import lombok.Data;

@Data
public class User {

    private int id;

    private String website;

    private String last_request_at;

    private String blob_id;

    private int owner_id;

    private String created_at;

    private String login;

    private int external_user_id;

    private String facebook_id;

    private String user_tags;

    private String full_name;

    private String updated_at;

    private String phone;

    private String twitter_id;

    private String custom_data;

    private String email;
}
