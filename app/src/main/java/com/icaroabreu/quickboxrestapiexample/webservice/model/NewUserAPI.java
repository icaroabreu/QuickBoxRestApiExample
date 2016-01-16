package com.icaroabreu.quickboxrestapiexample.webservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class NewUserAPI {

    private NewUser user;

}
