package sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class SimpleFileChooserExample extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(10));

        TextArea area = new TextArea();
        border.setCenter(area);
        Button button = new Button("Choose File");
        border.setBottom(button);
        BorderPane.setAlignment(button, Pos.BOTTOM_RIGHT);
        BorderPane.setMargin(button, new Insets(10, 0, 0, 0));

        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                fileChooser.setTitle("Open File to Load");
                ExtensionFilter txtFilter = new ExtensionFilter("Text files",
                        "*.txt");
                fileChooser.getExtensionFilters().add(txtFilter);

                File file = fileChooser.showOpenDialog(primaryStage);

                if (file != null && file.exists() && file.canRead()) {
                    try {
                        BufferedReader buffered = new BufferedReader(
                                new FileReader(file));
                        String line;
                        while ((line = buffered.readLine()) != null) {
                            area.appendText(line + "\n");
                        }
                        buffered.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        });

        Scene scene = new Scene(border);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}