package com.lakj.comspace.simpletextclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
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
    private Button BtnPost;
    private TextView tvIsConnected;
    private String messsage;
    private EditText serverIp;
    private String serverIpAddress = "";
    private BroadcastReceiver receiver;
    private boolean connected = false;
    private JsonChiller jsonMainObject;
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
        BtnPost = (Button) findViewById(R.id.btnPost); // reference to the send button
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

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

        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }
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
                client = new Socket("192.168.1.6", 8001); // connect to the server
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
    public void setViewNoStatic( JsonChiller jsonRootObject) {
//        Log.d("SlimpleTextClientActivity", "set view =" + StrValue);
        jsonMainObject = jsonRootObject;
        serverIp.setText(jsonRootObject.jsonRootObject.toString());
        new HttpAsyncTask().execute("http://192.168.1.6:8001/");//hmkcode.appspot.com/jsonservlet

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static String POST(String url,JsonChiller jsonRootObject){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

           /* // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name","reza");
            jsonObject.accumulate("country", "Shiraz");
            jsonObject.accumulate("twitter", "Iran");

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();*/
            json = jsonRootObject.jsonRootObject.toString();
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
            Log.d("SlimpleTextClientActivity",result);

        } catch (Exception e) {
            Log.d("SlimpleTextClientActivity", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void onClickbtnPost(View view) {

        switch(view.getId()){
            case R.id.btnPost:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                new HttpAsyncTask().execute("http://192.168.1.6:8001/");//hmkcode.appspot.com/jsonservlet
                break;
        }

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

     /*       JsonChiller JsonChillerObject = new JsonChiller();
            try {
                JsonChillerObject.jsonRootObject.getJSONObject("Status").put("CHWOutLetTemp","1.1");
                JsonChillerObject.jsonRootObject.getJSONObject("Status").put("HTWOutLetTemp","2.2");
                JsonChillerObject.jsonRootObject.getJSONObject("Status").put("RefTemp", "3.3");
                JsonChillerObject.jsonRootObject.getJSONObject("Status").put("ValvePosition", "4.4");
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            return POST(urls[0], jsonMainObject);

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
  //          Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validate(){
     /*   if(etName.getText().toString().trim().equals(""))
            return false;
        else if(etCountry.getText().toString().trim().equals(""))
            return false;
        else if(etTwitter.getText().toString().trim().equals(""))
            return false;
        else*/
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
