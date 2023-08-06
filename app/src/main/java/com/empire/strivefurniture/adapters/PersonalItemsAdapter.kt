package com.empire.strivefurniture.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.empire.strivefurniture.R
import com.empire.strivefurniture.databinding.RvItemBinding
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.ui.itemPages.ItemDetailViewModel

import com.empire.strivefurniture.utils.MethodUtils
import com.empire.strivefurniture.viewModel.AppViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PersonalItemsAdapter : ListAdapter<FurnitureItem, MyViewHolder>(MyDiffUtilCallback()) {

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
            priceView.text = MethodUtils.formatCurrency(item.price) + " UGX"
            locationView.text = item.location
            inventoryId.text = "${item.itemQty} Products"
            Glide.with(root.context).load(item.photo[0]).into(binding.imageView)

            container.setOnClickListener {
                Navigation.findNavController(itemView)
                    .navigate(R.id.action_global_itemDetailFragment, bundleOf("item" to item))
            }

            container.setOnLongClickListener {
                showDeleteDialog(item, root.context!!)
                true
            }
        }
    }

    private fun showDeleteDialog(item: FurnitureItem, context: Context) {
        MaterialAlertDialogBuilder(context).apply {
            setTitle("Do you want to delete this item?")
            setMessage("Once an item is deleted, it cannot be recovered. Press Ok if you are sure you want to delete this item.")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Ok") { _, _ ->
//                deleteItem(item)
            }
        }.show()
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