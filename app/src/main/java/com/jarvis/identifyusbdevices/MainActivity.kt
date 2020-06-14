package com.jarvis.identifyusbdevices

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val filter = IntentFilter()
        filter.addAction(TAG_LISTEN)
        filter.addAction(TAG_USB)
        filter.addAction(TAG_IN)
        filter.addAction(TAG_OUT)
        registerReceiver(receiver, filter)
//        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
//        val accessory = intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY) as UsbAccessory
        val map = manager.deviceList
        Log.d(TAG, "" + map.size)
        map.values.forEach {
            Log.d(TAG, "deviceName: ${it.deviceName}  vendorId: ${it.vendorId} + productId: ${it.productId}")
            Toast.makeText(this, "deviceName: ${it.deviceName}  vendorId: ${it.vendorId} + productId: ${it.productId}", Toast.LENGTH_SHORT).show()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: ""
            if (action == TAG_IN) {
                Toast.makeText(this@MainActivity, "外设已连接", Toast.LENGTH_SHORT).show()
            }
            if (action == TAG_OUT) {
                Toast.makeText(this@MainActivity, "外设已移除", Toast.LENGTH_SHORT).show()
            }
            if (action == TAG_USB) {
                val connected = intent?.extras?.getBoolean("connected", false) ?: false
                if (connected) {
                    Toast.makeText(this@MainActivity, "USB已连接", Toast.LENGTH_SHORT).show()
                } else {
                    if (flag) {
                        Toast.makeText(this@MainActivity, "USB断开", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (action == TAG_LISTEN) {
                val intExtra = intent?.getIntExtra("state", 0) ?: 0
                // state --- 0代表拔出，1代表插入
                // name--- 字符串，代表headset的类型。
                // microphone -- 1代表这个headset有麦克风，0则没有
                if (intExtra == 0) {
                    if (flag) {
                        Toast.makeText(this@MainActivity, "拔出耳机", Toast.LENGTH_SHORT).show()
                    }
                }
                if (intExtra == 1) {
                    if (flag) {
                        Toast.makeText(this@MainActivity, "耳机插入", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            flag = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        const val TAG = "MainActivity"
        const val TAG_LISTEN = "android.intent.action.HEADSET_PLUG"
        const val TAG_USB = "android.hardware.usb.action.USB_STATE"
        const val TAG_IN = "android.hardware.usb.action.USB_DEVICE_ATTACHED"
        const val TAG_OUT = "android.hardware.usb.action.USB_DEVICE_DETACHED"
        var flag = false
    }
}
