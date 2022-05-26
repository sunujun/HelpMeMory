package com.example.helpmemory

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.helpmemory.databinding.TodoItemRowBinding

class ToDoAdapter (val onClick: (ToDo) -> Unit) :
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    val todos = mutableListOf<ToDo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        return ToDoViewHolder (
            TodoItemRowBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ToDoViewHolder, position: Int) {
        viewHolder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size

    inner class ToDoViewHolder(private val binding: TodoItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onClick(todos[bindingAdapterPosition])
            }
        }

        fun bind(event: ToDo) {
            binding.toDoText.text = event.text
        }
    }
}