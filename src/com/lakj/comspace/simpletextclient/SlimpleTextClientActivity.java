package com.lakj.comspace.simpletextclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * This is a simple Android mobile client
 * This application read any string massage typed on the text field and
 * send it to the server when the Send button is pressed
 * Author by Lak J Comspace
 */
public class SlimpleTextClientActivity extends Activity {
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
    static int intentCallCount = 0;
    // default ip
    public static String SERVERIP = "192.168.1.6";

    // designate a port
    public static final int SERVERPORT = 502;

    private Socket client;
    private PrintWriter printwriter;
    private EditText textField;
    private Button connectPhones;
    private Button Stopthread;
    private String messsage;
    private EditText serverIp;
    private String serverIpAddress = "";
    private BroadcastReceiver receiver;
    private boolean connected = false;
    Intent intent;
    Thread cThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slimple_text_client);

        cThread = new Thread(new ClientThread());

        serverIp = (EditText) findViewById(R.id.editText1); // reference to the text field
        connectPhones = (Button) findViewById(R.id.button1); // reference to the send button
        Stopthread = (Button) findViewById(R.id.button2); // reference to the send button
        intent = new Intent();

        intent.setAction("com.tutorialspoint.CUSTOM_INTENT");

        DataRecieveHandle BR_DataRecieveHandle = null;
        BR_DataRecieveHandle = new DataRecieveHandle();
        BR_DataRecieveHandle.setMainActivityHandler(this);
        IntentFilter fltr_smsreceived = new IntentFilter("com.tutorialspoint.CUSTOM_INTENT");
        registerReceiver(BR_DataRecieveHandle, fltr_smsreceived);
        // Button press event listener
        connectPhones.setOnClickListener(new View.OnClickListener() {
                                             public void onClick(View v) {
                                                 Log.d("SlimpleTextClientActivity", "ddddddd");
                                                 if (!connected) {
                                                     Log.d("SlimpleTextClientActivity", "ddddddd2");
                                                     serverIpAddress = serverIp.getText().toString();
                                                     serverIpAddress = "192.168.1.6";
                                                     Log.d("SlimpleTextClientActivity", "serverIp= " + serverIpAddress);
                                                     if (!serverIpAddress.equals("")) {
                                                         cThread.start();
                                                     }
                                                 }
                                             }
                                         }
        );
      /*  Stopthread.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (cThread != null) cThread.interrupt();

            }});*/
    }

    public class ClientThread implements Runnable {
        public void run() {
            try {
                byte[] mybytearray = new byte[2000];
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("SlimpleTextClientActivity", "inthread");
                Socket socket = new Socket("192.168.1.6", 502);
                //   DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                //      BufferedReader inputSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Log.d("SlimpleTextClientActivity", "C: Sending command.");
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                // where you issue the commands
                connected = true;
                while (connected) {
                    try {
                        int LengthRecieve = socket.getInputStream().available();
                        if (LengthRecieve > 0) {
                            Log.d("SlimpleTextClientActivity", "WAIT ssst Length := " + LengthRecieve);
                            String response = "@";
                            int ready = socket.getInputStream().available();
                            byte[] bytes = new byte[ready];
                            response = response + socket.getInputStream().read(bytes, 0, ready);
                            intent.putExtra("Data", bytes);
                            intent.putExtra("Data_Length", ready);
                            sendBroadcast(intent);
                            //  }
                      /*  for(int i = 0 ;i < ready ; i++)
                            response = response +","+ bytes[i];
                        response = response+"#";
                        Log.d("SlimpleTextClientActivity", "response = " + response);*/
                    /*    String  incomingMessage = inputSocket.readLine();
                        if (incomingMessage != null ) {
                            Log.d("SlimpleTextClientActivity", "Recieve ssst"+incomingMessage  );}
                   */
                            Log.d("SlimpleTextClientActivity", "Recieve ssst2");
                            Thread.sleep(50);
                            //    if (response != null)
                            //  Log.d("SlimpleTextClientActivity", "Recieve from client" + response);
                        }
                    } catch (Exception e) {
                        Log.e("SlimpleTextClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("SlimpleTextClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("SlimpleTextClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
    private class SendMessage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                client = new Socket("192.168.1.6", 502); // connect to the server
                printwriter = new PrintWriter(client.getOutputStream(), true);
                printwriter.write(messsage); // write the message to output stream
                printwriter.flush();
                printwriter.close();
                client.close(); // closing the connection
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.slimple_text_client, menu);
        return true;
    }
    // broadcast a custom intent.
    public void broadcastIntent(View view) {
        byte[] bytes = new byte[100];
        for (int i = 0; i < 20; i++) bytes[i] = (byte) i;
        intent.putExtra("Data", bytes);
        sendBroadcast(intent);
    }
    public void setViewNoStatic( JSONObject jsonRootObject) {
//        Log.d("SlimpleTextClientActivity", "set view =" + StrValue);

        serverIp.setText(jsonRootObject.toString());

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
