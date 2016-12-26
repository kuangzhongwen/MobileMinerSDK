/*
 * This confidential and proprietary software may be used only as
 * authorised by a licensing agreement from ARM Limited
 *    (C) COPYRIGHT 2013 ARM Limited
 *        ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorised
 * copies and copies may only be made to the extent permitted
 * by a licensing agreement from ARM Limited.
 */
#include <jni.h>
#include <string.h>
#include "common.h"
#include "image.h"
#include <android/log.h>

#include <CL/cl.h>
#include <iostream>

#ifndef LOG_INCLUDED
#define LOG_INCLUDED

#define ANDROID_V

#ifdef ANDROID_V
#include <android/log.h>
#include <errno.h>

#define  LOG_TAG "kuang"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

#ifdef perror
#undef perror
#endif
#define perror(smg) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "opus error:%s :%s", smg, strerror(errno))

#ifdef fprintf
#undef fprintf
#endif
#define fprintf(strm, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

#else
#include <stdio.h>
#include <stdlib.h>
#define LOGE(fmt, arg...) fprintf(stderr, fmt, ##arg)
#define LOGD(fmt, arg...) fprintf(stderr, fmt, ##arg)
#endif

#endif

using namespace std;

void hello_world_test();

extern "C" JNIEXPORT void JNICALL Java_suishi_opencl_MainActivity_helloWorldOpenCL(JNIEnv *env, jobject thiz)
{
    printf("hahahaha");
    hello_world_test();
}

char *file_contents(const char *filename, size_t *length) {
    FILE *f = fopen(filename, "r");
    void *buffer;

    if (!f) {
        LOGE("Unable to open %s for reading\n", filename);
        return NULL;
    }

    fseek(f, 0, SEEK_END);
    *length = ftell(f);
    fseek(f, 0, SEEK_SET);

    buffer = malloc(*length+1);
    *length = fread(buffer, 1, *length, f);
    fclose(f);
    ((char*)buffer)[*length] = '\0';

    return (char*) buffer;
}

void get_program_build_log(cl_program program, cl_device_id device) {
    cl_int		status;
    char	    val[2 * 1024 * 1024];
    size_t		ret = 0;
    status = clGetProgramBuildInfo(program, device,
	    CL_PROGRAM_BUILD_LOG,
	    sizeof (val),	// size_t param_value_size
	    &val,		// void *param_value
	    &ret);		// size_t *param_value_size_ret

    if (status != CL_SUCCESS) {
        return;
	}

    fprintf(stderr, "%s\n", val);
}

