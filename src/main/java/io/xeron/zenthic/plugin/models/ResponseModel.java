package io.xeron.zenthic.plugin.models;

import org.json.simple.JSONObject;

public class ResponseModel {
    public String code;
    public String message;
    public JSONObject data;


    public ResponseModel(String code, String message, JSONObject data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
