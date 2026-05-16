package com.readshelf.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.readshelf.NotificationReceiver
import com.readshelf.NotificationScheduler
import com.readshelf.R

class SettingsFragment : Fragment() {

    private lateinit var switchNotification: Switch
    private lateinit var timePicker: TimePicker
    private lateinit var btnSaveSettings: Button
    private lateinit var btnTestNotification: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchNotification = view.findViewById(R.id.switchNotification)
        timePicker = view.findViewById(R.id.timePicker)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)
        btnTestNotification = view.findViewById(R.id.btnTestNotification)

        loadSettings()

        btnSaveSettings.setOnClickListener {
            saveSettings()
            Toast.makeText(requireContext(), "Ayarlar kaydedildi", Toast.LENGTH_SHORT).show()
        }

        btnTestNotification.setOnClickListener {
            val intent = Intent(requireContext(), NotificationReceiver::class.java)
            requireContext().sendBroadcast(intent)
            Toast.makeText(requireContext(), "Bildirim gonderildi!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSettings()
    }

    private fun saveSettings() {
        val prefs = requireContext().getSharedPreferences("readshelf_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("notification_enabled", switchNotification.isChecked)
            putInt("notification_hour", timePicker.hour)
            putInt("notification_minute", timePicker.minute)
            apply()
        }

        if (switchNotification.isChecked) {
            NotificationScheduler.scheduleNotification(
                requireContext(),
                timePicker.hour,
                timePicker.minute
            )
        } else {
            NotificationScheduler.cancelNotification(requireContext())
        }
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("readshelf_prefs", Context.MODE_PRIVATE)
        switchNotification.isChecked = prefs.getBoolean("notification_enabled", false)
        timePicker.hour = prefs.getInt("notification_hour", 20)
        timePicker.minute = prefs.getInt("notification_minute", 0)
    }
}