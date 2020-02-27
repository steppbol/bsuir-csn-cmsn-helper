package main.filter.impl;

import main.filter.FilterEngine;
import main.filter.FilterStrategy;
import main.filter.ImageType;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

public class FilterEngineImpl implements FilterEngine {
    private static final int RGB_SIZE = 3;
    public static final double RED_COEFFICIENT = 0.2125;
    public static final double GREEN_COEFFICIENT = 0.7154;
    public static final double BLUE_COEFFICIENT = 0.0721;

    @Override
    public BufferedImage doFilter(BufferedImage sourceBufferedImage, FilterStrategy filterStrategy) {
        if (sourceBufferedImage == null || filterStrategy == null) {
            throw new NullPointerException("BufferedImage or FilterStrategy cannot be null");
        }
        ImageType currentImageType = null;

        switch (sourceBufferedImage.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                currentImageType = ImageType.GRAY;
                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                currentImageType = ImageType.RGB;
                break;
            default:
                throw new IllegalArgumentException("FilterEngine does not support images with type: " + sourceBufferedImage.getType());
        }

        if (!filterStrategy.compatibleWith(currentImageType)) {
            throw new IllegalArgumentException("Filter does not support images with type: " + sourceBufferedImage.getType());
        }

        Raster sourceImageRaster = sourceBufferedImage.getData();
        WritableRaster filteredImageRaster = null;
        byte[] sourceImageData = null;

        if (sourceImageRaster.getDataBuffer() instanceof DataBufferByte) {
            sourceImageData = ((DataBufferByte) sourceImageRaster.getDataBuffer()).getData();
        } else {
            throw new IllegalArgumentException("BufferedImage with type " + sourceBufferedImage.getType() + "doesn't support (DataBuffer)");
        }

        byte[] filteredImageData = filterStrategy
                .doFilter(sourceImageData, sourceImageRaster.getHeight(), sourceImageRaster.getWidth(), currentImageType);

        filteredImageRaster = Raster.createWritableRaster(sourceBufferedImage.getSampleModel(),
                new DataBufferByte(filteredImageData, filteredImageData.length),
                null);

        return new BufferedImage(sourceBufferedImage.getColorModel(),
                filteredImageRaster,
                sourceBufferedImage.getColorModel().isAlphaPremultiplied(),
                null);
    }

    @Override
    public BufferedImage rgbToGrayscale(BufferedImage sourceBufferedImage) {
        if (sourceBufferedImage == null) {
            throw new NullPointerException("BufferedImage or FilterStrategy cannot be null");
        }

        DataBuffer sourceImageDataBuffer = sourceBufferedImage.getData().getDataBuffer();
        byte[] sourceImageData = null;

        if (sourceImageDataBuffer instanceof DataBufferByte) {
            sourceImageData = ((DataBufferByte) sourceImageDataBuffer).getData();
        } else {
            throw new IllegalArgumentException("BufferedImage with type " + sourceBufferedImage.getType() + "doesn't support (DataBuffer)");
        }

        byte[] convertedImageData = rgbToGrayscale(sourceImageData);

        WritableRaster convertedImageRaster = Raster.createWritableRaster(new ComponentSampleModel(DataBuffer.TYPE_BYTE,
                        sourceBufferedImage.getWidth(), sourceBufferedImage.getHeight(), 1, sourceBufferedImage.getWidth(), new int[]{0}),
                new DataBufferByte(convertedImageData, convertedImageData.length),
                null);

        BufferedImage convertedBufferedImage = new BufferedImage(sourceBufferedImage.getWidth(), sourceBufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        convertedBufferedImage.setData(convertedImageRaster);
        /*return new BufferedImage(
                new ComponentColorModel(
                        ColorSpace.getInstance(ColorSpace.CS_GRAY), false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE),
                convertedImageRaster,
                false,
                null);*/
        return convertedBufferedImage;
    }

    private byte[] rgbToGrayscale(byte[] sourceImageData) {
        byte[] convertedImageData = new byte[sourceImageData.length / 3];
        for (int i = 0, j = 0; i < sourceImageData.length; i += RGB_SIZE, j++) {
            convertedImageData[j] = (byte) (RED_COEFFICIENT * Byte.toUnsignedInt(sourceImageData[i])
                    + GREEN_COEFFICIENT * Byte.toUnsignedInt(sourceImageData[i + 1])
                    + BLUE_COEFFICIENT * Byte.toUnsignedInt(sourceImageData[i + 2]));
        }

        return convertedImageData;
    }
}
