package com.example.movieindex.core.di

import android.content.Context
import androidx.room.Room
import com.example.movieindex.core.data.local.CacheConstants.DATABASE_NAME
import com.example.movieindex.core.data.local.MovieDatabase
import com.example.movieindex.core.data.remote.NetworkConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductionModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        moshiConverterFactory: MoshiConverterFactory, okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder().addConverterFactory(moshiConverterFactory).client(okHttpClient)
        .baseUrl(NetworkConstants.BASE_URL).build()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): MovieDatabase =
        Room.databaseBuilder(context, MovieDatabase::class.java, DATABASE_NAME).build()

}