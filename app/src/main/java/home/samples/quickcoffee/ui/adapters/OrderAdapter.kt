package home.samples.quickcoffee.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import home.samples.quickcoffee.R
import home.samples.quickcoffee.databinding.OrderItemBinding
import home.samples.quickcoffee.models.OrderItem

private const val TAG = "OrderAdapter"

class OrderAdapter(
    val context: Context,
    private val onMinusClick: (Int) -> Unit,
    private val onPlusClick: (Int) -> Unit
) : RecyclerView.Adapter<OrderViewHolder>() {
    private var data: List<OrderItem> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<OrderItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            item?.let {
                name.text = it.name
                val priceText = "${it.priceSum} " + context.getString(R.string.ruble)
                priceSum.text = priceText
                quantity.text = it.quantity.toString()
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

class OrderViewHolder(val binding: OrderItemBinding) :
    RecyclerView.ViewHolder(binding.root)