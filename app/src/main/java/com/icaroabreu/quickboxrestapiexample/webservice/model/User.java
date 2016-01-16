package com.icaroabreu.quickboxrestapiexample.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class User {

    private String website;

    private String last_request_at;

    private String blob_id;

    private String owner_id;

    private String created_at;

    private String login;

    private String external_user_id;

    private String facebook_id;

    private String user_tags;

    private String full_name;

    private String updated_at;

    private String phone;

    private String twitter_digits_id;

    private String id;

    private String twitter_id;

    private String custom_data;

    private String email;

    private String tag_list;
}
