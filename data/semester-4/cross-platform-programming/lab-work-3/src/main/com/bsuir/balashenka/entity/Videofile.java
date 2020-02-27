package main.com.bsuir.balashenka.interface;

import java.util.Date;

/**
 * Class that contain videofile.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public class Videofile extends File {
    /**
     * Property that contain length of videofile
     */
    private String lengthOfVideofile;
    /**
     * Property that contain format of videofile
     */
    private String formatOfVideofile;
    /**
     * Property that contain name of videofile
     */
    private String nameOfVideofile;

    /**
     * @param formatOfVideofileTemp format of videofile
     * @param lengthOfVideofileTemp length of videofile
     * @param nameOfVideofileTemp   name of videofile
     * @param pathOfVideofileTemp   path to videofile
     */
    public Videofile(final String formatOfVideofileTemp, final String lengthOfVideofileTemp,
                     final String nameOfVideofileTemp, final String pathOfVideofileTemp) {
        this.formatOfVideofile = formatOfVideofileTemp;
        this.lengthOfVideofile = lengthOfVideofileTemp;
        this.nameOfVideofile = nameOfVideofileTemp;
        this.setDateOfCreating(new Date().toString());
        this.setPathToFile(pathOfVideofileTemp);
    }

    /**
     * @return name of videofile.
     */
    public String getNameOfVideofile() {
        return nameOfVideofile;
    }

    /**
     * @param nameOfVideofileTemp name of videofile
     */
    public void setNameOfVideofile(final String nameOfVideofileTemp) {
        this.nameOfVideofile = nameOfVideofileTemp;
    }

    /**
     * @return length of videofile
     */
    public String getLengthOfVideofile() {
        return lengthOfVideofile;
    }

    /**
     * @param lengthOfVideofileTemp length of videofile
     */
    public void setLengthOfVideofile(final String lengthOfVideofileTemp) {
        this.lengthOfVideofile = lengthOfVideofileTemp;
    }

    /**
     * @return format of videofile
     */
    public String getFormatOfVideofile() {
        return formatOfVideofile;
    }

    /**
     * @param formatOfVideofileTemp name of videofile
     */
    public void setFormatOfVideofile(final String formatOfVideofileTemp) {
        this.formatOfVideofile = formatOfVideofile;
    }
}
