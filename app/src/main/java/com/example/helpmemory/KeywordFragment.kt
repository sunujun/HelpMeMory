package com.example.helpmemory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmemory.databinding.FragmentKeywordBinding

class KeywordFragment : Fragment() {
    private lateinit var binding: FragmentKeywordBinding
    private var data: ArrayList<MyKeywordData> = ArrayList()
    private lateinit var adapter: FolderAdapter
    val myViewModel: MyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKeywordBinding.inflate(layoutInflater)
        initData()
        initRecyclerview()
        return binding.root
    }

    private fun initRecyclerview() {
        binding.folderlist.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        adapter = FolderAdapter(data)
        adapter.itemClickListener = object : FolderAdapter.OnItemClickListener {
            override fun OnItemClick(data: MyKeywordData, descriptionView: TextView) {
                if (data.isClicked) {
                    data.isClicked = false
                    descriptionView.visibility = View.GONE
                } else {
                    data.isClicked = true
                    descriptionView.visibility = View.VISIBLE
                }
            }
        }

        binding.folderlist.adapter = adapter

        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or
                        ItemTouchHelper.DOWN, ItemTouchHelper.LEFT
            ) {
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

        itemTouchHelper.attachToRecyclerView(binding.folderlist)
    }

//    fun readFileScan(scan:Scanner){
//        while(scan.hasNextLine()){
//            val word = scan.nextLine()
//            val meaning = scan.nextLine()
//
//            data.add(MyKeywordData(word, meaning))
//        }
//    }


    private fun initData() {

        if (myViewModel.addvalues.size > 0) {
            for (i in 0 until myViewModel.addvalues.size) {
                if (myViewModel.addvalues.size > i) {
                    data.add(i, myViewModel.addvalues[i])
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFolOrKey.setOnClickListener {
            val bottomSheet = BottomSheet()

            bottomSheet.show(parentFragmentManager, bottomSheet.tag)

        }
    }

}