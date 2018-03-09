# 1 "input.cl"
# 1 "<built-in>"
# 1 "<command-line>"
# 1 "/usr/include/stdc-predef.h" 1 3 4
# 1 "<command-line>" 2
# 1 "input.cl"
# 1 "param.h" 1
# 98 "param.h"

#pragma OPENCL EXTENSION cl_arm_printf : enable

typedef struct sols_s
{
    uint nr;
    uint likely_invalids;
    uchar valid[10];
    uint values[10][(1 << 9)];
} sols_t;
# 2 "input.cl" 2

#pragma OPENCL EXTENSION cl_khr_global_int32_base_atomics : enable
# 38 "input.cl"
__constant ulong blake_iv[] =
{
    0x6a09e667f3bcc908, 0xbb67ae8584caa73b,
    0x3c6ef372fe94f82b, 0xa54ff53a5f1d36f1,
    0x510e527fade682d1, 0x9b05688c2b3e6c1f,
    0x1f83d9abfb41bd6b, 0x5be0cd19137e2179,
};

__kernel
void selfTest(__global const float* A, __global const float* B, __global float* C)
{
    int id = get_global_id(0);
    C[id] = A[id] * B[id];
}

__kernel
void kernel_init_ht(__global char *ht, __global uint *rowCounters)
{
    printf ("kernel_init_ht");
    rowCounters[get_global_id(0)] = 0;
}

