package com.kweisa.secondarydevice;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import java.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    BluetoothAdapter bluetoothAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText editTextAddress;
    Button buttonEnroll;
    Button buttonConnect;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance();
        sharedPreferencesManager.load(getApplicationContext());
        sharedPreferences = sharedPreferencesManager.getSharedPreferences();
        editor = sharedPreferencesManager.getEditor();

        editTextAddress = findViewById(R.id.editText_address);
        buttonEnroll = findViewById(R.id.button_enroll);
        buttonConnect = findViewById(R.id.button_connect);

        if (sharedPreferences.getBoolean("isEnrolled", false)) {
            buttonEnroll.setEnabled(false);
            buttonConnect.setEnabled(true);
        } else {
            buttonEnroll.setEnabled(true);
            buttonConnect.setEnabled(false);
        }

        buttonEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = editTextAddress.getText().toString();

                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                EnrollTask enrollTask = new EnrollTask(bluetoothDevice);
                enrollTask.execute();
            }
        });

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = editTextAddress.getText().toString();

                BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
                ConnectTask connectTask = new ConnectTask(bluetoothDevice);
                connectTask.execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class EnrollTask extends BluetoothTask {
        EnrollTask(BluetoothDevice bluetoothDevice) {
            super(bluetoothDevice);
            Log.d(TAG, "EnrollTask: ");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);

            String password = receive();
            String salt = receive();

            SHA3.Digest256 digest256 = new SHA3.Digest256();
            digest256.update(password.getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(digest256.digest());

            editor.putString("password", hashedPassword);
            editor.putString("salt", salt);
            editor.apply();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            buttonEnroll.setEnabled(false);
            buttonConnect.setEnabled(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectTask extends BluetoothTask {
        ConnectTask(BluetoothDevice bluetoothDevice) {
            super(bluetoothDevice);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            super.doInBackground(voids);

            SHA3.Digest256 digest256 = new SHA3.Digest256();
            digest256.update(receive().getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(digest256.digest());

            String password = sharedPreferences.getString("password", "");
            if (password.equals(hashedPassword)) {
                String salt = sharedPreferences.getString("salt", null);
                send(salt);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
