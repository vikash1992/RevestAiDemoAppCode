package com.revest.ecommerce.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revest.ecommerce.domain.model.Product
import com.revest.ecommerce.domain.model.ProductResult
import com.revest.ecommerce.domain.usecase.ProductUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productUseCases: ProductUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var getProductsJob: Job? = null

    init {
        getCategories()
        getProducts()
    }

    fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.Refresh -> {
                getProducts(forceRefresh = true)
            }
            is ProductListEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                searchProducts(event.query)
            }
            is ProductListEvent.CategorySelected -> {
                _uiState.update { it.copy(selectedCategory = event.category) }
                if (event.category != null) {
                    getProductsByCategory(event.category)
                } else {
                    getProducts()
                }
            }
            ProductListEvent.ClearSearch -> {
                _uiState.update { it.copy(searchQuery = "") }
                getProducts()
            }
            ProductListEvent.RetryLastAction -> {
                when {
                    uiState.value.searchQuery.isNotBlank() -> searchProducts(uiState.value.searchQuery)
                    uiState.value.selectedCategory != null -> getProductsByCategory(uiState.value.selectedCategory!!)
                    else -> getProducts()
                }
            }
        }
    }

    private fun getProducts(forceRefresh: Boolean = false) {
        getProductsJob?.cancel()
        getProductsJob = viewModelScope.launch {
            if (forceRefresh) {
                try {
                    productUseCases.refreshProducts()
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "Failed to refresh products") }
                    return@launch
                }
            }

            productUseCases.getProducts().collect { result ->
                _uiState.update {
                    when (result) {
                        is ProductResult.Success -> it.copy(
                            products = result.data,
                            isLoading = false,
                            error = null
                        )
                        is ProductResult.Error -> it.copy(
                            isLoading = false,
                            error = result.message
                        )
                        ProductResult.Loading -> it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce search
            productUseCases.searchProducts(query).collect { result ->
                _uiState.update {
                    when (result) {
                        is ProductResult.Success -> it.copy(
                            products = result.data,
                            isLoading = false,
                            error = null
                        )
                        is ProductResult.Error -> it.copy(
                            isLoading = false,
                            error = result.message
                        )
                        ProductResult.Loading -> it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private fun getProductsByCategory(category: String) {
        getProductsJob?.cancel()
        getProductsJob = viewModelScope.launch {
            productUseCases.getProductsByCategory(category).collect { result ->
                _uiState.update {
                    when (result) {
                        is ProductResult.Success -> it.copy(
                            products = result.data,
                            isLoading = false,
                            error = null
                        )
                        is ProductResult.Error -> it.copy(
                            isLoading = false,
                            error = result.message
                        )
                        ProductResult.Loading -> it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            productUseCases.getCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }
}

data class ProductListUiState(
    val products: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ProductListEvent {
    data class SearchQueryChanged(val query: String) : ProductListEvent
    data class CategorySelected(val category: String?) : ProductListEvent
    data object Refresh : ProductListEvent
    data object ClearSearch : ProductListEvent
    data object RetryLastAction : ProductListEvent
}