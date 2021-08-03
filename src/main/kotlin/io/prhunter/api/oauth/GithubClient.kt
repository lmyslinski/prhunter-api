package io.prhunter.api.oauth

import okhttp3.ResponseBody
import retrofit2.http.*

interface GithubClient {

    @POST("/login/oauth/access_token")
    suspend fun getAccessToken(@Body accessTokenRequest: AccessTokenRequest): ResponseBody
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