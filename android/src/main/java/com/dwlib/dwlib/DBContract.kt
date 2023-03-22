package com.dwlib.dwlib

import android.provider.BaseColumns

object DBContract {

    /* Inner class that defines the table contents */
    class DownloadItemEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "downloads"
            val COLUMN__createdAt = "createdAt"
            val COLUMN_duration = "duration"
            val COLUMN_id = "id"
            val COLUMN_localFile = "localFile"
            val COLUMN_progress = "progress"
            val COLUMN_status = "status"
            val COLUMN_title = "title"
            val COLUMN_type = "type"
            val COLUMN_url = "url"
        }
    }
}