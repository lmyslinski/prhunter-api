package io.prhunter.api.github

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.pkcs.RSAPrivateKey
import org.bouncycastle.util.io.pem.PemReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPrivateKeySpec
import java.util.*
import kotlin.io.path.writeBytes

object GithubJwtService {

    fun generateJwtKey(githubAppId: String, privateKeyFile: File): String {
        val pk = readPKCS1PrivateKeyFromFile(privateKeyFile)
        return Jwts.builder().setIssuer(githubAppId)
            .signWith(pk, SignatureAlgorithm.RS256)
            .setIssuedAt(Date(System.currentTimeMillis() - 1000))
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 5))
            .compact()
    }

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

    private fun getPKCS1PrivateKey(keyBytes: ByteArray): PrivateKey? {
        var privateKey: PrivateKey? = null
        try {
            val asn1PrivKey = RSAPrivateKey.getInstance(ASN1Sequence.fromByteArray(keyBytes))
            val rsaPrivateKeySpec = RSAPrivateKeySpec(asn1PrivKey.modulus, asn1PrivKey.privateExponent)
            val kf = KeyFactory.getInstance("RSA")
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

    private fun readPKCS1PrivateKeyFromFile(file: File): PrivateKey? {
        val bytes = parsePEMFile(file)
        return getPKCS1PrivateKey(bytes)
    }

    fun generateTmpPrivateKey(privateKeyContent: String): File  {
        val temp: Path = Files.createTempFile("private-key", ".pem")
        val content = Base64.getDecoder().decode(privateKeyContent)
        temp.writeBytes(content, StandardOpenOption.WRITE)
        return temp.toFile()
    }
}