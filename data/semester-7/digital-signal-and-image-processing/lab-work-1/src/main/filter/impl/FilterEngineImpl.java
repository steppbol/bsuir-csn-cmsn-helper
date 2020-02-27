package main.filter.impl;

import main.filter.FilterEngine;
import main.filter.FilterStrategy;
import main.filter.ImageType;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class FilterEngineImpl implements FilterEngine {
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
}
