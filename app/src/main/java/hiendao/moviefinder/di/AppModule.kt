package hiendao.moviefinder.di

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hiendao.moviefinder.data.local.CreditDAO
import hiendao.moviefinder.data.local.CreditDatabase
import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.local.MovieDatabase
import hiendao.moviefinder.data.local.model.MovieEntity
import hiendao.moviefinder.data.network.MovieNetworkMediator
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.data.network.tvseries.TvSeriesApi
import hiendao.moviefinder.data.network.util.base_url.BaseUrl.BASE_API_URL
import hiendao.moviefinder.data.network.util.interceptor.MyInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AppModule {

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
            .baseUrl(BASE_API_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun provideMovieApi(retrofit: Retrofit): MovieApi {
        return retrofit.create(MovieApi::class.java)
    }

    @Provides
    fun provideTvSeriesApi(retrofit: Retrofit): TvSeriesApi{
        return retrofit.create(TvSeriesApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieDatabase(app: Application): MovieDatabase{
        return Room.databaseBuilder(
            app,
            MovieDatabase::class.java,
            "moviedb.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCreditDatabase(app: Application): CreditDatabase{
        return Room.databaseBuilder(
            app,
            CreditDatabase::class.java,
            "creditdb.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDAO(
        movieDatabase: MovieDatabase
    ): MovieDAO{
        return movieDatabase.dao
    }

    @Provides
    @Singleton
    fun provideCreditDAO(
        creditDatabase: CreditDatabase
    ): CreditDAO{
        return creditDatabase.creditDao
    }
}