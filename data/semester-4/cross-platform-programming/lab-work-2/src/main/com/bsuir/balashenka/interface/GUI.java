package main.com.bsuir.balashenka.interface;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Class of GUI. Contain buttons, labels and etc.
 * @author  Stsiapan Balashenka
 * @version 1.1
 * @since   2018-03-12
 */
public class GUI extends Pane implements Constants {

    /**Property that only use in class GUI.*/
    private Text userText;
    /**@see GUI#userText*/
    private Text browserText;
    /**@see GUI#userText*/
    private Text computerText;
    /**@see GUI#userText*/
    private Text playerText;
    /**@see GUI#userText*/
    private Separator separatorOpenUser;
    /**@see GUI#userText*/
    private Separator separatorCloseUser;
    /**@see GUI#userText*/
    private Separator separatorOpenBrowser;
    /**@see GUI#userText*/
    private Separator separatorCloseBrowser;
    /**@see GUI#userText*/
    private Separator separatorOpenComputer;
    /**@see GUI#userText*/
    private  Separator separatorCloseComputer;
    /**@see GUI#userText*/
    private Separator separatorOpenPlayer;
    /**@see GUI#userText*/
    private Label currentBrowserLabel;
    /**@see GUI#userText*/
    private Label currentActionBrowserLabel;
    /**@see GUI#userText*/
    private Label currentComputerLabel;
    /**@see GUI#userText*/
    private Label currentActionComputerLabel;
    /**@see GUI#userText*/
    private Label currentPlayerLabel;
    /**@see GUI#userText*/
    private Label currentActionPlayerLabel;
    /**@see GUI#userText*/
    private Label currentPlayerApplicationLabel;

    /**Property that use in class Controller.*/
    Button installBrowserButton;
    /**@see GUI#installBrowserButton */
    Button  downloadBrowserButton ;
    /**@see GUI#installBrowserButton */
    Button  deleteBrowserButton ;
    /**@see GUI#installBrowserButton */
    Button  installPlayerButton ;
    /**@see GUI#installBrowserButton */
    Button  downloadPlayerButton ;
    /**@see GUI#installBrowserButton */
    Button deletePlayerButton;
    /**@see GUI#installBrowserButton*/
    Button  watchFilmButton;
    /**@see GUI#installBrowserButton*/
    Button  downloadFilmButton;
    /**@see GUI#installBrowserButton*/
    Label playerLabel;
    /**@see GUI#installBrowserButton*/
    Label actionComputerLabel;
    /**@see GUI#installBrowserButton*/
    Label computerLabel;
    /**@see GUI#installBrowserButton*/
    Label actionBrowserLabel;
    /**@see GUI#installBrowserButton*/
    Label browserLabel;
    /**@see GUI#installBrowserButton*/
    Label actionPlayerLabel;
    /**@see GUI#installBrowserButton*/
    Label playerApplicationLabel;
    /**@see GUI#installBrowserButton*/
    TextField nameTextField;
    /**@see GUI#installBrowserButton*/
    TextField authorTextField;

    /**
     * Constructor of class GUI.
     * Create components of GUI.
     */
    public GUI() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPrefSize(MAX_WIDTH, MAX_HEIGHT);
        grid.setHgap(H_GAP);
        grid.setVgap(V_GAP);
        grid.setPadding(new Insets(GRID_TOP, GRID_RIGHT, GRID_BOTTOM, GRID_LEFT));

        userText = new Text("User");
        userText.setFont(Font.font("", FontWeight.NORMAL, SIZE_TEXT));
        grid.add(userText, COLUMN_INDEX, ROW_INDEX_0);
        separatorOpenUser = new Separator();
        separatorOpenUser.setMaxWidth(MAX_WIDTH);
        grid.add(separatorOpenUser, COLUMN_INDEX, ROW_INDEX_1);

        VBox userVBox = new VBox(SPACE_FIVE);

