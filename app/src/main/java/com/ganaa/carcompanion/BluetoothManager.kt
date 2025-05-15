package com.ganaa.carcompanion.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothManager(private val context: Context) {

    companion object {
        private const val TAG = "BluetoothManager"
        // Standard SerialPortService ID
        private val UUID_SPP = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    interface Callback {
        fun onConnectionEstablished()
        fun onConnectionFailed(message: String)
        fun onConnectionLost()
        fun onDataReceived(data: String)
    }

    private var callback: Callback? = null

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun isBluetoothSupported(): Boolean = bluetoothAdapter != null

    fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    fun getPairedDevices(): List<BluetoothDevice> {
        val devices = mutableListOf<BluetoothDevice>()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothAdapter?.bondedDevices?.let { devices.addAll(it) }
        }
        return devices
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            callback?.onConnectionFailed("Bluetooth permission not granted")
            return
        }

        // Cancel any thread currently running a connection
        disconnect()

        // Start a thread to connect with the given device
        Thread {
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID_SPP)
                socket?.connect()

                inputStream = socket?.inputStream
                outputStream = socket?.outputStream

                handler.post {
                    callback?.onConnectionEstablished()
                }

                startReading()
            } catch (e: IOException) {
                handler.post {
                    callback?.onConnectionFailed("Failed to connect: ${e.message}")
                }
                disconnect()
            }
        }.start()
    }

    private fun startReading() {
        isRunning = true
        Thread {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (isRunning) {
                try {
                    // Read data from the input stream
                    bytes = inputStream?.read(buffer) ?: -1

                    if (bytes > 0) {
                        val data = String(buffer, 0, bytes)
                        handler.post {
                            callback?.onDataReceived(data)
                        }
                    }
                } catch (e: IOException) {
                    isRunning = false
                    handler.post {
                        callback?.onConnectionLost()
                    }
                    break
                }
            }
        }.start()
    }

    fun sendCommand(command: String) {
        if (outputStream == null) {
            return
        }

        Thread {
            try {
                outputStream?.write((command + "\r").toByteArray())
            } catch (e: IOException) {
                handler.post {
                    callback?.onConnectionLost()
                }
                disconnect()
            }
        }.start()
    }

    fun disconnect() {
        isRunning = false
        try {
            socket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing Bluetooth socket: ${e.message}")
        }

        inputStream = null
        outputStream = null
        socket = null
    }
}