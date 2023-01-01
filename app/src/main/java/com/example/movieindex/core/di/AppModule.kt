package com.example.movieindex.core.di

import com.example.movieindex.core.common.ColorPalette
import com.example.movieindex.core.data.local.MovieDatabase
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.dao.MovieKeyDao
import com.example.movieindex.core.data.local.implementation.CacheDataSourceImpl
import com.example.movieindex.core.data.remote.MovieApi
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.implementation.NetworkDataSourceImpl
import com.example.movieindex.core.data.remote.interceptor.ApiKeyInterceptor
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.implementation.MovieRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun apiKeyInterceptor(): ApiKeyInterceptor = ApiKeyInterceptor()

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun okHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,/*, certificatePinner: CertificatePinner*/
    ) = OkHttpClient.Builder().addInterceptor(apiKeyInterceptor).addInterceptor(loggingInterceptor)
        /*.certificatePinner(certificatePinner)*/.readTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(15, TimeUnit.SECONDS).build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): MovieApi = retrofit.create(MovieApi::class.java)

    @Provides
    @Singleton
    fun provideMovieDao(
        database: MovieDatabase,
    ) = database.movieDao()

    @Provides
    @Singleton
    fun provideMovieKeyDao(
        database: MovieDatabase,
    ) = database.movieKeyDao()

    @Provides
    @Singleton
    fun provideNetworkDataSource(
        movieApi: MovieApi, @CoroutinesQualifiers.IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): NetworkDataSource = NetworkDataSourceImpl(movieApi = movieApi, ioDispatcher = ioDispatcher)

    @Provides
    @Singleton
    fun provideCacheDataSource(
        movieDao: MovieDao,
        movieKeyDao: MovieKeyDao,
        @CoroutinesQualifiers.IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CacheDataSource = CacheDataSourceImpl(movieDao = movieDao,
        movieKeyDao = movieKeyDao,
        ioDispatcher = ioDispatcher)

    @Provides
    @Singleton
    fun provideMovieRepository(
        network: NetworkDataSource,
        cache: CacheDataSource,
    ): MovieRepository = MovieRepositoryImpl(network = network, cache = cache)

    @Provides
    @Singleton
    fun provideColorPalette(): ColorPalette = ColorPalette()

//    @Provides
//    @Singleton
//    fun provideMoviesUseCase(
//        repository: MovieRepository,
//    ): MoviesUseCase = MoviesUseCaseImpl(repository = repository)
}