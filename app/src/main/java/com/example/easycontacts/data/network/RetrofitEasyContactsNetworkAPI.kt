package com.example.easycontacts.data.network

import com.example.easycontacts.BuildConfig
import com.example.easycontacts.data.model.Contact
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
private interface RetrofitContactsNetworkApi {
    @GET(value = "users/{username}/contacts")
    suspend fun getContacts(
        @Path("username") username: String,
    ): List<NetworkContact>
}

interface ContactsNetworkDataSource {
    suspend fun getContacts(username: String): List<Contact>
}

private const val CONTACTS_BASE_URL = BuildConfig.BACKEND_URL

/**
 * [Retrofit] backed [ContactsNetworkDataSource]
 */
@Singleton
class RetrofitContactsNetwork @Inject constructor(
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
        networkApi.getContacts(username = username).map { it.toContact() }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideRetrofit(impl: RetrofitContactsNetwork): ContactsNetworkDataSource = impl

    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    },
            )
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
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