package com.revest.ecommerce.presentation.productdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revest.ecommerce.domain.model.ProductDetailResult
import com.revest.ecommerce.domain.usecase.ProductUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productUseCases: ProductUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<Int>("productId")?.let { productId ->
            getProductDetails(productId)
        }
    }

    fun onEvent(event: ProductDetailEvent) {
        when (event) {
            is ProductDetailEvent.Refresh -> {
                getProductDetails(event.productId)
            }
            ProductDetailEvent.RetryLastAction -> {
                uiState.value.productId?.let { productId ->
                    getProductDetails(productId)
                }
            }
        }
    }

    private fun getProductDetails(productId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = true,
                error = null,
                productId = productId
            ) }

            try {
                when (val result = productUseCases.getProductDetails(productId)) {
                    is ProductDetailResult.Success -> {
                        _uiState.update { it.copy(
                            product = result.data,
                            isLoading = false,
                            error = null
                        ) }
                    }
                    is ProductDetailResult.Error -> {
                        _uiState.update { it.copy(
                            isLoading = false,
                            error = result.message
                        ) }
                    }
                    ProductDetailResult.Loading -> {
                        _uiState.update { it.copy(
                            isLoading = true,
                            error = null
                        ) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                ) }
            }
        }
    }
}

data class ProductDetailUiState(
    val product: com.revest.ecommerce.domain.model.Product? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val productId: Int? = null
)

sealed interface ProductDetailEvent {
    data class Refresh(val productId: Int) : ProductDetailEvent
    data object RetryLastAction : ProductDetailEvent
}