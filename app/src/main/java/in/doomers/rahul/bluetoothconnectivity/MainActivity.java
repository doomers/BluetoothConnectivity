package in.doomers.rahul.bluetoothconnectivity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    //declaring intentfilter
    private IntentFilter mActionFoundFilter, mActionDiscoveryFinishedFilter;
    //declaring bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    // declaring ArrayAdapter
    private ArrayAdapter<String> mPairedDeviceAdapter, mNewDevicesArrayAdapter;
    //used to be a parameter during onstartActivity call
    int REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //declaring and setting clicklistener for it
        FloatingActionButton scanDevicesButton = findViewById(R.id.scan_devices_button);
        scanDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call for startdiscovery()
                startDiscovery();
            }
        });
        //Initializing arrayadapter
        mPairedDeviceAdapter = new ArrayAdapter<>(this, R.layout.device_name, R.id.tv);
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name, R.id.tv);

        //initializing listviews for both new device and connected devices
        ListView pairedListView = findViewById(R.id.paired_device_list);
        pairedListView.setAdapter(mPairedDeviceAdapter);

        ListView newListView = findViewById(R.id.new_device_list);
        newListView.setAdapter(mNewDevicesArrayAdapter);

        //initialize bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "NO BLUETOOTH DEVICE FOUND", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initializing filters and registering call for broadcast receiver
        mActionFoundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, mActionFoundFilter);

        mActionDiscoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, mActionDiscoveryFinishedFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mReceiver);
    }

    public void startDiscovery() {
        if (!mBluetoothAdapter.isEnabled()) {//if bluetooth is turned off this will return true
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//ACTION_REQUEST_ENABLE is
            //bluetooth if bluetooth is turned off
            startActivityForResult(i, REQUEST_CODE);//this will call override methods -->onStartActivityForResult
        } else {//this block is executed if bluetooth is already turned on

            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //ACTION_REQUEST_DISCOVERABLE means if bluetooth  makes visible tour device bluetooth
            // for 120 seconds
            //NOTE VISIBLE TIME DEPENDS UPON THE ANDROID VERSION YOU ARE USING
            startActivityForResult(i, REQUEST_CODE);

        }
        //this will start discovering new devices
        mBluetoothAdapter.startDiscovery();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(this, "BLUETOOTH TURNED ON SUCCESSFULLY", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_CODE) {
            Toast.makeText(this, "BLUETOOTH IS ALREADY ON", Toast.LENGTH_LONG).show();
        }
        //Bluetooth Devices
        Set<BluetoothDevice> paireDevices = mBluetoothAdapter.getBondedDevices();
        if (paireDevices.size() > 0) {

            if (mPairedDeviceAdapter != null && !mPairedDeviceAdapter.isEmpty())
                mPairedDeviceAdapter.clear();

            for (BluetoothDevice bDevice : paireDevices) {
                mPairedDeviceAdapter.add(bDevice.getName() + " \n" + bDevice.getAddress());
            }

        } else {
            String noDevice = getString(R.string.no_devices_connected);
            mPairedDeviceAdapter.add(noDevice);

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
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);

                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "NO DEVICE FOUND";
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
