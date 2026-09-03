#include <jni.h>
#include <cstdlib>
#include "micro-ecc-master/uECC.h"

bool Hex2Buffer(const char* str, size_t len, unsigned char* buffer);

jbyteArray charToJByteArray(JNIEnv *env, unsigned char *buf, int len);

void throwIllegalArgumentException(JNIEnv* env, const char* message) {
    jclass exClass = env->FindClass("java/lang/IllegalArgumentException");
    if (exClass != nullptr) {
        env->ThrowNew(exClass, message);
    }
}

void throwRuntimeException(JNIEnv* env, const char* message) {
    jclass exClass = env->FindClass("java/lang/RuntimeException");
    if (exClass != nullptr) {
        env->ThrowNew(exClass, message);
    }
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_top_yinlingfeng_xlog_decode_core_ECDHUtils_getEcdhKeyFromJNI(
        JNIEnv* env,
        jclass obj,
        jstring publicKey,
        jstring privateKey) {
    if (publicKey == nullptr || privateKey == nullptr) {
        throwIllegalArgumentException(env, "Public key and private key must not be null.");
        return nullptr;
    }

    const char *nativePublicKey = env->GetStringUTFChars(publicKey, 0);
    if (nativePublicKey == nullptr) {
        return nullptr; // OutOfMemoryError will be thrown by JVM
    }

    const char *nativePrivateKey = env->GetStringUTFChars(privateKey, 0);
    if (nativePrivateKey == nullptr) {
        env->ReleaseStringUTFChars(publicKey, nativePublicKey);
        return nullptr; // OutOfMemoryError
    }

    unsigned char clientPubKey[64] = {0};
    if (!Hex2Buffer(nativePublicKey, 128, clientPubKey))
    {
        env->ReleaseStringUTFChars(publicKey, nativePublicKey);
        env->ReleaseStringUTFChars(privateKey, nativePrivateKey);
        throwIllegalArgumentException(env, "Invalid public key format or length.");
        return nullptr;
    }

    unsigned char svrPriKey[32] = {0};
    if (!Hex2Buffer(nativePrivateKey, 64, svrPriKey))
    {
        env->ReleaseStringUTFChars(publicKey, nativePublicKey);
        env->ReleaseStringUTFChars(privateKey, nativePrivateKey);
        throwIllegalArgumentException(env, "Invalid private key format or length.");
        return nullptr;
    }

    env->ReleaseStringUTFChars(publicKey, nativePublicKey);
    env->ReleaseStringUTFChars(privateKey, nativePrivateKey);

    unsigned char ecdhKey[32] = {0};
    if (0 == uECC_shared_secret(clientPubKey, svrPriKey, ecdhKey, uECC_secp256k1())) {
        throwRuntimeException(env, "Failed to compute shared secret.");
        return nullptr;
    }

    return charToJByteArray(env, ecdhKey, 32);
}

jbyteArray charToJByteArray(JNIEnv* env, unsigned char* buf, int len) {
    jbyteArray array = env->NewByteArray(len);
    if (array != nullptr) {
        env->SetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte*>(buf));
    }
    return array;
}

bool Hex2Buffer(const char* str, size_t len, unsigned char* buffer)
{
    if (str == nullptr || len == 0 || len % 2 != 0) {
        return false;
    }

    char tmp[3] = {0};
    size_t i;
    for (i = 0; i < len - 1; i += 2)
    {
        size_t j;
        for (j = 0; j < 2; ++j)
        {
            tmp[j] = str[i + j];
            if (!(('0' <= tmp[j] && tmp[j] <= '9') ||
                  ('a' <= tmp[j] && tmp[j] <= 'f') ||
                  ('A' <= tmp[j] && tmp[j] <= 'F')))
            {
                return false;
            }
        }

        buffer[i/2] = (unsigned char)strtol(tmp, nullptr, 16);
    }
    return true;
}

