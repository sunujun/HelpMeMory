package com.example.helpmemory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmemory.databinding.KeywordItemRowBinding


class KeywordAdapter (private val values: ArrayList<MyKeywordData>) : RecyclerView.Adapter<KeywordAdapter.FolderViewHolder>() {
    interface OnDelBtnClickListener{
        fun OnDelBtnClickListener(data:MyKeywordData, descriptionView: TextView)
    }
    interface OnItemClickListener{
        fun OnItemClick(data:MyKeywordData, descriptionView: TextView)
    }

    fun moveItem(oldPos: Int, newPos: Int) {
        val item = values[oldPos]
        values.removeAt(oldPos)
        values.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos: Int) {
        values.removeAt(pos)
        notifyItemRemoved(pos)
    }

    var itemClickListener:OnItemClickListener?=null
    var delBtnClickListner:OnDelBtnClickListener?=null
    override fun getItemCount(): Int = values.size

    inner class FolderViewHolder(val binding : KeywordItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.textView.setOnClickListener {
                itemClickListener?.OnItemClick(
                    values[bindingAdapterPosition],
                    binding.descriptionView
                )
                //수업에서 다뤘던 영어단어장에서는 bindingAdapterPosition이 아닌 adapterPosition을 사용했다.
            }
            binding.deletebtn.setOnClickListener {
                delBtnClickListner?.OnDelBtnClickListener(values[bindingAdapterPosition],binding.descriptionView)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = KeywordItemRowBinding.inflate(LayoutInflater.from(parent.context),parent, false)

        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.binding.textView.text = values[position].keyword
        holder.binding.descriptionView.text = values[position].description

        if (!values[position].isClicked) {
            holder.binding.descriptionView.visibility = View.GONE
        } else {
            holder.binding.descriptionView.text = values[position].description
            holder.binding.descriptionView.visibility = View.VISIBLE

        }

//        holder.binding.deletebtn.setOnClickListener {
//            MyKeywordDBHelper.deleteKeyword(values[position].id)
//        }

    }
}

