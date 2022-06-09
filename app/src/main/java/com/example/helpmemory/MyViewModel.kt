package com.example.helpmemory

import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {
    var addvalues :ArrayList<MyKeywordData> = ArrayList()
    fun setAddValues(values: MyKeywordData) {
        addvalues.add(values)
    }
}