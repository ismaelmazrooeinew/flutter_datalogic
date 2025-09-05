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
        try {
            when (intent.action) {
                "${context.packageName}${DLInterface.ACTION_SCANNER_INFO}" -> {
                    val extraKey = "${context.packageName}${DLInterface.EXTRA_SCANNER_INFO}"
                    if (intent.hasExtra(extraKey)) {
                        intent.getBundleExtra(extraKey)?.let { bundle ->
                            handleScannerStatus(bundle)
                        } ?: run {
                            Log.w(TAG, "Bundle is null for key: $extraKey")
                        }
                    }
                }
                else -> {
                    Log.d(TAG, "Unhandled action: ${intent.action}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onReceive", e)
        }
    }

    private fun handleScannerStatus(bundle: Bundle) {
        try {
            val status = bundle.getString(DLInterface.EXTRA_KEY_VALUE_SCANNER_STATUS) ?: ""
            val scanData = bundle.getString(DLInterface.EXTRA_KEY_VALUE_SCAN_DATA) ?: ""
            val scanDataId = bundle.getString(DLInterface.EXTRA_KEY_VALUE_SCAN_DATA_ID) ?: ""
            
            val scanResult = JSONObject().apply {
                put(MyEvents.EVENT_NAME, MyEvents.SCANNER_INFO)
                put("status", status)
                put("scanData", scanData)
                put("scanDataId", scanDataId)
            }

            events?.success(scanResult.toString()) ?: Log.w(TAG, "EventSink is null")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling scanner status", e)
        }
    }
}
