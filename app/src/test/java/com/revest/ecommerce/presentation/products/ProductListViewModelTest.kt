package com.revest.ecommerce.presentation.products

import com.revest.ecommerce.domain.model.Product
import com.revest.ecommerce.domain.model.ProductResult
import com.revest.ecommerce.domain.usecase.ProductUseCases
import com.revest.ecommerce.domain.usecase.GetProductsUseCase
import com.revest.ecommerce.domain.usecase.SearchProductsUseCase
import com.revest.ecommerce.domain.usecase.GetCategoriesUseCase
import com.revest.ecommerce.domain.usecase.GetProductsByCategoryUseCase
import com.revest.ecommerce.domain.usecase.RefreshProductsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModelTest {

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var searchProductsUseCase: SearchProductsUseCase
    private lateinit var getCategoriesUseCase: GetCategoriesUseCase
    private lateinit var getProductsByCategoryUseCase: GetProductsByCategoryUseCase
    private lateinit var refreshProductsUseCase: RefreshProductsUseCase
    private lateinit var productUseCases: ProductUseCases
    private lateinit var viewModel: ProductListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        getProductsUseCase = mockk()
        searchProductsUseCase = mockk()
        getCategoriesUseCase = mockk()
        getProductsByCategoryUseCase = mockk()
        refreshProductsUseCase = mockk()

        productUseCases = ProductUseCases(
            getProducts = getProductsUseCase,
            searchProducts = searchProductsUseCase,
            getCategories = getCategoriesUseCase,
            getProductsByCategory = getProductsByCategoryUseCase,
            refreshProducts = refreshProductsUseCase,
            getProductDetails = mockk() // Not used in this ViewModel
        )

        // Default mock responses
        coEvery { getProductsUseCase() } returns flowOf(ProductResult.Success(emptyList()))
        coEvery { getCategoriesUseCase() } returns flowOf(emptyList())
        coEvery { refreshProductsUseCase() } returns Unit

        viewModel = ProductListViewModel(productUseCases)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        assertEquals(ProductListUiState(), viewModel.uiState.value)
    }

    @Test
    fun `loading products updates state correctly`() = runTest {
        // Given
        val products = listOf(
            Product(
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

        coEvery { getProductsUseCase() } returns flowOf(
            ProductResult.Loading,
            ProductResult.Success(products)
        )

        // When
        viewModel = ProductListViewModel(productUseCases)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(products, viewModel.uiState.value.products)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `search query updates state and triggers search`() = runTest {
        // Given
        val query = "test"
        val searchResults = listOf(
            Product(
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

        coEvery { searchProductsUseCase(query) } returns flowOf(
            ProductResult.Loading,
            ProductResult.Success(searchResults)
        )

        // When
        viewModel.onEvent(ProductListEvent.SearchQueryChanged(query))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(query, viewModel.uiState.value.searchQuery)
        assertEquals(searchResults, viewModel.uiState.value.products)
        coVerify { searchProductsUseCase(query) }
    }

    @Test
    fun `selecting category updates state and fetches products`() = runTest {
        // Given
        val category = "Electronics"
        val categoryProducts = listOf(
            Product(
                id = 1,
                title = "Electronic Product",
                description = "Description",
                price = 99.99,
                discountPercentage = null,
                rating = 4.5,
                stock = 10,
                brand = "Brand",
                category = category,
                thumbnail = "thumbnail.jpg",
                images = listOf("image1.jpg")
            )
        )

        coEvery { getProductsByCategoryUseCase(category) } returns flowOf(
            ProductResult.Loading,
            ProductResult.Success(categoryProducts)
        )

        // When
        viewModel.onEvent(ProductListEvent.CategorySelected(category))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(category, viewModel.uiState.value.selectedCategory)
        assertEquals(categoryProducts, viewModel.uiState.value.products)
        coVerify { getProductsByCategoryUseCase(category) }
    }

    @Test
    fun `refresh triggers product reload`() = runTest {
        // Given
        coEvery { refreshProductsUseCase() } returns Unit
        val products = listOf(
            Product(
                id = 1,
                title = "Fresh Product",
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
        coEvery { getProductsUseCase() } returns flowOf(
            ProductResult.Loading,
            ProductResult.Success(products)
        )

        // When
        viewModel.onEvent(ProductListEvent.Refresh)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { refreshProductsUseCase() }
        coVerify { getProductsUseCase() }
        assertEquals(products, viewModel.uiState.value.products)
    }
}