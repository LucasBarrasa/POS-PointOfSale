package com.barradev.chester.core.data.mapper

import com.barradev.chester.core.data.local.entity.CustomerEntity
import com.barradev.chester.core.data.remote.dto.CustomerDto
import com.barradev.chester.core.model.models.Customer


//  Dto -> Entity
fun CustomerDto.toEntity(): CustomerEntity {
    return CustomerEntity(
        id = 0,
        remoteId = this.idRemote,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        phone = this.phone,
        hasCurrentAccount = this.currentAccount,
        isActive = this.state
    )
}

//  ENTITY -> DOMAIN
fun CustomerEntity.toDomain(): Customer {
    return Customer(
        id = this.id,
        remoteId = this.remoteId,
        fullName = this.firstName,
        address = this.address ?: "",
        phone = this.phone ?: "",
        hasCurrentAccount = this.hasCurrentAccount,
        isActive = this.isActive
    )
}