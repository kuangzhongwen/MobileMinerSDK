#include <dlfcn.h>
#include <stdio.h>
#include <string.h>
#include <CL/cl.h>
#include "OpenCLPhone.h"


 cl_int (*rclGetPlatformIDs)(
		cl_uint          /* num_entries */,
		cl_platform_id * /* platforms */,
		cl_uint *        /* num_platforms */);

 cl_int (*rclGetPlatformInfo)(
		cl_platform_id   /* platform */,
		cl_platform_info /* param_name */,
		size_t           /* param_value_size */,
		void *           /* param_value */,
		size_t *         /* param_value_size_ret */);

 cl_int (*rclGetDeviceIDs)(
		cl_platform_id   /* platform */,
		cl_device_type   /* device_type */,
		cl_uint          /* num_entries */,
		cl_device_id *   /* devices */,
		cl_uint *        /* num_devices */);


 cl_int (*rclGetDeviceInfo)(
		cl_device_id    /* device */,
		cl_device_info  /* param_name */,
		size_t          /* param_value_size */,
		void *          /* param_value */,
		size_t *        /* param_value_size_ret */);

 cl_context (*rclCreateContext)(
 		const cl_context_properties * /* properties */,
         cl_uint                 /* num_devices */,
         const cl_device_id *    /* devices */,
         void (CL_CALLBACK * /* pfn_notify */)(const char *, const void *, size_t, void *),
         void *                  /* user_data */,
         cl_int *                /* errcode_ret */);

 cl_int (*rclReleaseContext)(
		 cl_context /* context */);

 cl_command_queue (*rclCreateCommandQueue)(
 		cl_context                     /* context */,
 		cl_device_id                   /* device */,
 		cl_command_queue_properties    /* properties */,
 		cl_int *                       /* errcode_ret */);

 cl_int (*rclReleaseCommandQueue)(
		  cl_command_queue /* command_queue */) ;


 cl_program (*rclCreateProgramWithSource)(
 		cl_context        /* context */,
 		cl_uint           /* count */,
 		const char **     /* strings */,
 		const size_t *    /* lengths */,
 		cl_int *          /* errcode_ret */) ;

cl_int(*rclReleaseProgram)(
		cl_program /* program */);

 cl_int (*rclBuildProgram)(
 		cl_program           /* program */,
 		cl_uint              /* num_devices */,
 		const cl_device_id * /* device_list */,
 		const char *         /* options */,
 		void (CL_CALLBACK *  /* pfn_notify */)(cl_program /* program */, void * /* user_data */),
 		void *               /* user_data */) ;

 cl_int(*rclGetProgramBuildInfo)(
		 cl_program            /* program */,
         cl_device_id          /* device */,
         cl_program_build_info /* param_name */,
         size_t                /* param_value_size */,
         void *                /* param_value */,
         size_t *              /* param_value_size_ret */);

 cl_int (*rclGetContextInfo)(
 		cl_context         /* context */,
 		cl_context_info    /* param_name */,
 		size_t             /* param_value_size */,
        void *             /* param_value */,
        size_t *           /* param_value_size_ret */);

 cl_int (*rclGetEventProfilingInfo)(
 		cl_event            /* event */,
 		cl_profiling_info   /* param_name */,
 		size_t              /* param_value_size */,
 		void *              /* param_value */,
 		size_t *			/* param_value_size_ret */ );
 /*load library*/

 cl_mem (*rclCreateBuffer)(
 		cl_context   /* context */,
 		cl_mem_flags /* flags */,
 		size_t       /* size */,
 		void *       /* host_ptr */,
 		cl_int *     /* errcode_ret */);

 cl_int(*rclReleaseMemObject)(
		 cl_mem /* memobj */);

 cl_kernel	(*rclCreateKernel)(
 		cl_program      /* program */,
 		const char *    /* kernel_name */,
 		cl_int *        /* errcode_ret */) ;

 cl_int(*rclReleaseKernel)(
		 cl_kernel   /* kernel */) ;


 cl_int (*rclSetKernelArg)(
 		cl_kernel    /* kernel */,
 		cl_uint      /* arg_index */,
 		size_t       /* arg_size */,
 		const void * /* arg_value */);

 cl_int(*rclEnqueueNDRangeKernel)(
 		cl_command_queue /* command_queue */,
 		cl_kernel        /* kernel */,
 		cl_uint          /* work_dim */,
 		const size_t *   /* global_work_offset */,
 		const size_t *   /* global_work_size */,
 		const size_t *   /* local_work_size */,
 		cl_uint          /* num_events_in_wait_list */,
 		const cl_event * /* event_wait_list */,
 		cl_event *       /* event */);

 /* Flush and Finish APIs */
