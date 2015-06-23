package com.lakj.comspace.simpletextclient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zardosht on 06/17/2015.
 */

public class JsonChiller {
    String strJson = "{\"Status\": {" +
            "\"CHWOutLetTemp\": \"0.0\"," +
            "\"HTWOutLetTemp\": \"0.0\"," +
            "\"RefTemp\": \"0.0\"," +
            "\"ValvePosition\": \"0.0\"," +
            "\"CHWInLetTemp\": \"0.0\"," +
            "\"HTWInLetTemp\": \"0.0\"," +
            "\"COWOutLetTemp\": \"0.0\"," +
            "\"CowInLetTemp\": \"0.0\"," +
            "\"DilutionTemp\": \"0.0\"," +
            "\"SolutionTemp\": \"0.0\"," +
            "\"ExhaustTemp\": \"0.0\"" +
            "  }," +
            "\"Setting\":{" +
            "\"CHWOutLetTemp\": \"0.0\"," +
            "\"HTWOutLetTemp\": \"0.0\"," +
            "\"RefTemp\": \"0.0\"," +
           "\"ValvePosition\": \"0.0\"," +
            "\"CHWInLetTemp\": \"0.0\"," +
            "\"HTWInLetTemp\": \"0.0\"," +
            "\"COWOutLetTemp\": \"0.0\"," +
            "\"CowInLetTemp\": \"0.0\"," +
            "\"DilutionTemp\": \"0.0\"," +
            "\"SolutionTemp\": \"0.0\"," +
            "\"ExhaustTemp\": \"0.0\"" +
            "}}";
    public JSONObject jsonRootObject;
    public JsonChiller(){
        try {
            jsonRootObject = new JSONObject(strJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




}
