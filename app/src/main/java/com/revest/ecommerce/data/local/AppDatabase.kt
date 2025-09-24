package com.revest.ecommerce.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}

@androidx.room.TypeConverters
class Converters {
    @androidx.room.TypeConverter
    fun fromList(value: List<String>): String = value.joinToString(",")

    @androidx.room.TypeConverter
    fun toList(value: String): List<String> = value.split(",").filter { it.isNotEmpty() }
}

@androidx.room.Entity(tableName = "products")
data class ProductEntity(
    @androidx.room.PrimaryKey
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
    val images: List<String>?,
    val lastUpdated: Long = System.currentTimeMillis()
)

@androidx.room.Dao
interface ProductDao {
    @androidx.room.Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @androidx.room.Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity?

    @androidx.room.Query("SELECT * FROM products WHERE category = :category")
    suspend fun getProductsByCategory(category: String): List<ProductEntity>

    @androidx.room.Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%'")
    suspend fun searchProducts(query: String): List<ProductEntity>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @androidx.room.Query("DELETE FROM products")
    suspend fun clearAllProducts()

    @androidx.room.Query("SELECT DISTINCT category FROM products")
    suspend fun getAllCategories(): List<String>
}