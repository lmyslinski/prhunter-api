package io.prhunter.api.auth

import com.google.firebase.auth.FirebaseToken

data class FirebaseUser(val id: String, val name: String, val pictureUrl: String)

fun FirebaseToken.toUser(): FirebaseUser {
    return FirebaseUser(
        this.claims["user_id"].toString(),
        this.claims["name"].toString(),
        this.claims["picture"].toString()
    )
}