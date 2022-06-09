package com.example.helpmemory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmemory.databinding.FragmentKeywordBinding

class KeywordFragment : Fragment() {
    private var binding: FragmentKeywordBinding? = null

    lateinit var myDBHelper: MyKeywordDBHelper
    var data:ArrayList<MyKeywordData> = ArrayList()
    private lateinit var adapter : KeywordAdapter
    var id = ""
    val myViewModel : MyViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentKeywordBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myDBHelper = MyKeywordDBHelper(requireContext())

        getAllRecord()

        initRecyclerview()

        return binding!!.root
    }

    private fun getAllRecord(){
        myDBHelper.getAllRecord()
    }



    fun initRecyclerview() {
        if(myDBHelper.keywordData.size != data.size) {
            if(myDBHelper.keywordData.size > 0)
                data.addAll(myDBHelper.keywordData)
        }else if(myDBHelper.addKeywordcheck){
            data.addAll(myDBHelper.keywordData)
            myDBHelper.addKeywordcheck = false
        }
        binding!!.folderlist.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)
        Log.d("initRecyclerview",data.toString())
        adapter = KeywordAdapter(data)
        adapter.itemClickListener = object : KeywordAdapter.OnItemClickListener{
            override fun OnItemClick(data: MyKeywordData, descriptionView: TextView) {
                id = data.id
                if(data.isClicked){
                    data.isClicked = false
                    descriptionView.visibility = View.GONE
                }else{
                    data.isClicked = true
                    descriptionView.visibility = View.VISIBLE
                }
            }
        }

        adapter.delBtnClickListner = object : KeywordAdapter.OnDelBtnClickListener{
            override fun OnDelBtnClickListener(mydata: MyKeywordData, descriptionView: TextView) {
                val result = myDBHelper.deleteKeyword(mydata.id)
                if(result){
                    data.clear()
                    myDBHelper.keywordData.clear()
                    getAllRecord()
                    data.addAll(myDBHelper.keywordData)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Data DELETE SUCCESS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "Data DELETE SUCCESS", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding!!.folderlist.adapter = adapter


        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN, ItemTouchHelper.LEFT){
            override fun onMove(
                p0: RecyclerView,
                p1: RecyclerView.ViewHolder,
                p2: RecyclerView.ViewHolder
            ): Boolean {
                adapter.moveItem(p1.bindingAdapterPosition, p2.bindingAdapterPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem((viewHolder.bindingAdapterPosition))
            }

        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        itemTouchHelper.attachToRecyclerView(binding!!.folderlist)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.addFolOrKey.setOnClickListener {
            val bottomSheet = BottomSheet()

            bottomSheet.show(parentFragmentManager, bottomSheet.tag)

        }
    }


}