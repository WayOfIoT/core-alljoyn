# Define ALLJOYN_DIST before building. It should point to the "dist/cpp" directory for the appropriate
# platform and version (debug or release). For example:
# <AJ_ROOT>/core/alljoyn/build/android/arm/debug/dist/cpp

LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE := ajrouter
LOCAL_SRC_FILES := $(ALLJOYN_DIST)/lib/libajrouter.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := alljoyn
LOCAL_SRC_FILES := $(ALLJOYN_DIST)/lib/liballjoyn.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := alljoyn_about
LOCAL_SRC_FILES := $(ALLJOYN_DIST)/lib/liballjoyn_about.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := MyAllJoynCode

TARGET_PLATFORM := android-16

LOCAL_C_INCLUDES := \
	$(ALLJOYN_DIST)/../cpp/inc \
	$(ALLJOYN_DIST)/../cpp/inc/alljoyn \
	$(ALLJOYN_DIST)/../about/inc

LOCAL_CFLAGS := -std=c++11 -Wno-psabi -Wno-write-strings -DANDROID_NDK -DTARGET_ANDROID -DLINUX -DQCC_OS_GROUP_POSIX -DQCC_OS_ANDROID -DANDROID

LOCAL_CPP_EXTENSION := .cc

LOCAL_SRC_FILES := \
	AndroidJNIBridge.cc \
	MyAllJoynCode.cc

LOCAL_STATIC_LIBRARIES := \
	ajrouter \
	alljoyn \
	alljoyn_about

LOCAL_LDLIBS := \
	-llog \

LOCAL_ARM_MODE := arm

include $(BUILD_SHARED_LIBRARY)
