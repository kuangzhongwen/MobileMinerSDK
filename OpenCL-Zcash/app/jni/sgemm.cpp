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
#include "common.h"
#include "image.h"

#include <CL/cl.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <cstddef>
#include <cmath>
#include <cstdlib>
#include <android/log.h>

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

void sgemm();

/**
 * \brief Initialize the input matrices with random values.
 * \param[in] matrixOrder The order of the matrices (number of rows and columns). Matrices have to be symmetric.
 * \param[in] matrixA First input matrix.
 * \param[in] matrixB Second input matrix.
 * \param[in] matrixC Third input matrix.
 * \return matrixA, matrixB and matrixC with random values.
 */
void sgemmInitialize (int matrixOrder, float* matrixA, float* matrixB, float * matrixC)
{
    for (int i = 0; i < matrixOrder; i++)
    {
        for (int j = 0; j < matrixOrder; j++)
        {
            int index = i * matrixOrder + j;

            /* Keep the values in the range [-1, 1]. */
            float randomeNumber = rand() / (float) RAND_MAX * 2 - 1;
            matrixA[index] = randomeNumber;

            randomeNumber = rand() / (float) RAND_MAX * 2 - 1;
            matrixB[index] = randomeNumber;

            randomeNumber = rand() / (float) RAND_MAX * 2 - 1;
            matrixC[index] = randomeNumber;
        }
    }
}

