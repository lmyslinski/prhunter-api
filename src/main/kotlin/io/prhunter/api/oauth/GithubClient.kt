package io.prhunter.api.oauth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GithubClient {

    @POST("/login/oauth/access_token")
    @Headers("Accept: application/vnd.github.v3+json")
    suspend fun getAccessToken(@Body accessTokenRequest: AccessTokenRequest): Call<String>
//
//    @GET("/graphql/query/")
//    suspend fun getUserFeed(@Query("query_hash") queryHash: String = "bfa387b2992c3a52dcbe447467b4b771",
//                            @Query("variables") variables: String): ResponseBody
//
//    @GET("/{username}/?__a=1")
//    suspend fun getProfile(@Path("username") username: String): ResponseBody
//
//    @GET("/explore/locations/{igLocationId}/?__a=1")
//    suspend fun exploreLocations(@Path("igLocationId") igLocationId: String): ResponseBody

}