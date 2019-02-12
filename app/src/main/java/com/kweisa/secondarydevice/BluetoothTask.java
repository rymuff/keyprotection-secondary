package com.kweisa.secondarydevice;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class BluetoothTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "BluetoothTask";
    private static final java.util.UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    BluetoothTask(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    void send(String message) {
        try {
            bufferedWriter.write(message + "\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Send: " + message);
    }

    String receive() {
        String message = null;
        try {
            message = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "receive: " + message);

        return message;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID);
            bluetoothSocket.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(bluetoothSocket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(bluetoothSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        try {
            bufferedReader.close();
            bufferedWriter.close();
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}