# 81 "input.cl"
uint ht_store(uint round, __global char *ht, uint i, ulong xi0, ulong xi1, ulong xi2, ulong xi3, __global uint *rowCounters)
{
    uint row;
    __global char *p;
    uint cnt;
# 112 "input.cl"
    if (!(round % 2))
 row = (xi0 & 0xffff) | ((xi0 & 0xf00000) >> 4);
    else
 row = ((xi0 & 0xf0000) >> 0) |
     ((xi0 & 0xf00) << 4) | ((xi0 & 0xf00000) >> 12) |
     ((xi0 & 0xf) << 4) | ((xi0 & 0xf000) >> 12);

    xi0 = (xi0 >> 16) | (xi1 << (64 - 16));
    xi1 = (xi1 >> 16) | (xi2 << (64 - 16));
    xi2 = (xi2 >> 16) | (xi3 << (64 - 16));
    p = ht + row * ((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 32;
    uint rowIdx = row/8;
    uint rowOffset = 4*(row%8);
    uint xcnt = atomic_add(rowCounters + rowIdx, 1 << rowOffset);
    xcnt = (xcnt >> rowOffset) & 0x0F;
    cnt = xcnt;
    if (cnt >= ((1 << (((200 / (9 + 1)) + 1) - 20)) * 6))
      {

 atomic_sub(rowCounters + rowIdx, 1 << rowOffset);
 return 1;
      }
    p += cnt * 32 + (8 + ((round) / 2) * 4);

    *(__global uint *)(p - 4) = i;
    if (round == 0 || round == 1)
      {

 *(__global ulong *)(p + 0) = xi0;
 *(__global ulong *)(p + 8) = xi1;
 *(__global ulong *)(p + 16) = xi2;
      }
    else if (round == 2)
      {

 *(__global uint *)(p + 0) = xi0;
 *(__global ulong *)(p + 4) = (xi0 >> 32) | (xi1 << 32);
 *(__global ulong *)(p + 12) = (xi1 >> 32) | (xi2 << 32);
      }
    else if (round == 3)
      {

 *(__global uint *)(p + 0) = xi0;
 *(__global ulong *)(p + 4) = (xi0 >> 32) | (xi1 << 32);
 *(__global uint *)(p + 12) = (xi1 >> 32);
      }
    else if (round == 4)
      {

 *(__global ulong *)(p + 0) = xi0;
 *(__global ulong *)(p + 8) = xi1;
      }
    else if (round == 5)
      {

 *(__global ulong *)(p + 0) = xi0;
 *(__global uint *)(p + 8) = xi1;
      }
    else if (round == 6 || round == 7)
      {

 *(__global uint *)(p + 0) = xi0;
 *(__global uint *)(p + 4) = (xi0 >> 32);
      }
    else if (round == 8)
      {

 *(__global uint *)(p + 0) = xi0;
      }
    return 0;
}

# 204 "input.cl"
__kernel __attribute__((reqd_work_group_size(64, 1, 1)))
void kernel_round0(__global ulong *blake_state, __global char *ht,
 __global uint *rowCounters, __global uint *debug)
{
    uint tid = get_global_id(0);
    ulong v[16];
    uint inputs_per_thread = (1 << (200 / (9 + 1))) / get_global_size(0);
    uint input = tid * inputs_per_thread;
    uint input_end = (tid + 1) * inputs_per_thread;
    uint dropped = 0;
    while (input < input_end)
      {


 ulong word1 = (ulong)input << 32;

 v[0] = blake_state[0];
 v[1] = blake_state[1];
 v[2] = blake_state[2];
 v[3] = blake_state[3];
 v[4] = blake_state[4];
 v[5] = blake_state[5];
 v[6] = blake_state[6];
 v[7] = blake_state[7];
 v[8] = blake_iv[0];
 v[9] = blake_iv[1];
 v[10] = blake_iv[2];
 v[11] = blake_iv[3];
 v[12] = blake_iv[4];
 v[13] = blake_iv[5];
 v[14] = blake_iv[6];
 v[15] = blake_iv[7];

 v[12] ^= 140 + 4 ;

 v[14] ^= (ulong)-1;


 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + word1); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + word1); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + word1); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + word1); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + word1); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + word1); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + word1); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + word1); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + word1); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + word1); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + word1); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;

 v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 32); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 24); v[0] = (v[0] + v[4] + 0); v[12] = rotate((v[12] ^ v[0]), (ulong)64 - 16); v[8] = (v[8] + v[12]); v[4] = rotate((v[4] ^ v[8]), (ulong)64 - 63);;
 v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 32); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 24); v[1] = (v[1] + v[5] + 0); v[13] = rotate((v[13] ^ v[1]), (ulong)64 - 16); v[9] = (v[9] + v[13]); v[5] = rotate((v[5] ^ v[9]), (ulong)64 - 63);;
 v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 32); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 24); v[2] = (v[2] + v[6] + 0); v[14] = rotate((v[14] ^ v[2]), (ulong)64 - 16); v[10] = (v[10] + v[14]); v[6] = rotate((v[6] ^ v[10]), (ulong)64 - 63);;
 v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 32); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 24); v[3] = (v[3] + v[7] + 0); v[15] = rotate((v[15] ^ v[3]), (ulong)64 - 16); v[11] = (v[11] + v[15]); v[7] = rotate((v[7] ^ v[11]), (ulong)64 - 63);;
 v[0] = (v[0] + v[5] + word1); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 32); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 24); v[0] = (v[0] + v[5] + 0); v[15] = rotate((v[15] ^ v[0]), (ulong)64 - 16); v[10] = (v[10] + v[15]); v[5] = rotate((v[5] ^ v[10]), (ulong)64 - 63);;
 v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 32); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 24); v[1] = (v[1] + v[6] + 0); v[12] = rotate((v[12] ^ v[1]), (ulong)64 - 16); v[11] = (v[11] + v[12]); v[6] = rotate((v[6] ^ v[11]), (ulong)64 - 63);;
 v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 32); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 24); v[2] = (v[2] + v[7] + 0); v[13] = rotate((v[13] ^ v[2]), (ulong)64 - 16); v[8] = (v[8] + v[13]); v[7] = rotate((v[7] ^ v[8]), (ulong)64 - 63);;
 v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 32); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 24); v[3] = (v[3] + v[4] + 0); v[14] = rotate((v[14] ^ v[3]), (ulong)64 - 16); v[9] = (v[9] + v[14]); v[4] = rotate((v[4] ^ v[9]), (ulong)64 - 63);;



 ulong h[7];
 h[0] = blake_state[0] ^ v[0] ^ v[8];
 h[1] = blake_state[1] ^ v[1] ^ v[9];
 h[2] = blake_state[2] ^ v[2] ^ v[10];
 h[3] = blake_state[3] ^ v[3] ^ v[11];
 h[4] = blake_state[4] ^ v[4] ^ v[12];
 h[5] = blake_state[5] ^ v[5] ^ v[13];
 h[6] = (blake_state[6] ^ v[6] ^ v[14]) & 0xffff;



 dropped += ht_store(0, ht, input * 2,
  h[0],
  h[1],
  h[2],
  h[3], rowCounters);
 dropped += ht_store(0, ht, input * 2 + 1,
  (h[3] >> 8) | (h[4] << (64 - 8)),
  (h[4] >> 8) | (h[5] << (64 - 8)),
  (h[5] >> 8) | (h[6] << (64 - 8)),
  (h[6] >> 8), rowCounters);
 input++;
      }
}

# 424 "input.cl"
ulong half_aligned_long(__global ulong *p, uint offset)
{
    return
 (((ulong)*(__global uint *)((__global char *)p + offset + 0)) << 0) |
 (((ulong)*(__global uint *)((__global char *)p + offset + 4)) << 32);
}

