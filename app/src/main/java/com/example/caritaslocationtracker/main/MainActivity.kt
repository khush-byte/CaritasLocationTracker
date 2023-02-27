package com.example.caritaslocationtracker.main

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.*
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.RemoteViews
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.caritaslocationtracker.R
import com.example.caritaslocationtracker.databinding.ActivityMainBinding
import com.example.caritaslocationtracker.widget.MainWidget

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            0
        )
        setContentView(binding.root)
        sharedPreference = getSharedPreferences("LocalMemory", Context.MODE_PRIVATE)
        initAppSate()

        binding.btnStart.setOnClickListener {
            if(checkLocationActivityState()){
                runService()
            }
        }

        binding.btnStop.setOnClickListener {
            stopService()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initAppSate(){
        val context: Context = this
        var widgetText = context.getString(R.string.appwidget_text)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context.packageName, R.layout.main_widget)
        val thisWidget = ComponentName(context, MainWidget::class.java)

        if(sharedPreference.getInt("state", 0)!=0) {
            binding.mainStatusField.text = "tracking in progress..."
            binding.mainStatusField.setTextColor(Color.WHITE)
            binding.btnStart.isEnabled = false
            binding.btnStop.isEnabled = true

            widgetText = "tracking in progress..."
            remoteViews.setTextColor(R.id.appwidget_text, Color.WHITE)

        }else{
            binding.mainStatusField.text = "service is off..."
            binding.mainStatusField.setTextColor(Color.RED)
            binding.btnStart.isEnabled = true
            binding.btnStop.isEnabled = false

            widgetText = "service is off..."
            remoteViews.setTextColor(R.id.appwidget_text, Color.RED)
        }

        remoteViews.setTextViewText(R.id.appwidget_text, widgetText)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    private fun runService() {
        val editor = sharedPreference.edit()
        editor.putInt("state", 1)
        editor.apply()
        initAppSate()
//
//        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            buildAlertMessageNoGps()
//        }else{
//            Intent(applicationContext, LocationService::class.java).apply{
//                action = LocationService.ACTION_START
//                startService(this)
//            }
//        }
    }

    private fun stopService(){
        val editor = sharedPreference.edit()
        editor.putInt("state", 0)
        editor.apply()
        initAppSate()
    }

    private fun checkLocationActivityState(): Boolean{
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        return if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            false
        }else{
            true
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes"
            ) { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No"
            ) { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}