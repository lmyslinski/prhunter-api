package io.prhunter.api.oauth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.Response
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import java.io.File
import java.io.FileReader
import java.io.StringReader
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
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

    fun readPrivateKeySecondApproach(file: File): RSAPrivateKey {
        FileReader(file).use { keyReader ->
            val pemParser = PEMParser(keyReader)
            val converter = JcaPEMKeyConverter()
            val privateKeyInfo: PrivateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject())
            return converter.getPrivateKey(privateKeyInfo) as RSAPrivateKey
        }
    }

    fun readPrivateKey(): RSAPrivateKey {
        val keyBase64 = githubSecrets.privateKey
        val decoded = String(Base64.getDecoder().decode(keyBase64))
        PEMParser(StringReader(decoded)).use { pemParser ->
            val converter = JcaPEMKeyConverter()
            val privateKeyInfo: PrivateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject())
            return converter.getPrivateKey(privateKeyInfo) as RSAPrivateKey
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