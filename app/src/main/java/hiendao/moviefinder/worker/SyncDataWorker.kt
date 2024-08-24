package hiendao.moviefinder.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import hiendao.moviefinder.MainActivity
import hiendao.moviefinder.R
import hiendao.moviefinder.domain.repository.MovieRepository
import hiendao.moviefinder.domain.repository.TvSeriesRepository
import hiendao.moviefinder.util.Constant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random


@HiltWorker
class SyncDataWorker @AssistedInject constructor(
    private val movieRepository: MovieRepository,
    private val tvSeriesRepository: TvSeriesRepository,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private lateinit var foregroundInfo: ForegroundInfo
    override suspend fun doWork(): Result {
//        setForeground(showNotification())
//        Log.d("Worker", "Show notification 2")

        return withContext(Dispatchers.IO) {
            try {
                syncData()
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is CancellationException) {
                    throw e
                }
                Result.failure()
            }
        }
    }

    private suspend fun syncData() {
        movieRepository.getTopRatedMoviesRemote(page = 1)
        movieRepository.getTrendingDayMoviesRemote(page = 1)
        movieRepository.getTrendingWeekMoviesRemote(page = 1)

        tvSeriesRepository.getPopularTvSeriesRemote(page = 1)
        tvSeriesRepository.getTopRatedTvSeriesRemote(page = 1)

    }

    private fun showNotification(): ForegroundInfo {
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val channel = NotificationChannel("channel_100", "Data sync channel", NotificationManager.IMPORTANCE_HIGH)

        notificationManager.createNotificationChannel(channel)

        val notification =  NotificationCompat.Builder(applicationContext, "channel_100")
            .setSmallIcon(R.drawable.icon_app)
            .setContentText("Sync data...")
            .setContentTitle("Sync in progress")
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, "cancel", intent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setChannelId("channel_100")
            .build()

        notificationManager.notify(101, notification)

        foregroundInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                Random.nextInt(),
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(Random.nextInt(), notification)
        }

        Log.d("Worker", "Show notification 1")
        return foregroundInfo
    }
}