package home.samples.quickcoffee.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import home.samples.quickcoffee.databinding.CafeItemBinding
import home.samples.quickcoffee.models.CafeItem

private const val TAG = "CafeAdapter"

class CafeAdapter(
    val context: Context,
    private val onClick: (CafeItem) -> Unit
) : RecyclerView.Adapter<CafeViewHolder>() {
    private var data: List<CafeItem> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<CafeItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeViewHolder {
        return CafeViewHolder(
            CafeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CafeViewHolder, position: Int) {
        val item = data.getOrNull(position)
        with(holder.binding) {
            item?.let { cafe ->
                cafeName.text = cafe.name
                distance.text = cafe.distance
            }
        }
        holder.binding.root.setOnClickListener {
            if (item != null) {
                onClick(item)
            }
        }
    }
}

class CafeViewHolder(val binding: CafeItemBinding) :
    RecyclerView.ViewHolder(binding.root)