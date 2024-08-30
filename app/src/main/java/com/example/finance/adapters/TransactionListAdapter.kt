package com.example.finance.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.finance.R
import com.example.finance.entities.TransactionEntity
import com.example.finance.entities.TransactionType
import com.example.finance.entities.fromDate

class TransactionListAdapter(
    private val transactions: List<TransactionEntity>
) : RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeTextView: TextView = view.findViewById(R.id.tvType)
        val detailTextView: TextView = view.findViewById(R.id.tvDetail)
        val valueTextView: TextView = view.findViewById(R.id.tvValue)
        val dateTextView: TextView = view.findViewById(R.id.tvDate)
        val cardLayout: LinearLayout = view.findViewById(R.id.ll_transaction_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_card, parent, false)
        return TransactionViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction: TransactionEntity = transactions[position]

        holder.typeTextView.text = transaction.type.type
        holder.detailTextView.text = transaction.detail
        holder.valueTextView.text = "R$ %.2f".format(transaction.value)
        holder.dateTextView.text = transaction.date.fromDate()

        if (transaction.type == TransactionType.CREDIT) {
            holder.cardLayout.setBackgroundResource(R.color.green)

        } else {
            holder.cardLayout.setBackgroundResource(R.color.red)


        }
    }
}
