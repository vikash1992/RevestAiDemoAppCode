package com.revest.ecommerce.di

import com.revest.ecommerce.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {

    @Provides
    fun provideProductUseCases(
        getProducts: GetProductsUseCase,
        searchProducts: SearchProductsUseCase,
        getCategories: GetCategoriesUseCase,
        getProductsByCategory: GetProductsByCategoryUseCase,
        getProductDetails: GetProductDetailsUseCase,
        refreshProducts: RefreshProductsUseCase
    ): ProductUseCases {
        return ProductUseCases(
            getProducts = getProducts,
            searchProducts = searchProducts,
            getCategories = getCategories,
            getProductsByCategory = getProductsByCategory,
            getProductDetails = getProductDetails,
            refreshProducts = refreshProducts
        )
    }
}