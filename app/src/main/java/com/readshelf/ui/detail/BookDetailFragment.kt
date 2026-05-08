package com.readshelf.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.readshelf.R
import com.readshelf.data.BookItem
import com.readshelf.data.BookStorage
import com.google.gson.Gson

class BookDetailFragment : Fragment() {

    private lateinit var book: BookItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookJson = arguments?.getString("book_json")
        book = Gson().fromJson(bookJson, BookItem::class.java)

        view.findViewById<TextView>(R.id.tvTitle).text = book.volumeInfo.title
        view.findViewById<TextView>(R.id.tvAuthor).text =
            "Yazar: ${book.volumeInfo.authors?.joinToString(", ") ?: "Bilinmiyor"}"
        view.findViewById<TextView>(R.id.tvPublishedDate).text =
            "Yayın Tarihi: ${book.volumeInfo.publishedDate ?: "Bilinmiyor"}"
        view.findViewById<TextView>(R.id.tvPageCount).text =
            "Sayfa Sayısı: ${book.volumeInfo.pageCount ?: "Bilinmiyor"}"
        view.findViewById<TextView>(R.id.tvDescription).text =
            book.volumeInfo.description ?: "Açıklama yok"

        val coverUrl = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
        Glide.with(this)
            .load(coverUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(view.findViewById<ImageView>(R.id.ivBookCover))

        val btnAdd = view.findViewById<Button>(R.id.btnAddToLibrary)
        updateButton(btnAdd)

        btnAdd.setOnClickListener {
            if (BookStorage.isBookSaved(requireContext(), book.id)) {
                BookStorage.removeBook(requireContext(), book.id)
                Toast.makeText(requireContext(), "Kitaplıktan kaldırıldı", Toast.LENGTH_SHORT).show()
            } else {
                BookStorage.saveBook(requireContext(), book)
                Toast.makeText(requireContext(), "Kitaplığa eklendi", Toast.LENGTH_SHORT).show()
            }
            updateButton(btnAdd)
        }
    }

    private fun updateButton(btn: Button) {
        if (BookStorage.isBookSaved(requireContext(), book.id)) {
            btn.text = "Kitaplıktan Kaldır"
        } else {
            btn.text = "Kitaplığıma Ekle"
        }
    }
}