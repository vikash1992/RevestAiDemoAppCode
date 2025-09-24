package com.revest.ecommerce.data.repository

import com.revest.ecommerce.data.local.ProductDao
import com.revest.ecommerce.data.local.ProductEntity
import com.revest.ecommerce.data.remote.ProductApi
import com.revest.ecommerce.data.remote.ProductDto
import com.revest.ecommerce.data.remote.ProductResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductRepositoryImplTest {

    private lateinit var productApi: ProductApi
    private lateinit var productDao: ProductDao
    private lateinit var repository: ProductRepositoryImpl

    @Before
    fun setup() {
        productApi = mockk()
        productDao = mockk(relaxed = true)
        repository = ProductRepositoryImpl(productApi, productDao)
    }

    @Test
    fun `getProducts returns cached data first then fetches from api`() = runTest {
        // Given
        val cachedProducts = listOf(
            ProductEntity(
                id = 1,
                title = "Cached Product",
                description = "Description",
                price = 99.99,
                discountPercentage = null,
                rating = 4.5,
                stock = 10,
                brand = "Brand",
                category = "Category",
                thumbnail = "thumbnail.jpg",
                images = listOf("image1.jpg")
            )
        )

        val apiProducts = ProductResponse(
            products = listOf(
                ProductDto(
                    id = 2,
                    title = "API Product",
                    description = "Description",
                    price = 149.99,
                    discountPercentage = 10.0,
                    rating = 4.8,
                    stock = 15,
                    brand = "Brand",
                    category = "Category",
                    thumbnail = "thumbnail.jpg",
                    images = listOf("image1.jpg")
                )
            ),
            total = 1,
            skip = 0,
            limit = 20
        )

        coEvery { productDao.getAllProducts() } returns cachedProducts
        coEvery { productApi.getProducts() } returns apiProducts

        // When
        val result = repository.getProducts().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Cached Product", result[0].title)
        coVerify { productDao.getAllProducts() }
        coVerify { productApi.getProducts() }
        coVerify { productDao.insertProducts(any()) }
    }

    @Test
    fun `searchProducts searches in cache first then api`() = runTest {
        // Given
        val query = "test"
        val cachedResults = listOf(
            ProductEntity(
                id = 1,
                title = "Test Product",
                description = "Description",
                price = 99.99,
                discountPercentage = null,
                rating = 4.5,
                stock = 10,
                brand = "Brand",
                category = "Category",
                thumbnail = "thumbnail.jpg",
                images = listOf("image1.jpg")
            )
        )

        val apiResults = ProductResponse(
            products = listOf(
                ProductDto(
                    id = 2,
                    title = "Test API Product",
                    description = "Description",
                    price = 149.99,
                    discountPercentage = 10.0,
                    rating = 4.8,
                    stock = 15,
                    brand = "Brand",
                    category = "Category",
                    thumbnail = "thumbnail.jpg",
                    images = listOf("image1.jpg")
                )
            ),
            total = 1,
            skip = 0,
            limit = 20
        )

        coEvery { productDao.searchProducts(query) } returns cachedResults
        coEvery { productApi.searchProducts(query) } returns apiResults

        // When
        val result = repository.searchProducts(query).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test Product", result[0].title)
        coVerify { productDao.searchProducts(query) }
        coVerify { productApi.searchProducts(query) }
        coVerify { productDao.insertProducts(any()) }
    }

    @Test
    fun `getProductDetails returns cached product if available`() = runTest {
        // Given
        val productId = 1
        val cachedProduct = ProductEntity(
            id = productId,
            title = "Cached Product",
            description = "Description",
            price = 99.99,
            discountPercentage = null,
            rating = 4.5,
            stock = 10,
            brand = "Brand",
            category = "Category",
            thumbnail = "thumbnail.jpg",
            images = listOf("image1.jpg")
        )

        coEvery { productDao.getProductById(productId) } returns cachedProduct

        // When
        val result = repository.getProductDetails(productId)

        // Then
        assertEquals(productId, result.id)
        assertEquals("Cached Product", result.title)
        coVerify { productDao.getProductById(productId) }
        coVerify(exactly = 0) { productApi.getProductDetails(any()) }
    }
}