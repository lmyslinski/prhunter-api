package io.prhunter.api.oauth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.Response
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.util.io.pem.PemReader
import org.springframework.core.io.ClassPathResource
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPrivateKeySpec
import java.util.*


private val log = KotlinLogging.logger {}

class GithubJwtInterceptor(private val githubSecrets: GithubSecrets) : Interceptor {

    private fun parsePEMFile(pemFile: File): ByteArray {
        if (!pemFile.isFile || !pemFile.exists()) {
            throw FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.absolutePath))
        }
        val reader = PemReader(FileReader(pemFile))
        val pemObject = reader.readPemObject()
        val content = pemObject.content
        reader.close()
        return content
    }

    private fun getPKCS1PrivateKey(keyBytes: ByteArray, algorithm: String): PrivateKey? {
        var privateKey: PrivateKey? = null
        try {
            val asn1PrivKey = RSAPrivateKey.getInstance(ASN1Sequence.fromByteArray(keyBytes))
            val rsaPrivateKeySpec = RSAPrivateKeySpec(asn1PrivKey.modulus, asn1PrivKey.privateExponent)
            val kf = KeyFactory.getInstance(algorithm)
            privateKey = kf.generatePrivate(rsaPrivateKeySpec)
        } catch (e: NoSuchAlgorithmException) {
            println("Could not reconstruct the private key, the given algorithm could not be found.")
        } catch (e: InvalidKeySpecException) {
            println("Could not reconstruct the private key")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return privateKey
    }

    fun readPKCS1PrivateKeyFromFile(filepath: String, algorithm: String): PrivateKey? {
        val bytes = parsePEMFile(File(filepath))
        return getPKCS1PrivateKey(bytes, algorithm)
    }

    fun generateJwtKey(): String {

        val input = ClassPathResource("prhunter-io.2021-08-03.private-key.pem").file.absolutePath
        val pk = readPKCS1PrivateKeyFromFile(input, "RSA")

        return Jwts.builder().setIssuer(githubSecrets.appId)
                .signWith(pk, SignatureAlgorithm.RS256)
                .setIssuedAt(Date(System.currentTimeMillis() - 1000))
                .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .compact()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val jwtToken = generateJwtKey()
        val updated = request.newBuilder().header("Authentication", "Bearer $jwtToken").build()
        println(jwtToken)
        return chain.proceed(updated)
    }
}