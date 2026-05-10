package com.readshelf.ui.home

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

class HomeFragment : Fragment() {

    private lateinit var tvTotalBooks: TextView
    private lateinit var tvRecentBooks: TextView
    private lateinit var lvRecentBooks: ListView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvTotalBooks = view.findViewById(R.id.tvTotalBooks)
        tvRecentBooks = view.findViewById(R.id.tvRecentBooks)
        lvRecentBooks = view.findViewById(R.id.lvRecentBooks)

        loadHomeData()

        lvRecentBooks.setOnItemClickListener { _, _, position, _ ->
            val books = BookStorage.getLibraryBooks(requireContext())
            val recentBooks = books.takeLast(5).reversed()
            val book = recentBooks[position]
            val bookJson = Gson().toJson(book)
            val bundle = Bundle().apply {
                putString("book_json", bookJson)
            }
            findNavController().navigate(R.id.bookDetailFragment, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        loadHomeData()
    }

    private fun loadHomeData() {
        val books = BookStorage.getLibraryBooks(requireContext())
        tvTotalBooks.text = books.size.toString()

        val recentBooks = books.takeLast(5).reversed()
        tvRecentBooks.text = recentBooks.size.toString()

        val titles = recentBooks.map {
            "${it.volumeInfo.title} - ${it.volumeInfo.authors?.firstOrNull() ?: "Bilinmiyor"}"
        }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, titles)
        lvRecentBooks.adapter = adapter
    }
}