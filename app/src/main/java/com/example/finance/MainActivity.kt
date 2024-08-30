package com.example.finance

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finance.database.DatabaseHandler
import com.example.finance.databinding.ActivityMainBinding
import com.example.finance.entities.TransactionEntity
import com.example.finance.entities.TransactionType
import com.example.finance.entities.toDate
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DatabaseHandler(this)

        setupSpinners()
        setButtonListeners()
        setupDateFormatter(binding.etDate)
    }

    private fun setButtonListeners() {
        binding.btnHistory.setOnClickListener { showHistory() }
        binding.btnBalance.setOnClickListener { showBalance() }
        binding.btnSave.setOnClickListener { saveTransaction() }
    }

    private fun showHistory() {
        val intent = Intent(this, ListTransactionsActivity::class.java)
        startActivity(intent)
    }

    private fun showBalance() {
        val balance = db.getBalance()
        Toast.makeText(
            this,
            getString(R.string.current_balance).format(balance),
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun saveTransaction() {
        val type = binding.spinnerType.selectedItem.toString()
        val detail = binding.spinnerDetail.selectedItem.toString()
        val value = binding.etValue.text.toString().toDoubleOrNull()
        val date = binding.etDate.text.toString()

        if (value != null && date.isNotEmpty() && detail.isNotEmpty() && type.isNotEmpty()) {
            try {
                val formattedDate = date.toDate()
                if (formattedDate != null) {
                    db.insert(
                        TransactionEntity(
                            0,
                            TransactionType.fromType(type),
                            detail,
                            value,
                            formattedDate
                        )
                    )
                    Toast.makeText(
                        this,
                        getString(R.string.transaction_save_success), Toast.LENGTH_SHORT
                    )
                        .show()
                    binding.etValue.text.clear()
                    binding.etDate.text.clear()
                } else {
                    Toast.makeText(this, getString(R.string.invalid_date), Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.transaction_save_error), Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupSpinners() {
        val spinnerType: Spinner = binding.spinnerType
        val spinnerDetail: Spinner = binding.spinnerDetail

        val types = arrayOf(getString(R.string.credit), getString(R.string.debit))
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = typeAdapter

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedType = types[position]
                val details = when (selectedType) {
                    getString(R.string.credit) -> arrayOf("Salário", "Extra", "Dividendos")
                    getString(R.string.debit) -> arrayOf(
                        getString(R.string.health),
                        getString(R.string.education),
                        getString(R.string.transport),
                        getString(R.string.leisure),
                    )

                    else -> arrayOf()
                }
                val detailAdapter =
                    ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, details)
                detailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDetail.adapter = detailAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    private fun setupDateFormatter(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = getString(R.string.date_format)
            private val cal = java.util.Calendar.getInstance()

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    var clean = s.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")

                    val cl = clean.length
                    var sel = cl
                    for (i in 2..cl step 2) {
                        sel++
                    }

                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean += ddmmyyyy.substring(clean.length)
                    } else {
                        val day = clean.substring(0, 2).toIntOrNull()
                        val mon = clean.substring(2, 4).toIntOrNull()
                        val year = clean.substring(4, 8).toIntOrNull()

                        if (day != null && mon != null && year != null &&
                            mon in 1..12 && year in 1900..2100 &&
                            day <= cal.apply {
                                set(java.util.Calendar.MONTH, mon - 1)
                                set(java.util.Calendar.YEAR, year)
                            }.getActualMaximum(java.util.Calendar.DATE)
                        ) {
                            cal.set(java.util.Calendar.DAY_OF_MONTH, day)
                            clean = String.format("%02d%02d%04d", day, mon, year)
                        } else {
                            clean = ddmmyyyy
                        }
                    }

                    clean = String.format(
                        "%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8)
                    )

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    editText.setText(current)
                    editText.setSelection(if (sel < current.length) sel else current.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }
}
