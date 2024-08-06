package hiendao.moviefinder.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hiendao.moviefinder.data.local.CreditDAO
import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.local.MovieDatabase
import hiendao.moviefinder.data.local.TvSeriesDAO
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.data.network.search.SearchApi
import hiendao.moviefinder.data.network.tvseries.TvSeriesApi
import hiendao.moviefinder.data.repository.CommonRepositoryImp
import hiendao.moviefinder.data.repository.FavoriteRepositoryImp
import hiendao.moviefinder.data.repository.MovieRepositoryImp
import hiendao.moviefinder.data.repository.SearchRepositoryImp
import hiendao.moviefinder.data.repository.TvSeriesRepositoryImp
import hiendao.moviefinder.domain.repository.CommonRepository
import hiendao.moviefinder.domain.repository.FavoriteRepository
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.domain.repository.SearchRepository
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun bindMovieRepository(
        movieApi: MovieApi,
        movieDAO: MovieDAO,
        creditDAO: CreditDAO,
        tvSeriesDAO: TvSeriesDAO
    ): MovieRepository{
        return MovieRepositoryImp(movieApi, movieDAO, creditDAO, tvSeriesDAO)
    }

    @Provides
    @Singleton
    fun bindTvSeriesRepository(
        tvSeriesApi: TvSeriesApi,
        tvSeriesDAO: TvSeriesDAO
    ): TvSeriesRepository{
        return TvSeriesRepositoryImp(tvSeriesApi, tvSeriesDAO)
    }

    @Provides
    @Singleton
    fun bindCommonRepository(
        movieApi: MovieApi,
        movieDAO: MovieDAO,
        creditDAO: CreditDAO
    ): CommonRepository{
        return CommonRepositoryImp(movieApi, movieDAO, creditDAO)
    }

    @Provides
    @Singleton
    fun bindSearchRepository(
        searchApi: SearchApi,
        movieDAO: MovieDAO
    ): SearchRepository{
        return SearchRepositoryImp(searchApi, movieDAO)
    }

    @Provides
    @Singleton
    fun bindFavoriteRepository(
        movieDAO: MovieDAO,
        creditDAO: CreditDAO
    ): FavoriteRepository{
        return FavoriteRepositoryImp(movieDAO, creditDAO)
    }
}