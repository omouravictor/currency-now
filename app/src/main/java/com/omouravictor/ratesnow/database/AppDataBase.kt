package com.omouravictor.ratesnow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.omouravictor.ratesnow.database.dao.RateDao
import com.omouravictor.ratesnow.database.dao.StockDao
import com.omouravictor.ratesnow.database.entity.RatesEntity
import com.omouravictor.ratesnow.database.entity.StockEntity

@Database(entities = [RatesEntity::class, StockEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun rateDao(): RateDao
    abstract fun stockDao(): StockDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "currency_now_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}