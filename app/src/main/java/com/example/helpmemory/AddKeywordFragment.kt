package com.example.helpmemory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.helpmemory.databinding.NewKeywordBinding
import java.util.*


class AddKeywordFragment : Fragment() {
    lateinit var myDBHelper: MyKeywordDBHelper
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
//        initDB()
        return binding.root
    }

//    private fun initDB() {
//        val dbfile = getDatabasePath("mydb.db")
//        if(dbfile.parentFile.exists()){
//            dbfile.parentFile.mkdir()
//        }
//        if(!dbfile.exists()){
//            val file = resources.openRawResource(R.raw.mydb)
//            val fileSize = file.available()
//            val buffer = ByteArray(fileSize)
//            file.read(buffer)
//            file.close()
//            dbfile.createNewFile()
//
//            val output = FileOutputStream(dbfile)
//            output.write(buffer)
//            output.close()
//        }
//    }

    private fun initLayout() {
        myDBHelper = MyKeywordDBHelper(requireContext())
        binding.apply {
            Regbtn.setOnClickListener {
                val keyword = inputKeyword.text.toString()
                val description = inputDescription.text.toString()

                addKeyword(keyword, description)
            }
        }
    }
    private fun getAllRecord(){
        myDBHelper.getAllRecord()
    }

    private fun addKeyword(keyword: String, description:String ){

        val key = keyword
        val des =description
        val id =  UUID.randomUUID().toString()
        val Keyword = MyKeywordData(key, des, id)

        val result = myDBHelper.insertKeyword(Keyword)
        if(result){
            getAllRecord()
            Toast.makeText(requireContext(), "Data INSERT SUCCESS", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireContext(), "Data INSERT FAILED", Toast.LENGTH_SHORT).show()
        }
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