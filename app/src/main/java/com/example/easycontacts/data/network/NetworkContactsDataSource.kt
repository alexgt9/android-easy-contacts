package com.example.easycontacts.data.network

import com.example.easycontacts.BuildConfig
import com.example.easycontacts.data.model.Contact
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for Contacts Network API
 */
private interface RetrofitContactsApiDefinition {
    @GET(value = "users/{username}/contacts")
    suspend fun getContacts(
        @Path("username") username: String,
    ): List<NetworkContact>
}

@Serializable
data class NetworkContact(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val createdAt: String
)

/**
 * [Retrofit] backed [NetworkContactsDataSource]
 */
@Singleton
class NetworkContactsDataSource @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) {

    private val networkApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitContactsApiDefinition::class.java)

    suspend fun getContacts(username: String): List<Contact> =
        networkApi.getContacts(username = username).map { it.toContact() }
}


fun NetworkContact.toContact(): Contact {
    return Contact(
        UUID.fromString(id),
        name,
        phone,
        email,
        Instant.parse(createdAt),
    )
}