cl_int(*rclFlush)(
		  cl_command_queue /* command_queue */) ;
 cl_int	(*rclFinish)(
 		cl_command_queue /* command_queue */);

 cl_int (*rclEnqueueReadBuffer)(
 		cl_command_queue    /* command_queue */,
 		cl_mem              /* buffer */,
 		cl_bool             /* blocking_read */,
 		size_t              /* offset */,
 		size_t              /* size */,
 		void *              /* ptr */,
 		cl_uint             /* num_events_in_wait_list */,
 		const cl_event *    /* event_wait_list */,
 		cl_event *          /* event */);

 cl_int (*rclEnqueueWriteBuffer)(
 		cl_command_queue   /* command_queue */,
 		cl_mem             /* buffer */,
 		cl_bool            /* blocking_write */,
 		size_t             /* offset */,
 		size_t             /* size */,
 		const void *       /* ptr */,
 		cl_uint            /* num_events_in_wait_list */,
 		const cl_event *   /* event_wait_list */,
 		cl_event *         /* event */);

  cl_int(*rclEnqueueCopyBuffer)(
		  cl_command_queue    /* command_queue */,
		  cl_mem              /* src_buffer */,
		  cl_mem              /* dst_buffer */,
		  size_t              /* src_offset */,
		  size_t              /* dst_offset */,
		  size_t              /* size */,
		  cl_uint             /* num_events_in_wait_list */,
		  const cl_event *    /* event_wait_list */,
		  cl_event *          /* event */) ;

 cl_int(* rclGetKernelInfo)(
		 cl_kernel       /* kernel */,
         cl_kernel_info  /* param_name */,
         size_t          /* param_value_size */,
         void *          /* param_value */,
         size_t *        /* param_value_size_ret */) ;

cl_int(*rclGetKernelWorkGroupInfo)(
		  cl_kernel                  /* kernel */,
		  cl_device_id               /* device */,
		  cl_kernel_work_group_info  /* param_name */,
		  size_t                     /* param_value_size */,
		  void *                     /* param_value */,
		  size_t *                   /* param_value_size_ret */) ;

/* Event Object APIs */
 cl_int(*rclWaitForEvents)(
		  cl_uint             /* num_events */,
         const cl_event *    /* event_list */) ;

 cl_int(*rclGetEventInfo)(
		  cl_event         /* event */,
		  cl_event_info    /* param_name */,
		  size_t           /* param_value_size */,
		  void *           /* param_value */,
		  size_t *         /* param_value_size_ret */) ;

 cl_event(*rclCreateUserEvent)(
		  cl_context    /* context */,
         cl_int *      /* errcode_ret */) ;

 cl_int(*rclRetainEvent)(
		  cl_event /* event */) ;

 cl_int(*rclReleaseEvent)(
		  cl_event /* event */) ;

  cl_int (* rclSetUserEventStatus)(
		  cl_event   /* event */,
         cl_int     /* execution_status */) ;

 cl_int(*rclSetEventCallback)(
		  cl_event    /* event */,
         cl_int      /* command_exec_callback_type */,
          void (CL_CALLBACK * /* pfn_notify */)(cl_event, cl_int, void *),
          void *      /* user_data */) ;

 void * CL_API_CALL(*rclEnqueueMapBuffer)(
		 cl_command_queue /* command_queue */,
         cl_mem           /* buffer */,
         cl_bool          /* blocking_map */,
         cl_map_flags     /* map_flags */,
         size_t           /* offset */,
         size_t           /* size */,
         cl_uint          /* num_events_in_wait_list */,
         const cl_event * /* event_wait_list */,
         cl_event *       /* event */,
         cl_int *         /* errcode_ret */) ;

 cl_int (*rclEnqueueUnmapMemObject)(
		  cl_command_queue /* command_queue */,
		  cl_mem           /* memobj */,
		  void *           /* mapped_ptr */,
		  cl_uint          /* num_events_in_wait_list */,
		  const cl_event *  /* event_wait_list */,
		  cl_event *        /* event */) ;

