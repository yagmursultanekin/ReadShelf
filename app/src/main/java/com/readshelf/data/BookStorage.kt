package com.readshelf.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object BookStorage {
    private const val PREF_NAME = "readshelf_prefs"
    private const val KEY_LIBRARY = "library_books"
    private val gson = Gson()

    fun saveBook(context: Context, book: BookItem) {
        val books = getLibraryBooks(context).toMutableList()
        if (books.none { it.id == book.id }) {
            books.add(book)
            saveBooks(context, books)
        }
    }

    fun removeBook(context: Context, bookId: String) {
        val books = getLibraryBooks(context).toMutableList()
        books.removeAll { it.id == bookId }
        saveBooks(context, books)
    }

    fun getLibraryBooks(context: Context): List<BookItem> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LIBRARY, null) ?: return emptyList()
        val type = object : TypeToken<List<BookItem>>() {}.type
        return gson.fromJson(json, type)
    }

    fun isBookSaved(context: Context, bookId: String): Boolean {
        return getLibraryBooks(context).any { it.id == bookId }
    }

    private fun saveBooks(context: Context, books: List<BookItem>) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LIBRARY, gson.toJson(books)).apply()
    }
}