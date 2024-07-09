package hiendao.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import hiendao.data.ApiService
import hiendao.data.util.interceptor.MyInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton


@InstallIn(Singleton::class)
@Module
object NetworkModule {

    @Provides
    fun provideMyInterceptor(): MyInterceptor{
        return MyInterceptor()
    }

    @Provides
    fun provideOkHttpClient(myInterceptor: MyInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(myInterceptor)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService{
        return retrofit.create(ApiService::class.java)
    }
}