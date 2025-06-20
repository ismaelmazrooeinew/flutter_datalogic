package com.tusaamf.flutter_datalogic

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.tusaamf.flutter_datalogic.const.MyEvents
import io.flutter.plugin.common.EventChannel.EventSink
import org.json.JSONObject

class SinkBroadcastReceiver(private var events: EventSink? = null) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ///  A scan info has been changed/scanned
            "${context.packageName}${DLInterface.ACTION_SCANNER_INFO}" -> {
                val extraKey = "${context.packageName}${DLInterface.EXTRA_SCANNER_INFO}"
                if (intent.hasExtra(extraKey)) {
                    intent.getBundleExtra(extraKey)?.let {
                        handleScannerStatus(it)
                    }
                }
            }

            else -> {
                Log.d(TAG, "default_case")
            }
        }
    }

    private fun handleScannerStatus(b: Bundle) {
        val status =
            b.getString(DLInterface.EXTRA_KEY_VALUE_SCANNER_STATUS)
        val scanData =
            b.getString(DLInterface.EXTRA_KEY_VALUE_SCAN_DATA)
        val scanDataId =
            b.getString(DLInterface.EXTRA_KEY_VALUE_SCAN_DATA_ID)
        val scanResult = JSONObject()
        scanResult.put(MyEvents.EVENT_NAME, MyEvents.SCANNER_INFO)
        scanResult.put("status", status)
        scanResult.put("scanData", scanData)
        scanResult.put("scanDataId", scanDataId)

        events!!.success(scanResult.toString())
    }
}