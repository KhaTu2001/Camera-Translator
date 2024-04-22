package com.example.myapplication.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val coloredDrawing: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val anime: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val chibi: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val food: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val sketch: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val anatomy: MutableLiveData<ArrayList<String>> = MutableLiveData()
    val listPhoto = MutableLiveData<ArrayList<String>>()
    val listImageByCategoty = MutableLiveData<ArrayList<String>>()

    fun loadDataImage(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            FileRepository.getListImageDrawingByCategory(
                context,
                "colored_drawings"
            ).let {
                CoroutineScope(Dispatchers.Main).launch {
                    coloredDrawing.value = it
                }

            }
            FileRepository.getListImageDrawingByCategory(context, "Anime")
                .let {
                    CoroutineScope(Dispatchers.Main).launch {
                        anime.value = it
                    }

                }
            FileRepository.getListImageDrawingByCategory(context, "Chibi")
                .let {
                    CoroutineScope(Dispatchers.Main).launch {
                        chibi.value = it
                    }

                }
            FileRepository.getListImageDrawingByCategory(context, "Food")
                .let {
                    CoroutineScope(Dispatchers.Main).launch {
                        food.value = it
                    }

                }
            FileRepository.getListImageDrawingByCategory(
                context,
                "Sketch"
            ).let {
                CoroutineScope(Dispatchers.Main).launch {
                    sketch.value = it
                }

            }
            FileRepository.getListImageDrawingByCategory(
                context,
                "Anatomy"
            ).let {
                CoroutineScope(Dispatchers.Main).launch {
                    anatomy.value = it
                }

            }
        }
    }

    fun loadImageByCategory(context: Context, category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            FileRepository.getListImageDrawingByCategory(context, category).let {
                CoroutineScope(Dispatchers.Main).launch {
                    listImageByCategoty.value = it
                }

            }
        }

    }

    fun loadPhoto(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            FileRepository.getAllImages(context).let {
                CoroutineScope(Dispatchers.Main).launch {
                    listPhoto.value = it
                }

            }
        }

    }
}