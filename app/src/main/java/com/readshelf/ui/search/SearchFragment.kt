package com.readshelf.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.readshelf.R
import com.readshelf.data.BookItem
import com.readshelf.data.BookResponse
import com.readshelf.data.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var lvBooks: ListView
    private val bookList = mutableListOf<BookItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        btnSearch = view.findViewById(R.id.btnSearch)
        lvBooks = view.findViewById(R.id.lvBooks)

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
            } else {
                Toast.makeText(requireContext(), "Lütfen bir kitap adı girin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchBooks(query: String) {
        RetrofitInstance.api.searchBooks(query).enqueue(object : Callback<BookResponse> {
            override fun onResponse(call: Call<BookResponse>, response: Response<BookResponse>) {
                if (response.isSuccessful) {
                    bookList.clear()
                    response.body()?.items?.let { bookList.addAll(it) }
                    val titles = bookList.map {
                        "${it.volumeInfo.title} - ${it.volumeInfo.authors?.firstOrNull() ?: "Bilinmiyor"}"
                    }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, titles)
                    lvBooks.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Hata: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}