package com.tesla.assessment.service;
import java.util.ArrayList;
import java.util.List;

public class ErrorService {
    public final List<String> errorList = new ArrayList<>();
    public void addError(String error){
        errorList.add(error);
    }
    public List<String> getErrors() {
        return errorList;
    }

}
