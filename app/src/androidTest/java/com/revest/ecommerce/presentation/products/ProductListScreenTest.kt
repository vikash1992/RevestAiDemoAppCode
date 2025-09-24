package com.revest.ecommerce.presentation.products

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.revest.ecommerce.domain.model.Product
import com.revest.ecommerce.presentation.theme.RevestTheme
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testProducts = listOf(
        Product(
            id = 1,
            title = "Test Product",
            description = "Test Description",
            price = 99.99,
            discountPercentage = 10.0,
            rating = 4.5,
            stock = 10,
            brand = "Test Brand",
            category = "electronics",
            thumbnail = "thumbnail.jpg",
            images = listOf("image1.jpg")
        ),
        Product(
            id = 2,
            title = "Another Product",
            description = "Another Description",
            price = 149.99,
            discountPercentage = null,
            rating = 4.8,
            stock = 15,
            brand = "Another Brand",
            category = "clothing",
            thumbnail = "thumbnail2.jpg",
            images = listOf("image2.jpg")
        )
    )

    private val testCategories = listOf("electronics", "clothing")

    @Test
    fun productListDisplaysCorrectly() {
        // Given
        val uiState = ProductListUiState(
            products = testProducts,
            categories = testCategories,
            isLoading = false
        )

        // When
        composeTestRule.setContent {
            RevestTheme {
                ProductListScreen(
                    onProductClick = {},
                    viewModel = FakeProductListViewModel(uiState)
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Test Product").assertIsDisplayed()
        composeTestRule.onNodeWithText("Another Product").assertIsDisplayed()
        composeTestRule.onNodeWithText("$99.99").assertIsDisplayed()
        composeTestRule.onNodeWithText("$149.99").assertIsDisplayed()
    }

    @Test
    fun loadingStateDisplaysCorrectly() {
        // Given
        val uiState = ProductListUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            RevestTheme {
                ProductListScreen(
                    onProductClick = {},
                    viewModel = FakeProductListViewModel(uiState)
                )
            }
        }

        // Then
        composeTestRule.onNodeWithTag("loading_view").assertIsDisplayed()
    }

    @Test
    fun errorStateDisplaysCorrectly() {
        // Given
        val errorMessage = "Network error occurred"
        val uiState = ProductListUiState(error = errorMessage)

        // When
        composeTestRule.setContent {
            RevestTheme {
                ProductListScreen(
                    onProductClick = {},
                    viewModel = FakeProductListViewModel(uiState)
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun categoryFilteringWorksCorrectly() {
        // Given
        val uiState = ProductListUiState(
            products = testProducts,
            categories = testCategories,
            selectedCategory = "electronics"
        )

        // When
        composeTestRule.setContent {
            RevestTheme {
                ProductListScreen(
                    onProductClick = {},
                    viewModel = FakeProductListViewModel(uiState)
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("electronics").assertIsDisplayed()
        composeTestRule.onNodeWithText("clothing").assertIsDisplayed()
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
    }

    @Test
    fun searchBarWorksCorrectly() {
        // Given
        val uiState = ProductListUiState(
            products = testProducts,
            searchQuery = "test"
        )

        // When
        composeTestRule.setContent {
            RevestTheme {
                ProductListScreen(
                    onProductClick = {},
                    viewModel = FakeProductListViewModel(uiState)
                )
            }
        }

        // Then
        composeTestRule.onNode(hasText("test")).assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
    }
}

class FakeProductListViewModel(
    private val initialState: ProductListUiState = ProductListUiState()
) : ProductListViewModel(FakeProductUseCases()) {

    override val uiState = kotlinx.coroutines.flow.MutableStateFlow(initialState)

    override fun onEvent(event: ProductListEvent) {
        // No-op for tests
    }
}

class FakeProductUseCases : com.revest.ecommerce.domain.usecase.ProductUseCases(
    getProducts = mockk(),
    searchProducts = mockk(),
    getCategories = mockk(),
    getProductsByCategory = mockk(),
    getProductDetails = mockk(),
    refreshProducts = mockk()
)