void hello_world_test() {
    cl_context context = 0;
    cl_command_queue commandQueue = 0;
    cl_program program = 0;
    cl_platform_id plat_id = 0;
    cl_device_id device = 0;
    cl_kernel kernel = 0;
    int numberOfMemoryObjects = 3;
    cl_mem memoryObjects[3] = {0, 0, 0};
    cl_int errorNumber;

    if (!createContext(&context))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed to create an OpenCL context. ");
        return;
    }

    if (!createCommandQueue(context, &commandQueue, &device))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed to create the OpenCL command queue.");
        return;
    }

    size_t source_len;
    char* kernelStr = file_contents("/data/data/suishi.opencl/app_execdir/hello_world_opencl.cl", &source_len);
    program = clCreateProgramWithSource(context, 1, (const char **)&kernelStr, &source_len, &errorNumber);

    if (!checkSuccess(errorNumber) || program == NULL)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed to create OpenCL program.");
        return;
    }

    errorNumber = clBuildProgram(program, 1, &device, "", NULL, NULL);
    if (!checkSuccess(errorNumber)) {
       get_program_build_log(program, device);
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD("OpenCL build failed (%d). Build log follows:\n", errorNumber);
       return;
    }

    kernel = clCreateKernel(program, "do_opencl", &errorNumber);
    if (!checkSuccess(errorNumber))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed to create OpenCL kernel %d ", errorNumber);
        return;
    }

    /* [Setup memory] */
    /* Number of elements in the arrays of input and output data. */
    cl_int arraySize = 10;

    /* The buffers are the size of the arrays. */
    size_t bufferSize = arraySize * sizeof(cl_int);

    /*
     * Ask the OpenCL implementation to allocate buffers for the data.
     * We ask the OpenCL implemenation to allocate memory rather than allocating
     * it on the CPU to avoid having to copy the data later.
     * The read/write flags relate to accesses to the memory from within the kernel.
     */
    bool createMemoryObjectsSuccess = true;

    memoryObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, bufferSize, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);

    memoryObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, bufferSize, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);

    memoryObjects[2] = clCreateBuffer(context, CL_MEM_WRITE_ONLY | CL_MEM_ALLOC_HOST_PTR, bufferSize, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);

    if (!createMemoryObjectsSuccess)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed to create OpenCL buffer. ");
        return;
    }
    /* [Setup memory] */

    /* [Map the buffers to pointers] */
    /* Map the memory buffers created by the OpenCL implementation to pointers so we can access them on the CPU. */
    bool mapMemoryObjectsSuccess = true;

    cl_int* inputA = (cl_int*)clEnqueueMapBuffer(commandQueue, memoryObjects[0], CL_TRUE, CL_MAP_WRITE, 0, bufferSize, 0, NULL, NULL, &errorNumber);
    mapMemoryObjectsSuccess &= checkSuccess(errorNumber);

    cl_int* inputB = (cl_int*)clEnqueueMapBuffer(commandQueue, memoryObjects[1], CL_TRUE, CL_MAP_WRITE, 0, bufferSize, 0, NULL, NULL, &errorNumber);
    mapMemoryObjectsSuccess &= checkSuccess(errorNumber);

    if (!mapMemoryObjectsSuccess)
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD ("Failed to map buffer. ");
       return;
    }
    /* [Map the buffers to pointers] */

    /* [Initialize the input data] */
    for (int i = 0; i < arraySize; i++)
    {
       inputA[i] = i;
       inputB[i] = i;
    }
    /* [Initialize the input data] */

    /* [Un-map the buffers] */
    /*
     * Unmap the memory objects as we have finished using them from the CPU side.
     * We unmap the memory because otherwise:
     * - reads and writes to that memory from inside a kernel on the OpenCL side are undefined.
     * - the OpenCL implementation cannot free the memory when it is finished.
     */
    if (!checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[0], inputA, 0, NULL, NULL)))
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD ("Unmapping memory objects failed ");
       return;
    }

    if (!checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[1], inputB, 0, NULL, NULL)))
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD ("Unmapping memory objects failed ");
       return;
    }
    /* [Un-map the buffers] */

    /* [Set the kernel arguments] */
    bool setKernelArgumentsSuccess = true;
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 0, sizeof(cl_mem), &memoryObjects[0]));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 1, sizeof(cl_mem), &memoryObjects[1]));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 2, sizeof(cl_mem), &memoryObjects[2]));

    if (!setKernelArgumentsSuccess)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed setting OpenCL kernel arguments.");
        return;
    }

    for (int i = 0; i < 10; i++) {
        LOGD ("input[A] at %d is %d", i, inputA[i]);
    }
    for (int i = 0; i < 10; i++) {
        LOGD ("input[B] at %d is %d", i, inputB[i]);
    }
    /* [Set the kernel arguments] */

    /* An event to associate with the Kernel. Allows us to retrieve profiling information later. */
    cl_event event = 0;

    /* [Global work size] */
    /*
     * Each instance of our OpenCL kernel operates on a single element of each array so the number of
     * instances needed is the number of elements in the array.
     */
    size_t globalWorksize[1] = {10};
    /* Enqueue the kernel */
    if (!checkSuccess(clEnqueueNDRangeKernel(commandQueue, kernel, 1, NULL, globalWorksize, NULL, 0, NULL, &event)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        cerr << "Failed enqueuing the kernel. " << __FILE__ << ":"<< __LINE__ << endl;
        return;
    }
    /* [Global work size] */

    cl_int *ptr;
    ptr = (cl_int *)clEnqueueMapBuffer(commandQueue,
        memoryObjects[2],
        CL_TRUE,
        CL_MAP_READ,
        0,
        10 * sizeof(cl_int),
        0, NULL, NULL, NULL);

    LOGD ("================");
    for (int i = 0; i < 10; i++) {
        LOGD ("ptr[] at %d is %d", i, ptr[i]);
    }

    /* Wait for kernel execution completion. */
    if (!checkSuccess(clFinish(commandQueue)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        cerr << "Failed waiting for kernel execution to finish. " << __FILE__ << ":"<< __LINE__ << endl;
        return;
    }

    /* Print the profiling information for the event. */
    printProfilingInfo(event);
    /* Release the event object. */
    if (!checkSuccess(clReleaseEvent(event)))
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       cerr << "Failed releasing the event object. " << __FILE__ << ":"<< __LINE__ << endl;
       return;
    }

    /* Get a pointer to the output data. */
    cl_int* output = (cl_int*)clEnqueueMapBuffer(commandQueue, memoryObjects[2], CL_TRUE, CL_MAP_READ, 0, bufferSize, 0, NULL, NULL, &errorNumber);
    if (!checkSuccess(errorNumber))
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       cerr << "Failed to map buffer. " << __FILE__ << ":"<< __LINE__ << endl;
       return;
    }

    /* [Output the results] */
    /* Uncomment the following block to print results. */
    /*
    for (int i = 0; i < arraySize; i++)
    {
        cout << "i = " << i << ", output = " <<  output[i] << "\n";
    }
    */
    /* [Output the results] */

    /* Unmap the memory object as we are finished using them from the CPU side. */
    if (!checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[2], output, 0, NULL, NULL)))
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       cerr << "Unmapping memory objects failed " << __FILE__ << ":"<< __LINE__ << endl;
       return;
    }

    /* Release OpenCL objects. */
    cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
}