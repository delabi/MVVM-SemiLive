package com.example.mvvmsemilive.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmsemilive.model.LoadingState
import com.example.mvvmsemilive.model.Order
import com.example.mvvmsemilive.model.OrderDataGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    val ordersLiveData = MediatorLiveData<List<Order>>()
    private val _queryLiveData = MutableLiveData<String>()
    private val _allOrdersLiveData = MutableLiveData<List<Order>>()
    private val _searchOrdersLiveData = MutableLiveData<List<Order>>()
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    private var searchJob: Job? = null
    private val debouncePeriod = 500L

    init {
        ordersLiveData.addSource(_allOrdersLiveData){
            ordersLiveData.value = it
        }

        ordersLiveData.addSource(_searchOrdersLiveData){
            ordersLiveData.value = it
        }
    }

    fun onViewReady(){
        if(_allOrdersLiveData.value.isNullOrEmpty())
            fetchAllOrders()
    }

    private fun fetchAllOrders(){
        loadingStateLiveData.value = LoadingState.LOADING
        viewModelScope.launch(Dispatchers.IO){
            try {
                val orders = OrderDataGenerator.getAllOrders()
                _allOrdersLiveData.postValue(orders)
                loadingStateLiveData.postValue(LoadingState.LOADED)
            } catch (e: Exception) {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
        }
    }

    fun onSearchQuery(query: String){
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(debouncePeriod)
        }
    }
}