package io.github.ma1uta.identity.key

import io.github.ma1uta.identity.IdentityProperties
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.cert.Certificate
import java.util.regex.Pattern

open class KeyStoreProvider(protected val props: IdentityProperties.KeyStore) {

    protected lateinit var keyStore: KeyStore

    private val keyPattern: Pattern = Pattern.compile("(\\w+):(\\w+)")

    open fun init() {
        keyStore = initKeyStore(props)
    }

    protected fun initKeyStore(props: IdentityProperties.KeyStore): KeyStore {
        val keyStore = KeyStore.getInstance(props.keyStoreType)
        Files.newInputStream(Paths.get(props.keyStore)).use {
            keyStore.load(it, props.keyStorePassword.toCharArray())
        }
        return keyStore
    }

    open fun nextKey(): String? {
        val aliases = keyStore.aliases()
        return if (aliases.hasMoreElements()) aliases.nextElement() else null
    }

    fun key(key: String): Pair<String, Certificate>? {
        val matcher = keyPattern.matcher(key.trim())
        if (!matcher.matches()) {
            throw IllegalAccessException("Wrong key: $key")
        }

        val algorithm = matcher.group(1)
        val alias = matcher.group(2)
        val certificate = findKey(alias) ?: return null
        if (certificate.publicKey.algorithm != algorithm) {
            throw IllegalAccessException("Wrong key: $key")
        }
        return Pair(alias, certificate)
    }

    protected open fun findKey(alias: String): Certificate? = keyStore.getCertificate(alias)

    open fun valid(publicKey: String) = valid(publicKey, keyStore)

    protected fun valid(publicKey: String, keyStore: KeyStore): Boolean {
        val aliases = keyStore.aliases()
        while (aliases.hasMoreElements()) {
            val certificate = keyStore.getCertificate(aliases.nextElement())
            if (certificate.publicKey.encoded.toString(Charsets.UTF_8) == publicKey) {
                return true
            }
        }
        return false
    }

    protected fun store(keyStore: KeyStore, props: IdentityProperties.KeyStore) {
        Files.newOutputStream(Paths.get(props.keyStore)).use {
            keyStore.store(it, props.keyStorePassword.toCharArray())
        }
    }

    open fun sign(alias: String, content: String): Pair<String, String>? {
        if (keyStore.getCertificate(alias) == null) {
            return null
        }
        return sign(content, alias, keyStore, props)
    }

    protected fun sign(content: String, alias: String, keyStore: KeyStore, props: IdentityProperties.KeyStore): Pair<String, String>? {
        val sign = Signature.getInstance("Ed25519")
        sign.initSign(keyStore.getKey(alias, props.keyPassword.toCharArray()) as PrivateKey)
        sign.update(content.toByteArray())
        return Pair(alias, sign.sign().toString(Charsets.UTF_8))
    }

    open fun maxId(): Long {
        return maxId(keyStore)
    }

    protected fun maxId(keyStore: KeyStore): Long {
        var max = 0L
        val aliases = keyStore.aliases()
        while (aliases.hasMoreElements()) {
            val alias = aliases.nextElement().toLong()
            if (max < alias) {
                max = alias
            }
        }
        return max
    }

    fun addKey(key: String, keyPair: KeyPair, certificate: Certificate) {
        keyStore.setKeyEntry(key, keyPair.private, props.keyPassword.toCharArray(), arrayOf(certificate))
        store(keyStore, props)
        init()
    }
}