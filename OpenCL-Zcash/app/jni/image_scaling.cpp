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

void scale_image();

extern "C" JNIEXPORT void JNICALL Java_suishi_opencl_MainActivity_scaleImage(JNIEnv *env, jobject thiz)
{
    printf("scaleImage");
    scale_image();
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

void scale_image() {
    cl_context context = 0;
    cl_command_queue commandQueue = 0;
    cl_program program = 0;
    cl_device_id device = 0;
    cl_kernel kernel = 0;
    const int numMemoryObjects = 2;
    cl_mem memoryObjects[numMemoryObjects] = {0, 0};
    cl_int errorNumber;

    /* Set up OpenCL environment: create context, command queue, program and kernel. */
    if (!createContext(&context))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed to create an OpenCL context.");
        return;
    }

    if (!createCommandQueue(context, &commandQueue, &device))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed to create the OpenCL command queue.");
        return;
    }

    size_t source_len;
    char* kernelStr = file_contents("/data/data/suishi.opencl/app_execdir/image_scaling.cl", &source_len);
    program = clCreateProgramWithSource(context, 1, (const char **)&kernelStr, &source_len, &errorNumber);

    if (!checkSuccess(errorNumber) || program == NULL)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed to create OpenCL program.");
        return;
    }

     errorNumber = clBuildProgram(program, 1, &device, "", NULL, NULL);
     if (!checkSuccess(errorNumber)) {
        get_program_build_log(program, device);
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD("OpenCL build failed (%d). Build log follows:\n", errorNumber);
        return;
     }

    kernel = clCreateKernel(program, "image_scaling", &errorNumber);
    if (!checkSuccess(errorNumber))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed to create OpenCL kernel.");
        return;
    }

    /* Print the image formats that the OpenCL device supports. */
    cout << endl;
    printSupported2DImageFormats(context);
    cout << endl;

    /* The scaling factor to use when resizing the image. */
    const int scaleFactor = 8;

    /* Load the input image data. */
    unsigned char* inputImage = NULL;
    int width, height;
    loadFromBitmap("/data/data/suishi.opencl/app_execdir/input.bmp", &width, &height, &inputImage);

    /*
     * Calculate the width and height of the new image.
     * Used to allocate the correct amount of output memory and the number of kernels to use.
     */
    int newWidth = width * scaleFactor;
    int newHeight = height * scaleFactor;

    /* [Allocate image objects] */
    /*
     * Specify the format of the image.
     * The bitmap image we are using is RGB888, which is not a supported OpenCL image format.
     * We will use RGBA8888 and add an empty alpha channel.
     */
    cl_image_format format;
    format.image_channel_data_type = CL_UNORM_INT8;
    format.image_channel_order = CL_RGBA;

    /* Allocate memory for the input image that can be accessed by the CPU and GPU. */
    bool createMemoryObjectsSuccess = true;

    memoryObjects[0] = clCreateImage2D(context, CL_MEM_READ_ONLY | CL_MEM_ALLOC_HOST_PTR, &format, width, height, 0, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);

    memoryObjects[1] = clCreateImage2D(context, CL_MEM_WRITE_ONLY | CL_MEM_ALLOC_HOST_PTR, &format, newWidth, newHeight, 0, NULL, &errorNumber);
    createMemoryObjectsSuccess &= checkSuccess(errorNumber);

    if (!createMemoryObjectsSuccess)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed creating the image.");
        return;
    }
    /* [Allocate image objects] */

    /* [Map image objects to host pointers] */
    /*
     * Like with memory buffers, we now map the allocated memory to a host side pointer.
     * Unlike buffers, we must specify origin coordinates, width and height for the region of the image we wish to map.
     */
    size_t origin[3] = {0, 0, 0};
    size_t region[3] = {width, height, 1};

    /*
     * clEnqueueMapImage also returns the rowPitch; the width of the mapped region in bytes.
     * If the image format is not known, this is required information when accessing the image object as a normal array.
     * The number of bytes per pixel can vary with the image format being used,
     * this affects the offset into the array for a given coordinate.
     * In our case the image format is fixed as RGBA8888 so we don't need to worry about the rowPitch.
     */
    size_t rowPitch;

    unsigned char* inputImageRGBA = (unsigned char*)clEnqueueMapImage(commandQueue,  memoryObjects[0], CL_TRUE, CL_MAP_WRITE, origin, region, &rowPitch, NULL, 0, NULL, NULL, &errorNumber);
    if (!checkSuccess(errorNumber))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed mapping the input image.");
        return;
    }
    /* [Map image objects to host pointers] */

    /* Convert the input data from RGB to RGBA (moves it to the OpenCL allocated memory at the same time). */
    RGBToRGBA(inputImage, inputImageRGBA, width, height);
    delete[] inputImage;

    /* Unmap the image from the host. */
    if (!checkSuccess(clEnqueueUnmapMemObject(commandQueue, memoryObjects[0], inputImageRGBA, 0, NULL, NULL)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed unmapping the input image.");
        return;
    }

    /*
     * Calculate the normalization factor for the image coordinates.
     * By using normalized coordinates we don't have to manually map the destination coordinates to the source coordinates.
     */
    cl_float widthNormalizationFactor = 1.0f / newWidth;
    cl_float heightNormalizationFactor = 1.0f / newHeight;

    /* Setup the kernel arguments. */
    bool setKernelArgumentsSuccess = true;
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 0, sizeof(cl_mem), &memoryObjects[0]));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 1, sizeof(cl_mem), &memoryObjects[1]));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 2, sizeof(cl_float), &widthNormalizationFactor));
    setKernelArgumentsSuccess &= checkSuccess(clSetKernelArg(kernel, 3, sizeof(cl_float), &heightNormalizationFactor));
    if (!setKernelArgumentsSuccess)
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, 3);
        LOGD ("Failed setting OpenCL kernel arguments.");
        return;
    }

    /*
     * Set the kernel work size. Each kernel operates on one pixel of the ouput image.
     * Therefore, we need newWidth * newHeight kernel instances.
     * We are using two work dimensions because it maps nicely onto the coordinates of the image.
     * With one dimension we would have to derive the y coordinate from the x coordinate in the kernel.
     */
    const int workDimensions = 2;
    size_t globalWorkSize[workDimensions] = {newWidth, newHeight};

    /* An event to associate with the kernel. Allows us to retrieve profiling information later. */
    cl_event event = 0;

    /* Enqueue the kernel. */
    if (!checkSuccess(clEnqueueNDRangeKernel(commandQueue, kernel, workDimensions, NULL, globalWorkSize, NULL, 0, NULL, &event)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed enqueuing the kernel.");
        return;
    }

    /* Wait for kernel execution completion. */
    if (!checkSuccess(clFinish(commandQueue)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed waiting for kernel execution to finish.");
        return;
    }

    /* Print the profiling information for the event. */
    printProfilingInfo(event);
    /* Release the event object. */
    if (!checkSuccess(clReleaseEvent(event)))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed releasing the event object.");
        return;
    }

    size_t newRegion[3] = {newWidth, newHeight, 1};

    unsigned char* outputImage = (unsigned char*)clEnqueueMapImage(commandQueue,  memoryObjects[1], CL_TRUE, CL_MAP_READ, origin, newRegion, &rowPitch, NULL, 0, NULL, NULL, &errorNumber);
    if (!checkSuccess(errorNumber))
    {
        cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
        LOGD ("Failed releasing the event object.");
        return;
    }

    unsigned char* outputImageRGB = new unsigned char[newWidth * newHeight * 3];
    RGBAToRGB(outputImage, outputImageRGB, newWidth, newHeight);

    saveToBitmap("/data/data/suishi.opencl/app_execdir/output.bmp", newWidth, newHeight, outputImageRGB);

    delete[] outputImageRGB;

    cleanUpOpenCL(context, commandQueue, program, kernel, memoryObjects, numMemoryObjects);
}