package com.revest.ecommerce.domain.usecase

import com.revest.ecommerce.domain.model.Product
import com.revest.ecommerce.domain.model.ProductDetailResult
import com.revest.ecommerce.domain.model.ProductResult
import com.revest.ecommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


data class ProductUseCases(
    val getProducts: GetProductsUseCase,
    val searchProducts: SearchProductsUseCase,
    val getCategories: GetCategoriesUseCase,
    val getProductsByCategory: GetProductsByCategoryUseCase,
    val getProductDetails: GetProductDetailsUseCase,
    val refreshProducts: RefreshProductsUseCase
)

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(limit: Int = 20, skip: Int = 0): Flow<ProductResult> = flow {
        emit(ProductResult.Loading)
        repository.getProducts(limit, skip)
            .map { ProductResult.Success(it) }
            .catch { e -> emit(ProductResult.Error(e.message ?: "Unknown error occurred")) }
            .collect { emit(it) }
    }
}

class SearchProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(query: String): Flow<ProductResult> = flow {
        if (query.isBlank()) {
            emit(ProductResult.Success(emptyList()))
            return@flow
        }
        
        emit(ProductResult.Loading)
        repository.searchProducts(query)
            .map { ProductResult.Success(it) }
            .catch { e -> emit(ProductResult.Error(e.message ?: "Failed to search products")) }
            .collect { emit(it) }
    }
}

class GetCategoriesUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<String>> = repository.getCategories()
}

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(category: String): Flow<ProductResult> = flow {
        emit(ProductResult.Loading)
        repository.getProductsByCategory(category)
            .map { ProductResult.Success(it) }
            .catch { e -> emit(ProductResult.Error(e.message ?: "Failed to fetch category products")) }
            .collect { emit(it) }
    }
}

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: Int): ProductDetailResult {
        return try {
            val product = repository.getProductDetails(id)
            ProductDetailResult.Success(product)
        } catch (e: Exception) {
            ProductDetailResult.Error(e.message ?: "Failed to fetch product details")
        }
    }
}

class RefreshProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke() {
        try {
            repository.refreshProducts()
            repository.refreshCategories()
        } catch (e: Exception) {
            throw e
        }
    }
}