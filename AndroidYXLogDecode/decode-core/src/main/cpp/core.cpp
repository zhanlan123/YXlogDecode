#include <jni.h>
#include <cstring>
#include <cstdlib>
#include <cstdio>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dirent.h>
#include "micro-ecc-master/uECC.h"

bool Hex2Buffer(const char* str, size_t len, unsigned char* buffer);

jbyteArray charToJByteArray(JNIEnv *env, unsigned char *buf, int len);

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_top_yinlingfeng_xlog_decode_core_ECDHUtils_getEcdhKeyFromJNI(
        JNIEnv* env,
        jclass obj,
        jstring publicKey,
        jstring privateKey) {
    unsigned char result[] = "失败";
    unsigned char clientPubKey[64] = {0};
    const char *nativePublicKey = env->GetStringUTFChars(publicKey, 0);
    if (!Hex2Buffer(nativePublicKey, 128, clientPubKey))
    {
        return charToJByteArray(env, result, 2);
    }
    unsigned char svrPriKey[32] = {0};
    const char *nativePrivateKey = env->GetStringUTFChars(privateKey, 0);
    if (!Hex2Buffer(nativePrivateKey, 64, svrPriKey))
    {
        return charToJByteArray(env, result, 2);
    }
    unsigned char ecdhKey[32] = {0};
    if (0 == uECC_shared_secret(clientPubKey, svrPriKey, ecdhKey, uECC_secp256k1())) {
        return charToJByteArray(env, result, 2);
    }
    return charToJByteArray(env, ecdhKey, 32);
}

jbyteArray charToJByteArray(JNIEnv* env, unsigned char* buf, int len) {
    jbyteArray array = env->NewByteArray (len);
    env->SetByteArrayRegion (array, 0, len, reinterpret_cast<jbyte*>(buf));
    return array;
}

bool Hex2Buffer(const char* str, size_t len, unsigned char* buffer)
{
    if (NULL == str || len ==0 || len % 2 != 0) {
        return -1;
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

        buffer[i/2] = (unsigned char)strtol(tmp, NULL, 16);
    }
    return true;
}

