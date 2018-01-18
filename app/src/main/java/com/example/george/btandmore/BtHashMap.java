package com.example.george.btandmore;

import android.bluetooth.BluetoothDevice;

import java.util.HashMap;

/**
 * Created by George on 1/13/2018.
 */

public class BtHashMap extends HashMap<String,BluetoothDevice>{

    public BluetoothDevice getByPos(int position){
        int counter=0;
        for(Entry<String,BluetoothDevice> btDevice : this.entrySet()){
            if(counter==position){
                return btDevice.getValue();
            }
            counter++;
        }
        return null;
    }

}
