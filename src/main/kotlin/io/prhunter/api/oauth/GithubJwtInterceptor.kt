package io.prhunter.api.oauth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.Response
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import java.io.StringReader
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*


private val log = KotlinLogging.logger {}


class GithubJwtInterceptor(private val githubSecrets: GithubSecrets) : Interceptor {

    fun generateJwtKey(): String {
        return Jwts.builder().setIssuer(githubSecrets.appId)
                .signWith(readPrivateKey(), SignatureAlgorithm.HS256)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + 10 * 60))
                .compact()
    }

    fun readPrivateKey(): RSAPrivateKey {
        val factory = KeyFactory.getInstance("RSA")
        PemReader(StringReader(githubSecrets.privateKey)).use { pemReader ->
            val pemObject: PemObject = pemReader.readPemObject()
            val content: ByteArray = pemObject.content
            val privKeySpec = PKCS8EncodedKeySpec(content)
            return factory.generatePrivate(privKeySpec) as RSAPrivateKey
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val jwtToken = generateJwtKey()
        log.info { "Intercepted: ${request.headers} ${request.body}" }

        request.newBuilder().header("Authentication", "Bearer $jwtToken")

        return chain.proceed(chain.request())
    }
}