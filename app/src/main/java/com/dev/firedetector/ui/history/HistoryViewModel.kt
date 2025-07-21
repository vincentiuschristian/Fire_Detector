package com.dev.firedetector.ui.history

/*class HistoryViewModel(private val repository: FireRepository) : ViewModel() {
    private val _historyZona1 = MutableLiveData<Result<List<SensorDataResponse>>>()
    val historyZona1: LiveData<Result<List<SensorDataResponse>>> get() = _historyZona1

    private val _historyZona2 = MutableLiveData<Result<List<SensorDataResponse>>>()
    val historyZona2: LiveData<Result<List<SensorDataResponse>>> get() = _historyZona2

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun getHistoryZona1() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getSensorHistoryZona1()
            _historyZona1.postValue(result)
            _loading.value = false
        }
    }

    fun getHistoryZona2() {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.getSensorHistoryZona2()
            _historyZona2.postValue(result)
            _loading.value = false
        }
    }

}*/
