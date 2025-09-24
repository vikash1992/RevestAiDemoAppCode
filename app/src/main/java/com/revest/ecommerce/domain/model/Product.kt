package com.revest.ecommerce.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val discountPercentage: Double?,
    val rating: Double?,
    val stock: Int?,
    val brand: String?,
    val category: String,
    val thumbnail: String,
    val images: List<String>?
) {
    val formattedPrice: String
        get() = "$${"%.2f".format(price)}"

    val discountedPrice: Double?
        get() = discountPercentage?.let { discount ->
            price - (price * (discount / 100))
        }

    val formattedDiscountedPrice: String?
        get() = discountedPrice?.let { "$${"%.2f".format(it)}" }

    val hasDiscount: Boolean
        get() = discountPercentage != null && discountPercentage > 0
}

sealed interface ProductResult {
    data class Success(val data: List<Product>) : ProductResult
    data class Error(val message: String) : ProductResult
    data object Loading : ProductResult
}

sealed interface ProductDetailResult {
    data class Success(val data: Product) : ProductDetailResult
    data class Error(val message: String) : ProductDetailResult
    data object Loading : ProductDetailResult
}