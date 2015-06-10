package com.lakj.comspace.simpletextclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Zardosht on 06/09/2015.
 */
public class DataRecieveHandle extends BroadcastReceiver {

    static int mycall=0;
    SlimpleTextClientActivity main = null;
    void setMainActivityHandler(SlimpleTextClientActivity main){
        this.main=main;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        mycall++;
        String MyStr;
        MyStr ="";
        byte[] bytes;
        bytes = intent.getByteArrayExtra("Data");
        int bytesLength = intent.getIntExtra("Data_Length", 0);
        for(int i=0;i<bytesLength;i++)
            MyStr = MyStr+"," +(char) bytes[i];
     //   Toast.makeText(context, "I Data Reciever Intent Detected.@"+mycall+"@+"+ MyStr, Toast.LENGTH_LONG).show();
           main.setViewNoStatic(MyStr);
    }

}