extern "C" JNIEXPORT void JNICALL Java_suishi_opencl_MainActivity_sgemm(JNIEnv *env, jobject thiz) {
    printf("sgemm");
    sgemm();
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

/**
 * \brief Simple SGEMM OpenCL sample.
 * \details A sample which calculates the following SGEMM equation:
 * matrixC = alpha * (matrixA * matrixB) + beta * matrixC.
 *
 * \return The exit code of the application, non-zero if a problem occurred.
 */
void sgemm()
{
    cl_context context = 0;
    cl_command_queue commandQueue = 0;
    cl_program program = 0;
    cl_device_id device = 0;
    cl_kernel kernel = 0;
    const unsigned int numberOfMemoryObjects = 3;
    cl_mem memoryObjects[numberOfMemoryObjects] = {0, 0, 0};
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
        LOGD ("Failed to create the OpenCL command queue. ");
        return;
    }

    size_t source_len;
    char* kernelStr = file_contents("/data/data/suishi.opencl/app_execdir/sgemm.cl", &source_len);
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

    kernel = clCreateKernel(program, "sgemm", &errorNumber);
    if (!checkSuccess(errorNumber))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed to create OpenCL kernel. ");
        return;
    }

    /* Kernel variables. */
    unsigned int matrixOrder = 2048;
    float alpha = 1;
    float beta = 0.1;

    /* Create the matrices. */
    const size_t matrixSize = matrixOrder * matrixOrder;

    /* As all the matrices have the same size, the buffer size is common. */
    size_t bufferSize = matrixSize * sizeof(float);

    /* Create buffers for the matrices used in the kernel. */
    bool createMemoryObjectsSuccess = true;
    memoryObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, bufferSize, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);
    memoryObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, bufferSize, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);
    memoryObjects[2] = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR, bufferSize, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);
    if (!createMemoryObjectsSuccess)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed to create OpenCL buffers. " );
        return;
    }

    /* Map the input memory objects to a host side pointers. */
    bool mapMemoryObjectsSuccess = true;
    cl_float* matrixA = (cl_float*)clEnqueueMapBuffer(commandQueue, memoryObjects[0], CL_TRUE, CL_MAP_WRITE, 0, bufferSize, 0, NULL, NULL, &errorNumber);
    mapMemoryObjectsSuccess &= checkSuccess(errorNumber);
    cl_float* matrixB = (cl_float*)clEnqueueMapBuffer(commandQueue, memoryObjects[1], CL_TRUE, CL_MAP_WRITE, 0, bufferSize, 0, NULL, NULL, &errorNumber);
    mapMemoryObjectsSuccess &= checkSuccess(errorNumber);
    cl_float* matrixC = (cl_float*)clEnqueueMapBuffer(commandQueue, memoryObjects[2], CL_TRUE, CL_MAP_WRITE, 0, bufferSize, 0, NULL, NULL, &errorNumber);
    mapMemoryObjectsSuccess &= checkSuccess(errorNumber);
    if (!mapMemoryObjectsSuccess)
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD ("Mapping memory objects failed ");
       return;
    }

    /* Fill the matrices with random data. */
    sgemmInitialize(matrixOrder, matrixA, matrixB, matrixC);

    for (int i = 0; i < 10; i ++) {
        LOGD ("%f", matrixC[i]);
    }

    /* Unmap the memory so we can pass it to the kernel. */
    bool unmapMemoryObjectsSuccess = true;
    unmapMemoryObjectsSuccess &= checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[0], matrixA, 0, NULL, NULL));
    unmapMemoryObjectsSuccess &= checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[1], matrixB, 0, NULL, NULL));
    unmapMemoryObjectsSuccess &= checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[2], matrixC, 0, NULL, NULL));
    if (!unmapMemoryObjectsSuccess)
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD ("Unmapping memory objects failed ");
       return;
    }

    /* Setup kernel arguments. */
    bool setKernelArgumentsSuccess = true;
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 0, sizeof(cl_mem), &memoryObjects[0]));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 1, sizeof(cl_mem), &memoryObjects[1]));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 2, sizeof(cl_mem), &memoryObjects[2]));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 3, sizeof(cl_uint), &matrixOrder));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 4, sizeof(cl_float), &alpha));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 5, sizeof(cl_float), &beta));
    if (!createMemoryObjectsSuccess)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed setting OpenCL kernel arguments. ");
        return;
    }

    /* An event to associate with the Kernel. Allows us to retrieve profiling information later. */
    cl_event event = 0;

    /* [Kernel size] */
    /*
     * Each kernel outputs one element in matrixC,
     * therefore the total number of work items must be the number of elements (matrixOrder * matrixOrder).
     * To accomplish this we use a global worksize split into 2 dimensions both of matrixOrder size.
     */
    size_t globalWorksize[2] = {matrixOrder, matrixOrder};
    /* [Kernel size] */

    /* Enqueue the kernel */
    if (!checkSuccess(clEnqueueNDRangeKernel(commandQueue, kernel, 2, NULL, globalWorksize, NULL, 0, NULL, &event)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed enqueuing the kernel. ");
        return;
    }

    /* Wait for kernel execution completion */
    if (!checkSuccess(clFinish(commandQueue)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed waiting for kernel execution to finish. ");
        return;
    }

    cl_float *ptr;
    ptr = (cl_float *)clEnqueueMapBuffer(commandQueue,
        memoryObjects[2],
        CL_TRUE,
        CL_MAP_READ,
        0,
        10 * sizeof(cl_float),
        0, NULL, NULL, NULL);

    LOGD ("================");
    for (int i = 0; i < 10; i++) {
        LOGD ("ptr[] at %d is %d", i, ptr[i]);
    }

    /* Print the profiling information for the event. */
    printProfilingInfo(event);
    /* Release the event object. */
    if (!checkSuccess(clReleaseEvent(event)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
        LOGD ("Failed releasing the event object. ");
        return;
    }

    /* Map the output to a host side pointer. */
    matrixC = (cl_float*)clEnqueueMapBuffer(commandQueue, memoryObjects[2], CL_TRUE, CL_MAP_READ, 0, bufferSize, 0, NULL, NULL, &errorNumber);
    if (!checkSuccess(errorNumber))
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD ("Mapping memory objects failed. ");
       return;
    }

    /* Do something with the output (matrixC) here. */

    /* Unmap the output. */
    if (!checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[2], matrixC, 0, NULL, NULL)))
    {
       cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
       LOGD ("Unmapping memory objects failed. ");
       return;
    }

    /* Release OpenCL objects. */
    cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numberOfMemoryObjects);
}
