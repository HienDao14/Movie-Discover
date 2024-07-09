package hiendao.moviefinder.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hiendao.moviefinder.data.local.MovieDAO
import hiendao.moviefinder.data.local.MovieDatabase
import hiendao.moviefinder.data.network.movie.MovieApi
import hiendao.moviefinder.data.repository.MovieRepositoryImp
import hiendao.moviefinder.domain.repository.MovieRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun bindMovieRepository(
        movieDatabase: MovieDatabase,
        movieApi: MovieApi,
        movieDAO: MovieDAO
    ): MovieRepository{
        return MovieRepositoryImp(movieDatabase, movieApi, movieDAO)
    }
}