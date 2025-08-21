package nova.android.novastore.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import nova.android.novastore.domain.repository.BookRepository
import nova.android.novastore.domain.usecase.ClearLocalDataUseCase
import nova.android.novastore.domain.usecase.GetBookStreamUseCase
import nova.android.novastore.domain.usecase.GetBooksStreamUseCase
import nova.android.novastore.domain.usecase.RefreshBooksUseCase
import nova.android.novastore.domain.usecase.SearchBooksStreamUseCase

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideRefreshBooksUseCase(
        bookRepository: BookRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): RefreshBooksUseCase = RefreshBooksUseCase(bookRepository, ioDispatcher)

    @Provides
    fun provideClearLocalDataUseCase(
        bookRepository: BookRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ClearLocalDataUseCase = ClearLocalDataUseCase(bookRepository, ioDispatcher)

    @Provides
    fun provideGetBooksStreamUseCase(
        bookRepository: BookRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GetBooksStreamUseCase = GetBooksStreamUseCase(bookRepository, ioDispatcher)

    @Provides
    fun provideGetBookStreamUseCase(
        bookRepository: BookRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GetBookStreamUseCase = GetBookStreamUseCase(bookRepository, ioDispatcher)

    @Provides
    fun provideSearchBooksStreamUseCase(
        bookRepository: BookRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): SearchBooksStreamUseCase = SearchBooksStreamUseCase(bookRepository, ioDispatcher)
}


