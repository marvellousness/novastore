package nova.android.novastore.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nova.android.novastore.data.local.LocalBookDataSource
import nova.android.novastore.data.local.RoomStoreDatabase
import nova.android.novastore.data.local.dao.BookDao
import nova.android.novastore.data.remote.BookApi
import nova.android.novastore.data.remote.RemoteBookDataSource
import nova.android.novastore.domain.repository.BookRepository
import nova.android.novastore.data.repository.BookRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): RoomStoreDatabase {
        return RoomStoreDatabase.getDatabase(appContext)
    }

    @Provides
    fun provideUserDao(roomStoreDatabase: RoomStoreDatabase): BookDao {
        return roomStoreDatabase.userDao()
    }


    @Provides
    fun provideRemoteBookDataSource(bookApi: BookApi): RemoteBookDataSource {
        return RemoteBookDataSource(bookApi)
    }

    @Provides
    fun provideLocalBookDataSource(bookDao: BookDao): LocalBookDataSource = LocalBookDataSource(bookDao)

    @Provides
    fun provideRepository(
        remoteDataSource: RemoteBookDataSource,
        localDataSource: LocalBookDataSource
    ): BookRepository {
        return BookRepositoryImpl(remoteDataSource, localDataSource)
    }
}