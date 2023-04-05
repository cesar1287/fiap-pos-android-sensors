package com.github.cesar1287.sensores

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class StepsDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "StepsDatabase"

        private val TABLE_STEPS_SUMMARY = "StepsSummary"
        private val ID = "id"
        private val STEPS_COUNT = "stepscount"
        private val CREATION_DATE = "creationdate"
    }

    private val CREATE_TABLE_STEPS_SUMMARY = ("CREATE TABLE "
            + TABLE_STEPS_SUMMARY) + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CREATION_DATE + " TEXT," + STEPS_COUNT + " INTEGER" + ")"

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE_STEPS_SUMMARY);
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun createStepsEntry(): Boolean {
        var isDateAlreadyPresent = false
        var createSuccessful = false
        var currentDateStepCounts = 0

        val mCalendar = Calendar.getInstance()
        val todayDate =
            (mCalendar.get(Calendar.DAY_OF_MONTH)
                .toString() + "/" + (mCalendar.get(Calendar.MONTH) + 1).toString()
                    + "/" + mCalendar.get(Calendar.YEAR).toString())

        val selectQuery =
            "SELECT $STEPS_COUNT FROM $TABLE_STEPS_SUMMARY WHERE $CREATION_DATE = '$todayDate'"

        try {
            val db = this.readableDatabase
            val c = db.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    isDateAlreadyPresent = true
                    currentDateStepCounts = c.getInt((c.getColumnIndex(STEPS_COUNT)))
                } while (c.moveToNext())
            }
            db.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(CREATION_DATE, todayDate)
            if (isDateAlreadyPresent) {
                values.put(STEPS_COUNT, ++currentDateStepCounts)
                val row = db.update(
                    TABLE_STEPS_SUMMARY, values, "$CREATION_DATE = '$todayDate'", null)
                if (row == 1) {
                    createSuccessful = true
                }
                db.close()
            } else {
                values.put(STEPS_COUNT, 1)
                val row = db.insert(
                    TABLE_STEPS_SUMMARY, null,
                    values
                )
                if (row != -1L) {
                    createSuccessful = true
                }
                db.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return createSuccessful
    }

    fun readStepsEntries(): ArrayList<Step> {
        val mStepCountList = ArrayList<Step>()
        val selectQuery = "SELECT * FROM $TABLE_STEPS_SUMMARY"
        try {
            val db = this.readableDatabase
            val c = db.rawQuery(selectQuery, null)
            if (c.moveToFirst()) {
                do {
                    val mDateStepsModel = Step()
                    mDateStepsModel.mDate = c.getString((c.getColumnIndex(CREATION_DATE)))
                    mDateStepsModel.mStepCount = c.getInt((c.getColumnIndex(STEPS_COUNT)))
                    mStepCountList.add(mDateStepsModel)
                } while (c.moveToNext())
            }
            c.close()
            db.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mStepCountList
    }
}
