package com.dwlib.dwlib

import alirezat775.lib.downloader.Downloader
import alirezat775.lib.downloader.core.OnDownloadListener
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.net.URI

/** DwlibPlugin */
class DwlibPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var activity: Activity

    lateinit var usersDBHelper: UsersDBHelper
    var sharedPref: SharedPreferences? = null;
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "thekee_shared_prefs"
    private val STORAGE_PERMISSION_CODE = 200
    private var downloader: Downloader? = null
    private lateinit var globalItem: DownloadItem;
    private val TAG: String = this::class.java.name
    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dwlib")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "get_list") {
            var items = Data(activity).allItems()
            result.success(items)
        } else if (call.method == "start") {
            if (getActiveConnections() < 5) {
                var id: String? = call.argument("id")
                var url: String? = call.argument("link")
                var fileName: String? = call.argument("fileName")
                var whereToSave: String? = call.argument("savedDir")
                var item: DownloadItem =
                    DownloadItem("", "", id, whereToSave, 0.0, "active", fileName, "", url)
                usersDBHelper.insertDownloadItem(item)
                getDownloader(item.id)
                downloader?.download()
                result.success(true)
            } else {
                result.success(false)
            }
        } else if (call.method == "pause") {
            var id: String? = call.argument("id")
            var item = usersDBHelper.getDownloadItem(id).get(0)
            item.status = "paused"
            usersDBHelper.updateDownloadItem(item)
            downloader?.pauseDownload()
            result.success(true)
        } else if (call.method == "resume") {
            var id: String? = call.argument("id")
            var item = usersDBHelper.getDownloadItem(id).get(0)
            item.status = "active"
            usersDBHelper.updateDownloadItem(item)
            getDownloader(item.id)
            downloader?.resumeDownload()
            result.success(true)
        } else if (call.method == "cancel") {
            var id: String? = call.argument("id")
            var item = usersDBHelper.getDownloadItem(id).get(0)
            item.status = "stoped"
            usersDBHelper.updateDownloadItem(item)
            downloader?.cancelDownload()
            result.success(true)
        } else if (call.method == "delete") {
            var id: String? = call.argument("id")
            var item = usersDBHelper.getDownloadItem(id).get(0)
            usersDBHelper.deleteDownloadItem(item.id)
            downloader?.cancelDownload()
            result.success(true)
        } else if (call.method == "retry") {
            var id: String? = call.argument("id")
            var item = usersDBHelper.getDownloadItem(id).get(0)
            item.status = "active"
            usersDBHelper.updateDownloadItem(item)
            getDownloader(item.id)
            downloader?.download()
            result.success(true)
        } else if (call.method == "delete_local") {
            var id: String? = call.argument("id")
            var item = usersDBHelper.getDownloadItem(id).get(0)
            val uri = URI(item.localFile)
            val path = uri.getPath()
            val ThefileName = path.substring(path.lastIndexOf('/') + 1).substringBeforeLast('.')
            val ext = path.substring(path.lastIndexOf('/') + 1).substringAfterLast('.')
            var sb = StringBuilder()
            sb.append(context.filesDir.absolutePath)
            sb.append(java.io.File.separator)
            sb.append(ThefileName)
            sb.append(".")
            sb.append(ext)
            var file: File = File(sb.toString())
            if (file.exists()) {
                var isDeleted = file.delete()
            } else {
                var sb = StringBuilder()
                sb.append(context.getExternalFilesDir(null))
                sb.append(java.io.File.separator)
                sb.append(item.localFile.toString().replace("/", java.io.File.separator))
                sb.append(java.io.File.separator)
                sb.append(ThefileName)
                sb.append(".")
                sb.append(ext)
                var file: File = File(sb.toString())
                if (file.exists()) {
                    var isDeleted = file.delete()
                }
            }
            result.success(true)
        } else {
            result.notImplemented()
        }
    }


    private fun checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }


    private fun getActiveConnections(): Int {
        var activeConnections: Int = 0
        var downloadUrlList = usersDBHelper.getAllDownloadItems()
        val downloads = downloadUrlList
        var result: ArrayList<Any> = arrayListOf()
        for (download in downloads) {
            if (download.status == "active") {
                activeConnections++
            }
        }
        return activeConnections
    }


    private fun getDownloader(id: String?) {
        var currentItem: DownloadItem = usersDBHelper.getDownloadItem(id)[0]
        globalItem = currentItem
        val uri = URI(currentItem.url)
        val path = uri.getPath()
        val ThefileName = id.toString()
        val ext = "mp4"
        val permissionCheckStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheckStorage != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
        val sd_main = File(context.filesDir.absolutePath)
        var success = true
        if (!sd_main.exists()) {
            success = sd_main.mkdir()
        }
        downloader = Downloader.Builder(
            context,
            currentItem.url.toString().replace("http://", "https://").toString()
//                "https://file-examples.com/wp-content/uploads/2017/04/file_example_MP4_480_1_5MG.mp4"
        ).downloadListener(object : OnDownloadListener {
            override fun onStart() {
                var item: DownloadItem = usersDBHelper.getDownloadItem(id)[0]
                item.status = "active"
                usersDBHelper.updateDownloadItem(item)
            }

            override fun onPause() {
                var item: DownloadItem = usersDBHelper.getDownloadItem(id)[0]
                item.status = "paused"
                usersDBHelper.updateDownloadItem(item)
            }

            override fun onResume() {
                var item: DownloadItem = usersDBHelper.getDownloadItem(id)[0]
                item.status = "active"
                usersDBHelper.updateDownloadItem(item)
            }

            override fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int) {
                var item: DownloadItem = usersDBHelper.getDownloadItem(id)[0]
                if (percent <= 100 || percent >= 0 || percent != null) {
                    item.progress = (percent.toDouble() / 100)
                }
                usersDBHelper.updateDownloadItem(item)
                item = usersDBHelper.getDownloadItem(id)[0]

            }

            override fun onCompleted(file: File?) {
                var item: DownloadItem = usersDBHelper.getDownloadItem(id)[0]
                item.status = "completed"
                usersDBHelper.updateDownloadItem(item)
            }

            override fun onFailure(reason: String?) {
                Log.d(TAG, "onFailure: reason --> $reason")
                var item: DownloadItem = usersDBHelper.getDownloadItem(id)[0]
                item.status = "failed"
                usersDBHelper.updateDownloadItem(item)
            }

            override fun onCancel() {
                Log.d(TAG, "onCancel")
            }
        })
            .fileName(ThefileName, ext)
            .downloadDirectory(context.filesDir.absolutePath)
            .build()


    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        usersDBHelper = UsersDBHelper(context)
        sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        checkStoragePermissions()
    }

    override fun onDetachedFromActivityForConfigChanges() {
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }
}
