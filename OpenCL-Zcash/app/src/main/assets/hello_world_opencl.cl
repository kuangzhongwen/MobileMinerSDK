__kernel
void do_opencl(__global const int* inputA, __global const int* inputB, __global int* output)
{
    int i = get_global_id(0);
    output[i] = inputA[i] + inputB[i];
}













