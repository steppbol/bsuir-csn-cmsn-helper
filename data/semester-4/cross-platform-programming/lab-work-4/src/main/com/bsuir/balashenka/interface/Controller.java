package main.com.bsuir.balashenka.interface;

import com.home.threads.MainThread;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that control gui.
 *
 * @author Stsiapan Balashenka
 * @version 1.1
 * @since 2018-04-11
 */
public class Controller {
    /**
     * ComboBox event. Choose count of rows of matrix.
     * @param comboBox comboBox, that contain size
     * @param textFields textFields, that will be displayed matrix
     */
    public void comboBoxRowsEvent(JComboBox comboBox, JTextField[][] textFields) {
        ActionListener comboBoxRowsListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String item = (String) box.getSelectedItem();
                if (item.equals("2")) {
                    for (int i = 0; i < 3; i++) {
                        textFields[2][i].setEnabled(false);
                    }
                } else if (item.equals("3")) {
                    for (int i = 0; i < 3; i++) {
                        textFields[2][i].setEnabled(true);
                    }
                }
            }
        };

        comboBox.addActionListener(comboBoxRowsListener);
    }

    /**
     * ComboBox event. Choose count of cols of matrix.
     * @param comboBox comboBox, that contain size
     * @param textFields textFields, that will be displayed matrix
     */
    public void comboBoxColsEvent(JComboBox comboBox, JTextField[][] textFields) {
        ActionListener comboBoxColsListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox box = (JComboBox) e.getSource();
                String item = (String) box.getSelectedItem();
                if (item.equals("2")) {
                    for (int i = 0; i < 3; i++) {
                        textFields[i][2].setEnabled(false);
                    }
                } else if (item.equals("3")) {
                    for (int i = 0; i < 3; i++) {
                        textFields[i][2].setEnabled(true);
                    }
                }
            }
        };

        comboBox.addActionListener(comboBoxColsListener);
    }

    /**
     * Event of button. Set answert to answer matrix.
     * @param frame main frame
     * @param button button that contain event
     * @param textFieldsA matrix #1
     * @param textFieldsB matrix #2
     * @param textFieldsC answer matrix
     * @param comboBoxRowsA count of rows matrix #1
     * @param comboBoxColsA count of cols matrix #1
     * @param comboBoxRowsB count of rows matrix #2
     * @param comboBoxColsB count of cols matrix #2
     */
    public void buttonEvent(JFrame frame, JButton button, JTextField[][] textFieldsA, JTextField[][] textFieldsB,
                            JTextField[][] textFieldsC, JComboBox comboBoxRowsA, JComboBox comboBoxColsA,
                            JComboBox comboBoxRowsB, JComboBox comboBoxColsB) {
        ActionListener buttonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String strRowA = (String) comboBoxRowsA.getSelectedItem();
                String strColA = (String) comboBoxColsA.getSelectedItem();
                String strRowB = (String) comboBoxRowsB.getSelectedItem();
                String strColB = (String) comboBoxColsB.getSelectedItem();

                int rowA = Integer.parseInt(strRowA);
                int colA = Integer.parseInt(strColA);
                int rowB = Integer.parseInt(strRowB);
                int colB = Integer.parseInt(strColB);

                float tempMatrixA[][] = new float[rowA][colA];
                float tempMatrixB[][] = new float[rowB][colB];
                float tempMatrixC[][];

                try {
                    for (int i = 0; i < rowA; i++) {
                        for (int j = 0; j < colA; j++) {
                            tempMatrixA[i][j] = Integer.parseInt(textFieldsA[i][j].getText());
                        }
                    }

                    for (int i = 0; i < rowB; i++) {
                        for (int j = 0; j < colB; j++) {
                            tempMatrixB[i][j] = Integer.parseInt(textFieldsB[i][j].getText());
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Wrong parameters!",
                            "ERROR!",
                            JOptionPane.ERROR_MESSAGE);

                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            textFieldsA[i][j].setText("0");
                            textFieldsB[i][j].setText("0");
                        }
                    }
                }

                if (strRowA.equals(strColB) || strColA.equals(strRowB)) {

                    MainThread mainThread = new MainThread();

                    if (strRowA.equals(strColB)) {
                        for (int i = 0; i < rowA; i++) {
                            for (int j = 0; j < 3; j++) {
                                textFieldsC[i][j].setEnabled(true);
                            }
                        }

                        if (rowA == 2) {
                            for (int i = 0; i < 3; i++) {
                                textFieldsC[2][i].setEnabled(false);
                                textFieldsC[i][2].setEnabled(false);
                            }
                        }

                        tempMatrixC = new float[rowA][rowA];
                        tempMatrixC = mainThread.getComposition(tempMatrixA, tempMatrixB, rowA, true);

                        for (int i = 0; i < tempMatrixC.length; i++) {
                            for (int j = 0; j < tempMatrixC.length; j++) {
                                String str = String.valueOf((int)tempMatrixC[i][j]);
                                textFieldsC[j][i].setText(str);
                            }
                        }

                    } else if (strColA.equals(strRowB)) {
                        for (int i = 0; i < colA; i++) {
                            for (int j = 0; j < 3; j++) {
                                textFieldsC[i][j].setEnabled(true);
                            }
                        }

                        if (colA == 2) {
                            for (int i = 0; i < 3; i++) {
                                textFieldsC[2][i].setEnabled(false);
                                textFieldsC[i][2].setEnabled(false);
                            }
                        }

                        tempMatrixC = new float[colA][colA];
                        tempMatrixC = mainThread.getComposition(tempMatrixA, tempMatrixB, colA, false);

                        for (int i = 0; i < tempMatrixC.length; i++) {
                            for (int j = 0; j < tempMatrixC.length; j++) {
                                String str = String.valueOf((int)tempMatrixC[i][j]);
                                textFieldsC[j][i].setText(str);
                            }
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Rows != Cols.",
                            "ERROR!",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        };

        button.addActionListener(buttonListener);
    }
}
