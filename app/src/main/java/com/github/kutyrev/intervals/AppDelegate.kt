package com.github.kutyrev.intervals

import android.app.Application
import androidx.room.Room
import com.github.kutyrev.intervals.database.IntervalsDatabase

class AppDelegate: Application() {

    /*Основной конструктор не может содержать в себе исполняемого кода.
    Инициализирующий код может быть помещён в соответствующий блок (initializers blocks), который помечается словом init.
    */

    init {
        appInstance = this
    }

    companion object {
        lateinit var appInstance: AppDelegate
        private val db by lazy {Room.databaseBuilder(
                appInstance.applicationContext,
                IntervalsDatabase::class.java, "intervals_db"
        ).build()}
        val repository by lazy {db.intervalsDao()}
    }

//    override fun onCreate() {
//        super.onCreate()
//
//        /*db by lazy {Room.databaseBuilder(
//                applicationContext,
//                IntervalsDatabase::class.java, "intervals_db"
//        ).build()}
//
//        val db by lazy Room.databaseBuilder(
//                applicationContext,
//                IntervalsDatabase::class.java, "intervals_db"
//        ).build();
//
//        val repository by lazy {db.intervalsDao()}*/
//
//
//    }
//

}