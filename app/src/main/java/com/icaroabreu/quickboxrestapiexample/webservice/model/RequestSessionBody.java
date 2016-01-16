package com.icaroabreu.quickboxrestapiexample.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RequestSessionBody {

    private String signature;

    private String auth_key;

    private int application_id;

    private int nonce;

    private long timestamp;

    private UserAuthCredentials user;
}
