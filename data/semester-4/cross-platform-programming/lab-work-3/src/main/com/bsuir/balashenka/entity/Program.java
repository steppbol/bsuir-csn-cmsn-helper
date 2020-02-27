package main.com.bsuir.balashenka.interface;

/**
 * Abstract class of Program.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-03-12
 */
public abstract class Program extends File {
    /**
     * Name of program.
     */
    private String nameOfProgram;
    /**
     * Author of program.
     */
    private String authorOfProgram;
    /**
     * Status of program.
     */
    private boolean statusOfProgram;

    /**
     * @return name of program
     */
    public String getNameOfProgram() {
        return nameOfProgram;
    }

    /**
     * @param nameOfProgramTemp name of program
     */
    public void setNameOfProgram(final String nameOfProgramTemp) {
        this.nameOfProgram = nameOfProgramTemp;
    }

    /**
     * @return author of program
     */
    public String getAuthorOfProgram() {
        return authorOfProgram;
    }

    /**
     * @param authorOfProgramTemp author of program
     */
    public void setAuthorOfProgram(final String authorOfProgramTemp) {
        this.authorOfProgram = authorOfProgramTemp;
    }

    /**
     * @return status of program (install or not)
     */
    public boolean isStatusOfProgram() {
        return statusOfProgram;
    }

    /**
     * @param statusOfProgramTemp status of program ( install or not)
     */
    public void setStatusOfProgram(final boolean statusOfProgramTemp) {
        this.statusOfProgram = statusOfProgramTemp;
    }
}
