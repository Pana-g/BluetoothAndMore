package com.example.george.btandmore;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class ConnectionThread extends Thread {
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Bluetooth bluetooth;
    private MainActivity mainActivity;
    private BufferedWriter bw;

    public ConnectionThread(BluetoothDevice device, Bluetooth bluetooth, MainActivity mainActivity) {
        this.bluetooth=bluetooth;
        this.mainActivity = mainActivity;
        mmDevice = device;

        try {
            if(!bluetooth.isSecureConnection()) {
                mmSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            }else{
                mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }

            Log.i("app1","Insecure connection started");
        } catch (IOException e) {
            Log.i("app1", "Socket's create() method failed", e);
        }

    }

    @Override
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetooth.cancelDiscovery();

        Log.i("app1", "skataaaaaaa");

        try {
            mmSocket.connect();
            Log.i("app1", "Connected insecure with " + mmDevice.getName());

            ConnectionHandler connectionHandler = new ConnectionHandler(mmSocket, mainActivity);
            connectionHandler.start();
            Log.i("app1", "Connection Handler started");
            try {
                bw = new BufferedWriter(new OutputStreamWriter(mmSocket.getOutputStream()));
                Log.i("app1", "Bw created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            cancel();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainActivity, "Couldn't connect with " + mmSocket.getRemoteDevice().getName(), Toast.LENGTH_LONG).show();

                }
            });
            Log.i("app1", "Skata h sundesi");
            connectException.getStackTrace();

        }
        if (mmSocket != null) {

            Log.i("app1", "1. " + mmSocket.isConnected() + " 2. " + mmSocket.getRemoteDevice().getName());
        }
    }


    public void cancel() {
        try {
            mmSocket.close();
            mainActivity.getBluetooth().setConnectionThread(null);
            mainActivity.getBluetooth().setConnected(Bluetooth.DISCONNECTED);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.getSendButton().setEnabled(false);
                    mainActivity.setTitle("Disconnected");
                    mainActivity.getTextView().setText("");
                    Toast.makeText(mainActivity,"Connection closed",Toast.LENGTH_LONG).show();
                }
            });
        } catch (IOException e) {
            Log.i("app1", "Could not close the client socket", e);
        }
    }


    public void sendMessage(String s) throws IOException {
        bw.write(s);
        bw.newLine();
        bw.flush();
    }
}

