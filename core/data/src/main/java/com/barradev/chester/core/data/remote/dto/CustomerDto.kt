package com.barradev.chester.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    @SerialName("id") val idRemote: Long,
    @SerialName("state") val state: Boolean,
    @SerialName("created_date") val createDate: String,
    @SerialName("modified_date") val modifiedDate: String,
    @SerialName("deleted_date") val deleteDate: String?,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String?,
    @SerialName("address") val address: String?,
    @SerialName("phone") val phone: String?,
    @SerialName("comment") val comment: String?,
    @SerialName("current_account") val currentAccount: Boolean,

)