uint well_aligned_int(__global ulong *_p, uint offset)
{
    __global char *p = (__global char *)_p;
    return *(__global uint *)(p + offset);
}

# 450 "input.cl"
uint xor_and_store(uint round, __global char *ht_dst, uint row,
 uint slot_a, uint slot_b, __global ulong *a, __global ulong *b,
 __global uint *rowCounters)
{
    ulong xi0, xi1, xi2;
    if (round == 1 || round == 2)
      {

 xi0 = *(a++) ^ *(b++);
 xi1 = *(a++) ^ *(b++);
 xi2 = *a ^ *b;
 if (round == 2)
   {

     xi0 = (xi0 >> 8) | (xi1 << (64 - 8));
     xi1 = (xi1 >> 8) | (xi2 << (64 - 8));
     xi2 = (xi2 >> 8);
   }
      }
    else if (round == 3)
      {

 xi0 = half_aligned_long(a, 0) ^ half_aligned_long(b, 0);
 xi1 = half_aligned_long(a, 8) ^ half_aligned_long(b, 8);
 xi2 = well_aligned_int(a, 16) ^ well_aligned_int(b, 16);
      }
    else if (round == 4 || round == 5)
      {

 xi0 = half_aligned_long(a, 0) ^ half_aligned_long(b, 0);
 xi1 = half_aligned_long(a, 8) ^ half_aligned_long(b, 8);
 xi2 = 0;
 if (round == 4)
   {

     xi0 = (xi0 >> 8) | (xi1 << (64 - 8));
     xi1 = (xi1 >> 8);
   }
      }
    else if (round == 6)
      {

 xi0 = *a++ ^ *b++;
 xi1 = *(__global uint *)a ^ *(__global uint *)b;
 xi2 = 0;
 if (round == 6)
   {

     xi0 = (xi0 >> 8) | (xi1 << (64 - 8));
     xi1 = (xi1 >> 8);
   }
      }
    else if (round == 7 || round == 8)
      {

 xi0 = half_aligned_long(a, 0) ^ half_aligned_long(b, 0);
 xi1 = 0;
 xi2 = 0;
 if (round == 8)
   {

     xi0 = (xi0 >> 8);
   }
      }


    if (!xi0 && !xi1)
 return 0;
    return ht_store(round, ht_dst, ((row << 12) | ((slot_b & 0x3f) << 6) | (slot_a & 0x3f)),
     xi0, xi1, xi2, 0, rowCounters);
}


void equihash_round(uint round,
 __global char *ht_src,
 __global char *ht_dst,
 __global uint *debug,
 __local uchar *first_words_data,
 __local uint *collisionsData,
 __local uint *collisionsNum,
 __global uint *rowCountersSrc,
 __global uint *rowCountersDst)
{
    uint tid = get_global_id(0);
    uint tlid = get_local_id(0);
    __global char *p;
    uint cnt;
    __local uchar *first_words = &first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*tlid];
    uchar mask;
    uint i, j;


    uint n;
    uint dropped_coll = 0;
    uint dropped_stor = 0;
    __global ulong *a, *b;
    uint xi_offset;

    xi_offset = (8 + ((round - 1) / 2) * 4);
