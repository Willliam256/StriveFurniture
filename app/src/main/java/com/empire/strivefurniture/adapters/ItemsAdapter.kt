package com.empire.strivefurniture.ui.homePage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.empire.strivefurniture.R
import com.empire.strivefurniture.databinding.RvItemBinding
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.utils.MethodUtils.formatCurrency

class ItemsAdapter : ListAdapter<FurnitureItem, MyViewHolder>(MyDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            binding = RvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(item = getItem(position))
    }
}

class MyViewHolder(val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FurnitureItem) {
        binding.apply {
            nameView.text = item.name
            priceView.text = formatCurrency(item.price) + " UGX"
            locationView.text = item.location
            Glide.with(root.context).load(item.photo[0]).into(imageView)

            container.setOnClickListener {
                Navigation.findNavController(itemView)
                    .navigate(R.id.action_global_itemDetailFragment, bundleOf("item" to item))
            }
        }
    }
}


class MyDiffUtilCallback : DiffUtil.ItemCallback<FurnitureItem>() {
    override fun areItemsTheSame(oldItem: FurnitureItem, newItem: FurnitureItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FurnitureItem, newItem: FurnitureItem): Boolean {
        return oldItem.id == newItem.id
    }
}