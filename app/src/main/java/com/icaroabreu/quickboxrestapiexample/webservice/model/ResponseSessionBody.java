package com.icaroabreu.quickboxrestapiexample.webservice.model;

import lombok.Data;

@Data
public class ResponseSessionBody {

    private int device_id;

    private String updated_at;

    private int user_id;

    private String created_at;

    private String _id;

    private int id;

    private int application_id;

    private int nonce;

    private String token;

    private int ts;
}
