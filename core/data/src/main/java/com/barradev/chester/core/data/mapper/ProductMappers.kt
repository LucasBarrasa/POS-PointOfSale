package com.barradev.chester.core.data.mapper

import com.barradev.chester.core.data.local.entity.ProductEntity
import com.barradev.chester.core.data.remote.dto.ProductDto
import com.barradev.chester.core.model.models.Product


fun ProductDto.toEntity(): ProductEntity{
    return ProductEntity(
        id = 0,
        idRemote = this.idRemote,
        name = this.name,
        price = this.price,
        stock = this.stock,
        hasStockControl = this.stockControl,
        imageUrl = this.imageUrl ?: "",
        categoryId = this.categoryId ?: 0,
        modificatedDate = this.modifiedDate,
        deletedDate = this.deletedDate
    )
}

fun ProductEntity.toDomain(): Product{
    return Product(
        id = this.id,
        idRemote = this.idRemote,
        name = this.name,
        price = this.price,
        currentStock = this.stock,
        imageUrl = this.imageUrl ?: "",
        category = this.categoryId
    )
}
