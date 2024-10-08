package hiendao.moviefinder.data.network.util.interceptor

import hiendao.moviefinder.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class MyInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = BuildConfig.ACCESS_TOKEN
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(request)
    }
}