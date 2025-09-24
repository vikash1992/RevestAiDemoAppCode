package com.revest.ecommerce.data.repository

import android.util.Log
import com.revest.ecommerce.data.local.ProductDao
import com.revest.ecommerce.data.local.ProductEntity
import com.revest.ecommerce.data.remote.ProductApi
import com.revest.ecommerce.data.remote.ProductDto
import com.revest.ecommerce.domain.model.Product
import com.revest.ecommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val TAG = "ProductRepositoryImpl"
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val dao: ProductDao
) : ProductRepository {

    override fun getProducts(limit: Int, skip: Int): Flow<List<Product>> = flow {
        // Emit cached data first
        emit(dao.getAllProducts().map { it.toDomain() })

        try {
            // Fetch fresh data from API
            val response = api.getProducts(limit = limit, skip = skip)
            // Update cache
            dao.insertProducts(response.products.map { it.toEntity() })
            // Emit fresh data
            emit(dao.getAllProducts().map { it.toDomain() })
        } catch (e: HttpException) {
            // If API call fails, we still have cached data
            throw Exception("Failed to fetch products: ${e.message}")
        } catch (e: IOException) {
            throw Exception("Couldn't reach server. Check your internet connection")
        }
    }

    override fun searchProducts(query: String): Flow<List<Product>> = flow {
        // Search in cache first
        emit(dao.searchProducts(query).map { it.toDomain() })

        try {
            // Search via API
            val response = api.searchProducts(query)
            // Update cache with search results
            dao.insertProducts(response.products.map { it.toEntity() })
            // Emit fresh results
            emit(dao.searchProducts(query).map { it.toDomain() })
        } catch (e: Exception) {
            throw Exception("Failed to search products: ${e.message}")
        }
    }

    override fun getCategories(): Flow<List<String>> = flow {
        // Emit cached categories
        emit(dao.getAllCategories())

        try {
            // Fetch fresh categories
            val categories = api.getCategories()
            // We'll get categories from products in cache
            emit(categories)
        } catch (e: Exception) {
            Log.d(TAG, "getCategories: error L"+e.message)
//            Result.failure(e)
        //            throw Exception("Failed to fetch categories: ${e.message.toString()}")
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> = flow {
        // Emit cached products for category
        emit(dao.getProductsByCategory(category).map { it.toDomain() })

        try {
            // Fetch fresh products for category
            val response = api.getProductsByCategory(category)
            // Update cache
            dao.insertProducts(response.products.map { it.toEntity() })
            // Emit fresh data
            emit(dao.getProductsByCategory(category).map { it.toDomain() })
        } catch (e: Exception) {
            throw Exception("Failed to fetch products for category $category: ${e.message}")
        }
    }

    override suspend fun getProductDetails(id: Int): Product {
        // Check cache first
        dao.getProductById(id)?.let {
            return it.toDomain()
        }

        try {
            // Fetch from API if not in cache
            val product = api.getProductDetails(id)
            // Update cache
            dao.insertProduct(product.toEntity())
            return product.toDomain()
        } catch (e: Exception) {
            throw Exception("Failed to fetch product details: ${e.message}")
        }
    }

    override suspend fun refreshProducts() {
        try {
            val response = api.getProducts()
            dao.clearAllProducts()
            dao.insertProducts(response.products.map { it.toEntity() })
        } catch (e: Exception) {
            throw Exception("Failed to refresh products: ${e.message}")
        }
    }

    override suspend fun refreshCategories() {
        try {
            api.getCategories()
            // Categories are derived from products, no need to store separately
        } catch (e: Exception) {
            throw Exception("Failed to refresh categories: ${e.message}")
        }
    }

    private fun ProductDto.toEntity() = ProductEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = images
    )

    private fun ProductEntity.toDomain() = Product(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = images
    )

    private fun ProductDto.toDomain() = Product(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = images
    )
}