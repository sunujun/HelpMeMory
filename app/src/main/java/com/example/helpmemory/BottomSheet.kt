package com.example.helpmemory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.helpmemory.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: FragmentBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
        val addfolder : Button = view.findViewById(R.id.addfolder)
        val addkeyword : Button = view.findViewById(R.id.addkeyword)

//        val addFolderFragment = AddFolderFragment()

        val addKeywordFragment = AddKeywordFragment()




        addfolder.setOnClickListener {
//            initFragment(addFolderFragment)
            dismiss()//버튼 클릭 후 bottomsheet 닫기

        }

        addkeyword.setOnClickListener {
            initFragment(addKeywordFragment)
            dismiss()//버튼 클릭 후 bottomsheet 닫기
        }

        return view
    }
    private fun initFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.menuFrameLayout, fragment)
            commit()
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}