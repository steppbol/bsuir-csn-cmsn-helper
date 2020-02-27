package com.bsuir.balashenka;

/**
 * Include Java SWT libraries
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Class is used to create the application window that add and subtract numbers.
 *
 * @author Stepan
 * @version 1.0
 * @since 2018-02-24
 */
public class SimpleAdder {
    /** Property - result of calculation. */
    private int intResOfCalc;
    /** Property - for int to String. */
    private String strRes;
    /** Property to change the result of calculations. */
    private Button buttonTakeOne;
    /** @see SimpleAdder#buttonTakeOne. */
    private Button buttonTakeTen;
    /** @see SimpleAdder#buttonTakeOne. */
    private Button buttonAddOne;
    /** @see SimpleAdder#buttonTakeOne. */
    private Button buttonAddTen;
    /** Property - here the result of the calculation will be printed. */
    private Text textFieldResult;
    /** Property - add or minus numbers. */
    public static final int NUM_ONE = 1, NUM_TEN = 10,
            NUM_MINUS_ONE = -1, NUM_MINUS_TEN = -10;
    /** Property - margin bottom. */
    public static final int MARGIN_BOTTOM = 25;
    /** Property - space between elements. */
    public static final int SPACE_LAYOUT = -2;
    /** Property - size of textfield. */
    public static final int ROWDATA_TXT_H = 15, ROWDATA_TXT_V = 100;
    /** Property - size of buttom. */
    public static final int ROWDATA_BTN_H = 25, ROWDATA_BTN_V = 80;
    /** Property - size of shell. */
    public static final int SIZE_SHELL_H = 200, SIZE_SHELL_V = 400;
    /** Property - count of columns. */
    public static final int NUM_COLUMNS = 3;

    /** Constructor - creating a new object. */
    public SimpleAdder() {
        Display display = new Display();

        final Shell shell = new Shell(display);

        GridLayout gridLayout = new GridLayout(NUM_COLUMNS, false);
        shell.setLayout(gridLayout);

        shell.setText("Simple Adder");
        shell.setSize(SIZE_SHELL_V, SIZE_SHELL_H);

        RowData rowDataButton = new RowData();
        rowDataButton.height = ROWDATA_BTN_H;
        rowDataButton.width = ROWDATA_BTN_V;

        RowData rowDataText = new RowData();
        rowDataText.height = ROWDATA_TXT_H;
        rowDataText.width = ROWDATA_TXT_V;

        Composite compositeTaker = new Composite(shell, SWT.NONE);
        RowLayout rowLayoutTaker = new RowLayout();
        rowLayoutTaker.type = SWT.VERTICAL;
        rowLayoutTaker.spacing = SPACE_LAYOUT;
        compositeTaker.setLayout(rowLayoutTaker);

        buttonTakeOne = new Button(compositeTaker, SWT.PUSH);
        buttonTakeOne.setLayoutData(rowDataButton);
        buttonTakeOne.setText("-1");

        buttonTakeTen = new Button(compositeTaker, SWT.PUSH);
        buttonTakeTen.setLayoutData(rowDataButton);
        buttonTakeTen.setText("-10");

        Composite compositeText = new Composite(shell, SWT.NONE);
        RowLayout rowLayoutText = new RowLayout();
        rowLayoutText.type = SWT.VERTICAL;
        rowLayoutText.spacing = SPACE_LAYOUT;
        rowLayoutText.marginBottom = MARGIN_BOTTOM;
        compositeText.setLayout(rowLayoutText);
        textFieldResult = new Text(compositeText, SWT.BORDER);
        textFieldResult.setLayoutData(rowDataText);
        textFieldResult.setText("0");

        Composite compositeAdder = new Composite(shell, SWT.NONE);
        RowLayout rowLayoutAdder = new RowLayout();
        rowLayoutAdder.type = SWT.VERTICAL;
        rowLayoutAdder.spacing = SPACE_LAYOUT;
        compositeAdder.setLayout(rowLayoutAdder);

        buttonAddOne = new Button(compositeAdder, SWT.PUSH);
        buttonAddOne.setLayoutData(rowDataButton);
        buttonAddOne.setText("+1");

        buttonAddTen = new Button(compositeAdder, SWT.PUSH);
        buttonAddTen.setLayoutData(rowDataButton);
        buttonAddTen.setText("+10");

        /** @see SimpleAdder#buttonEvent(int, Button) */
        buttonEvent(NUM_MINUS_ONE, buttonTakeOne, shell);
        buttonEvent(NUM_MINUS_TEN, buttonTakeTen, shell);
        buttonEvent(NUM_ONE, buttonAddOne, shell);
        buttonEvent(NUM_TEN, buttonAddTen, shell);

        shell.pack();
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        display.dispose();
    }

    /**
     * Assigns an event to the button.
     *
     * @param value
     *            - value for assignment.
     * @param button
     *            - that button which assigned event.
     * @param shell - main shell.
     */
    private void buttonEvent(final int value,
            final Button button, final Shell shell) {
        button.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(final Event e) {
                try {
                    intResOfCalc = Integer.parseInt(textFieldResult.getText());
                } catch (Exception e1) {
                    intResOfCalc = 0;
                    MessageBox msgBx = new MessageBox(shell);
                    msgBx.setMessage("Wrong input!");
                    msgBx.open();
                }
                intResOfCalc += value;
                strRes = String.valueOf(intResOfCalc);
                textFieldResult.setText(strRes);
            }
        });
    }

    /**
     * The main method where the window object is created.
     *
     * @param args
     *            Unused.
     */
    public static void main(final String[] args) {
        SimpleAdder simpleAdder = new SimpleAdder();
    }

}
