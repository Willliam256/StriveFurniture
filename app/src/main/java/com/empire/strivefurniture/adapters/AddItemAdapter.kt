package com.empire.strivefurniture.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.empire.strivefurniture.R

class ImageAdapter(private val context: Context, private val images: List<Uri>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.imageViewGridItem)
        val imageUri = images[position]
        imageView.setImageURI(imageUri)
        return view
    }

    override fun getItem(position: Int): Any {
        return images[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return images.size
    }
}