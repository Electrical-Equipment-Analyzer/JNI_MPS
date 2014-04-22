

#include "NativeUtils.h"

const char * toChars(JNIEnv *env, jstring string) {
    return (string == NULL ? NULL : env->GetStringUTFChars(string, NULL));
}

struct MPS * getStruct(JNIEnv *env, jobject obj) {
    return (struct MPS *) (env->GetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "nativeStruct", "J")));
}

void errorCheck(JNIEnv *env, jobject obj, int error) {
//    if (error != VI_SUCCESS) {
//        ViChar errMsg[256];
//        niFgen_ErrorHandler(getStruct(env, obj)->viSession, error, errMsg);
//        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), errMsg);
//        fflush(stdout);
//    }
}