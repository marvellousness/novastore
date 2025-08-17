package nova.android.novastore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import nova.android.novastore.data.local.entity.BookEntity
import nova.android.novastore.data.local.dao.BookDao

@Database(entities = [BookEntity::class], version = 1, exportSchema = false)
abstract class RoomStoreDatabase : RoomDatabase() {

    abstract fun userDao(): BookDao

    companion object Companion {
        @Volatile
        private var INSTANCE: RoomStoreDatabase? = null

        fun getDatabase(context: Context): RoomStoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomStoreDatabase::class.java,
                    "app_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not covered in this basic example.
                    .fallbackToDestructiveMigration(true) // Be cautious with this in production - true means drop all tables
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}