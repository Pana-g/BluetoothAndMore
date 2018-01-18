package com.example.george.btandmore;

import android.app.Fragment;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private TextView textView1;
    private EditText editText1;
    private Button sendButton;
    private Bluetooth bluetooth;
    private ListView listView;
    private BtDevicesAdapter btDevicesAdapter;
    private LinearLayout list_layout;
    private Button returnButton,scanButton;
    private BtHashMap bluetoothDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Not Connected");


        textView1 = findViewById(R.id.textView1);
        list_layout = findViewById(R.id.list_layout);
        scanButton = findViewById(R.id.scan_button);
        sendButton = findViewById(R.id.sendButton);
        returnButton=findViewById(R.id.return_button);
        editText1 = findViewById(R.id.editText1);
        listView = findViewById(R.id.list_view);


        bluetoothDevices = new BtHashMap();
        btDevicesAdapter=new BtDevicesAdapter(this,bluetoothDevices);
        listView.setAdapter(btDevicesAdapter);
        textView1.setMovementMethod(new ScrollingMovementMethod());
        bluetooth = new Bluetooth(this);


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bluetooth.isDiscovering()) {
                    bluetooth.startDiscovery();
                }else{
                    bluetooth.cancelDiscovery();
                }
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list_layout.setVisibility(View.GONE);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText1.getText().toString().trim().length()>0){
                    try {
                        bluetooth.getConnectionThread().sendMessage(editText1.getText().toString().trim());
                    } catch (IOException e) {
                       Log.i("app1","Can't send message");
                    }
                    String s = "<font color='green'>"+editText1.getText().toString().trim()+"</font>";
                    textView1.append(Html.fromHtml(s));
                    textView1.append("\n");
                editText1.setText("");
            }
            }
        });
    }

    public LinearLayout getList_layout() {
        return list_layout;
    }

    public Button getSendButton() {
        return sendButton;
    }

    public TextView getTextView(){
        return textView1;
}

    public Button getScanButton(){
        return scanButton;
    }


    public Bluetooth getBluetooth() {
        return bluetooth;
    }

    public BtHashMap getBluetoothDevices(){
        return bluetoothDevices;
    }

    public BtDevicesAdapter getBtDevicesAdapter(){
        return btDevicesAdapter;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.insecure_connections) {
            bluetooth.addPairedDevices();
            bluetooth.setSecureConnection(false);
            list_layout.setVisibility(View.VISIBLE);
            return true;
        }else if(id ==R.id.secure_connection){
            bluetooth.addPairedDevices();
            bluetooth.setSecureConnection(true);
            list_layout.setVisibility(View.VISIBLE);
            return true;
        }else if(id==R.id.disconnect){
            if(bluetooth.getConnectionThread()!=null) {
                bluetooth.getConnectionThread().cancel();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy(){
        bluetooth.destroy();
        super.onDestroy();
    }
}
