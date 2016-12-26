/*
 * This confidential and proprietary software may be used only as
 * authorised by a licensing agreement from ARM Limited
 *    (C) COPYRIGHT 2013 ARM Limited
 *        ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorised
 * copies and copies may only be made to the extent permitted
 * by a licensing agreement from ARM Limited.
 */

/**
 * \brief SGEMM kernel function.
 * \param[in] matrixA First input matrix.
 * \param[in] matrixB Second input matrix.
 * \param[in, out] matrixC Third input matrix. The output is stored in this matrix.
 * \param[in] matrixOrder Matrix order (number of rows and columns). Matrix has to be symmetric.
 * \param[in] alpha Scaling parameter.
 * \param[in] beta Scaling paratemer.
 */
__kernel void sgemm(__global float* restrict matrixA,
                    __global float* restrict const matrixB,
                    __global float* restrict matrixC,
                    const uint matrixOrder,
                    const float alpha,
                    const float beta)
{
    /* [Kernel size] */
    const int i = get_global_id(1);
    const int j = get_global_id(0);
    float4 sum = (float4)0.0f;
    float4 matrixBColumn;
    /* [Kernel size] */

    /* Move to a specific row in matrixA. */
    matrixA += i * matrixOrder;

    /* Move to a specific column in matrixB. */
    uint bOffset = j;

    /* [Load column] */
    /*
     * Load 4 values from a column of data from matrixB, and 4 values from a row in matrixA,
     * then multiply them together. Repeat until all values in the column/row have been multiplied.
     * We only want the sum of the calculation so we can add the result of each calculation to the last.
     */
    for (int k = 0; k < matrixOrder; k+=4)
    {
        matrixBColumn.x = matrixB[bOffset];
        bOffset += matrixOrder;

        matrixBColumn.y = matrixB[bOffset];
        bOffset += matrixOrder;

        matrixBColumn.z = matrixB[bOffset];
        bOffset += matrixOrder;

        matrixBColumn.w = matrixB[bOffset];
        bOffset += matrixOrder;
        /* [Load column] */

        /* [Calculation] */
        sum += vload4 (0, matrixA) * matrixBColumn;
        matrixA += 4;
    }
    /* [Calculation] */

    /* [Store] */
    /*
     * Sum the 4 results to get the single output of multiplying a row of matrixA by a column of matrixB.
     * Then carry out the final calculation.
     */
    matrixC[i * matrixOrder + j] = alpha * (sum.x + sum.y + sum.z + sum.w) + beta * matrixC[i * matrixOrder + j];
    /* [Store] */
}