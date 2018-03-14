#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <CL/cl.h>
#include"OpenCL_phone.h"

#define  _GNU_SOURCE	1
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <stdint.h>
#include <inttypes.h>
#include <assert.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <getopt.h>
#include <errno.h>
#include <time.h>
#include <unistd.h>
#include <errno.h>
#include <unistd.h>
#include "blake.h"
#include "sha256.h"

#ifndef LOG_INCLUDED
#define LOG_INCLUDED

#define ANDROID_V

#ifdef ANDROID_V
#include <android/log.h>
#include <errno.h>

#define  LOG_TAG "WaterholeZcashMiner"
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

typedef uint8_t		uchar;
typedef uint32_t	uint;
#include "param.h"

#define MIN(A, B)	(((A) < (B)) ? (A) : (B))
#define MAX(A, B)	(((A) > (B)) ? (A) : (B))

int		    verbose = 0;
uint32_t	show_encoded = 0;
uint64_t	nr_nonces = 1000;
uint32_t	do_list_devices = 0;
uint32_t	gpu_to_use = 0;
uint32_t	mining = 1;
double		kern_avg_run_time = 0;

// 挖矿频率
const unsigned int mining_sleep_seconds = 0;

typedef struct  debug_s {
    uint32_t    dropped_coll;
    uint32_t    dropped_stor;
}               debug_t;

void warn(const char *fmt, ...) {
    va_list     ap;
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
}

void fatal(const char *fmt, ...) {
    va_list     ap;
    va_start(ap, fmt);
    printf("fatal: %s", fmt);
    va_end(ap);
    exit(1);
}

