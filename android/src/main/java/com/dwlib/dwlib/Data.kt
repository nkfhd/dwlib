package com.dwlib.dwlib

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.annotation.NonNull
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class Data(context: Context) {
    var usersDBHelper = UsersDBHelper(context)
    var downloadUrlList: ArrayList<DownloadItem> = usersDBHelper.getAllDownloadItems()
    

    @NonNull
    private fun getFilePath(@NonNull url: String): String {
        val uri = Uri.parse(url)
        val fileName = uri.lastPathSegment
        val dir = getSaveDir()
        return dir + "/DownloadList/" + fileName
    }

    @NonNull
    fun getSaveDir(): String {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/fetch"
    }

    @NonNull
    internal fun getNameFromUrl(url: String): String? {
        return Uri.parse(url).lastPathSegment
    }

    @NonNull
    internal fun generateJsonFromDownload(item: DownloadItem?): JSONObject {
        var download = item
        var jsonObject = json {
            "id" To download?.id
            "title" To download?.title
            "url" To download?.url
            "progress" To download?.progress
            "status" To download?.status
        }
        return jsonObject
    }


    @NonNull
    internal fun allItems(): String? {
        downloadUrlList = usersDBHelper.getAllDownloadItems()
        val downloads = downloadUrlList
        var result: ArrayList<Any> = arrayListOf()
        for (download in downloads) {
            result.add(generateJsonFromDownload(download))
        }
        return result.toString()
    }
}

fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
    return JsonObjectBuilder().json(build)
}

class JsonObjectBuilder {
    private val deque: Deque<JSONObject> = ArrayDeque()

    fun json(build: JsonObjectBuilder.() -> Unit): JSONObject {
        deque.push(JSONObject())
        this.build()
        return deque.pop()
    }

    infix fun <T> String.To(value: T) {
        deque.peek().put(this, value)
    }
}


