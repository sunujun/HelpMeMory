package com.example.helpmemory


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.helpmemory.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var firstKeyFrag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toDoFragment = ToDoFragment()
        val keywordFragment = KeywordFragment()
        initFragment(toDoFragment)


        binding.menuBottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.toDo -> initFragment(toDoFragment)
                R.id.keyword -> initFragment(keywordFragment)
            }
            true
        }
    }

    private fun initFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.menuFrameLayout, fragment)
            commit()
        }
    }

}