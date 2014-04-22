/* 
 * File:   NativeUtils.h
 * Author: Leo
 *
 * Created on 2013年11月25日, 下午 11:31
 */

#include <windows.h>
#include <jni.h>
#include "MPS_140801_IEPE.h"

#ifndef NATIVEUTILS_H
#define	NATIVEUTILS_H

#ifdef	__cplusplus
extern "C" {
#endif

    struct MPS {
        Handle DeviceHandle;
    };

    const char * toChars(JNIEnv *, jstring);
    struct MPS * getStruct(JNIEnv *, jobject);
//    void errorCheck(JNIEnv *, jobject, ViStatus);

#ifdef	__cplusplus
}
#endif

#endif	/* NATIVEUTILS_H */

