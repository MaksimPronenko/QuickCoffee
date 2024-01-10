package home.samples.quickcoffee.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import home.samples.quickcoffee.R
import home.samples.quickcoffee.databinding.MenuItemBinding
import home.samples.quickcoffee.models.MenuData

class CafeMenuAdapter(
    val context: Context,
    private val onMinusClick: (Int) -> Unit,
    private val onPlusClick: (Int) -> Unit
) : RecyclerView.Adapter<MenuViewHolder>() {
    private var data: List<MenuData> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<MenuData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(
            MenuItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            if (item != null) {
                Glide
                    .with(image.context)
                    .load(item.imageURL)
                    .into(image)
                coffeeName.text = item.name
                val costText = "${item.price} " + context.getString(R.string.ruble)
                cost.text = costText
            }
        }
        holder.binding.minusButton.setOnClickListener {
            if (item != null) {
                onMinusClick(position)
            }
        }
        holder.binding.plusButton.setOnClickListener {
            if (item != null) {
                onPlusClick(position)
            }
        }
    }
}

class MenuViewHolder(val binding: MenuItemBinding) :
    RecyclerView.ViewHolder(binding.root)