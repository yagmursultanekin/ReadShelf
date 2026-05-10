package com.readshelf.ui.goals

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.readshelf.R
import com.readshelf.data.BookStorage
import java.util.Calendar

class GoalsFragment : Fragment() {

    private lateinit var etGoal: EditText
    private lateinit var btnSetGoal: Button
    private lateinit var datePicker: DatePicker
    private lateinit var tvGoalStatus: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressText: TextView
    private lateinit var tvBadges: TextView
    private lateinit var btnAddToCalendar: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etGoal = view.findViewById(R.id.etGoal)
        btnSetGoal = view.findViewById(R.id.btnSetGoal)
        datePicker = view.findViewById(R.id.datePicker)
        tvGoalStatus = view.findViewById(R.id.tvGoalStatus)
        progressBar = view.findViewById(R.id.progressBar)
        tvProgressText = view.findViewById(R.id.tvProgressText)
        tvBadges = view.findViewById(R.id.tvBadges)
        btnAddToCalendar = view.findViewById(R.id.btnAddToCalendar)

        loadGoal()

        btnSetGoal.setOnClickListener {
            val goalText = etGoal.text.toString().trim()
            if (goalText.isNotEmpty()) {
                val goal = goalText.toInt()
                saveGoal(goal)
                loadGoal()
                Toast.makeText(requireContext(), "Hedef belirlendi: $goal kitap", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Lütfen bir hedef girin", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddToCalendar.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_CALENDAR, android.Manifest.permission.READ_CALENDAR),
                    100
                )
            } else {
                addGoalToCalendar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadGoal()
    }

    private fun addGoalToCalendar() {
        val prefs = requireContext().getSharedPreferences("readshelf_prefs", Context.MODE_PRIVATE)
        val goal = prefs.getInt("yearly_goal", 0)

        if (goal == 0) {
            Toast.makeText(requireContext(), "Önce bir hedef belirleyin", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, datePicker.year)
            set(Calendar.MONTH, datePicker.month)
            set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "📚 ReadShelf Hedefi: $goal Kitap")
            putExtra(CalendarContract.Events.DESCRIPTION, "Bu tarihe kadar $goal kitap okuma hedefimi tamamla!")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis + 3600000)
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Takvim uygulaması bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveGoal(goal: Int) {
        val prefs = requireContext().getSharedPreferences("readshelf_prefs", Context.MODE_PRIVATE)
        prefs.edit().putInt("yearly_goal", goal).apply()
    }

    private fun loadGoal() {
        val prefs = requireContext().getSharedPreferences("readshelf_prefs", Context.MODE_PRIVATE)
        val goal = prefs.getInt("yearly_goal", 0)
        val totalBooks = BookStorage.getLibraryBooks(requireContext()).size

        if (goal > 0) {
            val progress = if (goal > 0) (totalBooks * 100) / goal else 0
            progressBar.progress = progress.coerceAtMost(100)
            tvGoalStatus.text = "Hedef: $goal kitap | Okunan: $totalBooks kitap"
            tvProgressText.text = "%$progress tamamlandı"
            etGoal.setText(goal.toString())

            tvBadges.text = when {
                totalBooks >= goal -> "🏆 Hedefini tamamladın!"
                totalBooks >= goal * 0.75 -> "🥈 Hedefe çok yakınsın!"
                totalBooks >= goal * 0.50 -> "🥉 Yarı yoldasın!"
                totalBooks > 0 -> "🌱 Yolculuk başladı!"
                else -> "Henüz kitap eklenmedi"
            }
        } else {
            tvGoalStatus.text = "Henüz hedef belirlenmedi"
            tvProgressText.text = ""
            tvBadges.text = ""
            progressBar.progress = 0
        }
    }
}