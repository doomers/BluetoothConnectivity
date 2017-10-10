package in.doomers.rahul.bluetoothconnectivity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //declaring intentfilter
    IntentFilter  filter ;
    //declaring bluetooth adapter
    BluetoothAdapter badapter;
    // declaring ArrayAdapter
    private ArrayAdapter<String> pairedDeviceAdapter,NewDevicesArrayAdapter;
    //used to be a parameter during onstartActivity call
    int REQUEST_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //declaring and setting clicklistener for it
        Button bn=(Button)findViewById(R.id.button);
        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call for startdiscovery()
                startDiscovery();
            }
        });
        //Initializing arrayadapter
        pairedDeviceAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        NewDevicesArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        //initializing listviews for both new device and connected devices
        ListView pairedListView =(ListView)findViewById(R.id.paired_device_list);
        pairedListView.setAdapter(pairedDeviceAdapter);

        ListView newlistView =(ListView)findViewById(R.id.new_device_list);
        newlistView.setAdapter(NewDevicesArrayAdapter);

        //initializing filters and registering call for broadcast receiver
        filter= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver,filter);

        filter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver,filter);

        //initialize bluetooth adapter
        badapter=BluetoothAdapter.getDefaultAdapter();
        if(badapter==null){
            Toast.makeText(this, "NO BLUETOOTH DEVICE FOUND", Toast.LENGTH_SHORT).show();

        }
    }
    public void startDiscovery(){
        if(!badapter.isEnabled()){//if bluetooth is turned off this will return true
            Intent i= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//ACTION_REQUEST_ENABLE is
           //bluetooth if bluetooth is turned off
            startActivityForResult(i,REQUEST_CODE);//this will call override methos -->onStartActivityForResult
        }else {//this block is executed if bluetooth is already turned on

                Intent i =new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //ACTION_REQUEST_DISCOVERABLE means if bluetooth  makes visibile tour device bluetooth
            // for 120 seconds
            //NOTE vISIBLE TIME DEPENDS UPON THE ANDROID VERSION YOU ARE USING
                startActivityForResult(i,REQUEST_CODE);

        }
       //this will start discovering new devices
        badapter.startDiscovery();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){
            if(requestCode==RESULT_OK) {
                Toast.makeText(this, "BLUETOOTH TURNED ON SUCCESSFULLY", Toast.LENGTH_LONG).show();
                         }
                    }
            if(requestCode==REQUEST_CODE){
                Toast.makeText(this, "BLUETOOTH IS ALREADY ON", Toast.LENGTH_LONG).show();
            }
        //Bluettoth Devices
        Set<BluetoothDevice> pairedevices = badapter.getBondedDevices();
            if(pairedevices.size()>0){

                if (pairedDeviceAdapter != null && !pairedDeviceAdapter.isEmpty())
                    pairedDeviceAdapter.clear();

                for(BluetoothDevice bdevice: pairedevices) {
                    pairedDeviceAdapter.add(bdevice.getName().toString() + " \n" + bdevice.getAddress());
                }

                }else{
                    String noDevice="NO DEVICE HAS BEEN CONNECTED";
                    pairedDeviceAdapter.add(noDevice);

                }
            }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    NewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);

                if (NewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "NO DEVICE FOUND";
                    NewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
