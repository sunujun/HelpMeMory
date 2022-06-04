package com.example.helpmemory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.helpmemory.databinding.NewKeywordBinding


class AddKeywordFragment : Fragment() {

    lateinit var binding : NewKeywordBinding
    val keywordData = mutableListOf<List<MyKeywordData>>()
    val myViewModel : MyViewModel by activityViewModels()
    var index = 0
    val keywordFragment = KeywordFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =  NewKeywordBinding.inflate(layoutInflater)
        initLayout()
        return binding.root
    }

    private fun initLayout() {
        binding.apply {
            Regbtn.setOnClickListener {
                val keyword = inputKeyword.text.toString()
                val description = inputDescription.text.toString()

                addKeyword(keyword, description)
            }

        }


    }

    private fun addKeyword(keyword: String, description:String ){
        myViewModel.setAddValues(MyKeywordData(keyword, description))
        initFragment(keywordFragment)
    }

    private fun initFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.menuFrameLayout, fragment)
            commit()
        }
    }

//    private fun writeFile(keyword: String, description:String){
//        val output = PrintStream(requireActivity().openFileOutput("out.txt", Context.MODE_APPEND))
//        output.println(keyword)
//        output.println(description)
//        output.close()
//        initFragment(keywordFragment)
//    }

}