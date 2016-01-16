package com.icaroabreu.quickboxrestapiexample.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class NewUser {

    private String password;

    private String website;

    private String full_name;

    private String phone;

    private String tag_list;

    private String login;

    private String twitter_id;

    private String email;

    private String external_user_id;

    private String facebook_id;
}
