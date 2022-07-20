package com.omouravictor.ratesnow

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.omouravictor.ratesnow.adapter.ConversionAdapter
import com.omouravictor.ratesnow.databinding.ActivityMainBinding
import com.omouravictor.ratesnow.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEtAmount()
        initSpFromCurrency()
        initConversionsRecyclerView()

        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.rvConversions.isVisible = true
                        (binding.rvConversions.adapter as ConversionAdapter).setList(event.conversionsList)
                    }
                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.rvConversions.isVisible = false
                        Toast.makeText(applicationContext, event.errorText, Toast.LENGTH_SHORT)
                            .show()
                    }
                    is MainViewModel.CurrencyEvent.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.rvConversions.isVisible = false
                    }
                    is MainViewModel.CurrencyEvent.Empty -> {
                        binding.rvConversions.isVisible = false
                    }
                }
            }
        }
    }

    private fun initEtAmount() {
        binding.etAmount.setText("1")
        binding.etAmount.setSelection(1)
        binding.etAmount.doAfterTextChanged { text ->
            viewModel.convertFromDb(
                binding.spFromCurrency.selectedItemPosition,
                text.toString()
            )
        }
    }

    private fun initSpFromCurrency() {
        binding.spFromCurrency.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                viewModel.convertFromApi(
                    position,
                    binding.etAmount.text.toString()
                )
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    private fun initConversionsRecyclerView() {
        binding.rvConversions.apply {
            adapter = ConversionAdapter(mutableListOf())
            layoutManager = LinearLayoutManager(context)
        }
    }
}