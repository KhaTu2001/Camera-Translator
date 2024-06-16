package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemTranslateResultBinding

class TranslateResultAdapter(
    private val nomArrayList:ArrayList<String> ,
    private var resultArrayList:ArrayList<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolderItem(ItemTranslateResultBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return nomArrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val holderGrid = holder as ViewHolderItem
        holderGrid.bind(position)

    }
    inner class ViewHolderItem(val binding: ItemTranslateResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
           binding.textResult.text = nomArrayList[position] + " --> "+resultArrayList[position]
        }
    }
}