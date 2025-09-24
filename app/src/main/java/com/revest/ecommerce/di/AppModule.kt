package com.revest.ecommerce.di

import android.content.Context
import androidx.room.Room
import com.revest.ecommerce.data.local.AppDatabase
import com.revest.ecommerce.data.remote.ProductApi
import com.revest.ecommerce.data.repository.ProductRepositoryImpl
import com.revest.ecommerce.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideProductApi(okHttpClient: OkHttpClient): ProductApi {
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "revest_ecommerce.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        api: ProductApi,
        db: AppDatabase
    ): ProductRepository {
        return ProductRepositoryImpl(api, db.productDao())
    }
}