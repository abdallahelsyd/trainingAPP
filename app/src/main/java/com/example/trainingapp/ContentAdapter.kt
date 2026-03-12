package com.example.trainingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.trainingapp.models.Item

class ContentAdapter(private val onclick:(item:Item)-> Unit)
    : ListAdapter<Item, ContentAdapter.ViewHolder>(ItemDiffCallBack) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.liblary_item,parent,false)
        return ViewHolder(view,onclick)
    }

    override fun onBindViewHolder(
        viweHolder: ViewHolder,
        position: Int
    ) {
        viweHolder.bind(getItem(position))
    }

    class ViewHolder(view: View,val onclick: (item: Item) -> Unit) : RecyclerView.ViewHolder(view){
        fun bind(item: Item){
            itemView.setOnClickListener{
                onclick(item)
            }
        }
    }
    object ItemDiffCallBack : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(old: Item, new: Item) = old.id == new.id
        override fun areContentsTheSame(old: Item, new: Item) = old.id == new.id

    }
}