# 566 "input.cl"
    mask = 0;
    uint thCollNum = 0;
    *collisionsNum = 0;
    barrier(CLK_LOCAL_MEM_FENCE);
    p = (ht_src + tid * ((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 32);
    uint rowIdx = tid/8;
    uint rowOffset = 4*(tid%8);
    cnt = (rowCountersSrc[rowIdx] >> rowOffset) & 0x0F;
    cnt = min(cnt, (uint)((1 << (((200 / (9 + 1)) + 1) - 20)) * 6));
    if (!cnt)

 goto part2;
    p += xi_offset;
    for (i = 0; i < cnt; i++, p += 32)
 first_words[i] = (*(__global uchar *)p) & mask;

    for (i = 0; i < cnt-1 && thCollNum < (((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5); i++)
      {
 uchar data_i = first_words[i];
 uint collision = (tid << 10) | (i << 5) | (i + 1);
 for (j = i+1; (j+4) < cnt;)
   {
       {
  uint isColl = ((data_i == first_words[j]) ? 1 : 0);
  if (isColl)
    {
      thCollNum++;
      uint index = atomic_inc(collisionsNum);
      collisionsData[index] = collision;
    }
  collision++;
  j++;
       }
       {
  uint isColl = ((data_i == first_words[j]) ? 1 : 0);
  if (isColl)
    {
      thCollNum++;
      uint index = atomic_inc(collisionsNum);
      collisionsData[index] = collision;
    }
  collision++;
  j++;
       }
       {
  uint isColl = ((data_i == first_words[j]) ? 1 : 0);
  if (isColl)
    {
      thCollNum++;
      uint index = atomic_inc(collisionsNum);
      collisionsData[index] = collision;
    }
  collision++;
  j++;
       }
       {
  uint isColl = ((data_i == first_words[j]) ? 1 : 0);
  if (isColl)
    {
      thCollNum++;
      uint index = atomic_inc(collisionsNum);
      collisionsData[index] = collision;
    }
  collision++;
  j++;
       }
   }
 for (; j < cnt; j++)
   {
     uint isColl = ((data_i == first_words[j]) ? 1 : 0);
     if (isColl)
       {
  thCollNum++;
  uint index = atomic_inc(collisionsNum);
  collisionsData[index] = collision;
       }
     collision++;
   }
      }

part2:
    barrier(CLK_LOCAL_MEM_FENCE);
    uint totalCollisions = *collisionsNum;
    for (uint index = tlid; index < totalCollisions; index += get_local_size(0))
      {
 uint collision = collisionsData[index];
 uint collisionThreadId = collision >> 10;
 uint i = (collision >> 5) & 0x1F;
 uint j = collision & 0x1F;
 __global uchar *ptr = ht_src + collisionThreadId * ((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 32 +
     xi_offset;
 a = (__global ulong *)(ptr + i * 32);
 b = (__global ulong *)(ptr + j * 32);
 dropped_stor += xor_and_store(round, ht_dst, collisionThreadId, i, j,
  a, b, rowCountersDst);
      }
}

# 686 "input.cl"
__kernel __attribute__((reqd_work_group_size(64, 1, 1))) void kernel_round1(__global char *ht_src, __global char *ht_dst, __global uint *rowCountersSrc, __global uint *rowCountersDst, __global uint *debug) { __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64]; __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64]; __local uint collisionsNum; equihash_round(1, ht_src, ht_dst, debug, first_words_data, collisionsData, &collisionsNum, rowCountersSrc, rowCountersDst); }
__kernel __attribute__((reqd_work_group_size(64, 1, 1))) void kernel_round2(__global char *ht_src, __global char *ht_dst, __global uint *rowCountersSrc, __global uint *rowCountersDst, __global uint *debug) { __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64]; __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64]; __local uint collisionsNum; equihash_round(2, ht_src, ht_dst, debug, first_words_data, collisionsData, &collisionsNum, rowCountersSrc, rowCountersDst); }
__kernel __attribute__((reqd_work_group_size(64, 1, 1))) void kernel_round3(__global char *ht_src, __global char *ht_dst, __global uint *rowCountersSrc, __global uint *rowCountersDst, __global uint *debug) { __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64]; __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64]; __local uint collisionsNum; equihash_round(3, ht_src, ht_dst, debug, first_words_data, collisionsData, &collisionsNum, rowCountersSrc, rowCountersDst); }
__kernel __attribute__((reqd_work_group_size(64, 1, 1))) void kernel_round4(__global char *ht_src, __global char *ht_dst, __global uint *rowCountersSrc, __global uint *rowCountersDst, __global uint *debug) { __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64]; __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64]; __local uint collisionsNum; equihash_round(4, ht_src, ht_dst, debug, first_words_data, collisionsData, &collisionsNum, rowCountersSrc, rowCountersDst); }
__kernel __attribute__((reqd_work_group_size(64, 1, 1))) void kernel_round5(__global char *ht_src, __global char *ht_dst, __global uint *rowCountersSrc, __global uint *rowCountersDst, __global uint *debug) { __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64]; __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64]; __local uint collisionsNum; equihash_round(5, ht_src, ht_dst, debug, first_words_data, collisionsData, &collisionsNum, rowCountersSrc, rowCountersDst); }
__kernel __attribute__((reqd_work_group_size(64, 1, 1))) void kernel_round6(__global char *ht_src, __global char *ht_dst, __global uint *rowCountersSrc, __global uint *rowCountersDst, __global uint *debug) { __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64]; __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64]; __local uint collisionsNum; equihash_round(6, ht_src, ht_dst, debug, first_words_data, collisionsData, &collisionsNum, rowCountersSrc, rowCountersDst); }
__kernel __attribute__((reqd_work_group_size(64, 1, 1))) void kernel_round7(__global char *ht_src, __global char *ht_dst, __global uint *rowCountersSrc, __global uint *rowCountersDst, __global uint *debug) { __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64]; __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64]; __local uint collisionsNum; equihash_round(7, ht_src, ht_dst, debug, first_words_data, collisionsData, &collisionsNum, rowCountersSrc, rowCountersDst); }


