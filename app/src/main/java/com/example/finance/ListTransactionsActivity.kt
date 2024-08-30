package com.example.finance

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finance.adapters.TransactionListAdapter
import com.example.finance.database.DatabaseHandler
import com.example.finance.databinding.ActivityListTransactionsBinding
import com.example.finance.entities.TransactionEntity

class ListTransactionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListTransactionsBinding
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListTransactionsBinding.inflate(layoutInflater)
        db = DatabaseHandler(this)
        setContentView(binding.root)

        val recyclerView = binding.transactionList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()

        val transactions: List<TransactionEntity> = db.list()

        val adapter = TransactionListAdapter(transactions)
        binding.transactionList.adapter = adapter

    }
}
