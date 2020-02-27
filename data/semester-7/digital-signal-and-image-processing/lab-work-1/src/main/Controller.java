package main;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.filter.FilterEngine;
import main.filter.impl.ContrastFilter;
import main.filter.impl.FilterEngineImpl;
import main.filter.impl.HighPassFilter;
import main.filter.impl.LowPassFilter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    public BarChart<String, Number> originalBarChart;
    @FXML
    public BarChart<String, Number> filteredBarChart;
    @FXML
    public BarChart<String, Number> contrastedBarChart;
    @FXML
    public ImageView returnFilteredImageView;
    @FXML
    public BarChart<String, Number> returnFilteredBarChart;
    @FXML
    private ImageView sourceImageView;
    @FXML
    private ImageView filteredImageView;
    @FXML
    private ImageView contrastedImageView;
    @FXML
    private TextField gMinTextField;
    @FXML
    private TextField gMaxTextField;

    private static final String[] IMAGES_EXTENSIONS = {"*.png", "*.jpg", "*.jpeg", "*.bmp"};

    private BufferedImage originalBufferedImage;
    private BufferedImage filteredBufferedImage;
    private BufferedImage contrastedBufferedImage;
    private BufferedImage returnFilteredBufferedImage;
    private FilterEngine filterEngine = new FilterEngineImpl();

    public void openImageAction(ActionEvent actionEvent) {
        try {
            File imageFile = getFileByMask("Image", IMAGES_EXTENSIONS);

            if (imageFile == null) {
                createAlertError("Cannot open file");
                return;
            }

            originalBufferedImage = ImageIO.read(imageFile);
            if (originalBufferedImage.getType() != BufferedImage.TYPE_BYTE_GRAY && originalBufferedImage.getType() != BufferedImage.TYPE_3BYTE_BGR) {
                originalBufferedImage = null;
                createAlertError("Image must be grayscale");
                return;
            }
            fillBarChart(originalBarChart, originalBufferedImage);
            sourceImageView.setImage(SwingFXUtils.toFXImage(originalBufferedImage, null));
        } catch (IOException e) {
            createAlertError("Cannot open image");
        }
    }

    public void filterImageAction(ActionEvent actionEvent) {
        if (originalBufferedImage != null) {
            filteredBufferedImage = filterEngine.doFilter(
                    filteredBufferedImage != null ? filteredBufferedImage : originalBufferedImage,
                    LowPassFilter.FILTER_A);
            fillBarChart(filteredBarChart, filteredBufferedImage);
            filteredImageView.setImage(SwingFXUtils.toFXImage(filteredBufferedImage, null));
        }
    }

    public void saveImagesAction(ActionEvent actionEvent) {
        try {
            if (filteredBufferedImage != null) {
                ImageIO.write(filteredBufferedImage, "jpg", new File("filtered.jpg"));
            }
            if (contrastedBufferedImage != null) {
                ImageIO.write(contrastedBufferedImage, "jpg", new File("contrasted.jpg"));
            }
            if (returnFilteredBufferedImage != null) {
                ImageIO.write(returnFilteredBufferedImage, "jpg", new File("returnFilteredImage.jpg"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeContrastAction(ActionEvent actionEvent) {
        int gMin;
        int gMax;
        try {
            gMin = Integer.parseInt(gMinTextField.getText());
            gMax = Integer.parseInt(gMaxTextField.getText());
        } catch (NumberFormatException ex) {
            createAlertError("Cannot parse gMin or gMax " + ex.getMessage());
            return;
        }

        contrastedBufferedImage = filterEngine.doFilter(
                originalBufferedImage,
                new ContrastFilter(gMin, gMax));

        fillBarChart(contrastedBarChart, contrastedBufferedImage);
        contrastedImageView.setImage(SwingFXUtils.toFXImage(contrastedBufferedImage, null));
    }

    public void returnFilteredImageAction(ActionEvent actionEvent) {
        if (filteredBufferedImage != null) {
            returnFilteredBufferedImage = filterEngine.doFilter(
                    returnFilteredBufferedImage != null ? returnFilteredBufferedImage : filteredBufferedImage,
                    HighPassFilter.FILTER_B);
            fillBarChart(returnFilteredBarChart, returnFilteredBufferedImage);
            returnFilteredImageView.setImage(SwingFXUtils.toFXImage(returnFilteredBufferedImage, null));
        } else {
            createAlertError("Filtered image not created");
        }
    }

    public void resetImagesAction(ActionEvent actionEvent) {
        filteredBufferedImage = null;
        contrastedBufferedImage = null;
        returnFilteredBufferedImage = null;

        filteredImageView.setImage(null);
        contrastedImageView.setImage(null);
        returnFilteredImageView.setImage(null);

        filteredBarChart.getData().clear();
        contrastedBarChart.getData().clear();
        returnFilteredBarChart.getData().clear();
    }

    private void fillBarChart(BarChart<String, Number> barChart, BufferedImage bufferedImage) {
        switch (bufferedImage.getType()) {
            case BufferedImage.TYPE_BYTE_GRAY:
                fillGrayscaleHistogram(barChart,
                        grayHistogramValues(((DataBufferByte) bufferedImage.getData().getDataBuffer()).getData()));

                break;
            case BufferedImage.TYPE_3BYTE_BGR:
                fillRgbHistogram(barChart,
                        rgbHistogramValues(((DataBufferByte) bufferedImage.getData().getDataBuffer()).getData()));
                break;
        }
    }

    private void fillGrayscaleHistogram(BarChart<String, Number> barChart, int[] data) {
        barChart.getXAxis().setTickLabelsVisible(false);
        barChart.getXAxis().setTickMarkVisible(false);
        barChart.setBarGap(0);
        barChart.setCategoryGap(0);
        barChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.length; i++) {
            series.getData().add(new XYChart.Data<>(Integer.toString(i), data[i]));
        }
        barChart.getData().addAll(series);
    }

    private void fillRgbHistogram(BarChart<String, Number> barChart, int[][] data) {
        barChart.getXAxis().setTickLabelsVisible(false);
        barChart.getXAxis().setTickMarkVisible(false);
        barChart.setBarGap(5);
        barChart.setCategoryGap(20);
        barChart.getData().clear();

        for (int i = 0; i < data.length; i++) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(String.valueOf(i));

            for (int j = 0; j < data[i].length; j++) {
                series.getData().add(new XYChart.Data<>(Integer.toString(j), data[i][j]));
            }
            barChart.getData().addAll(series);
        }
    }

    private int[] grayHistogramValues(byte[] imageData) {
        int[] colors = new int[256];

        for (int i = 0; i < imageData.length; i++) {
            int pixelValue = Byte.toUnsignedInt(imageData[i]);

            if (pixelValue > 255) {
                colors[255]++;
                continue;
            }
            colors[pixelValue]++;
        }
        return colors;
    }

    private int[][] rgbHistogramValues(byte[] imageData) {
        int[][] rgbColorsValues = new int[3][256];

        for (int i = 0; i < imageData.length; i++) {
            int pixelValue = Byte.toUnsignedInt(imageData[i]);
            if (i % 3 == 0) {
                rgbColorsValues[0][pixelValue]++;
                continue;
            }
            if (i % 2 == 0) {
                rgbColorsValues[1][pixelValue]++;
                continue;
            }
            rgbColorsValues[2][pixelValue]++;
        }
        return rgbColorsValues;
    }

    private File getFileByMask(String neededFile, String[] mask) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(neededFile, mask));
        return fileChooser.showOpenDialog(new Stage());
    }

    public static void createAlertError(String alertMessage) {
        Alert alertFileNotFound = new Alert(Alert.AlertType.ERROR);
        alertFileNotFound.setTitle("Hello amigo");
        alertFileNotFound.setContentText(alertMessage);
        alertFileNotFound.showAndWait();
    }
}