void *getCLHandle(){
			void *res = NULL;
			res = dlopen("/system/vendor/lib/libOpenCL.so", RTLD_LAZY); // Adreno GPU
			if (res == NULL) {
				res = dlopen("/system/lib/libOpenCL.so", RTLD_LAZY); // Adreno GPU older
			}
			if (res == NULL) {
				res = dlopen("/system/vendor/lib/libPVROCL.so", RTLD_LAZY); // PowerVR GPU
			}
			if (res == NULL) {
				res = dlopen("/system/lib/libPVROCL.so", RTLD_LAZY); // // PowerVR GPU older
			}
			if (res == NULL) {
				res = dlopen("/system/vendor/lib/egl/libGLES_mali.so", RTLD_LAZY);  // Mali GPU
			}
			if (res == NULL) {
				res = dlopen("/system/lib/egl/libGLES_mali.so", RTLD_LAZY); // Mali GPU older
			}
			if (res == NULL) {
				printf("No OpenCL library in your Phone ");
			}
			return res;
}


int  load_Func(){
			void *handle = getCLHandle();
			if(handle==NULL){
					return ERROR_LIBRARY;
			}

			rclGetPlatformIDs = (cl_int (*)(cl_uint,cl_platform_id *,cl_uint*))dlsym(handle,"clGetPlatformIDs");
			if(rclGetPlatformIDs==NULL){
					printf("%s at %d : load clGetPlatformIDs error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}


			rclGetPlatformInfo = (cl_int (*)(cl_platform_id, cl_platform_info, size_t, void *, size_t*))dlsym(handle,"clGetPlatformInfo");
			if(rclGetPlatformInfo==NULL){
						printf("%s at %d : load rclGetPlatformInfo error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclGetDeviceIDs = (cl_int (*)(cl_platform_id, cl_device_type, cl_uint, cl_device_id *, cl_uint*))dlsym(handle,"clGetDeviceIDs");
			if(rclGetDeviceIDs==NULL){
						printf("%s at %d : load rclGetDeviceIDs error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclGetDeviceInfo = (cl_int (*)(cl_device_id, cl_device_info, size_t, void *, size_t*))dlsym(handle,"clGetDeviceInfo");
			if(rclGetDeviceInfo==NULL){
						printf("%s at %d : load rclGetDeviceInfo error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclCreateContext = (cl_context(*)(const cl_context_properties*, cl_uint, const cl_device_id*,void (CL_CALLBACK * /* pfn_notify */)(const char *, const void *,  size_t, void *),void*,cl_int* ))dlsym(handle,"clCreateContext");
			if(rclCreateContext==NULL){
						printf("%s at %d : load rclCreateContext error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclReleaseContext=(cl_int(*)(cl_context))dlsym(handle,"clReleaseContext");
			if(rclReleaseContext==NULL){
						printf("%s at %d : load rclReleaseContext error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclCreateCommandQueue=(cl_command_queue(*)(cl_context,  cl_device_id,cl_command_queue_properties,cl_int *) )dlsym(handle,"clCreateCommandQueue");
			if(rclCreateCommandQueue==NULL){
					printf("%s at %d : load rclCreateCommandQueue error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclReleaseCommandQueue=(cl_int(*)(cl_command_queue))dlsym(handle,"clReleaseCommandQueue");
			if(rclReleaseCommandQueue==NULL){
						printf("%s at %d : load rclReleaseCommandQueue error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			 rclCreateProgramWithSource=(cl_program(*)(cl_context ,cl_uint  ,const char ** ,const size_t * ,cl_int *)) dlsym(handle,"clCreateProgramWithSource");
			 if(rclCreateProgramWithSource==NULL){
						printf("%s at %d : load rclCreateProgramWithSource error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			 }

			 rclReleaseProgram=(cl_int(*)(cl_program))dlsym(handle,"clReleaseProgram");
			 if(rclReleaseProgram==NULL){
						printf("%s at %d : load rclReleaseProgram error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			 rclBuildProgram=(cl_int(*)(	cl_program,cl_uint,const cl_device_id *,const char *,void (CL_CALLBACK * )(cl_program , void * ),void *  ) )dlsym(handle,"clBuildProgram");
			 if(rclBuildProgram==NULL){
							printf("%s at %d : load rclBuildProgram error! ",__FILE__ , __LINE__-2);
							return ERROR_FUN_INIT;
				 }

			rclGetProgramBuildInfo=(cl_int(*)(cl_program, cl_device_id,cl_program_build_info,size_t, void * , size_t *   ) )dlsym(handle,"clGetProgramBuildInfo");
			if(rclGetProgramBuildInfo==NULL){
					printf("%s at %d : load rclGetProgramBuildInfo error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclGetContextInfo=(cl_int(*)(cl_context,cl_context_info ,size_t,void * ,size_t *   ) )dlsym(handle,"clGetContextInfo");
			if(rclGetContextInfo==NULL){
					printf("%s at %d : load clGetContextInfo error! ",__FILE__ , __LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclGetEventProfilingInfo=(cl_int(*)(cl_event,cl_profiling_info,size_t,void *,size_t *  ) )dlsym(handle,"clGetEventProfilingInfo");
			if(rclGetEventProfilingInfo==NULL){
					printf("%s at %d : load clGetEventProfilingInfo error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclCreateBuffer=(cl_mem(*)(cl_context,cl_mem_flags ,size_t ,void *,cl_int *  ) )dlsym(handle,"clCreateBuffer");
			if(rclCreateBuffer==NULL){
					printf("%s at %d : load rclCreateBuffer error! ",__FILE__ , __LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclReleaseMemObject=(cl_int(*)(cl_mem))dlsym(handle,"clReleaseMemObject");
			if(rclReleaseMemObject==NULL){
					printf("%s at %d : load rclReleaseMemObject error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclCreateKernel=(cl_kernel(*)(cl_program,const char * ,cl_int *   ) )dlsym(handle,"clCreateKernel");
			if(rclCreateKernel==NULL){
					printf("%s at %d : load rclCreateKernel error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclReleaseKernel=(cl_int(*)(cl_kernel))dlsym(handle,"clReleaseKernel");
			if(rclReleaseKernel==NULL){
						printf("%s at %d : load rclReleaseKernel error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclSetKernelArg=(cl_int(*)(cl_kernel,cl_uint,size_t,const void *   ) )dlsym(handle,"clSetKernelArg");
			if(rclSetKernelArg==NULL){
					printf("%s at %d : load clSetKernelArg error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclEnqueueNDRangeKernel=(cl_int(*)(cl_command_queue ,cl_kernel ,cl_uint,const size_t *,const size_t *  ,const size_t *,cl_uint,const cl_event *,cl_event * ) )dlsym(handle,"clEnqueueNDRangeKernel");
			if(rclEnqueueNDRangeKernel==NULL){
					printf("%s at %d : load rclEnqueueNDRangeKernel error! ",__FILE__ ,__LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclFlush=(cl_int(*)(cl_command_queue) )dlsym(handle,"clFlush");
			if(rclFlush==NULL){
					printf("%s at %d : load rclFlush error! ",__FILE__ , __LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclFinish=(cl_int(*)(cl_command_queue) )dlsym(handle,"clFinish");
			if(rclFinish==NULL){
					printf("%s at %d : load rclFinish error! ",__FILE__ , __LINE__-2);
					return ERROR_FUN_INIT;
			}

			rclEnqueueReadBuffer=(cl_int(*)(cl_command_queue,cl_mem,cl_bool,size_t,size_t,void *,cl_uint ,const cl_event * ,cl_event * ) )dlsym(handle,"clEnqueueReadBuffer");
			if(rclEnqueueReadBuffer==NULL){
						printf("%s at %d : load rclEnqueueReadBuffer error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclEnqueueWriteBuffer=(cl_int(*)(		cl_command_queue ,cl_mem ,cl_bool ,size_t ,size_t ,const void *,cl_uint ,const cl_event *,cl_event *  ) )dlsym(handle,"clEnqueueWriteBuffer");
			if(rclEnqueueWriteBuffer==NULL){
						printf("%s at %d : load rclEnqueueWriteBuffer error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}


			rclEnqueueCopyBuffer=(cl_int(*)( cl_command_queue, cl_mem,cl_mem,size_t,size_t, size_t,cl_uint,const cl_event * , cl_event *   ))dlsym(handle,"clEnqueueCopyBuffer");
			if(clEnqueueCopyBuffer==NULL){
							printf("%s at %d : load clEnqueueCopyBuffer error! ",__FILE__ ,__LINE__-2);
							return ERROR_FUN_INIT;
			}


			rclGetKernelInfo=(cl_int(*)( cl_kernel,cl_kernel_info ,size_t, void * , size_t *))dlsym(handle,"clGetKernelInfo");
			if(rclGetKernelInfo==NULL){
						printf("%s at %d : load rclGetKernelInfo error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclGetKernelWorkGroupInfo=(cl_int(*)( cl_kernel,cl_device_id, cl_kernel_work_group_info,size_t,void *,size_t *))dlsym(handle,"clGetKernelWorkGroupInfo");
			if(rclGetKernelWorkGroupInfo==NULL){
						printf("%s at %d : load rclGetKernelWorkGroupInfo error! ",__FILE__ ,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			/* Event Object APIs */
			rclWaitForEvents=(cl_int(*)(cl_uint , const cl_event * ))dlsym(handle,"clWaitForEvents");
			if(rclWaitForEvents==NULL){
						printf("%s at %d : load rclWaitForEvents error! ",__FILE__,__LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclGetEventInfo=(cl_int(*)(cl_event,cl_event_info,size_t,void*,size_t*))dlsym(handle,"clGetEventInfo");
			if(rclGetEventInfo==NULL){
						printf("%s at %d : load rclGetEventInfo error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclCreateUserEvent=(cl_event(*)(cl_context ,cl_int *))dlsym(handle,"clCreateUserEvent");
			if(rclCreateUserEvent==NULL){
						printf("%s at %d : load rclCreateUserEvent error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclRetainEvent=(cl_int(*)(cl_event))dlsym(handle,"clRetainEvent");
			if(rclRetainEvent==NULL){
						printf("%s at %d : load rclRetainEvent error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}

			rclReleaseEvent=(cl_int(*)(cl_event))dlsym(handle,"clReleaseEvent");
			if(rclReleaseEvent==NULL){
							printf("%s at %d : load rclReleaseEvent error! ",__FILE__,__LINE__-2);
							return ERROR_FUN_INIT;
			}

			rclSetUserEventStatus=(cl_int(*)(cl_event,cl_int))dlsym(handle,"clSetUserEventStatus");
			if(rclSetUserEventStatus==NULL){
						printf("%s at %d : load rclSetUserEventStatus error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
			}

		rclSetEventCallback=(cl_int(*)( cl_event ,cl_int , void (CL_CALLBACK * )(cl_event, cl_int, void *), void *))dlsym(handle,"clSetEventCallback");
		if(rclSetEventCallback==NULL){
					printf("%s at %d : load rclSetEventCallback error! ",__FILE__ , __LINE__-2);
					return ERROR_FUN_INIT;
		}


		rclEnqueueMapBuffer=(void * CL_API_CALL(*)(cl_command_queue,cl_mem,cl_bool,cl_map_flags,size_t, size_t,cl_uint, const cl_event *,cl_event * ,cl_int *))dlsym(handle,"clEnqueueMapBuffer");
		if(rclEnqueueMapBuffer==NULL){
						printf("%s at %d : load rclEnqueueMapBuffer error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
		}
		rclEnqueueUnmapMemObject=(cl_int(*)(cl_command_queue ,cl_mem, void *,cl_uint,const cl_event *, cl_event * ))dlsym(handle,"clEnqueueUnmapMemObject");
		if(rclEnqueueUnmapMemObject==NULL){
						printf("%s at %d : load rclEnqueueUnmapMemObject error! ",__FILE__ , __LINE__-2);
						return ERROR_FUN_INIT;
		}

		return ERROR_NO;
}
