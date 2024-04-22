package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.databinding.ItemDrawBinding
import com.example.myapplication.databinding.ItemDrawGrid2Binding
import com.example.myapplication.databinding.ItemDrawGridBinding
import com.jakewharton.rxbinding4.view.clicks
import java.util.concurrent.TimeUnit


class ImageAdapter(
    private val gridLayout: Boolean,
    private val limite: Int,
    private var spanCount: Int
) :
    Adapter<ViewHolder>() {
    private val listPath = arrayListOf<String>()
    private var drawListenner: DrawingListenner? = null
    private lateinit var mContext: Context
    private var type = ""
    private lateinit var hanlder: Handler

    fun initData(list: ArrayList<String>, type: String) {
        this.type = type
        listPath.clear()
        listPath.addAll(list)
        notifyDataSetChanged()
    }

    fun setListenner(listenner: DrawingListenner) {
        drawListenner = listenner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        hanlder = Handler(mContext.mainLooper)
        return if (gridLayout) {
            if (spanCount == 3) {
                ViewHolderItemImageGrid(ItemDrawGridBinding.inflate(LayoutInflater.from(parent.context)))
            } else {
                ViewHolderItemImageGrid2(ItemDrawGrid2Binding.inflate(LayoutInflater.from(parent.context)))
            }
        } else {
            ViewHolderItemImage(ItemDrawBinding.inflate(LayoutInflater.from(parent.context)))
        }
    }

    override fun getItemCount(): Int {
        return if (limite == 0) {
            listPath.size
        } else {
            limite
        }

    }

    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (listPath.size != 0) {
            val path = listPath[position]
            if (gridLayout) {
                if (spanCount == 3) {
                    val holderGrid = holder as ViewHolderItemImageGrid
                    holderGrid.bind(path)
                    holderGrid.itemView.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        drawListenner?.OnClickImage(path)
                    }
                } else {
                    val holderGrid = holder as ViewHolderItemImageGrid2
                    holderGrid.bind(path)
                    holderGrid.itemView.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe {
                        drawListenner?.OnClickImage(path)
                    }
                }

            } else {
                val holderGrid = holder as ViewHolderItemImage
                holderGrid.bind(path)
                holderGrid.itemView.clicks().throttleFirst(1, TimeUnit.SECONDS).subscribe {
                    drawListenner?.OnClickImage(path)
                }
            }
        }
    }

    inner class ViewHolderItemImage(val binding: ItemDrawBinding) : ViewHolder(binding.root) {
        fun bind(path: String) {
            Glide.with(mContext)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Không lưu vào cache để load từ asset
                .skipMemoryCache(true) // Không lưu vào bộ nhớ cache để load từ asset
                .transform(CenterCrop(), RoundedCorners(52))
                .into(binding.imgDraw);

        }
    }

    inner class ViewHolderItemImageGrid(val binding: ItemDrawGridBinding) :
        ViewHolder(binding.root) {
        fun bind(path: String) {
            Glide.with(mContext)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Không lưu vào cache để load từ asset
                .skipMemoryCache(true) // Không lưu vào bộ nhớ cache để load từ asset
                .transform(CenterCrop(), RoundedCorners(35))
                .into(binding.imgDraw);
        }
    }

    inner class ViewHolderItemImageGrid2(val binding: ItemDrawGrid2Binding) :
        ViewHolder(binding.root) {
        fun bind(path: String) {
            Glide.with(mContext)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Không lưu vào cache để load từ asset
                .skipMemoryCache(true) // Không lưu vào bộ nhớ cache để load từ asset
                .transform(CenterCrop(), RoundedCorners(20))
                .into(binding.imgDraw);
        }
    }

    interface DrawingListenner {
        fun OnClickImage(path: String)
    }
}