package com.example.movieindex.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import com.example.movieindex.core.common.ColorPalette
import com.example.movieindex.core.data.local.MovieDatabase
import com.example.movieindex.core.data.local.abstraction.CacheDataSource
import com.example.movieindex.core.data.local.dao.MovieDao
import com.example.movieindex.core.data.local.implementation.CacheDataSourceImpl
import com.example.movieindex.core.data.remote.MovieApi
import com.example.movieindex.core.data.remote.NetworkConstants.CACHE_SIZE
import com.example.movieindex.core.data.remote.abstraction.NetworkDataSource
import com.example.movieindex.core.data.remote.implementation.NetworkDataSourceImpl
import com.example.movieindex.core.data.remote.interceptor.ApiKeyInterceptor
import com.example.movieindex.core.data.remote.interceptor.CacheInterceptor
import com.example.movieindex.core.data.remote.interceptor.ForceCacheInterceptor
import com.example.movieindex.core.repository.abstraction.AuthRepository
import com.example.movieindex.core.repository.abstraction.MovieRepository
import com.example.movieindex.core.repository.implementation.AuthRepositoryImpl
import com.example.movieindex.core.repository.implementation.MovieRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context) =
        Cache(File(context.cacheDir.path + File.separatorChar + "okhttp_cache"), CACHE_SIZE)

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): ApiKeyInterceptor = ApiKeyInterceptor()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideCacheInterceptor(): CacheInterceptor = CacheInterceptor()

    @Provides
    @Singleton
    fun okHttpClient(
        cache: Cache,
        apiKeyInterceptor: ApiKeyInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
        cacheInterceptor: CacheInterceptor,
        forceCacheInterceptor: ForceCacheInterceptor,
        /*, certificatePinner: CertificatePinner*/
    ) = OkHttpClient.Builder()
//        .cache(cache)
//        .addNetworkInterceptor(forceCacheInterceptor)
//        .addInterceptor(cacheInterceptor)
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
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
    fun provideNetworkDataSource(
        movieApi: MovieApi, @CoroutinesQualifiers.IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): NetworkDataSource = NetworkDataSourceImpl(movieApi = movieApi, ioDispatcher = ioDispatcher)

    @Provides
    @Singleton
    fun provideCacheDataSource(
        movieDao: MovieDao,
        dataStore: DataStore<Preferences>,
        @CoroutinesQualifiers.IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CacheDataSource = CacheDataSourceImpl(movieDao = movieDao,
        dataStore = dataStore,
        ioDispatcher = ioDispatcher)

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideMovieRepository(
        workManager: WorkManager,
        network: NetworkDataSource,
        cache: CacheDataSource,
    ): MovieRepository =
        MovieRepositoryImpl(workManager = workManager, network = network, cache = cache)

    @Provides
    @Singleton
    fun provideAuthRepository(
        network: NetworkDataSource,
        cache: CacheDataSource,
    ): AuthRepository =
        AuthRepositoryImpl(network = network, cache = cache)

    @Provides
    @Singleton
    fun provideColorPalette(): ColorPalette = ColorPalette()

}