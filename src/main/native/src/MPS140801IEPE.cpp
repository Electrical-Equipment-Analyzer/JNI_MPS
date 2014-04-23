
#include <stdio.h>
#include "NativeUtils.h"
#include "tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE.h"

typedef Handle(*lpMPS_OpenDevice)(int DeviceNumber); //打开设备函数的声明 ;
typedef int(*lpMPS_CloseDevice)(Handle DeviceHandle); //关闭设备函数的声明 
typedef int(*lpMPS_Configure)(int SampleRate, Handle DeviceHandle); //向设备发送设置命令函数的声明 
typedef int(*lpMPS_Start)(Handle DeviceHandle); //开始采集函数的声明 
typedef int(*lpMPS_Stop)(Handle DeviceHandle); //关闭设备函数的声明 
typedef int(*lpMPS_DataIn)(int *DataArray, int SampleNumber, Handle DeviceHandle); //采集函数的声明 
typedef int(*lpMPS_GetDeviceID)(Handle DeviceHandle); //获取板卡ID序号函数的声明 

lpMPS_OpenDevice MPS_OpenDevice;
lpMPS_CloseDevice MPS_CloseDevice;
lpMPS_Configure MPS_Configure;
lpMPS_Start MPS_Start;
lpMPS_Stop MPS_Stop;
lpMPS_DataIn MPS_DataIn;
lpMPS_GetDeviceID MPS_GetDeviceID;

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_loadLibrary
(JNIEnv *env, jclass cls, jstring path) {
    HINSTANCE hDll;
    if ((hDll = LoadLibrary(toChars(env, path))) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find DLL");
    else if ((MPS_OpenDevice = (lpMPS_OpenDevice) GetProcAddress(hDll, "MPS_OpenDevice")) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find <MPS_OpenDevice> function");
    else if ((MPS_CloseDevice = (lpMPS_CloseDevice) GetProcAddress(hDll, "MPS_CloseDevice")) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find <MPS_CloseDevice> function");
    else if ((MPS_Configure = (lpMPS_Configure) GetProcAddress(hDll, "MPS_Configure")) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find <MPS_Configure> function");
    else if ((MPS_Start = (lpMPS_Start) GetProcAddress(hDll, "MPS_Start")) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find <MPS_Start> function");
    else if ((MPS_Stop = (lpMPS_Stop) GetProcAddress(hDll, "MPS_Stop")) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find <MPS_Stop> function");
    else if ((MPS_DataIn = (lpMPS_DataIn) GetProcAddress(hDll, "MPS_DataIn")) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find <MPS_DataIn> function");
    else if ((MPS_GetDeviceID = (lpMPS_GetDeviceID) GetProcAddress(hDll, "MPS_GetDeviceID")) == NULL)
        env->ThrowNew(env->FindClass("tw/edu/sju/ee/eea/jni/mps/MPSException"), "Can’t find <MPS_GetDeviceID> function");
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_construct
(JNIEnv *env, jobject obj) {
    printf("construct");
    MPS *MPS_struct = (MPS *) malloc(sizeof (*MPS_struct));
    env->SetLongField(obj, env->GetFieldID(env->GetObjectClass(obj), "nativeStruct", "J"), (jlong) MPS_struct);
    MPS_struct->DeviceHandle = NULL;
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_destruct
(JNIEnv *env, jobject obj) {
    struct MPS *MPS_struct = getStruct(env, obj);
    MPS_struct->DeviceHandle = NULL;
    free(MPS_struct);
}

JNIEXPORT jboolean JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_isAlive
(JNIEnv *env, jobject obj) {
    return (getStruct(env, obj)->DeviceHandle != NULL);
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_openDevice
(JNIEnv *env, jobject obj, jint deviceNumber) {
    getStruct(env, obj)->DeviceHandle = MPS_OpenDevice(deviceNumber);
}

JNIEXPORT jint JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_getDeviceId
(JNIEnv *env, jobject obj) {
    return MPS_GetDeviceID(getStruct(env, obj)->DeviceHandle);
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_configure
(JNIEnv *env, jobject obj, jint sampleRate) {
    MPS_Configure(sampleRate, getStruct(env, obj)->DeviceHandle);
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_dataIn
(JNIEnv *env, jobject obj, jobjectArray dataBuffer) {
    int channel_length = env -> GetArrayLength(dataBuffer);
    jdoubleArray channel_0 = (jdoubleArray) env->GetObjectArrayElement(dataBuffer, 0);
    int data_length = env -> GetArrayLength(channel_0);
    int sampleNumber = data_length * 8;
    int buffer[sampleNumber];
    MPS_DataIn(buffer, sampleNumber, getStruct(env, obj)->DeviceHandle);
    int i, j;
    for (i = 0; i < channel_length; i++) {
        double ch_data[data_length];
        for (j = 0; j < data_length; j++) {
            ch_data[j] = (double) buffer[j * 8 + i] * 10.218 / (65536 * 128);
        }
        jdoubleArray channel_data = (jdoubleArray) env->GetObjectArrayElement(dataBuffer, i);
        env->SetDoubleArrayRegion(channel_data, 0, data_length, (const jdouble*) ch_data);
    }
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_start
(JNIEnv *env, jobject obj) {
    MPS_Start(getStruct(env, obj)->DeviceHandle);
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_stop
(JNIEnv *env, jobject obj) {
    MPS_Stop(getStruct(env, obj)->DeviceHandle);
}

JNIEXPORT void JNICALL Java_tw_edu_sju_ee_eea_jni_mps_MPS140801IEPE_closeDevice
(JNIEnv *env, jobject obj) {
    MPS_CloseDevice(getStruct(env, obj)->DeviceHandle);
}
