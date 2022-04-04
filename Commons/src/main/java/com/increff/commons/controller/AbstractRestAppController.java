package com.increff.commons.controller;


import com.increff.commons.Data.MessageData;
import com.increff.commons.Exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
// change name to controller advice
public class AbstractRestAppController extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ApiException.class})
    @ResponseBody
    public MessageData handleApiException(HttpServletRequest req, ApiException e){
        MessageData data = new MessageData();
        data.setMessage(e.getMessage());
        return data;
    }

    @ExceptionHandler(RestClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageData handle(RestClientResponseException e) {
        MessageData data = new MessageData();
        data.setMessage(e.getResponseBodyAsString());
        return data;
    }



    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Throwable.class})
    @ResponseBody
    public MessageData handleUnknownException(HttpServletRequest req, Throwable t){
        t.setStackTrace(t.getStackTrace());
        MessageData data = new MessageData();
        data.setMessage("Internal error");
        return data;
    }


}

