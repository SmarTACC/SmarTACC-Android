package com.ort.smartacc.net;

public class HttpResponse {
    public int code;
    public String response;

    public HttpResponse(int code, String response){
        this.code = code;
        this.response = response;
    }
}
