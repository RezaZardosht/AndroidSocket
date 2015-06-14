package com.lakj.comspace.simpletextclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

//#define  MAX_PACK_LENGTH  60

/**
 * Created by Zardosht on 06/09/2015.
 */


public class DataRecieveHandle extends BroadcastReceiver {


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
         /*   "\"ValvePosition\": \"0.0\"," +
            "\"CHWInLetTemp\": \"0.0\"," +
            "\"HTWInLetTemp\": \"0.0\"," +
            "\"COWOutLetTemp\": \"0.0\"," +
            "\"CowInLetTemp\": \"0.0\"," +
            "\"DilutionTemp\": \"0.0\"," +
            "\"SolutionTemp\": \"0.0\"," +
          */  "\"ExhaustTemp\": \"0.0\"" +
            "}}";
    JSONObject jsonRootObject;

    public DataRecieveHandle() {
        try {
            jsonRootObject = new JSONObject(strJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public enum FollowLogic {
        RequestHeader,
        RequestBody,
        ResponseHeader,
        ResponseBody,
    }

    char[] RequestHeader = new char[10];
    char[] RequestBody = new char[100];
    char[] ResponseHeader = new char[10];
    char[] ResponseBody = new char[100];
    int RequestBodyLength = 0;
    int ResponseBodyLength = 0;
    int[] RecivedData = new int[100];
    int ConstReqPos = 0;
    SlimpleTextClientActivity main = null;
    FollowLogic PointFollowLogic = FollowLogic.RequestHeader;
    int MaxValueForCurrentFolloLogic = 7;

    void setMainActivityHandler(SlimpleTextClientActivity main) {
        this.main = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int i, j;
        String MyStr, MyStr2;
        MyStr2 = MyStr = "";
        byte[] bytes;
        bytes = intent.getByteArrayExtra("Data");
        int bytesLength = intent.getIntExtra("Data_Length", 0);
        for (i = 0; i < bytesLength; i++) {
            RecivedData[ConstReqPos++] = bytes[i];
            if (ConstReqPos < 7) continue;
            if (ConstReqPos > 60) {
                ConstReqPos = 0;
                continue;
            }
            //Todo check for all Request Type
            if (RecivedData[ConstReqPos - 7] == 0x02 && RecivedData[ConstReqPos - 6] == 0x7D && RecivedData[ConstReqPos - 5] == 0x65 &&
                    RecivedData[ConstReqPos - 4] == -1 && RecivedData[ConstReqPos - 3] == -1 &&
                    (RecivedData[ConstReqPos - 2] == -111 || RecivedData[ConstReqPos - 2] == -112) && RecivedData[ConstReqPos - 1] == 0x77) {
                for (j = 0; j < 7; j++)
                    RequestHeader[j] = (char) RecivedData[ConstReqPos - (7 - j)];
                for (j = 0; j < ConstReqPos - 7; j++)
                    ResponseBody[j] = (char) RecivedData[j];
                ResponseBodyLength = ConstReqPos - 7;
                MyStr = "1 -->";
                for (j = 0; j < ConstReqPos - 7; j++)
                    MyStr = MyStr + "," + String.format("%02X", (ResponseBody[j] & 0xFF));
                MyStr = MyStr + " ,Shck = " + CheckPackageSum(ResponseHeader, ResponseBody, ConstReqPos - 7);
                CheckResponsePackage();
//               main.setViewNoStatic(MyStr + MyStr2);
                Log.d("DataEncript ", MyStr);
                ConstReqPos = 0;
                //    RecivedData[ConstReqPos++] = bytes[i];

            } else if (RecivedData[ConstReqPos - 7] == 0x02 && RecivedData[ConstReqPos - 6] == -1 && RecivedData[ConstReqPos - 5] == -1 &&
                    RecivedData[ConstReqPos - 4] == 0x7D && RecivedData[ConstReqPos - 3] == 0x65 && RecivedData[ConstReqPos - 2] == 0x06 &&
                    (RecivedData[ConstReqPos - 1] == -111 || RecivedData[ConstReqPos - 1] == -112)) {
                for (j = 0; j < 7; j++)
                   ResponseHeader[j] = (char) RecivedData[ConstReqPos - (7 - j)];
                for (j = 0; j < ConstReqPos - 7; j++)
                    RequestBody[j] = (char) RecivedData[j];
                RequestBodyLength = ConstReqPos - 7;
                MyStr2 = "2 -->";
                for (j = 0; j < ConstReqPos - 7; j++)
                    MyStr2 = MyStr2 + "," + String.format("%02X",( RequestBody[j] & 0xFF));
                MyStr2 = MyStr2 + " ,Shck= " + CheckPackageSum(RequestHeader, RequestBody, ConstReqPos - 7);
                //              main.setViewNoStatic(MyStr + MyStr2);
                Log.d("DataEncript ", MyStr2);
                ConstReqPos = 0;
                //  RecivedData[ConstReqPos++] = bytes[i];

            }

        }
    }

    public void CheckRequestPackage() {
        if (!CheckPackageSum(RequestHeader, RequestBody, RequestBodyLength)) return;

        if (RequestHeader[5] == -112) {//0x90
            ////////
        }
    }

    public void CheckResponsePackage() {
        //Todo check check sum
        if(ResponseBodyLength<7 )return;
        if (!CheckPackageSum(ResponseHeader, ResponseBody, ResponseBodyLength)) return;
       try {
           String MStr="";
           MStr = MStr + ResponseBody[0];
           MStr = MStr + ResponseBody[1];
           MStr = MStr + ResponseBody[3];
           Log.d("DataEncript ",MStr);
           if (ResponseBody[0] == 2 && ResponseBody[1] == 5 && ResponseBody[3] == 0) {
               float value = (ResponseBody[8] & 0Xff) * 256 + (ResponseBody[9] & 0XFF);
               Log.d("DataEncript ",  String.format("%2.2f", value / 100));
               switch (ResponseBody[2]) {
                   case 0:
                       jsonRootObject.getJSONObject("Status").put("CHWOutLetTemp", String.format("%2.2f", value / 256));
                       break;
                   case 1:
                       jsonRootObject.getJSONObject("Status").put("HTWOutLetTemp", String.format("%2.2f", value / 256));
                       break;
                   case 2:
                       jsonRootObject.getJSONObject("Status").put("RefTemp", String.format("%2.2f", value / 256));
                       break;
                   case 3:
                       jsonRootObject.getJSONObject("Status").put("ValvePosition", String.format("%2.2f", value));
                       break;
               }
           }
       } catch (JSONException e) {
           e.printStackTrace();
       }
        main.setViewNoStatic(jsonRootObject);
    }

    boolean CheckPackageSum(char[] header, char[] body, int Length) {
        if(Length<7) return false;
        int Sum = 0;
        for (int j = 0; j < 7; j++)
            Sum = (header[j] & 0xFF) + Sum;
        for (int j = 0; j < Length - 1; j++)
            Sum = Sum + (body[j] & 0XFF);
        return (Sum & 0XFF) == (body[Length - 1] & 0XFF);
    }
}
