package com.revest.ecommerce.domain.repository

import com.revest.ecommerce.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(limit: Int = 20, skip: Int = 0): Flow<List<Product>>
    
    fun searchProducts(query: String): Flow<List<Product>>
    
    fun getCategories(): Flow<List<String>>
    
    fun getProductsByCategory(category: String): Flow<List<Product>>
    
    suspend fun getProductDetails(id: Int): Product
    
    suspend fun refreshProducts()
    
    suspend fun refreshCategories()
}