__kernel __attribute__((reqd_work_group_size(64, 1, 1)))
void kernel_round8(__global char *ht_src, __global char *ht_dst,
 __global uint *rowCountersSrc, __global uint *rowCountersDst,
 __global uint *debug, __global sols_t *sols)
{
    uint tid = get_global_id(0);
    __local uchar first_words_data[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6)+2)*64];
    __local uint collisionsData[(((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 5) * 64];
    __local uint collisionsNum;
    equihash_round(8, ht_src, ht_dst, debug, first_words_data, collisionsData,
     &collisionsNum, rowCountersSrc, rowCountersDst);
    if (!tid)
    sols->nr = sols->likely_invalids = 0;
}

uint expand_ref(__global char *ht, uint xi_offset, uint row, uint slot)
{
    return *(__global uint *)(ht + row * ((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 32 +
     slot * 32 + xi_offset - 4);
}
uint expand_refs(uint *ins, uint nr_inputs, __global char **htabs,
 uint round)
{
    __global char *ht = htabs[round % 2];
    uint i = nr_inputs - 1;
    uint j = nr_inputs * 2 - 1;
    uint xi_offset = (8 + ((round) / 2) * 4);
    int dup_to_watch = -1;
    do
      {
 ins[j] = expand_ref(ht, xi_offset,
  (ins[i] >> 12), ((ins[i] >> 6) & 0x3f));
 ins[j - 1] = expand_ref(ht, xi_offset,
  (ins[i] >> 12), (ins[i] & 0x3f));
 if (!round)
   {
     if (dup_to_watch == -1)
  dup_to_watch = ins[j];
     else if (ins[j] == dup_to_watch || ins[j - 1] == dup_to_watch)
  return 0;
   }
 if (!i)
     break ;
 i--;
 j -= 2;
      }
    while (1);
    return 1;
}

void potential_sol(__global char **htabs, __global sols_t *sols,
 uint ref0, uint ref1)
{
    uint nr_values;
    uint values_tmp[(1 << 9)];
    uint sol_i;
    uint i;
    nr_values = 0;
    values_tmp[nr_values++] = ref0;
    values_tmp[nr_values++] = ref1;
    uint round = 9 - 1;
    do
      {
 round--;
 if (!expand_refs(values_tmp, nr_values, htabs, round))
     return ;
 nr_values *= 2;
      }
    while (round > 0);

    sol_i = atomic_inc(&sols->nr);
    if (sol_i >= 10)
 return ;
    for (i = 0; i < (1 << 9); i++)
 sols->values[sol_i][i] = values_tmp[i];
    sols->valid[sol_i] = 1;
}




__kernel __attribute__((reqd_work_group_size(64, 1, 1)))
void kernel_sols(__global char *ht0, __global char *ht1, __global sols_t *sols,
 __global uint *rowCountersSrc, __global uint *rowCountersDst) {
    uint tid = get_global_id(0);
    __global char *htabs[2] = { ht0, ht1 };
    uint ht_i = (9 - 1) % 2;
    uint cnt;
    uint xi_offset = (8 + ((9 - 1) / 2) * 4);
    uint i, j;
    __global char *a, *b;
    uint ref_i, ref_j;

    ulong collisions;
    uint coll;

    uint mask = 0xffffff;

    a = htabs[ht_i] + tid * ((1 << (((200 / (9 + 1)) + 1) - 20)) * 6) * 32;
    uint rowIdx = tid / 8;
    uint rowOffset = 4 * (tid % 8);
    cnt = (rowCountersSrc[rowIdx] >> rowOffset) & 0x0F;
    cnt = min(cnt, (uint)((1 << (((200 / (9 + 1)) + 1) - 20)) * 6));
    coll = 0;
    a += xi_offset;

    for (i = 0; i < cnt; i++, a += 32) {
        uint a_data = ((*(__global uint *)a) & mask);
        ref_i = *(__global uint *)(a - 4);

        for (j = i + 1, b = a + 32; j < cnt; j++, b += 32) {
            if (a_data == ((*(__global uint *)b) & mask)) {
                ref_j = *(__global uint *)(b - 4);
                collisions = ((ulong)ref_i << 32) | ref_j;
                goto exit1;
            }
        }
    }

  return;

exit1:
    potential_sol(htabs, sols, collisions >> 32, collisions & 0xffffffff);
}
