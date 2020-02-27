package main.com.bsuir.balashenka.interface;

import javax.swing.*;
import java.awt.*;

/**
 * Class of graphic interface. Main window.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-04-11
 */
public class GUI {

    /**
     * Property that only use in class GUI.
     */
    private int sizeRowsAndCols = 3;
    /**
     * @see GUI#sizeRowsAndCols
     */
    private JFrame mainFrame;
    /**
     * @see GUI#sizeRowsAndCols
     */
    private JComboBox sizeMatrixRowsA;
    /**
     * @see GUI#sizeRowsAndCols
     */
    private JComboBox sizeMatrixRowsB;
    /**
     * @see GUI#sizeRowsAndCols
     */
    private JComboBox sizeMatrixColsA;
    /**
     * @see GUI#sizeRowsAndCols
     */
    private JComboBox sizeMatrixColsB;
    /**
     * @see GUI#sizeRowsAndCols
     */
    private JButton getAnswerButton;
    /**
     * @see GUI#sizeRowsAndCols
     */
    private String sizeMatrixStr[] = {"2", "3"};

    /**
     * Property that also use in class Controller.
     */
    JTextField textFieldA[][];
    /**
     * @see GUI#textFieldA
     */
    JTextField textFieldB[][];
    /**
     * @see GUI#textFieldA
     */
    JTextField textFieldC[][];

    /**
     * Constructor of class GUI that create main window.
     */
    public GUI() {
        mainFrame = new JFrame("Matrix Calculator");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 370);
        mainFrame.setLocation(500, 300);
        mainFrame.setResizable(false);

        setPlaf(mainFrame, "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

        textFieldA = new JTextField[sizeRowsAndCols][sizeRowsAndCols];
        textFieldB = new JTextField[sizeRowsAndCols][sizeRowsAndCols];
        textFieldC = new JTextField[sizeRowsAndCols][sizeRowsAndCols];

        Container content = mainFrame.getContentPane();
        content.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        sizeMatrixRowsA = new JComboBox(sizeMatrixStr);
        sizeMatrixRowsA.setSelectedIndex(1);
        new Controller().comboBoxRowsEvent(sizeMatrixRowsA, textFieldA);
        setComboBoxToFrame(content, sizeMatrixRowsA, 0, 0);

        sizeMatrixColsA = new JComboBox(sizeMatrixStr);
        sizeMatrixColsA.setSelectedIndex(1);
        new Controller().comboBoxColsEvent(sizeMatrixColsA, textFieldA);
        setComboBoxToFrame(content, sizeMatrixColsA, 0, 1);

        sizeMatrixRowsB = new JComboBox(sizeMatrixStr);
        sizeMatrixRowsB.setSelectedIndex(1);
        new Controller().comboBoxRowsEvent(sizeMatrixRowsB, textFieldB);
        setComboBoxToFrame(content, sizeMatrixRowsB, 3, 0);

        sizeMatrixColsB = new JComboBox(sizeMatrixStr);
        sizeMatrixColsB.setSelectedIndex(1);
        new Controller().comboBoxColsEvent(sizeMatrixColsB, textFieldB);
        setComboBoxToFrame(content, sizeMatrixColsB, 3, 1);

        getAnswerButton = new JButton("GET ANSWER!");
        new Controller().buttonEvent(mainFrame, getAnswerButton, textFieldA, textFieldB, textFieldC, sizeMatrixRowsA,
                sizeMatrixColsA, sizeMatrixRowsB, sizeMatrixColsB);
        setButtonToFrame(content, getAnswerButton, 0, 5);

        setMatrixToFrame(content, textFieldA, 0, 0);
        setMatrixToFrame(content, textFieldB, 3, 0);
        setAnswerMatrixToFrame(content, textFieldC, 0, 6);

        mainFrame.setVisible(true);
    }

    public void setPlaf(JFrame frame, String plaf){
        try {
            UIManager.setLookAndFeel(plaf);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method that adds comboBox to container.
     * @param content class Container property
     * @param comboBox comboBox, that contain size of matrix
     * @param x cell address
     * @param y cell address
     */
    private void setComboBoxToFrame(Container content, JComboBox comboBox, int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.ipady = 10;
        c.weightx = 0.5;
        c.gridwidth = 3;
        c.gridx = x;
        c.gridy = y;
        content.add(comboBox, c);
    }

    /**
     * Method that adds matrix to container.
     * @param content class Container property
     * @param textField textField, that contain param of matrix
     * @param x cell address
     * @param y cell address
     */
    private void setMatrixToFrame(Container content, JTextField[][] textField, int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < sizeRowsAndCols; i++) {
            for (int j = 0; j < sizeRowsAndCols; j++) {
                textField[i][j] = new JTextField();
                textField[i][j].setText("0");
                c.ipady = 10;
                c.weightx = 0.5;
                c.gridx = x + j;
                c.gridy = y + 2 + i;
                content.add(textField[i][j], c);
            }
        }
    }

    /**
     * Method that adds button to container.
     * @param content class Container property
     * @param button button, that need to add
     * @param x cell address
     * @param y cell address
     */
    private void setButtonToFrame(Container content, JButton button, int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.ipady = 10;
        c.weightx = 0.0;
        c.gridwidth = 6;
        c.gridx = x;
        c.gridy = y;
        content.add(button, c);
    }

    /**
     * Method that adds answer matrix to container.
     * @param content class Container property
     * @param textField textField, that contain param of matrix
     * @param x cell address
     * @param y cell address
     */
    private void setAnswerMatrixToFrame(Container content, JTextField[][] textField, int x, int y) {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < sizeRowsAndCols; i++) {
            for (int j = 0; j < sizeRowsAndCols; j++) {
                textField[i][j] = new JTextField();
                c.ipady = 10;
                c.weightx = 0.5;
                c.gridwidth = 2;
                c.gridx = x + 2 * i;
                c.gridy = y + j;
                content.add(textField[i][j], c);

                textField[i][j].setEnabled(false);
            }
        }
    }
}