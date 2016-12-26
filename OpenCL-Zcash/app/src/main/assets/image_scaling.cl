/*
 * This confidential and proprietary software may be used only as
 * authorised by a licensing agreement from ARM Limited
 *    (C) COPYRIGHT 2013 ARM Limited
 *        ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorised
 * copies and copies may only be made to the extent permitted
 * by a licensing agreement from ARM Limited.
 */

/* [Define a sampler] */
const sampler_t sampler = CLK_NORMALIZED_COORDS_TRUE | CLK_ADDRESS_CLAMP | CLK_FILTER_LINEAR;
/* [Define a sampler] */

/**
 * \brief Image scaling kernel function.
 * \param[in] sourceImage Input image object.
 * \param[out] destinationImage Re-sized output image object.
 * \param[in] widthNormalizationFactor 1 / destinationImage width.
 * \param[in] heightNormalizationFactor 1 / destinationImage height.
 */
__kernel void image_scaling(__read_only image2d_t sourceImage,
                            __write_only image2d_t destinationImage,
                            const float widthNormalizationFactor,
                            const float heightNormalizationFactor)
{
    /*
     * It is possible to get the width and height of an image object (using get_image_width and get_image_height).
     * You could use this to calculate the normalization factors in the kernel.
     * In this case, because the width and height doesn't change for each kernel,
     * it is better to pass normalization factors to the kernel as parameters.
     * This way we do the calculations once on the host side instead of in every kernel.
     */

    /* [Calculate the coordinates] */
    /*
     * There is one kernel instance per pixel in the destination image.
     * The global id of this kernel instance is therefore a coordinate in the destination image.
     */
    int2 coordinate = (int2)(get_global_id(0), get_global_id(1));

    /*
     * That coordinate is only valid for the destination image.
     * If we normalize the coordinates to the range [0.0, 1.0] (using the height and width of the destination image),
     * we can use them as coordinates in the sourceImage.
     */
    float2 normalizedCoordinate = convert_float2(coordinate) * (float2)(widthNormalizationFactor, heightNormalizationFactor);
    /* [Calculate the coordinates] */

    /* [Read from the source image] */
    /*
     * Read colours from the source image.
     * The sampler determines how the coordinates are interpreted.
     * Because bilinear filtering is enabled, the value of colour will be the average of the 4 pixels closest to the coordinate.
     */
    float4 colour = read_imagef(sourceImage, sampler, normalizedCoordinate);
    /* [Read from the source image] */

    /* [Write to the destination image] */
    /*
     * Write the colour to the destination image.
     * No sampler is used here as all writes must specify an exact valid pixel coordinate.
     */
    write_imagef(destinationImage, coordinate, colour);
    /* [Write to the destination image] */
}