        VBox userBrowserVBox = new VBox(SPACE_FIVE);
        installBrowserButton = new Button("Install Browser");
        installBrowserButton.setPrefWidth(MAX_WIDTH);
        downloadBrowserButton = new Button("Download Browser");
        downloadBrowserButton.setPrefWidth(MAX_WIDTH);
        deleteBrowserButton = new Button("Delete Browser");
        deleteBrowserButton.setPrefWidth(MAX_WIDTH);
        userBrowserVBox.getChildren().addAll(installBrowserButton, downloadBrowserButton, deleteBrowserButton);
        grid.add(userBrowserVBox, COLUMN_INDEX, ROW_INDEX_2);

        separatorOpenUser = new Separator();
        separatorOpenUser.setMaxWidth(MAX_WIDTH);
        grid.add(separatorOpenUser, COLUMN_INDEX, ROW_INDEX_3);

        VBox userPlayerVBox = new VBox(SPACE_FIVE);
        installPlayerButton = new Button("Install Player");
        installPlayerButton.setPrefWidth(MAX_WIDTH);
        downloadPlayerButton = new Button("Download Player");
        downloadPlayerButton.setPrefWidth(MAX_WIDTH);
        deletePlayerButton = new Button("Delete Player");
        deletePlayerButton.setPrefWidth(MAX_WIDTH);
        userPlayerVBox.getChildren().addAll(installPlayerButton, downloadPlayerButton,
                deletePlayerButton);
        grid.add(userPlayerVBox, COLUMN_INDEX, ROW_INDEX_4);

        separatorOpenUser = new Separator();
        separatorOpenUser.setMaxWidth(MAX_WIDTH);
        grid.add(separatorOpenUser, COLUMN_INDEX, ROW_INDEX_5);

        watchFilmButton = new Button("Watch Film");
        watchFilmButton.setPrefWidth(MAX_WIDTH);
        grid.add(watchFilmButton, COLUMN_INDEX, ROW_INDEX_6);

        downloadFilmButton = new Button("Download Film");
        downloadFilmButton.setPrefWidth(MAX_WIDTH);
        grid.add(downloadFilmButton, COLUMN_INDEX, ROW_INDEX_7);

        separatorOpenUser = new Separator();
        separatorOpenUser.setMaxWidth(MAX_WIDTH);
        grid.add(separatorOpenUser, COLUMN_INDEX, ROW_INDEX_8);

        authorTextField = new TextField();
        authorTextField.setPrefWidth(MAX_WIDTH);
        authorTextField.setPromptText("Author");

        nameTextField = new TextField();
        nameTextField.setPrefWidth(MAX_WIDTH);
        nameTextField.setPromptText("Name");

        userVBox.getChildren().addAll(nameTextField, authorTextField);

        grid.add(userVBox, COLUMN_INDEX, ROW_INDEX_9);

        separatorCloseUser = new Separator();
        separatorCloseUser.setMaxWidth(MAX_WIDTH);
        grid.add(separatorCloseUser, COLUMN_INDEX, ROW_INDEX_10);

        browserText = new Text("Browser");
        browserText.setFont(Font.font("", FontWeight.NORMAL, SIZE_TEXT));
        grid.add(browserText, COLUMN_INDEX, ROW_INDEX_11);

        separatorOpenBrowser = new Separator();
        separatorOpenBrowser.setMaxWidth(MAX_WIDTH);
        grid.add(separatorOpenBrowser, COLUMN_INDEX, ROW_INDEX_12);

        VBox browserVBox = new VBox(SPACE_FIVE);
        HBox currentBrowserHBox = new HBox(SPACE_FIVE);
        currentBrowserLabel = new Label("Browser: ");
        browserLabel = new Label("...");
        currentBrowserHBox.getChildren().addAll(currentBrowserLabel, browserLabel);

        HBox currentActionBrowserHBox = new HBox(SPACE_FIVE);
        currentActionBrowserLabel = new Label("Action: ");
        actionBrowserLabel = new Label("...");
        currentActionBrowserHBox.getChildren().addAll(currentActionBrowserLabel, actionBrowserLabel);

