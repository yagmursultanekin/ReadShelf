package com.readshelf.ui.goals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.readshelf.R
import com.readshelf.data.BookStorage

class GoalsFragment : Fragment() {

    private lateinit var etGoal: EditText
    private lateinit var btnSetGoal: Button
    private lateinit var tvGoalStatus: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressText: TextView
    private lateinit var tvBadges: TextView

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
        tvGoalStatus = view.findViewById(R.id.tvGoalStatus)
        progressBar = view.findViewById(R.id.progressBar)
        tvProgressText = view.findViewById(R.id.tvProgressText)
        tvBadges = view.findViewById(R.id.tvBadges)

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
    }

    override fun onResume() {
        super.onResume()
        loadGoal()
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