char *file_contents(const char *filename, int *length) {
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

uint64_t parse_num(char *str) {
    char	*endptr;
    uint64_t	n;
    n = strtoul(str, &endptr, 0);

    if (endptr == str || *endptr) {
	    fatal("'%s' is not a valid number\n", str);
	}

    return n;
}

uint64_t now(void) {
    struct timeval	tv;
    gettimeofday(&tv, NULL);

    return (uint64_t)tv.tv_sec * 1000 * 1000 + tv.tv_usec;
}

void show_time(uint64_t t0) {
    uint64_t            t1;
    t1 = now();
    fprintf(stderr, "Elapsed time: %.1f msec\n", (t1 - t0) / 1e3);
}

void set_blocking_mode(int fd, int block) {
    int		f;
    if (-1 == (f = fcntl(fd, F_GETFL))) {
	    fatal("fcntl F_GETFL: %s\n", strerror(errno));
	}
    if (-1 == fcntl(fd, F_SETFL, block ? (f & ~O_NONBLOCK) : (f | O_NONBLOCK))) {
	    fatal("fcntl F_SETFL: %s\n", strerror(errno));
	}
}

void randomize(void *p, ssize_t l) {
    const char	*fname = "/dev/urandom";
    int		fd;
    ssize_t	ret;

    if (-1 == (fd = open(fname, O_RDONLY))) {
	    fatal("open %s: %s\n", fname, strerror(errno));
	}
    if (-1 == (ret = read(fd, p, l))) {
	    fatal("read %s: %s\n", fname, strerror(errno));
	}
    if (ret != l) {
	    fatal("%s: short read %d bytes out of %d\n", fname, ret, l);
	}
    if (-1 == close(fd)) {
	    fatal("close %s: %s\n", fname, strerror(errno));
	}
}

#define NSEC 1e-9
double timespec_to_double(struct timespec *t) {
    return ((double)t -> tv_sec) + ((double) t -> tv_nsec) * NSEC;
}

void double_to_timespec(double dt, struct timespec *t) {
    t -> tv_sec = (long)dt;
    t -> tv_nsec = (long)((dt - t->tv_sec) / NSEC);
}

void get_time(struct timespec *t) {
    clock_gettime(CLOCK_MONOTONIC, t);
}

void check_clSetKernelArg(cl_kernel k, cl_uint a_pos, cl_mem *a) {
    cl_int	status;
    status = rclSetKernelArg(k, a_pos, sizeof (*a), a);

    if (status != CL_SUCCESS) {
	    fatal("clSetKernelArg (%d)\n", status);
	}
}

void check_clEnqueueNDRangeKernel(cl_command_queue queue, cl_kernel k, cl_uint
	work_dim, const size_t *global_work_offset, const size_t *global_work_size,
	const size_t *local_work_size, cl_uint num_events_in_wait_list,
	const cl_event *event_wait_list, cl_event *event) {

    cl_uint	status;
    status = rclEnqueueNDRangeKernel(queue, k, work_dim, global_work_offset,
	    global_work_size, local_work_size, num_events_in_wait_list, event_wait_list, event);

    if (status != CL_SUCCESS) {
        LOGD ("clEnqueueNDRangeKernel (%d)\n", status);
	    fatal("clEnqueueNDRangeKernel (%d)\n", status);
	}
}

void check_clEnqueueReadBuffer(cl_command_queue queue, cl_mem buffer, cl_bool blocking_read,
    size_t offset, size_t size, void *ptr, cl_uint num_events_in_wait_list,
    const cl_event *event_wait_list, cl_event *event) {

    cl_int	status;
    status = rclEnqueueReadBuffer(queue, buffer, blocking_read, offset, size, ptr,
        num_events_in_wait_list, event_wait_list, event);
    if (status != CL_SUCCESS) {
        fatal("clEnqueueReadBuffer (%d)\n", status);
    }
}

void hexdump(uint8_t *a, uint32_t a_len) {
    for (uint32_t i = 0; i < a_len; i++) {
        fprintf(stderr, "%02x", a[i]);
    }
}

char *s_hexdump(const void *_a, uint32_t a_len) {
    const uint8_t	*a = _a;
    static char		buf[4096];
    uint32_t		i;

    for (i = 0; i < a_len && i + 2 < sizeof (buf); i++) {
        sprintf(buf + i * 2, "%02x", a[i]);
    }
    buf[i * 2] = 0;
    return buf;
}

uint8_t hex2val(const char *base, size_t off) {
    const char  c = base[off];
    if (c >= '0' && c <= '9') {
        return c - '0';
    } else if (c >= 'a' && c <= 'f') {
        return 10 + c - 'a';
    } else if (c >= 'A' && c <= 'F') {
        return 10 + c - 'A';
    }

    fatal("Invalid hex char at offset %zd: ...%c...\n", off, c);

    return 0;
}

unsigned nr_compute_units(const char *gpu) {
    if (!strcmp(gpu, "rx480")) {
        return 4;
    }

    fprintf(stderr, "Unknown GPU: %s\n", gpu);
    return 0;
}

void get_program_build_log(cl_program program, cl_device_id device) {
    cl_int		status;
    char	    val[2 * 1024 * 1024];
    size_t		ret = 0;
    status = rclGetProgramBuildInfo(program, device,
	    CL_PROGRAM_BUILD_LOG,
	    sizeof (val),	// size_t param_value_size
	    &val,		// void *param_value
	    &ret);		// size_t *param_value_size_ret

    if (status != CL_SUCCESS) {
	    fatal("clGetProgramBuildInfo (%d)\n", status);
	}

    fprintf(stderr, "%s\n", val);
}

void dump(const char *fname, void *data, size_t len) {
    int			fd;
    ssize_t		ret;
    if (-1 == (fd = open(fname, O_WRONLY | O_CREAT | O_TRUNC, 0666))) {
    	fatal("%s: %s\n", fname, strerror(errno));
    }
    ret = write(fd, data, len);
    if (ret == -1) {
	    fatal("write: %s: %s\n", fname, strerror(errno));
	}
    if ((size_t)ret != len) {
	    fatal("%s: partial write\n", fname);
	}
    if (-1 == close(fd)) {
	    fatal("close: %s: %s\n", fname, strerror(errno));
	}
}

void print_platform_info(cl_platform_id plat) {
    char	name[1024];
    size_t	len = 0;
    int		status;
    status = rclGetPlatformInfo(plat, CL_PLATFORM_NAME, sizeof (name), &name, &len);
    if (status != CL_SUCCESS) {
	    fatal("clGetPlatformInfo (%d)\n", status);
	}
    printf("Devices on platform \"%s\":\n", name);
    fflush(stdout);
}

void print_device_info(unsigned i, cl_device_id d) {
    char	name[1024];
    size_t	len = 0;
    int		status;
    status = rclGetDeviceInfo(d, CL_DEVICE_NAME, sizeof (name), &name, &len);
    if (status != CL_SUCCESS) {
	    fatal("clGetDeviceInfo (%d)\n", status);
	}
    printf("  ID %d: %s\n", i, name);
    fflush(stdout);
}

void print_cl_mem_uint(cl_command_queue queue, cl_mem cl_m) {
    uint     *ht;
    ht = (uint *) malloc(20);
    if (!ht) {
       fatal("malloc: %s\n", strerror(errno));
    }

    check_clEnqueueReadBuffer(queue, cl_m,
         CL_FALSE,	// cl_bool	blocking_read
         0,		// size_t	offset
         20,    // size_t	size
         ht,	        // void		*ptr
         0,		// cl_uint	num_events_in_wait_list
         NULL,	// cl_event	*event_wait_list
         NULL);	// cl_event	*event

    rclFlush(queue);

    for (int i = 0; i < 20; i++) {
         fprintf(stderr, "cl_mem[%d] is: %d\n", i, ht[i]);
    }

    free(ht);
}

#ifdef ENABLE_DEBUG
uint32_t has_i(uint32_t round, uint8_t *ht, uint32_t row, uint32_t i, uint32_t mask, uint32_t *res) {
    uint32_t	slot;
    uint8_t	*p = (uint8_t *)(ht + row * NR_SLOTS * SLOT_LEN);
    uint32_t	cnt = *(uint32_t *)p;
    cnt = MIN(cnt, NR_SLOTS);
    for (slot = 0; slot < cnt; slot++, p += SLOT_LEN) {
	if ((*(uint32_t *)(p + xi_offset_for_round(round) - 4) & mask) == (i & mask)) {
	    if (res) {
		    *res = slot;
		}

	    return 1;
	  }
    }

    return 0;
}

uint32_t has_xi(uint32_t round, uint8_t *ht, uint32_t row, uint32_t xi, uint32_t *res) {
    uint32_t slot;
    uint8_t	*p = (uint8_t *)(ht + row * NR_SLOTS * SLOT_LEN);
    uint32_t cnt = *(uint32_t *)p;
    cnt = MIN(cnt, NR_SLOTS);

    for (slot = 0; slot < cnt; slot++, p += SLOT_LEN) {
	  if ((*(uint32_t *)(p + xi_offset_for_round(round))) == (xi)) {
	    if (res) {
		    *res = slot;
		}
	    return 1;
	  }
    }
    return 0;
}

void examine_ht(unsigned round, cl_command_queue queue, cl_mem * buf_ht) {
    uint8_t *ht;
    uint8_t	*p;

    if (verbose < 3) {
	    return ;
	}
    /*ht = (uint8_t *)malloc(8);
    if (!ht)
	fatal("malloc: %s\n", strerror(errno));
    check_clEnqueueReadBuffer(queue, buf_ht,
	    CL_FALSE,	// cl_bool	blocking_read
	    0,		// size_t	offset
	    8,    // size_t	size
	    ht,	        // void		*ptr
	    0,		// cl_uint	num_events_in_wait_list
	    NULL,	// cl_event	*event_wait_list
	    NULL);	// cl_event	*event
    fprintf(stderr, "%s", "0");
	for (int i = 0; i < 8; i++)
              fprintf(stderr, "ht[%d] is: %d\n", i, ht[i]);
    fprintf(stderr, "%s", "1");
*/

    cl_int status;
    ht = (uint8_t *) rclEnqueueMapBuffer(queue, buf_ht, CL_FALSE, CL_MAP_WRITE, 0, HT_SIZE, 0, NULL, NULL, &status);
    rclEnqueueReadBuffer(queue, buf_ht, CL_FALSE, 0, HT_SIZE, ht, 0, NULL, NULL);

    for (int i = 0; i < 128; i++) {
        LOGD ("ht[%d]: %02x\n", i, *ht);
    }

    for (unsigned row = 0; row < NR_ROWS; row++) {
	    char show = 0;
	    uint32_t star = 0;
	    if (round == 0) {
	        // i = 0x35c and 0x12d31f collide on first 20 bits
	        show |= has_i(round, ht, row, 0x35c, 0xffffffffUL, &star);
	        show |= has_i(round, ht, row, 0x12d31f, 0xffffffffUL, &star);
	    }
	    if (round == 1) {
	        show |= has_xi(round, ht, row, 0xf0937683, &star);
	    }
	    if (round == 2) {
	        show |= has_xi(round, ht, row, 0x3519d2e0, &star);
	    }
	    if (round == 3) {
	        show |= has_xi(round, ht, row, 0xd6950b66, &star);
	    }
	    if (round == 4) {
	        show |= has_xi(round, ht, row, 0xa92db6ab, &star);
	    }
	    if (round == 5) {
	        show |= has_xi(round, ht, row, 0x2daaa343, &star);
	    }
	    if (round == 6) {
	        show |= has_xi(round, ht, row, 0x53b9dd5d, &star);
	    }
	    if (round == 7) {
	        show |= has_xi(round, ht, row, 0xb9d374fe, &star);
	    }
	    if (round == 8) {
	        show |= has_xi(round, ht, row, 0x005ae381, &star);
	    }
	    // show |= (row < 256);
	    if (show) {
            LOGD ("row %#x:\n", row);
	        uint32_t cnt = *(uint32_t *)(ht + row * NR_SLOTS * SLOT_LEN);
	        cnt = MIN(cnt, NR_SLOTS);
	        for (unsigned slot = 0; slot < cnt; slot++) {
		        if (slot < NR_SLOTS) {
		            p = ht + row * NR_SLOTS * SLOT_LEN + slot * SLOT_LEN;
		            LOGD ("%c%02x ", (star == slot) ? '*' : ' ', slot);
		            for (unsigned i = 0; i < 4; i++, p++) {
			            !slot ? LOGD ("%02x", *p) : LOGD ("__");
			        }

		            uint64_t val[3] = {0,};
		            for (unsigned i = 0; i < 28; i++, p++) {
			            if (i == round / 2 * 4 + 4) {
			                val[0] = *(uint64_t *)(p + 0);
			                val[1] = *(uint64_t *)(p + 8);
			                val[2] = *(uint64_t *)(p + 16);
                            LOGD (" | ");
			            } else if (!(i % 4)) {
			                LOGD (" ");
			            }

			            LOGD ("%02x", *p);
		            }

		            val[0] = (val[0] >> 4) | (val[1] << (64 - 4));
		            val[1] = (val[1] >> 4) | (val[2] << (64 - 4));
		            val[2] = (val[2] >> 4);
		        }
		    }
	    }
    }

    free(ht);
}

#else
void examine_ht(unsigned round, cl_command_queue queue, cl_mem buf_ht) {
    (void)round;
    (void)queue;
    (void)buf_ht;
}
#endif

void examine_dbg(cl_command_queue queue, cl_mem buf_dbg, size_t dbg_size) {
    debug_t     *dbg;
    size_t      dropped_coll_total, dropped_stor_total;
    if (verbose < 2) {
	    return;
	}

    dbg = (debug_t *)malloc(dbg_size);
    if (!dbg) {
	    fatal("malloc: %s\n", strerror(errno));
	}
    check_clEnqueueReadBuffer(queue, buf_dbg,
            CL_TRUE,	// cl_bool	blocking_read
            0,		// size_t	offset
            dbg_size,   // size_t	size
            dbg,	// void		*ptr
            0,		// cl_uint	num_events_in_wait_list
            NULL,	// cl_event	*event_wait_list
            NULL);	// cl_event	*event

    dropped_coll_total = dropped_stor_total = 0;

    for (unsigned tid = 0; tid < dbg_size / sizeof (*dbg); tid++) {
        dropped_coll_total += dbg[tid].dropped_coll;
        dropped_stor_total += dbg[tid].dropped_stor;
	    if (0 && (dbg[tid].dropped_coll || dbg[tid].dropped_stor)) {
	        LOGD("thread %6d: dropped_coll %zd dropped_stor %zd\n", tid, dbg[tid].dropped_coll, dbg[tid].dropped_stor);
		}
    }

    LOGD("Dropped: %zd (coll) %zd (stor)\n", dropped_coll_total, dropped_stor_total);
    free(dbg);
}

size_t select_work_size_blake(void) {
    size_t  work_size = 64 * /* thread per wavefront */
    BLAKE_WPS * /* wavefront per simd */
    4 * /* simd per compute unit */
    nr_compute_units("rx480");
    // Make the work group size a multiple of the nr of wavefronts, while
    // dividing the number of inputs. This results in the worksize being a
    // power of 2.
    while (NR_INPUTS % work_size) {
        work_size += 64;
    }

    return work_size;
}

void init_ht(cl_command_queue queue, cl_kernel k_init_ht, cl_mem buf_ht, cl_mem rowCounters) {
    size_t      global_ws = NR_ROWS / ROWS_PER_UINT;
    size_t      local_ws = 256;

    cl_int status = rclSetKernelArg(k_init_ht, 0, sizeof (buf_ht), &buf_ht);
    rclSetKernelArg(k_init_ht, 1, sizeof (rowCounters), &rowCounters);

    if (status != CL_SUCCESS) {
	    fatal("clSetKernelArg (%d)\n", status);
	}

    check_clEnqueueNDRangeKernel(queue, k_init_ht,
	    1,		// cl_uint	work_dim
	    NULL,	// size_t	*global_work_offset
	    &global_ws,	// size_t	*global_work_size
	    &local_ws,	// size_t	*local_work_size
	    0,		// cl_uint	num_events_in_wait_list
	    NULL,	// cl_event	*event_wait_list
	    NULL);	// cl_event	*event
}

/*
** Write ZCASH_SOL_LEN bytes representing the encoded solution as per the
** Zcash protocol specs (512 x 21-bit inputs).
**
** out		ZCASH_SOL_LEN-byte buffer where the solution will be stored
** inputs	array of 32-bit inputs
** n		number of elements in array
*/
void store_encoded_sol(uint8_t *out, uint32_t *inputs, uint32_t n) {
    uint32_t byte_pos = 0;
    int32_t bits_left = PREFIX + 1;
    uint8_t x = 0;
    uint8_t x_bits_used = 0;

    while (byte_pos < n) {
        if (bits_left >= 8 - x_bits_used) {
            x |= inputs[byte_pos] >> (bits_left - 8 + x_bits_used);
            bits_left -= 8 - x_bits_used;
            x_bits_used = 8;
        } else if (bits_left > 0) {
            uint32_t mask = ~(-1 << (8 - x_bits_used));
            mask = ((~mask) >> bits_left) & mask;
            x |= (inputs[byte_pos] << (8 - x_bits_used - bits_left)) & mask;
            x_bits_used += bits_left;
            bits_left = 0;
        } else if (bits_left <= 0) {
            assert(!bits_left);
            byte_pos++;
            bits_left = PREFIX + 1;
        } if (x_bits_used == 8) {
	        *out++ = x;
            x = x_bits_used = 0;
        }
    }
}

/*
** Print on stdout a hex representation of the encoded solution as per the
** zcash protocol specs (512 x 21-bit inputs).
**
** inputs	array of 32-bit inputs
** n		number of elements in array
*/
void print_encoded_sol(uint32_t *inputs, uint32_t n) {
    uint8_t	sol[ZCASH_SOL_LEN];
    uint32_t	i;
    store_encoded_sol(sol, inputs, n);

    for (i = 0; i < sizeof (sol); i++) {
	    printf("%02x", sol[i]);
	}

    printf("\n");
    fflush(stdout);
}

void print_sol(uint32_t *values, uint64_t *nonce) {
    uint32_t	show_n_sols;
    show_n_sols = (1 << PARAM_K);

    if (verbose < 2) {
	    show_n_sols = MIN(10, show_n_sols);
	}
    fprintf(stderr, "Soln:");
    // for brievity, only print "small" nonces
    if (*nonce < (1ULL << 32)) {
	    fprintf(stderr, " 0x%" PRIx64 ":", *nonce);
	}
    for (unsigned i = 0; i < show_n_sols; i++) {
	    fprintf(stderr, " %x", values[i]);
	}
    fprintf(stderr, "%s\n", (show_n_sols != (1 << PARAM_K) ? "..." : ""));
}

/*
* Compare two 256-bit values interpreted as little-endian 256-bit integers.
*/
int32_t cmp_target_256(void *_a, void *_b) {
    uint8_t	*a = _a;
    uint8_t	*b = _b;
    int32_t	i;
    for (i = SHA256_TARGET_LEN - 1; i >= 0; i--) {
	    if (a[i] != b[i]) {
	        return (int32_t)a[i] - b[i];
	    }
	}
    return 0;
}

/*
** Verify if the solution's block hash is under the target, and if yes print
** it formatted as:
** "sol: <job_id> <ntime> <nonce_rightpart> <solSize+sol>"
**
** Return 1 iff the block hash is under the target.
*/
uint32_t print_solver_line(uint32_t *values, uint8_t *header, size_t fixed_nonce_bytes, uint8_t *target, char *job_id) {
    uint8_t	buffer[ZCASH_BLOCK_HEADER_LEN + ZCASH_SOLSIZE_LEN + ZCASH_SOL_LEN];
    uint8_t	hash0[SHA256_DIGEST_SIZE];
    uint8_t	hash1[SHA256_DIGEST_SIZE];
    uint8_t	*p;

    p = buffer;
    memcpy(p, header, ZCASH_BLOCK_HEADER_LEN);
    p += ZCASH_BLOCK_HEADER_LEN;
    memcpy(p, "\xfd\x40\x05", ZCASH_SOLSIZE_LEN);
    p += ZCASH_SOLSIZE_LEN;
    store_encoded_sol(p, values, 1 << PARAM_K);

    Sha256_Onestep(buffer, sizeof (buffer), hash0);
    Sha256_Onestep(hash0, sizeof (hash0), hash1);

    // compare the double SHA256 hash with the target
    if (cmp_target_256(target, hash1) < 0) {
	    LOGD("Hash is above target\n");
	    return 0;
    }

    LOGD("Hash is under target\n");
    printf("sol: %s ", job_id);
    p = header + ZCASH_BLOCK_OFFSET_NTIME;
    printf("%02x%02x%02x%02x ", p[0], p[1], p[2], p[3]);
    printf("%s ", s_hexdump(header + ZCASH_BLOCK_HEADER_LEN - ZCASH_NONCE_LEN + fixed_nonce_bytes, ZCASH_NONCE_LEN - fixed_nonce_bytes));
    printf("%s%s\n", ZCASH_SOLSIZE_HEX, s_hexdump(buffer + ZCASH_BLOCK_HEADER_LEN + ZCASH_SOLSIZE_LEN, ZCASH_SOL_LEN));

    fflush(stdout);
    return 1;
}

int sol_cmp(const void *_a, const void *_b) {
    const uint32_t	*a = _a;
    const uint32_t	*b = _b;

    for (uint32_t i = 0; i < (1 << PARAM_K); i++) {
	    if (*a != *b) {
	        return *a - *b;
	    }

	    a++;
	    b++;
    }

    return 0;
}

/*
** Print all solutions.
**
** In mining mode, return the number of shares, that is the number of solutions
** that were under the target.
*/
uint32_t print_sols(sols_t *all_sols, uint64_t *nonce, uint32_t nr_valid_sols,
	uint8_t *header, size_t fixed_nonce_bytes, uint8_t *target, char *job_id) {
    uint8_t		*valid_sols;
    uint32_t	counted;
    uint32_t	shares = 0;
    valid_sols = malloc(nr_valid_sols * SOL_SIZE);

    if (!valid_sols) {
	    fatal("malloc: %s\n", strerror(errno));
	}

    counted = 0;

    for (uint32_t i = 0; i < all_sols->nr; i++) {
	    if (all_sols->valid[i]) {
	        if (counted >= nr_valid_sols) {
		        fatal("Bug: more than %d solutions\n", nr_valid_sols);
		    }
	        memcpy(valid_sols + counted * SOL_SIZE, all_sols->values[i], SOL_SIZE);
	        counted++;
	    }
	}

    assert(counted == nr_valid_sols);
    // sort the solutions amongst each other, to make the solver's output
    // deterministic and testable
    qsort(valid_sols, nr_valid_sols, SOL_SIZE, sol_cmp);

    for (uint32_t i = 0; i < nr_valid_sols; i++) {
	    uint32_t *inputs = (uint32_t *)(valid_sols + i * SOL_SIZE);
	    if (!mining && show_encoded) {
	        print_encoded_sol(inputs, 1 << PARAM_K);
	    }
	    if (verbose) {
	        print_sol(inputs, nonce);
	    }
	    if (mining) {
	        shares += print_solver_line(inputs, header, fixed_nonce_bytes, target, job_id);
		}
    }

    free(valid_sols);

    return shares;
}

/*
** Sort a pair of binary blobs (a, b) which are consecutive in memory and
** occupy a total of 2*len 32-bit words.
**
** a            points to the pair
** len          number of 32-bit words in each pair
*/
void sort_pair(uint32_t *a, uint32_t len) {
    uint32_t    *b = a + len;
    uint32_t    tmp, need_sorting = 0;
    for (uint32_t i = 0; i < len; i++) {
	    if (need_sorting || a[i] > b[i]) {
	        need_sorting = 1;
	        tmp = a[i];
	        a[i] = b[i];
	        b[i] = tmp;
	    } else if (a[i] < b[i]) {
	        return;
	    }
	}
}

/*
** If solution is invalid return 0. If solution is valid, sort the inputs
** and return 1.
*/
uint32_t verify_sol(sols_t *sols, unsigned sol_i) {
    uint32_t	*inputs = sols->values[sol_i];
    uint32_t	seen_len = (1 << (PREFIX + 1)) / 8;
    uint8_t	    seen[seen_len];
    uint32_t	i;
    uint8_t	tmp;
    // look for duplicate inputs
    memset(seen, 0, seen_len);
    for (i = 0; i < (1 << PARAM_K); i++) {
	    if (inputs[i] / 8 >= seen_len) {
	        warn("Invalid input retrieved from device: %d\n", inputs[i]);
	        sols -> valid[sol_i] = 0;
	        return 0;
	    }

	    tmp = seen[inputs[i] / 8];
	    seen[inputs[i] / 8] |= 1 << (inputs[i] & 7);
	    if (tmp == seen[inputs[i] / 8]) {
	        // at least one input value is a duplicate
	        sols -> valid[sol_i] = 0;
	        return 0;
	    }
    }

    // the valid flag is already set by the GPU, but set it again because
    // I plan to change the GPU code to not set it
    sols -> valid[sol_i] = 1;
    // sort the pairs in place
    for (uint32_t level = 0; level < PARAM_K; level++) {
	    for (i = 0; i < (1 << PARAM_K); i += (2 << level)) {
	        sort_pair(&inputs[i], 1 << level);
	    }
	}

    return 1;
}

/*
** Return the number of valid solutions.
*/
uint32_t verify_sols(cl_command_queue queue, cl_mem buf_sols, uint64_t *nonce,
	uint8_t *header, size_t fixed_nonce_bytes, uint8_t *target,
	char *job_id, uint32_t *shares, struct timespec *target_time)
{
    sols_t	*sols;
    uint32_t	nr_valid_sols;
    sols = (sols_t *)malloc(sizeof (*sols));
    if (!sols)
	fatal("malloc: %s\n", strerror(errno));
    // Most OpenCL implementations of clEnqueueReadBuffer in blocking mode are
    // good, except Nvidia implementing it as a wasteful busywait, so let's
    // work around it by trying to sleep just a bit less than the expected
    // amount of time.
    cl_event readEvent;
    check_clEnqueueReadBuffer(queue, buf_sols,
	    CL_FALSE,	// cl_bool	blocking_read
	    0,		// size_t	offset
	    sizeof (*sols),	// size_t	size
	    sols,	// void		*ptr
	    0,		// cl_uint	num_events_in_wait_list
	    NULL,	// cl_event	*event_wait_list
	    &readEvent);	// cl_event	*event
    // flushing is crucial to initiate the read *now* before sleeping
    rclFlush(queue);
    struct timespec start_time;
    get_time(&start_time);
    double dtarget = timespec_to_double(target_time);
    cl_int readStatus;
    rclGetEventInfo(readEvent, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof (cl_int), &readStatus, NULL);

    while (readStatus != CL_COMPLETE && SLEEP_SKIP_RATIO != 1) {
	    struct timespec t;
	    get_time(&t);
	    double dt = timespec_to_double(&t);
	    double delta = dtarget - dt;
	    if (delta < 0) {
	        break;
	    }
	    double_to_timespec(delta * SLEEP_RECHECK_RATIO, &t);
	    nanosleep(&t, NULL);
	    rclGetEventInfo(readEvent, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof (cl_int), &readStatus, NULL);
    }

    rclWaitForEvents(1, &readEvent);
    struct timespec end_time;
    get_time(&end_time);
    double dstart, dend, delta;
    dstart = timespec_to_double(&start_time);
    dend = timespec_to_double(&end_time);
    delta = dend - dstart;
    kern_avg_run_time = kern_avg_run_time * 6.0 / 10.0 + delta * (4.0 / 10.0);
    kern_avg_run_time *= (1 - (double)SLEEP_SKIP_RATIO);

    // let's check these solutions we just read...
    if (sols -> nr > MAX_SOLS) {
	    fprintf(stderr, "%d (probably invalid) solutions were dropped!\n",
		sols -> nr - MAX_SOLS);
	    sols -> nr = MAX_SOLS;
    }
    LOGD("Retrieved %d potential solutions\n", sols -> nr);

    nr_valid_sols = 0;
    for (unsigned sol_i = 0; sol_i < sols->nr; sol_i++) {
	    nr_valid_sols += verify_sol(sols, sol_i);
	}

    uint32_t sh = print_sols(sols, nonce, nr_valid_sols, header, fixed_nonce_bytes, target, job_id);

    if (shares) {
	    *shares = sh;
	}
    if (!mining || verbose) {
	    fprintf(stderr, "Nonce %s: %d sol%s\n", s_hexdump(nonce, ZCASH_NONCE_LEN), nr_valid_sols,
		nr_valid_sols == 1 ? "" : "s");
	}

    LOGD("Stats: %d likely invalids\n", sols->likely_invalids);
    free(sols);
    return nr_valid_sols;
}

unsigned get_value(unsigned *data, unsigned row) {
    return data[row];
}

/*
** Attempt to find Equihash solutions for the given Zcash block header and
** nonce. The 'header' passed in argument is a 140-byte header specifying
** the nonce, which this function may auto-increment if 'do_increment'. This
** allows repeatedly calling this fuction to solve different Equihash problems.
**
** header	must be a buffer allocated with ZCASH_BLOCK_HEADER_LEN bytes
** header_len	number of bytes initialized in header (either 140 or 108)
** shares	if not NULL, in mining mode the number of shares (ie. number
**		of solutions that were under the target) are stored here
**
** Return the number of solutions found.
*/
uint32_t solve_equihash(cl_context ctx, cl_command_queue queue,
	cl_kernel k_init_ht, cl_kernel *k_rounds, cl_kernel k_sols,
	cl_mem *buf_ht, cl_mem buf_sols, cl_mem buf_dbg, size_t dbg_size,
	uint8_t *header, size_t header_len, char do_increment,
	size_t fixed_nonce_bytes, uint8_t *target, char *job_id,
	uint32_t *shares, cl_mem *rowCounters) {

    blake2b_state_t     blake;
    cl_mem              buf_blake_st;
    size_t		global_ws;
    size_t              local_work_size = 64;
    uint32_t		sol_found = 0;
    uint64_t		*nonce_ptr;
    assert(header_len == ZCASH_BLOCK_HEADER_LEN);
    if (mining) {
	    assert(target && job_id);
	}
    nonce_ptr = (uint64_t *)(header + ZCASH_BLOCK_HEADER_LEN - ZCASH_NONCE_LEN);
    if (do_increment) {
	    // Increment the nonce
	    if (mining) {
	        // increment bytes 17-19
	        (*(uint32_t *)((uint8_t *)nonce_ptr + 17))++;
	        // byte 20 and above must be zero
	        *(uint32_t *)((uint8_t *)nonce_ptr + 20) = 0;
	    } else {
	        // increment bytes 0-7
	        (*nonce_ptr)++;
	    }
    }

    LOGD("\nSolving nonce %s\n", s_hexdump(nonce_ptr, ZCASH_NONCE_LEN));
    // Process first BLAKE2b-400 block
    zcash_blake2b_init(&blake, ZCASH_HASH_LEN, PARAM_N, PARAM_K);
    zcash_blake2b_update(&blake, header, 128, 0);
    cl_int status;
    buf_blake_st = rclCreateBuffer(ctx, CL_MEM_READ_ONLY |
                                       CL_MEM_COPY_HOST_PTR, sizeof (blake.h), &blake.h, &status);
    if (status != CL_SUCCESS) {
        fatal("EOF on clEnqueueMapBuffer buf_blake_st \n");
    }

    for (unsigned round = 0; round < PARAM_K; round++) {
	    // Now on every round!!!!
	    init_ht(queue, k_init_ht, buf_ht[round % 2], rowCounters[round % 2]);
	    if (!round) {
	        check_clSetKernelArg(k_rounds[round], 0, &buf_blake_st);
	        check_clSetKernelArg(k_rounds[round], 1, &buf_ht[round % 2]);
	        check_clSetKernelArg(k_rounds[round], 2, &rowCounters[round % 2]);
	        global_ws = select_work_size_blake();
	    } else {
	        check_clSetKernelArg(k_rounds[round], 0, &buf_ht[(round - 1) % 2]);
	        check_clSetKernelArg(k_rounds[round], 1, &buf_ht[round % 2]);
	        check_clSetKernelArg(k_rounds[round], 2, &rowCounters[(round - 1) % 2]);
	        check_clSetKernelArg(k_rounds[round], 3, &rowCounters[round % 2]);
	        global_ws = NR_ROWS;
	    }

	    check_clSetKernelArg(k_rounds[round], round == 0 ? 3 : 4, &buf_dbg);
	    if (round == PARAM_K - 1) {
	        check_clSetKernelArg(k_rounds[round], 5, &buf_sols);
	    }

	    check_clEnqueueNDRangeKernel(queue, k_rounds[round], 1, NULL, &global_ws, &local_work_size, 0, NULL, NULL);

	    examine_ht(round, queue, buf_ht[round % 2]);
	    examine_dbg(queue, buf_dbg, dbg_size);
    }

    check_clSetKernelArg(k_sols, 0, &buf_ht[0]);
    check_clSetKernelArg(k_sols, 1, &buf_ht[1]);
    check_clSetKernelArg(k_sols, 2, &buf_sols);
    check_clSetKernelArg(k_sols, 3, &rowCounters[0]);
    check_clSetKernelArg(k_sols, 4, &rowCounters[1]);

    global_ws = NR_ROWS;
    check_clEnqueueNDRangeKernel(queue, k_sols, 1, NULL, &global_ws, &local_work_size, 0, NULL, NULL);

    // compute the expected run time of the kernels that have been queued
    struct timespec start_time, target_time;
    get_time(&start_time);
    double dstart, dtarget = 0;
    dstart = timespec_to_double(&start_time);
    dtarget = dstart + kern_avg_run_time;
    double_to_timespec(dtarget, &target_time);
    // read solutions
    sol_found = verify_sols(queue, buf_sols, nonce_ptr, header, fixed_nonce_bytes, target, job_id, shares, &target_time);
    rclReleaseMemObject(buf_blake_st);

    return sol_found;
}

/*
** Read a complete line from stdin. If 2 or more lines are available, store
** only the last one in the buffer.
**
** buf		buffer to store the line
** len		length of the buffer
** block	blocking mode: do not return until a line was read
**
** Return 1 iff a line was read.
*/
int read_last_line(char *buf, size_t len, int block) {
    char	*start;
    size_t	pos = 0;
    ssize_t	n;
    set_blocking_mode(0, block);

    while (42) {
	    n = read(0, buf + pos, len - pos);
	    if (n == -1 && errno == EINTR) {
	       continue ;
	    } else if (n == -1 && (errno == EAGAIN || errno == EWOULDBLOCK)) {
	        if (!pos) {
		       return 0;
		    }

	        warn("strange: a partial line was read\n");
	        // a partial line was read, continue reading it in blocking mode
	        // to be sure to read it completely
	        set_blocking_mode(0, 1);
	        continue ;
	    } else if (n == -1) {
	        fatal("read stdin: %s\n", strerror(errno));
	    } else if (!n) {
	        fatal("EOF on stdin\n");
	    }

	    pos += n;
	    if (buf[pos - 1] == '\n') {
	        // 1 (or more) complete lines were read
	        break ;
	    }
    }

    start = memrchr(buf, '\n', pos - 1);
    if (start) {
	    warn("strange: more than 1 line was read\n");
	    // more than 1 line; copy the last line to the beginning of the buffer
	    pos -= (start + 1 - buf);
	    memmove(buf, start + 1, pos);
    }
    // overwrite '\n' with NUL
    buf[pos - 1] = 0;

    return 1;
}

/*
** Parse a string:
**   "<target> <job_id> <header> <nonce_leftpart>"
** (all the parts are in hex, except job_id which is a non-whitespace string),
** decode the hex values and store them in the relevant buffers.
**
** The remaining part of <header> that is not set by
** <header><nonce_leftpart> will be randomized so that the miner
** solves a unique Equihash PoW.
**
** str		string to parse
** target	buffer where the <target> will be stored
** target_len	size of target buffer
** job_id	buffer where the <job_id> will be stored
** job_id_len	size of job_id buffer
** header	buffer where the <header><nonce_leftpart> will be
** 		concatenated and stored
** header_len	size of the header_buffer
** fixed_nonce_bytes
** 		nr of bytes represented by <nonce_leftpart> will be stored here;
** 		this is the number of nonce bytes fixed by the stratum server
*/
void mining_parse_job(char *str, uint8_t *target, size_t target_len, char *job_id, size_t job_id_len,
    uint8_t *header, size_t header_len, size_t *fixed_nonce_bytes) {
    uint32_t  str_i, i;

    // parse target
    str_i = 0;
    for (i = 0; i < target_len; i++, str_i += 2) {
	    target[i] = hex2val(str, str_i) * 16 + hex2val(str, str_i + 1);
	}
    assert(str[str_i] == ' ');
    str_i++;

    // parse job_id
    for (i = 0; i < job_id_len && str[str_i] != ' '; i++, str_i++) {
	    job_id[i] = str[str_i];
	}

    assert(str[str_i] == ' ');
    assert(i < job_id_len);

    job_id[i] = 0;
    str_i++;

    // parse header and nonce_leftpart
    for (i = 0; i < header_len && str[str_i] != ' '; i++, str_i += 2) {
	    header[i] = hex2val(str, str_i) * 16 + hex2val(str, str_i + 1);
	}
    assert(str[str_i] == ' ');

    str_i++;
    *fixed_nonce_bytes = 0;
    while (i < header_len && str[str_i]) {
	    header[i] = hex2val(str, str_i) * 16 + hex2val(str, str_i + 1);
	    i++;
	    str_i += 2;
       	(*fixed_nonce_bytes)++;
    }
    assert(!str[str_i]);

    // Randomize rest of the bytes except N_ZERO_BYTES bytes which must be zero
    LOGD("Randomizing %d bytes in nonce\n", header_len - N_ZERO_BYTES - i);
    randomize(header + i, header_len - N_ZERO_BYTES - i);
    memset(header + header_len - N_ZERO_BYTES, 0, N_ZERO_BYTES);
}

/*
** Run in mining mode.
*/
void mining_mode(cl_context ctx, cl_command_queue queue, cl_kernel k_init_ht, cl_kernel *k_rounds, cl_kernel k_sols,
	cl_mem *buf_ht, cl_mem buf_sols, cl_mem buf_dbg, size_t dbg_size, uint8_t *header, cl_mem *rowCounters) {

    char		line[4096];
    uint8_t		target[SHA256_DIGEST_SIZE];
    char		job_id[256];
    size_t		fixed_nonce_bytes = 0;
    uint64_t	i;
    uint64_t	total = 0;
    uint32_t	shares;
    uint64_t	total_shares = 0;
    uint64_t	t0 = 0, t1;
    uint64_t	status_period = 500e3; // time (usec) between statuses

    for (i = 0; ; i++) {
        // iteration #0 always reads a job or else there is nothing to do
//        if (read_last_line(line, sizeof (line), !i)) {
//            mining_parse_job(line, target, sizeof (target), job_id, sizeof (job_id),
//               header, ZCASH_BLOCK_HEADER_LEN, &fixed_nonce_bytes);
//        }
        sleep(mining_sleep_seconds);
        total += solve_equihash(ctx, queue, k_init_ht, k_rounds, k_sols, buf_ht,
                buf_sols, buf_dbg, dbg_size, header, ZCASH_BLOCK_HEADER_LEN, 1,
                fixed_nonce_bytes, target, job_id, &shares, rowCounters);

        total_shares += shares;
        printf("total_shares %" PRId64, total_shares);

        if ((t1 = now()) > t0 + status_period) {
            t0 = t1;
            printf("status: %" PRId64 " %" PRId64 "\n", total, total_shares);
            fflush(stdout);
        }
    }
}

char* itostr(char *str, uint64_t i) {
    sprintf(str, "%"PRId64, i);
    return str;
}

void run_opencl(uint8_t *header, size_t header_len, cl_context ctx, cl_command_queue queue, cl_kernel k_init_ht, cl_kernel *k_rounds, cl_kernel k_sols) {
    cl_mem              buf_ht[2], buf_sols, buf_dbg, rowCounters[2];
#ifdef ENABLE_DEBUG
    size_t              dbg_size = NR_ROWS * sizeof (debug_t);
#else
    size_t              dbg_size = 1 * sizeof (debug_t);
#endif
    uint64_t		nonce;
    uint64_t		total;
    if (!mining || verbose) {
	    fprintf(stderr, "Hash tables will use %.1f MB\n", 2.0 * HT_SIZE / 1e6);
	}

    cl_int status;
    buf_dbg = rclCreateBuffer(ctx, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR, dbg_size, NULL, &status);
    buf_ht[0] = rclCreateBuffer(ctx, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR, HT_SIZE, NULL, &status);
    buf_ht[1] = rclCreateBuffer(ctx, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR, HT_SIZE, NULL, &status);
    buf_sols = rclCreateBuffer(ctx, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR, sizeof (sols_t), NULL, &status);
    rowCounters[0] = rclCreateBuffer(ctx, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR, NR_ROWS, NULL, &status);
    rowCounters[1] = rclCreateBuffer(ctx, CL_MEM_READ_WRITE | CL_MEM_ALLOC_HOST_PTR, NR_ROWS, NULL, &status);

    if (mining) {
        fprintf(stderr, "mining_mode...\n");
        mining_mode(ctx, queue, k_init_ht, k_rounds, k_sols, buf_ht, buf_sols, buf_dbg, dbg_size, header, rowCounters);
    }

    fprintf(stderr, "Running...\n");
    total = 0;
    uint64_t t0 = now();
    // Solve Equihash for a few nonces
    for (nonce = 0; nonce < nr_nonces; nonce++) {
	    total += solve_equihash(ctx, queue, k_init_ht, k_rounds, k_sols, buf_ht, buf_sols, buf_dbg,
	        dbg_size, header, header_len, !!nonce, 0, NULL, NULL, NULL, rowCounters);
	}
    uint64_t t1 = now();
    fprintf(stderr, "Total %" PRId64 " solutions in %.1f ms (%.1f Sol/s)\n", total, (t1 - t0) / 1e3, total / ((t1 - t0) / 1e6));

    // Clean up
    rclReleaseMemObject(buf_dbg);
    rclReleaseMemObject(buf_sols);
    rclReleaseMemObject(buf_ht[0]);
    rclReleaseMemObject(buf_ht[1]);
}

/*
** Scan the devices available on this platform. Try to find the device
** selected by the "--use <id>" option and, if found, store the platform and
** device in plat_id and dev_id.
**
** plat			platform being scanned
** nr_devs_total	total number of devices detected so far, will be
** 			incremented by the number of devices available on this
** 			platform
** plat_id		where to store the platform id
** dev_id		where to store the device id
**
** Return 1 iff the selected device was found.
*/
unsigned scan_platform(cl_platform_id plat, cl_uint *nr_devs_total, cl_platform_id *plat_id, cl_device_id *dev_id) {

    cl_device_type	typ = CL_DEVICE_TYPE_ALL;
    cl_uint		nr_devs = 0;
    cl_device_id	*devices;
    cl_int		status;

    unsigned		found = 0;
    unsigned		i;

    if (do_list_devices) {
	    print_platform_info(plat);
	}

    status = rclGetDeviceIDs(plat, typ, 0, NULL, &nr_devs);
    if (status != CL_SUCCESS) {
	    fatal("clGetDeviceIDs (%d)\n", status);
	}

    if (nr_devs == 0) {
	    return 0;
	}

    devices = (cl_device_id *)malloc(nr_devs * sizeof (*devices));
    status = rclGetDeviceIDs(plat, typ, nr_devs, devices, NULL);
    if (status != CL_SUCCESS) {
	    fatal("clGetDeviceIDs (%d)\n", status);
	}

    i = 0;
    while (i < nr_devs) {
	    if (do_list_devices) {
	        print_device_info(*nr_devs_total, devices[i]);
	    } else if (*nr_devs_total == gpu_to_use) {
	        found = 1;
	        *plat_id = plat;
	        *dev_id = devices[i];
	        break ;
	    }

	    (*nr_devs_total)++;
	    i++;
    }

    free(devices);

    return found;
}

/*
** Stores the platform id and device id that was selected by the "--use <id>"
** option.
**
** plat_id		where to store the platform id
** dev_id		where to store the device id
*/
void scan_platforms(cl_platform_id *plat_id, cl_device_id *dev_id) {
    cl_uint		nr_platforms;
    cl_platform_id	*platforms;
    cl_uint		i, nr_devs_total;
    cl_int		status;
    status = rclGetPlatformIDs(0, NULL, &nr_platforms);

    if (status != CL_SUCCESS) {
	    fatal("Cannot get OpenCL platforms (%d)\n", status);
	}

    if (!nr_platforms || verbose) {
	    fprintf(stderr, "Found %d OpenCL platform(s)\n", nr_platforms);
	}

    if (!nr_platforms) {
	    exit(1);
	}

    platforms = (cl_platform_id *)malloc(nr_platforms * sizeof (*platforms));
    if (!platforms) {
	    fatal("malloc: %s\n", strerror(errno));
	}

    status = rclGetPlatformIDs(nr_platforms, platforms, NULL);
    if (status != CL_SUCCESS) {
	    fatal("clGetPlatformIDs (%d)\n", status);
	}

    i = nr_devs_total = 0;
    while (i < nr_platforms) {
	    if (scan_platform(platforms[i], &nr_devs_total, plat_id, dev_id)) {
	        break;
	    }
	    i++;
    }

    if (do_list_devices) {
	    exit(0);
	}

    LOGD("Using GPU device ID %d\n", gpu_to_use);
    free(platforms);
}

void init_and_run_opencl(uint8_t *header, size_t header_len) {
    cl_platform_id	plat_id = 0;
    cl_device_id	dev_id = 0;
    cl_kernel		k_rounds[PARAM_K];
    cl_int		    status;

    int loadedCL=load_Func();

    scan_platforms(&plat_id, &dev_id);
    if (!plat_id || !dev_id) {
	    fatal("Selected device (ID %d) not found; see --list\n", gpu_to_use);
	}

    /* Create context.*/
    cl_context context = rclCreateContext(NULL, 1, &dev_id, NULL, NULL, &status);
    if (status != CL_SUCCESS || !context) {
	    fatal("clCreateContext (%d)\n", status);
	}

    /* Creating command queue associate with the context.*/
    cl_command_queue queue = rclCreateCommandQueue(context, dev_id, 0, &status);
    if (status != CL_SUCCESS || !queue) {
	    fatal("clCreateCommandQueue (%d)\n", status);
	}

    /* Create program object */
    cl_program program;

    size_t source_len;
    char* kernel = file_contents("/data/data/io.waterhole.miner/app_execdir/kernel.cl", &source_len);

    program = rclCreateProgramWithSource(context, 1, (const char **)&kernel, &source_len, &status);
    if (status != CL_SUCCESS || !program) {
	    fatal("clCreateProgramWithSource (%d)\n", status);
	}

    /* Build program. */
    if (!mining || verbose) {
	    fprintf(stderr, "Building program\n");
	}

    status = rclBuildProgram(program, 1, &dev_id, "", NULL, NULL);
    if (status != CL_SUCCESS) {
       warn("OpenCL build failed (%d). Build log follows:\n", status);
       get_program_build_log(program, dev_id);
	   exit(1);
    }


    cl_kernel k_self_test = rclCreateKernel(program, "selfTest", &status);
    if (status != CL_SUCCESS || !k_self_test) {
        fatal("clCreateKernel (%d)\n", status);
    }

    float *buf1 = 0;
    float *buf2 = 0;
    float *buf = 0;
    buf1 = (float *) malloc(6 * sizeof(float));
    buf2 = (float *) malloc(6 * sizeof(float));
    buf = (float *) malloc(6 * sizeof(float));
    // 初始化buf1, buf2
    int i;
    for (i = 0; i < 6; i++) {
        buf1[i] = i;
    }

    for (int i = 0; i < 6; i++) {
        printf ("buf1[%d] : %f", i, buf1[i]);
    }

    for (i = 0; i < 6; i++) {
        buf2[i] = i * 2;
    }

    for (int i = 0; i < 6; i++) {
        printf ("buf2[%d] : %f", i, buf2[i]);
    }

    for (i = 0; i < 6; i++) {
        buf[i] = buf1[i] + buf2[i];
    }

    for (int i = 0; i < 6; i++) {
        printf ("buf[%d] : %f", i, buf[i]);
    }

    cl_mem clbuf1 = rclCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, 6 * sizeof(cl_float), buf1, NULL);
    cl_mem clbuf2 = rclCreateBuffer(context, CL_MEM_READ_ONLY, 6 * sizeof(cl_float), NULL, NULL);
    status = rclEnqueueWriteBuffer(queue, clbuf2, 1, 0, 6 * sizeof(cl_float), buf2, 0, 0, 0);

    cl_mem buffer = rclCreateBuffer(context, CL_MEM_WRITE_ONLY, 6 * sizeof(cl_float), NULL, NULL);

    //设置 Kernel 参数
    status = rclSetKernelArg(k_self_test, 0, sizeof(cl_mem), (void*) &clbuf1);
    status = rclSetKernelArg(k_self_test, 1, sizeof(cl_mem), (void*) &clbuf2);
    rclSetKernelArg(k_self_test, 2, sizeof(cl_mem), (void*) &buffer);

    cl_event ev;
    size_t global_work_size = 6;

    for (int i = 0; i < 20; i++) {
        rclEnqueueNDRangeKernel(queue,
            k_self_test,
            1,
            NULL,
            &global_work_size,
            NULL, 0, NULL, &ev);
    }

    cl_float *ptr;
    ptr = (cl_float *)rclEnqueueMapBuffer(queue,
        buffer,
        CL_TRUE,
        CL_MAP_READ,
        0,
        6 * sizeof(cl_float),
        0, NULL, NULL, NULL);

    for (int i = 0; i < 6; i++) {
        printf ("alfter gpu buf[%d] : %f", i, ptr[i]);
    }

    // Create kernel objects
    cl_kernel k_init_ht = rclCreateKernel(program, "kernel_init_ht", &status);
    if (status != CL_SUCCESS || !k_init_ht) {
	    fatal("clCreateKernel (%d)\n", status);
	}

    for (unsigned round = 0; round < PARAM_K; round++) {
	    char	name[128];
	    snprintf(name, sizeof (name), "kernel_round%d", round);
	    k_rounds[round] = rclCreateKernel(program, name, &status);

	    if (status != CL_SUCCESS || !k_rounds[round]) {
	        fatal("clCreateKernel (%d)\n", status);
	    }
    }

    cl_kernel k_sols = rclCreateKernel(program, "kernel_sols", &status);

    if (status != CL_SUCCESS || !k_sols) {
	    fatal("clCreateKernel (%d)\n", status);
	}

    // Run
    run_opencl(header, header_len, context, queue, k_init_ht, k_rounds, k_sols);

    // Release resources
    assert(CL_SUCCESS == 0);
    status = CL_SUCCESS;
    status |= rclReleaseKernel(k_init_ht);

    for (unsigned round = 0; round < PARAM_K; round++) {
	    status |= rclReleaseKernel(k_rounds[round]);
	}

    status |= rclReleaseKernel(k_sols);
    status |= rclReleaseKernel(k_self_test);
    status |= rclReleaseProgram(program);
    status |= rclReleaseCommandQueue(queue);
    status |= rclReleaseContext(context);

    if (status) {
	    fprintf(stderr, "Cleaning resources failed\n");
	}
}