        browserVBox.getChildren().addAll(currentBrowserHBox, currentActionBrowserHBox);
        grid.add(browserVBox, COLUMN_INDEX, ROW_INDEX_13);

        separatorCloseBrowser = new Separator();
        separatorCloseBrowser.setMaxWidth(MAX_WIDTH);
        grid.add(separatorCloseBrowser, COLUMN_INDEX, ROW_INDEX_14);

        computerText = new Text("Computer");
        computerText.setFont(Font.font("", FontWeight.NORMAL, SIZE_TEXT));
        grid.add(computerText, COLUMN_INDEX, ROW_INDEX_15);

        separatorOpenComputer = new Separator();
        separatorOpenComputer.setMaxWidth(MAX_WIDTH);
        grid.add(separatorOpenComputer, COLUMN_INDEX, ROW_INDEX_16);

        VBox computerVBox = new VBox(SPACE_FIVE);
        HBox currentComputerHBox = new HBox(SPACE_FIVE);
        currentComputerLabel = new Label("Application: ");
        computerLabel = new Label("...");
        currentComputerHBox.getChildren().addAll(currentComputerLabel, computerLabel);

        HBox currentActionComputerHBox = new HBox(SPACE_FIVE);
        currentActionComputerLabel = new Label("Action: ");
        actionComputerLabel = new Label("...");
        currentActionComputerHBox.getChildren().addAll(currentActionComputerLabel, actionComputerLabel);

        computerVBox.getChildren().addAll(currentComputerHBox, currentActionComputerHBox);
        grid.add(computerVBox, COLUMN_INDEX, ROW_INDEX_17);

        separatorCloseComputer = new Separator();
        separatorCloseComputer.setMaxWidth(MAX_WIDTH);
        grid.add(separatorCloseComputer, COLUMN_INDEX, ROW_INDEX_18);

        playerText = new Text("Player");
        playerText.setFont(Font.font("", FontWeight.NORMAL, SIZE_TEXT));
        grid.add(playerText, COLUMN_INDEX, ROW_INDEX_19);

        separatorOpenPlayer = new Separator();
        separatorOpenPlayer.setMaxWidth(MAX_WIDTH);
        grid.add(separatorOpenPlayer, COLUMN_INDEX, ROW_INDEX_20);

        VBox playerVBox = new VBox(SPACE_FIVE);
        HBox currentPlayerHBox = new HBox(SPACE_FIVE);
        currentPlayerLabel = new Label("Film: ");
        playerLabel = new Label("...");
        HBox currentActionPlayerHBox = new HBox(SPACE_FIVE);
        currentActionPlayerLabel = new Label("Action: ");
        actionPlayerLabel = new Label("...");
        HBox currentPlayerApplicationHBox = new HBox(SPACE_FIVE);
        currentPlayerApplicationLabel = new Label("Player: ");
        playerApplicationLabel = new Label("...");
        currentPlayerApplicationHBox.getChildren().addAll(currentPlayerApplicationLabel, playerApplicationLabel);
        currentPlayerHBox.getChildren().addAll(currentPlayerLabel, playerLabel);
        currentActionPlayerHBox.getChildren().addAll(currentActionPlayerLabel, actionPlayerLabel);

        playerVBox.getChildren().addAll(currentPlayerHBox, currentActionPlayerHBox, currentPlayerApplicationHBox);

        grid.add(playerVBox, COLUMN_INDEX, ROW_INDEX_21);

        setDisable();

        this.getChildren().add(grid);
    }

    /**
     * Set disable button.
     */
    private void setDisable() {
        installBrowserButton.setDisable(true);
        downloadBrowserButton.setDisable(true);
        deleteBrowserButton.setDisable(true);
        installPlayerButton.setDisable(true);
        downloadPlayerButton.setDisable(true);
        deletePlayerButton.setDisable(true);
        watchFilmButton.setDisable(true);
        downloadFilmButton.setDisable(true);
    }
}
