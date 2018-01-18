package com.example.george.btandmore;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Set;


public class Bluetooth extends Thread {
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private MainActivity mainActivity;
    private ArrayList<BluetoothDevice> newDevices;
    private ConnectionThread connectionThread;
    private int connected;
    static final int CONNECTING=1;
    static final int CONNECTED=2;
    static final int DISCONNECTED=3;
    private boolean secureConnection;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i("app1", "Discovery Started");
                mainActivity.getScanButton().setText("Stop scanning");
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mainActivity.getScanButton().setText("Start scanning");
                //discovery finishes, dismis progress dialog
                Log.i("app1", "Discovery Finished");
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("app1", "New device discovered: " + device.getName() + "-" + device.getAddress());
                mainActivity.getBluetoothDevices().put(device.getAddress(), device);
                mainActivity.getBtDevicesAdapter().notifyDataSetChanged();
            }

        }

    };

    //Bluetooth constructor
    public Bluetooth(MainActivity mainActivity) {
        connected = DISCONNECTED;
        this.mainActivity = mainActivity;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.i("app1", "Doesn t support bluetooth");
            mainActivity.finish();
        } else {
            Log.i("app1", "Supports bluetooth");
        }


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mainActivity.startActivityForResult(enableBtIntent, 1);
        }

        addPairedDevices();


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mainActivity.registerReceiver(mReceiver, filter);
    }




    public void cancelDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    public boolean isDiscovering() {
        return bluetoothAdapter.isDiscovering();
    }

    public void startDiscovery() {
        if (!bluetoothAdapter.isDiscovering()) {

            new AsyncTask<Void, Integer, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    bluetoothAdapter.startDiscovery();
                    return null;
                }
            }.doInBackground();
        }

    }

    public void addPairedDevices() {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                mainActivity.getBluetoothDevices().put(device.getAddress(), device);
                mainActivity.getBtDevicesAdapter().notifyDataSetChanged();
            }
        }
    }

    public boolean isSecureConnection() {
        return secureConnection;
    }

    public void setSecureConnection(boolean secureConnection) {
        this.secureConnection = secureConnection;
    }

    public int isConnected() {
        return connected;
    }

    public void setConnected(int connected) throws InvalidParameterException{
        if(connected == CONNECTED || connected==CONNECTING || connected==DISCONNECTED){

            this.connected = connected;
        }else {
            throw new InvalidParameterException();
        }
    }

    public void destroy() {
        mainActivity.unregisterReceiver(mReceiver);
    }

    public ConnectionThread getConnectionThread() {
        return connectionThread;
    }

    public void setConnectionThread(ConnectionThread connectionThread) {
        this.connectionThread = connectionThread;
    }
}

