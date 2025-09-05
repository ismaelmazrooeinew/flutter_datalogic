package com.tusaamf.flutter_datalogic

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.datalogic.decode.BarcodeManager
import com.datalogic.decode.DecodeException
import com.datalogic.decode.configuration.LengthControlMode
import com.datalogic.decode.configuration.ScanMode
import com.datalogic.decode.configuration.ScannerProperties
import com.datalogic.device.configuration.ConfigException
import com.tusaamf.flutter_datalogic.const.MyChannels
import com.tusaamf.flutter_datalogic.const.ScannerStatus
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler


/** FlutterDatalogicPlugin */
class FlutterDatalogicPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {

    private lateinit var scanEventChannel: EventChannel
    private lateinit var commandMethodChannel: MethodChannel

    private var manager: BarcodeManager? = null
    private var eventSink: EventSink? = null
    private lateinit var context: Context

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext

        try {
            // Initialize BarcodeManager
            manager = BarcodeManager()
            setupScannerConfiguration()
            setupBarcodeListeners()

            // Setup channels
            configureChannels(flutterPluginBinding)

        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error initializing plugin", e)
        }
    }

    private fun setupScannerConfiguration() {
        try {
            val config = ScannerProperties.edit(manager)
            
            // Basic scanner settings
            config.scannerOptions.scanMode.set(ScanMode.SINGLE)
            config.multiScan.enable.set(false)
            config.format.labelSuffix.set("")
            
            // Barcode types
            config.code39.enable.set(true)
            config.code128.enable.set(true)
            config.code128.gs1_128.set(true)
            config.upcA.enable.set(true)
            config.ean8.enable.set(true)
            config.ean13.enable.set(true)
            config.interleaved25.enable.set(true)
            
            config.store(manager, false)
            
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error configuring scanner", e)
        }
    }

    private fun setupBarcodeListeners() {
        manager?.addReadListener { result ->
            // مستقیماً به Flutter ارسال کنید
            val scanData = mapOf(
                "eventName" to "scannerInfo",
                "status" to "SCANNED",
                "scanData" to result.text,
                "scanDataId" to result.barcodeID.name
            )
            
            // ارسال مستقیم به Flutter بدون استفاده از Broadcast
            eventSink?.success(scanData)
        }

        manager?.addStartListener {
            val statusData = mapOf(
                "eventName" to "scannerInfo",
                "status" to "SCANNING",
                "scanData" to "",
                "scanDataId" to ""
            )
            eventSink?.success(statusData)
        }

        manager?.addStopListener {
            val statusData = mapOf(
                "eventName" to "scannerInfo", 
                "status" to "IDLE",
                "scanData" to "",
                "scanDataId" to ""
            )
            eventSink?.success(statusData)
        }
    }

    private fun configureChannels(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // Event Channel for scan data
        scanEventChannel = EventChannel(flutterPluginBinding.binaryMessenger, MyChannels.scanChannel)
        scanEventChannel.setStreamHandler(this)

        // Method Channel for commands
        commandMethodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, MyChannels.commandChannel)
        commandMethodChannel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "startScanning" -> {
                try {
                    manager?.pressTrigger()
                    result.success(true)
                } catch (e: Exception) {
                    result.error("START_SCAN_ERROR", "Failed to start scanning", e.message)
                }
            }

            "stopScanning" -> {
                try {
                    manager?.releaseTrigger()
                    result.success(true)
                } catch (e: Exception) {
                    result.error("STOP_SCAN_ERROR", "Failed to stop scanning", e.message)
                }
            }

            else -> result.notImplemented()
        }
    }

    override fun onListen(arguments: Any?, events: EventSink?) {
        eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        manager?.releaseTrigger()
        commandMethodChannel.setMethodCallHandler(null)
        scanEventChannel.setStreamHandler(null)
        eventSink = null
    }

    companion object {
        private const val LOG_TAG = "FlutterDatalogic"
    }
}
