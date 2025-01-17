package ru.scoltech.openran.speedtest.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import ru.scoltech.openran.speedtest.domain.SpeedTestResultOld
import java.util.*
import kotlin.jvm.Throws

class SpeedTestResultRepositoryImpl
@Throws(SQLException::class, SQLiteException::class)
constructor(context: Context) : SpeedTestResultRepository {
    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)

    override fun save(result: SpeedTestResultOld) {
        try {
            databaseHelper.writableDatabase
                .insertOrThrow(SPEEDTEST_RESULT_TABLE_NAME, null, ContentValues(6).apply {
                    this.put(UPLOAD_SPEED_COLUMN_NAME, result.downloadSpeed)
                    this.put(DOWNLOAD_SPEED_COLUMN_NAME, result.uploadSpeed)
                    this.put(PING_COLUMN_NAME, result.ping)
                    this.put(CREATION_TIME_COLUMN_NAME, result.creationTime.time)
                    this.put(SERVER_ADDRESS_COLUMN_NAME, result.serverAddress)
                    this.put(DESCRIPTION_COLUMN_NAME, result.description)
                })
        } catch (e: SQLException) {
            Log.e(LOG_TAG, "Could not save $result to the database", e)
        }
    }

    override fun findAllByPageAndSizeOrderedById(page: Long, size: Long): List<SpeedTestResultOld> {
        if (page < 0 || size <= 0) {
            Log.e(LOG_TAG, "Negative page ($page) or non-positive size ($size). " +
                    "Returning empty list...")
            return listOf()
        }
        return querySpeedTestResult(
            orderBy = ID_COLUMN_NAME,
            limit = "${page * size}, $size"
        )
    }

    override fun findAll() =
        querySpeedTestResult()

    private fun querySpeedTestResult(
        distinct: Boolean = false,
        selection: String? = null,
        selectionArgs: Array<String>? = null,
        groupBy: String? = null,
        having: String? = null,
        orderBy: String? = null,
        limit: String? = null,
    ): List<SpeedTestResultOld> {
        return databaseHelper.readableDatabase.query(
            distinct,
            SPEEDTEST_RESULT_TABLE_NAME,
            arrayOf(
                UPLOAD_SPEED_COLUMN_NAME,
                DOWNLOAD_SPEED_COLUMN_NAME,
                PING_COLUMN_NAME,
                CREATION_TIME_COLUMN_NAME,
                SERVER_ADDRESS_COLUMN_NAME,
                DESCRIPTION_COLUMN_NAME,
                ID_COLUMN_NAME,
            ),
            selection,
            selectionArgs,
            groupBy,
            having,
            orderBy,
            limit,
        ).use {
            it.toList()
        }
    }

    private fun Cursor.toList(): List<SpeedTestResultOld> {
        return try {
            moveToFirst()
            (0 until count).map {
                SpeedTestResultOld(
                    getLong(getColumnIndexOrThrow(UPLOAD_SPEED_COLUMN_NAME)),
                    getLong(getColumnIndexOrThrow(DOWNLOAD_SPEED_COLUMN_NAME)),
                    getLong(getColumnIndexOrThrow(PING_COLUMN_NAME)),
                    Date(getLong(getColumnIndexOrThrow(CREATION_TIME_COLUMN_NAME))),
                    getString(getColumnIndexOrThrow(SERVER_ADDRESS_COLUMN_NAME)),
                    getString(getColumnIndexOrThrow(DESCRIPTION_COLUMN_NAME)),
                    getLong(getColumnIndexOrThrow(ID_COLUMN_NAME)),
                ).also { moveToNext() }
            }
        } catch (e: IllegalArgumentException) {
            Log.e(LOG_TAG, "Could not fetch speed test results", e)
            listOf()
        }
    }

    private class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS $SPEEDTEST_RESULT_TABLE_NAME (" +
                        "$ID_COLUMN_NAME INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "$UPLOAD_SPEED_COLUMN_NAME INTEGER NOT NULL, " +
                        "$DOWNLOAD_SPEED_COLUMN_NAME INTEGER NOT NULL, " +
                        "$PING_COLUMN_NAME INTEGER NOT NULL, " +
                        "$CREATION_TIME_COLUMN_NAME INTEGER NOT NULL, " +
                        "$SERVER_ADDRESS_COLUMN_NAME TEXT NOT NULL, " +
                        "$DESCRIPTION_COLUMN_NAME TEXT NOT NULL, " +
                        "CHECK(LENGTH($SERVER_ADDRESS_COLUMN_NAME) <= $SERVER_ADDRESS_COLUMN_MAX_LENGTH), " +
                        "CHECK(LENGTH($DESCRIPTION_COLUMN_NAME) <= $DESCRIPTION_COLUMN_MAX_LENGTH)" +
                        ");"
            )
        }

        private fun changeVersion(
            db: SQLiteDatabase,
            actionMessage: String,
            oldVersion: Int,
            newVersion: Int
        ) {
            Log.w(
                LOG_TAG, "$actionMessage database from version $oldVersion to $newVersion, " +
                        "which will destroy all old data"
            )
            db.execSQL("DROP TABLE IF EXISTS $SPEEDTEST_RESULT_TABLE_NAME;")
            onCreate(db)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            changeVersion(db, "Upgrading", oldVersion, newVersion)
        }

        override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            changeVersion(db, "Downgrading", oldVersion, newVersion)
        }
    }

    companion object {
        private const val LOG_TAG = "SpeedTestResultRepository"
        private const val DATABASE_NAME = "speedtest.db"
        private const val DATABASE_VERSION = 1
        private const val SPEEDTEST_RESULT_TABLE_NAME = "speedtest_result"
        private const val ID_COLUMN_NAME = "id"
        private const val UPLOAD_SPEED_COLUMN_NAME = "upload_speed"
        private const val DOWNLOAD_SPEED_COLUMN_NAME = "download_speed"
        private const val PING_COLUMN_NAME = "ping"
        private const val CREATION_TIME_COLUMN_NAME = "creation_time"
        private const val SERVER_ADDRESS_COLUMN_NAME = "server_address"
        private const val SERVER_ADDRESS_COLUMN_MAX_LENGTH = 32
        private const val DESCRIPTION_COLUMN_NAME = "description"
        private const val DESCRIPTION_COLUMN_MAX_LENGTH = 10_000
    }
}
