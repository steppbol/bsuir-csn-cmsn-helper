package main.com.bsuir.balashenka.interface;

/**
 * Class of Computer.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-27
 */
public abstract class File {
    /**
     * Property that contain path to file.
     */
    private String pathToFile;
    /**
     * Property that contain date of create.
     */
    private String dateOfCreating;

    /**
     * @return path to file
     */
    public String getPathToFile() {
        return pathToFile;
    }

    /**
     * @param pathToFileTemp path to file
     */
    public void setPathToFile(final String pathToFileTemp) {
        this.pathToFile = pathToFileTemp;
    }

    /**
     * @return date of creating file
     */
    public String getDateOfCreating() {
        return dateOfCreating;
    }

    /**
     * @param dateOfCreatingTemp date of creating file
     */
    public void setDateOfCreating(final String dateOfCreatingTemp) {
        this.dateOfCreating = dateOfCreatingTemp;
    }
}
