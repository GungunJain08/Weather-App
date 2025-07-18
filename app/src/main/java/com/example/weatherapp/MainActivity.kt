package com.example.weatherapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.search.SearchView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Delhi")
        searchCity()


    }
   //

    private fun searchCity() {
        val searchView = binding.searchView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("SearchDebug", "Search submitted: $query")
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




    private fun fetchWeatherData(cityName:String){

            Log.d("TAG", "fetchWeatherData:() called")
             val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"enter your API key here","metric")

     response.enqueue(object : Callback<WeatherApp>{
         override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
            val responseBody = response.body()
             if(response.isSuccessful && responseBody != null){
                 val temperature = responseBody.main.temp.toString()
                 val humidity = responseBody.main.humidity
                 val windSpeed = responseBody.wind.speed
                 val sunRise = responseBody.sys.sunrise.toLong()
                 val sunSet = responseBody.sys.sunset.toLong()
                 val seaLevel = responseBody.main.pressure
                 val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                 val maxTemp = responseBody.main.temp_max
                 val minTemp = responseBody.main.temp_min

                 //Log.d("TAG",  "onResponse: $temperature")
                 binding.textView5.text = "$temperature °C"
                 binding.textView6.text = condition
                 binding.textView7.text = "Max: $maxTemp °C"
                 binding.textView9.text = "Min: $minTemp °C"

                 binding.sea.text = "$seaLevel hPa"
                 binding.wind.text = "$windSpeed m/s"
                 binding.Humidity.text = "$humidity %"
                 binding.sunrise.text = "${time(sunRise)}"
                 binding.sunset.text ="${time(sunSet)}"
                 binding.textView8.text =dayName(System.currentTimeMillis())
                     binding.textView10.text =date()
                     binding.textView2.text ="$cityName"

                 changeImagesAccordingToWeatherCondition(condition)






             }else{
                 Log.e("TAG", "Response not successful or body is null.")
                 Log.e("TAG", "Response Code: ${response.code()}")
                 Log.e("TAG", "Response Message: ${response.message()}")
                 try {
                     Log.e("TAG", "Error Body: ${response.errorBody()?.string()}")
                 } catch (e: Exception) {
                     Log.e("TAG", "Error reading error body: ${e.message}")
                 }
             }


         }

         override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
             Log.e("RetrofitError", "Failed: ${t.message}")
         }


     })


        }

    private fun changeImagesAccordingToWeatherCondition(conditions:String){
            when(conditions) {
                "Clear Sky", "Sunny", "Clear" -> {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }

                "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                    binding.root.setBackgroundResource(R.drawable.colud_background)
                    binding.lottieAnimationView.setAnimation(R.raw.cloud)
                }

                "Light Rain","Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_background)
                    binding.lottieAnimationView.setAnimation(R.raw.rain)
                }

                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.snow_background)
                    binding.lottieAnimationView.setAnimation(R.raw.snow)
                }

                else -> {
                    binding.root.setBackgroundResource(R.drawable.sunny_background)
                    binding.lottieAnimationView.setAnimation(R.raw.sun)
                }
            }
                binding.lottieAnimationView.playAnimation()

            }
    }

private fun time(timestamp: Long): String{
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp*1000))
}



    private fun date(): String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())

    }
