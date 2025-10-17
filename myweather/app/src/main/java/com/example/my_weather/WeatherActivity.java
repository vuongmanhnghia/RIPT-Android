package com.example.my_weather;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeatherActivity extends AppCompatActivity {

    private TextView tvCityName, tvTemperature, tvHumidity, tvCondition;
    private ImageView ivWeatherIcon;
    private Button btnBack;

    // Simulated weather data
    private static class WeatherData {
        String condition;
        int temperature;
        int humidity;
        int iconResId;

        WeatherData(String condition, int temperature, int humidity, int iconResId) {
            this.condition = condition;
            this.temperature = temperature;
            this.humidity = humidity;
            this.iconResId = iconResId;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // Initialize views
        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvCondition = findViewById(R.id.tvCondition);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        btnBack = findViewById(R.id.btnBack);

        // Get city name from intent
        String cityName = getIntent().getStringExtra("CITY_NAME");
        if (cityName != null) {
            tvCityName.setText(cityName);
            displayWeatherInfo(cityName);
        }

        // Back button listener
        btnBack.setOnClickListener(v -> finish());
    }

    private void displayWeatherInfo(String cityName) {
        // Generate simulated weather data
        WeatherData weatherData = generateWeatherData(cityName);

        // Display weather information
        tvCondition.setText(weatherData.condition);
        tvTemperature.setText(weatherData.temperature + "°C");
        tvHumidity.setText(weatherData.humidity + "%");
        ivWeatherIcon.setImageResource(weatherData.iconResId);
    }

    private WeatherData generateWeatherData(String cityName) {
        // Create weather conditions array
        String[] conditions = {"Trời nắng", "Trời nhiều mây", "Trời mưa"};
        int[] icons = {R.drawable.ic_sunny, R.drawable.ic_cloudy, R.drawable.ic_rainy};

        // Use city name hashcode to generate consistent random data for each city
        Random random = new Random(cityName.hashCode());

        int conditionIndex = random.nextInt(conditions.length);
        String condition = conditions[conditionIndex];
        int iconResId = icons[conditionIndex];

        // Generate temperature between 20-35°C
        int temperature = 20 + random.nextInt(16);

        // Generate humidity between 50-90%
        int humidity = 50 + random.nextInt(41);

        return new WeatherData(condition, temperature, humidity, iconResId);
    }
}

