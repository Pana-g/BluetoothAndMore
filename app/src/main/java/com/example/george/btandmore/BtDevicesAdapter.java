package com.example.george.btandmore;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by George on 1/12/2018.
 */

public class BtDevicesAdapter extends BaseAdapter{

    private TextView nameTextView;
    private TextView addressTextView;
    private Context context;
    private BtHashMap btDevices;
    private static LayoutInflater inflater=null;
    private MainActivity mainActivity;

    public BtDevicesAdapter(MainActivity mainActivity, BtHashMap btDevices) {
        // TODO Auto-generated constructor stub
        this.mainActivity = mainActivity;
        this.context=mainActivity;
        this.btDevices=btDevices;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
         return btDevices.size();
    }

    @Override
    public Object getItem(int  position) {
        return btDevices.getByPos(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView=inflater.inflate(R.layout.devices_list,null);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("app1","");
            }
        });
        nameTextView=rowView.findViewById(R.id.nameTextView);
        addressTextView=rowView.findViewById(R.id.addressTextView);
        nameTextView.setText(btDevices.getByPos(position).getName());
        addressTextView.setText(btDevices.getByPos(position).getAddress());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mainActivity.getBluetooth().isConnected() == Bluetooth.DISCONNECTED) {
                    mainActivity.getBluetooth().setConnected(Bluetooth.CONNECTING);
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mainActivity,"Trying to connect with "+btDevices.getByPos(position).getName(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    ConnectionThread connectionThread = new ConnectionThread(btDevices.getByPos(position), mainActivity.getBluetooth(), mainActivity);
                    connectionThread.start();
                    mainActivity.getBluetooth().setConnectionThread(connectionThread);

                    //Log.i("app1","connected with "+btDevices.getByPos(position).getAddress());
                }else if(mainActivity.getBluetooth().isConnected()==Bluetooth.CONNECTED){
                    mainActivity.getBluetooth().getConnectionThread().cancel();
                    mainActivity.getBluetooth().setConnected(Bluetooth.CONNECTING);
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mainActivity,"Trying to connect with "+btDevices.getByPos(position).getName(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    ConnectionThread connectionThread = new ConnectionThread(btDevices.getByPos(position), mainActivity.getBluetooth(), mainActivity);
                    connectionThread.start();
                    mainActivity.getBluetooth().setConnectionThread(connectionThread);


                }
            }
        });
        return rowView;
    }
}
