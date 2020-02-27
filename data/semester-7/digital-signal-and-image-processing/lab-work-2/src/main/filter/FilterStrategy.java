package main.filter;

public interface FilterStrategy {
    byte[] doFilter(byte[] imageData, int height, int width, ImageType imageType);

    boolean compatibleWith(ImageType imageType);
}
