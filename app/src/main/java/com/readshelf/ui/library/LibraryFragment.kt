package com.readshelf.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.readshelf.R
import com.readshelf.data.BookStorage

class LibraryFragment : Fragment() {

    private lateinit var lvLibrary: ListView
    private lateinit var tvEmpty: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lvLibrary = view.findViewById(R.id.lvLibrary)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        loadLibrary()

        lvLibrary.setOnItemClickListener { _, _, position, _ ->
            val books = BookStorage.getLibraryBooks(requireContext())
            val book = books[position]
            val bookJson = Gson().toJson(book)
            val bundle = Bundle().apply {
                putString("book_json", bookJson)
            }
            findNavController().navigate(R.id.bookDetailFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        loadLibrary()
    }

    private fun loadLibrary() {
        val books = BookStorage.getLibraryBooks(requireContext())
        if (books.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            lvLibrary.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            lvLibrary.visibility = View.VISIBLE
            val titles = books.map {
                "${it.volumeInfo.title} - ${it.volumeInfo.authors?.firstOrNull() ?: "Bilinmiyor"}"
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, titles)
            lvLibrary.adapter = adapter
        }
    }
}