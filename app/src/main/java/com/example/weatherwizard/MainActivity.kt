package com.example.weatherwizard

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwizard.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//e5177abe511133a4b7f8eb8e9f3fe666 API Key
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("mumbai")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName , "e5177abe511133a4b7f8eb8e9f3fe666" , "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val windSpeed = responseBody.wind.speed
                    val seaLevel = responseBody.main.sea_level
                    val minTemp = responseBody.main.temp_min
                    val maxTemp = responseBody.main.temp_max
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"

                    binding.temp.text = "$temperature ℃"
                    binding.weather.text = condition
                    binding.minTemp.text = "Min Temp: $minTemp ℃"
                    binding.maxTemp.text = "Max Temp: $maxTemp ℃"
                    binding.humidity.text = "$humidity %"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.conditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text = date()
                    binding.city.text = "$cityName"

                    changeImageWithWeather(condition)

                }
            }

            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {

            }

        })

        }

    private fun changeImageWithWeather(conditions: String) {
        when (conditions){
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Moderate rain", "Rain", "Drizzle", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snowy)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }else -> {
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun time(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun date(): String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}