uint32_t parse_header(uint8_t *h, size_t h_len, const char *hex) {
    size_t      hex_len;
    size_t      bin_len;
    size_t	    opt0 = ZCASH_BLOCK_HEADER_LEN;
    size_t      i;

    if (!hex) {
	    if (!do_list_devices && !mining) {
	        fprintf(stderr, "Solving default all-zero %zd-byte header\n", opt0);
	    }

	    return opt0;
    }

    hex_len = strlen(hex);
    bin_len = hex_len / 2;

    if (hex_len % 2) {
	    fatal("Error: input header must be an even number of hex digits\n");
	}
    if (bin_len != opt0) {
	    fatal("Error: input header must be a %zd-byte full header\n", opt0);
	}
    assert(bin_len <= h_len);

    for (i = 0; i < bin_len; i ++) {
	    h[i] = hex2val(hex, i * 2) * 16 + hex2val(hex, i * 2 + 1);
	}

    while (--i >= bin_len - N_ZERO_BYTES) {
	    if (h[i]) {
	        fatal("Error: last %d bytes of full header (ie. last %d "
                "bytes of 32-byte nonce) must be zero due to an "
		        "optimization in my BLAKE2b im≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈≈plementation\n", N_ZERO_BYTES, N_ZERO_BYTES);
		}
    }

    return bin_len;
}

void tests(void) {
    // if NR_ROWS_LOG is smaller, there is not enough space to store all bits
    // of Xi in a 32-byte slot
    assert(NR_ROWS_LOG >= 12);
}

void Java_waterhole_miner_zcash_ZcashMiner_execSilentarmy(JNIEnv *env, jobject thiz) {
    tests();

    uint8_t header[ZCASH_BLOCK_HEADER_LEN] = {0, };
    uint32_t header_len;

    char *hex_header = NULL;

    header_len = parse_header(header, sizeof (header), hex_header);

    for (int i = 0; i < 10; i++) {
       header[i] = '1';
    }

    init_and_run_opencl(header, header_len);
}
