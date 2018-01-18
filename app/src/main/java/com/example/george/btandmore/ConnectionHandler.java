package com.example.george.btandmore;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.Buffer;

/**
 * Created by George on 1/16/2018.
 */

public class ConnectionHandler extends Thread{
    private final BluetoothSocket mmSocket;
    private BufferedReader br;
    private MainActivity mainActivity;
    private String s;


    public ConnectionHandler(BluetoothSocket mmSocket,MainActivity mainActivity){
        this.mainActivity=mainActivity;
        this.mmSocket = mmSocket;


    }

    @Override
    public void run(){

        try {

            br = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));
            mainActivity.getBluetooth().setConnected(Bluetooth.CONNECTED);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setTitle("Connected-"+mmSocket.getRemoteDevice().getName());
                    mainActivity.getList_layout().setVisibility(View.INVISIBLE);
                    mainActivity.getSendButton().setEnabled(true);
                    Toast.makeText(mainActivity,"Connected with "+mmSocket.getRemoteDevice().getName(),Toast.LENGTH_LONG).show();
                }
            });
            Log.i("app1","Start Listening "+mmSocket.getRemoteDevice().getName());

            while((s=br.readLine()) !=null){

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.getTextView().append(s+"\n");
                    }
                });
                Log.i("app1",s);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

            if(mainActivity.getBluetooth().getConnectionThread()!=null) {
                mainActivity.getBluetooth().getConnectionThread().cancel();
            }



    }


}
