package com.example.weatherapp

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception


class CurrentWeatherResponseController(): Callback<CurrentWeatherResponse> {

    private val BASE_URL = "http://api.openweathermap.org/"

    private var response: Observable<CurrentWeatherResponse>? = null
    val gson = GsonBuilder()
            .setLenient()
            .create()
    val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    val apiRequests = retrofit.create(ApiRequests::class.java)

    fun getWeather(city: String, result : MethodChannel.Result)  {
        apiCall(city)
        response?.subscribe {
            result.success(gson.toJson(it))
        }
    }

    private fun apiCall(location: String?) {
        Log.d(TAG, "Weather apiCall start with Location $location")
        location?.let {
            val call: Call<CurrentWeatherResponse> = apiRequests.getCurrentWeatherData(it)
            call.enqueue(this)
        }
    }

    override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
        Log.e("TAG", t.message)
    }

    override fun onResponse(call: Call<CurrentWeatherResponse>, response: Response<CurrentWeatherResponse>) {
        this.response = Observable.create {
            try {
                it.onNext(response.body())
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    companion object {
        const val TAG = "WeatherController"
    }
}
