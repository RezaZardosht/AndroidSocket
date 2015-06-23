package com.lakj.comspace.simpletextclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.json.JSONException;

//#define  MAX_PACK_LENGTH  60

/**
 * Created by Zardosht on 06/09/2015.
 */


public class DataRecieveHandle extends BroadcastReceiver {


    public DataRecieveHandle() {

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
    JsonChiller jsonRootObject = new JsonChiller();

    void setMainActivityHandler(SlimpleTextClientActivity main) {
        this.main = main;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int i, j;
        String MyStr, MyStr2;
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
                ResponseBodyLength = (RecivedData[6]+ 8);
                for (j = 0; j < ResponseBodyLength +1 ; j++)
                    ResponseBody[j] = (char) RecivedData[j];
                MyStr = "1 -->";
                for (j = 0; j <ResponseBodyLength +1; j++)
                    MyStr = MyStr + "," + String.format("%02X", (ResponseBody[j] & 0xFF));
                MyStr = MyStr + " ,Shck = " + CheckPackageSum(ResponseHeader, ResponseBody, ConstReqPos - 7);
                try {
                    CheckResponsePackage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("DataEncript ", MyStr);
                ConstReqPos = 0;

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
                    MyStr2 = MyStr2 + "," + String.format("%02X", (RequestBody[j] & 0xFF));
                MyStr2 = MyStr2 + " ,Shck= " + CheckPackageSum(RequestHeader, RequestBody, ConstReqPos - 7);
                Log.d("DataEncript ", MyStr2);
                ConstReqPos = 0;

            }

        }
    }

    public void CheckRequestPackage() {
        if (!CheckPackageSum(RequestHeader, RequestBody, RequestBodyLength)) return;

        if (RequestHeader[5] == -112) {//0x90
            ////////
        }
    }

    public void CheckResponsePackage() throws JSONException {
        //Todo check check sum
        if (ResponseBodyLength < 7) return;
        if (!CheckPackageSum(ResponseHeader, ResponseBody, ResponseBodyLength)) return;
        String MStr = "";
        MStr = MStr + ResponseBody[0];
        MStr = MStr + ResponseBody[1];
        MStr = MStr + ResponseBody[3];
        Log.d("DataEncript ", MStr);
        if (ResponseBody[0] == 2 && ResponseBody[1] == 5 && ResponseBody[3] == 0) {
            float value = (ResponseBody[8] & 0Xff) * 256 + (ResponseBody[9] & 0XFF);
            Log.d("DataEncript ", String.format("%2.2f", value / 100));
            switch (ResponseBody[2]) {
                case 0:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("CHWOutLetTemp", String.format("%2.2f", value / 256));
                    break;
                case 1:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("HTWOutLetTemp", String.format("%2.2f", value / 256));
                    break;
                case 2:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("RefTemp", String.format("%2.2f", value / 256));
                    break;
                case 3:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("ValvePosition", String.format("%2.2f", value));
                    break;
                case 8:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("CHWInLetTemp", String.format("%2.2f", value));
                    break;
                case 9:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("HTWInLetTemp", String.format("%2.2f", value));
                    break;
                case 10:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("COWOutLetTemp", String.format("%2.2f", value));
                    break;
                case 11:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("CowInLetTemp", String.format("%2.2f", value));
                    break;
                case 13:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("DilutionTemp", String.format("%2.2f", value));
                    break;
                case 14:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("SolutionTemp", String.format("%2.2f", value));
                    break;
                case 15:
                    jsonRootObject.jsonRootObject.getJSONObject("Status").put("ExhaustTemp", String.format("%2.2f", value));
                    break;
            }
        }
        main.setViewNoStatic(jsonRootObject);
    }

    boolean CheckPackageSum(char[] header, char[] body, int Length) {
        if (Length < 7) return false;
        int Sum = 0;
        for (int j = 0; j < 7; j++)
            Sum = (header[j] & 0xFF) + Sum;
        for (int j = 0; j < Length ; j++)
            Sum = Sum + (body[j] & 0XFF);
        return (Sum & 0XFF) == (body[Length ] & 0XFF);
    }
}
