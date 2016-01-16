package com.icaroabreu.quickboxrestapiexample.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAPI {

    private User user;

}
