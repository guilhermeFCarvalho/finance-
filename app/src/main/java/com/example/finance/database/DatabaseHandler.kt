package com.example.finance.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.finance.entities.TransactionEntity
import com.example.finance.entities.TransactionType
import com.example.finance.entities.fromDate
import com.example.finance.entities.toDate

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, detail TEXT, value DOUBLE, date TEXT ) ")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insert(transaction: TransactionEntity) {
        val db = this.writableDatabase

        val register = ContentValues()
        register.put("type", transaction.type.toString())
        register.put("detail", transaction.detail)
        register.put("value", transaction.value)
        register.put("detail", transaction.detail)
        register.put("date", transaction.date.fromDate())


        db.insert(TABLE_NAME, null, register)
    }


    fun list(): MutableList<TransactionEntity> {
        val db = this.writableDatabase
        val register = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val registers = mutableListOf<TransactionEntity>()


        while (register.moveToNext()) {
            val transaction = TransactionEntity(
                register.getInt(ID),
                TransactionType.valueOf(register.getString(TYPE)),
                register.getString(DETAIL),
                register.getDouble(VALUE),
                register.getString(DATE).toDate()!!
            )

            registers.add(transaction)
        }

        return registers.sortedByDescending { it.date }.toMutableList()
    }

    fun cursorList(): Cursor {
        val db = this.writableDatabase

        val register = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        return register
    }

    fun getBalance(): Double {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT type, value FROM $TABLE_NAME", null)
        var balance = 0.0

        while (cursor.moveToNext()) {
            val type = TransactionType.valueOf(cursor.getString(0))
            val value = cursor.getDouble(1)

            balance += if (type == TransactionType.CREDIT) value else -value
        }

        cursor.close()
        return balance
    }


    companion object {
        private const val DATABASE_NAME = "dbfile.sqlite"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "transactions"
        public const val ID = 0
        public const val TYPE = 1
        public const val DETAIL = 2
        public const val VALUE = 3
        public const val DATE = 4

    }

}