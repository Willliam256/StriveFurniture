package com.empire.strivefurniture.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.empire.strivefurniture.databinding.RvCartItemBinding
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.ui.homePage.MyDiffUtilCallback
import com.empire.strivefurniture.utils.FirebaseUtils.addItemsToSold
import com.empire.strivefurniture.utils.FirebaseUtils.removeCartItem
import com.empire.strivefurniture.utils.MethodUtils.formatCurrency
import com.empire.strivefurniture.utils.MethodUtils.makePhoneCall

class CartItemAdapter :
    ListAdapter<FurnitureItem, CartItemAdapter.MyViewHolder>(MyDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            binding = RvCartItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false

            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyViewHolder(val binding: RvCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FurnitureItem) {
            binding.apply {
                Glide.with(root.context).load(item.photo[0]).into(image)
                name.text = item.name
                price.text = formatCurrency(item.price)
                sellerName.text = "Sold by : ${item.sellerName}"
                markBought.setOnClickListener {
                    val removeItem = removeCartItem(item)
                    val addItem = addItemsToSold(item, item.seller)
                    if (removeItem) {
                        Toast.makeText(root.context, "Completed", Toast.LENGTH_SHORT).show()
                    }
                }
                contactSeller.setOnClickListener {
                    //todo: make call
                    makePhoneCall(item.contact, root.context)

                }
            }
        }
    }

}