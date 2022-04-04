package com.increff.commons.Util;

import com.increff.commons.Data.ErrorData;
import com.increff.commons.Exception.ApiException;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.increff.commons.Constants.ConstantNames.*;
import static com.increff.commons.Constants.ConstantNames.MAX_DAYS_DIFFERENCE;

public class ValidationUtil {

    public static void checkLimit(Long qty, String entity) throws ApiException {
        if(qty<=SEQ_MIN_VAL){
            throw new ApiException(entity+" should be greater than zero");
        }
        if(qty>SEQ_MAX_VAL){
            throw new ApiException(entity+" should be less than "+SEQ_MAX_VAL);
        }
    }

    public  static void checkDatesLimit(ZonedDateTime startDate, ZonedDateTime endDate) throws ApiException {
        if(startDate.isAfter(endDate)){
            throw new ApiException("Start Date should be before End Date");
        }
        if(ChronoUnit.DAYS.between(startDate, endDate)>MAX_DAYS_DIFFERENCE){
            throw new ApiException("Difference between Start Date and End Date must not exceed "+ MAX_DAYS_DIFFERENCE);
        }
    }

    public static void validateEmptyField(String fieldVal, String entity) throws ApiException {
        if(fieldVal.trim().isEmpty()){
            throw new ApiException(entity+" must not be empty");
        }
    }

    public static void validateEmptyFields(List<String> inList, String entity) throws ApiException {
        List<Long> indexes = new ArrayList<>();
        Long index = 1L;
        for(String obj:inList){
            if(obj.trim().isEmpty()){
                indexes.add(index);
            }
            index++;
        }
        if(indexes.size()>0){
            throw new ApiException(ErrorData.convert("Empty "+entity+" at indexes: ", indexes));
        }
    }


    public static <T>void checkDuplicates(List<T> inList, String entity) throws ApiException {
        List<T> objs = new ArrayList<>();
        Set<T> entities = new HashSet<>();
        for(T obj:inList){
            if(!entities.contains(obj)){
                entities.add(obj);
            }
            else{
                objs.add(obj);
            }
        }
        if(objs.size()>0){
            throw new ApiException(ErrorData.convert("Duplicate "+entity+ ": ", objs));
        }
    }
}
