package com.readshelf.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.readshelf.R
import com.readshelf.data.BookStorage

class ProfileFragment : Fragment() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSaveProfile: Button
    private lateinit var tvTotalBooksProfile: TextView
    private lateinit var tvGoalProfile: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etUsername = view.findViewById(R.id.etUsername)
        etEmail = view.findViewById(R.id.etEmail)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)
        tvTotalBooksProfile = view.findViewById(R.id.tvTotalBooksProfile)
        tvGoalProfile = view.findViewById(R.id.tvGoalProfile)

        loadProfile()

        btnSaveProfile.setOnClickListener {
            saveProfile()
            Toast.makeText(requireContext(), "Profil kaydedildi!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun saveProfile() {
        val prefs = requireContext().getSharedPreferences("readshelf_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("username", etUsername.text.toString().trim())
            putString("email", etEmail.text.toString().trim())
            apply()
        }
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences("readshelf_prefs", Context.MODE_PRIVATE)
        etUsername.setText(prefs.getString("username", ""))
        etEmail.setText(prefs.getString("email", ""))

        val totalBooks = BookStorage.getLibraryBooks(requireContext()).size
        tvTotalBooksProfile.text = totalBooks.toString()

        val goal = prefs.getInt("yearly_goal", 0)
        tvGoalProfile.text = goal.toString()
    }
}