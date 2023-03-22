package com.dwlib.dwlib

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

import java.util.ArrayList

class UsersDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertDownloadItem(user: DownloadItem): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.DownloadItemEntry.COLUMN__createdAt, user.createdAt)
        values.put(DBContract.DownloadItemEntry.COLUMN_id, user.id)
        values.put(DBContract.DownloadItemEntry.COLUMN_localFile, user.localFile)
        values.put(DBContract.DownloadItemEntry.COLUMN_status, user.status)
        values.put(DBContract.DownloadItemEntry.COLUMN_progress, user.progress)
        values.put(DBContract.DownloadItemEntry.COLUMN_duration, user.duration)
        values.put(DBContract.DownloadItemEntry.COLUMN_url, user.url)
        values.put(DBContract.DownloadItemEntry.COLUMN_type, user.type)
        values.put(DBContract.DownloadItemEntry.COLUMN_title, user.title)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(DBContract.DownloadItemEntry.TABLE_NAME, null, values)

        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteDownloadItem(id: String?): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.DownloadItemEntry.COLUMN_id + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(id)
        // Issue SQL statement.
        db.delete(DBContract.DownloadItemEntry.TABLE_NAME, selection, selectionArgs)

        return true
    }

    fun getDownloadItem(id: String?): ArrayList<DownloadItem> {
        val downloadItems = ArrayList<DownloadItem>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.DownloadItemEntry.TABLE_NAME + " WHERE " + DBContract.DownloadItemEntry.COLUMN_id + "='" + id + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var createdAt: String
        var duration: String
        var id: String
        var localFile: String
        var progress: String
        var status: String
        var title: String
        var type: String
        var url: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                title = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_title))
                type = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_type))
                url = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_url))
                duration = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_duration))
                progress = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_progress))
                status = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_status))
                localFile = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_localFile))
                createdAt = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN__createdAt))
                id = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_id))

                downloadItems.add(DownloadItem(createdAt, duration, id, localFile, progress.toDouble(), status, title, type, url))
                cursor.moveToNext()
            }
        }
        return downloadItems
    }

    fun updateDownloadItem(item: DownloadItem): Int {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.DownloadItemEntry.COLUMN__createdAt, item.createdAt)
        values.put(DBContract.DownloadItemEntry.COLUMN_id, item.id)
        values.put(DBContract.DownloadItemEntry.COLUMN_localFile, item.localFile)
        values.put(DBContract.DownloadItemEntry.COLUMN_status, item.status)
        values.put(DBContract.DownloadItemEntry.COLUMN_progress, item.progress)
        values.put(DBContract.DownloadItemEntry.COLUMN_duration, item.duration)
        values.put(DBContract.DownloadItemEntry.COLUMN_url, item.url)
        values.put(DBContract.DownloadItemEntry.COLUMN_type, item.type)
        values.put(DBContract.DownloadItemEntry.COLUMN_title, item.title)
        val whereclause = "${DBContract.DownloadItemEntry.COLUMN_id}=?"
        val whereargs = arrayOf(item.id.toString())
        return db.update(DBContract.DownloadItemEntry.TABLE_NAME, values, whereclause, whereargs)
    }

    fun getAllDownloadItems(): ArrayList<DownloadItem> {
        val downloadItems = ArrayList<DownloadItem>()
        val db = writableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("select * from " + DBContract.DownloadItemEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var createdAt: String
        var duration: String
        var id: String
        var localFile: String
        var progress: String
        var status: String
        var title: String
        var type: String
        var url: String
        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                title = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_title))
                type = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_type))
                url = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_url))
                duration = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_duration))
                progress = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_progress))
                status = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_status))
                localFile = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_localFile))
                createdAt = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN__createdAt))
                id = cursor.getString(cursor.getColumnIndex(DBContract.DownloadItemEntry.COLUMN_id))

                downloadItems.add(DownloadItem(createdAt, duration, id, localFile, progress.toDouble(), status, title, type, url))
                cursor.moveToNext()
            }
        }
        return downloadItems
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "FeedReader.db"

        private val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + DBContract.DownloadItemEntry.TABLE_NAME + " (" +
                        DBContract.DownloadItemEntry.COLUMN_id + " TEXT PRIMARY KEY," +
                        DBContract.DownloadItemEntry.COLUMN__createdAt + " TEXT," +
                        DBContract.DownloadItemEntry.COLUMN_duration + " TEXT," +
                        DBContract.DownloadItemEntry.COLUMN_localFile + " TEXT," +
                        DBContract.DownloadItemEntry.COLUMN_progress + " TEXT," +
                        DBContract.DownloadItemEntry.COLUMN_status + " TEXT," +
                        DBContract.DownloadItemEntry.COLUMN_title + " TEXT," +
                        DBContract.DownloadItemEntry.COLUMN_type + " TEXT," +
                        DBContract.DownloadItemEntry.COLUMN_url + " TEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.DownloadItemEntry.TABLE_NAME
    }

}