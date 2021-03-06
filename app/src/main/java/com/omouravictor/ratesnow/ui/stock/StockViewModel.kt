package com.omouravictor.ratesnow.ui.stock

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.omouravictor.ratesnow.api.hgbrasil.SourceRequestStockItemModel
import com.omouravictor.ratesnow.database.entity.StockEntity
import com.omouravictor.ratesnow.repository.StocksRepository
import com.omouravictor.ratesnow.util.DispatcherProvider
import com.omouravictor.ratesnow.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class StockViewModel @ViewModelInject constructor(

    private val repository: StocksRepository,
    private val dispatchers: DispatcherProvider

) : ViewModel() {

    val stocksList = repository.getAllStocksFromDb().asLiveData()

    sealed class StockEvent {
        class Failure(val errorText: String) : StockEvent()
        object Success : StockEvent()
        object Loading : StockEvent()
    }

    private val stockFields = "stocks"
    private val _stocks = MutableStateFlow<StockEvent>(StockEvent.Success)
    val stocks: StateFlow<StockEvent> = _stocks

    fun getStocks() {
        viewModelScope.launch(dispatchers.io) {
            _stocks.value = StockEvent.Loading
            tryStocksFromApi()
        }
    }

    private suspend fun tryStocksFromApi() {
        when (val request = repository.getStocksFromApi(stockFields)) {
            is Resource.Success -> {
                replaceStocksOnDb(request.data!!.sourceResultStocks.resultsStocks)
                _stocks.value = StockEvent.Success
            }
            is Resource.Error -> {
                _stocks.value = StockEvent.Failure("Verifique sua conexão :(")
            }
        }
    }

    private fun replaceStocksOnDb(apiResponse: LinkedHashMap<String, SourceRequestStockItemModel>) {
        val stocksList: MutableList<StockEntity> = mutableListOf()
        apiResponse.forEach {
            stocksList.add(
                StockEntity(
                    it.key,
                    it.value.requestStockName,
                    it.value.requestStockLocation,
                    it.value.requestStockPoints,
                    it.value.requestStockVariation,
                    Date()
                )
            )
        }
        repository.insertStocksOnDb(stocksList)
    }
}