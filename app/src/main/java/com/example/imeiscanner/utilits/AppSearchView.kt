package com.example.imeiscanner.utilits

import androidx.appcompat.widget.SearchView

class AppSearchView(val function: (String) -> Unit) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
        function(query)
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        function(newText)
        return false
    }
}