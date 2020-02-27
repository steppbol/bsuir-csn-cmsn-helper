package main.com.bsuir.balashenka.interface;

import java.util.Date;

/**
 * Class of Player.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public class Player extends Program {

    /**
     * Property that contain format of file
     */
    private String wavFormat = ".wav";
    /**
     * @see Player#mp4Format
     */
    private String mp4Format = ".mp4";

    /**
     * Constructor of class Player.
     */
    public Player() {
    }

    /**
     * Constructor of class Player.
     *
     * @param name   - name of film
     * @param author - author of film
     * @param path   - path to file
     */
    public Player(final String name, final String author, final String path) {
        setNameOfProgram(name);
        setAuthorOfProgram(author);
        setPathToFile(path);
        setStatusOfProgram(false);
        setDateOfCreating(new Date().toString());
    }

    /**
     * Watch film.
     *
     * @param videofile object of class Videofile
     * @return name of film
     */
    public String watchFilm(final Videofile videofile) {
        if (videofile.getFormatOfVideofile().equals(wavFormat) || videofile.getFormatOfVideofile().equals(mp4Format)) {
            return videofile.getNameOfVideofile();
        } else return "Wrong format";
    }

    /**
     * @return get wav format of video
     */
    public String getWavFormat() {
        return wavFormat;
    }

    /**
     * @param wavFormatTemp set wav format of video
     */
    public void setWavFormat(final String wavFormatTemp) {
        this.wavFormat = wavFormat;
    }

    /**
     * @return get mp4 format of video
     */
    public String getMp4Format() {
        return mp4Format;
    }

    /**
     * @param mp4FormatTemp set mp4 format of video
     */
    public void setMp4Format(final String mp4FormatTemp) {
        this.mp4Format = mp4FormatTemp;
    }
}
