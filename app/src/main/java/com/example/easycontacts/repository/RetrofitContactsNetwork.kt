package com.example.easycontacts.repository

import com.example.easycontacts.BuildConfig
import com.example.easycontacts.ui.screens.Contact
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for Contacts Network API
 */
private interface RetrofitContactsNetworkApi {
    @GET(value = "users/{username}/contacts")
    suspend fun getContacts(
        @Path("username") username: String,
    ): NetworkResponse<List<Contact>>
}

/**
 * Wrapper for data provided from the [CONTACTS_BASE_URL]
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

interface ContactsNetworkDataSource {
    suspend fun getContacts(username: String): List<Contact>
}

private const val CONTACTS_BASE_URL = "https://contacts-api-yy1b.onrender.com/"

/**
 * [Retrofit] backed [ContactsNetworkDataSource]
 */
@Singleton
internal class RetrofitContactsNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : ContactsNetworkDataSource {

    private val networkApi =
        Retrofit.Builder()
            .baseUrl(CONTACTS_BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitContactsNetworkApi::class.java)

    override suspend fun getContacts(username: String): List<Contact> =
        networkApi.getContacts(username = username).data
}