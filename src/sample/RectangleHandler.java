package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class RectangleHandler implements EventHandler<MouseEvent> {

    Rectangle rec = new Rectangle();
    Pane canvas = new Pane();

    public RectangleHandler(Rectangle rec, Pane canvas){
        this.rec=rec;
        this.canvas=canvas;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        int x = (int) ((int) rec.getX() + rec.getWidth()/2);
        int y = (int) ((int) rec.getY()+rec.getHeight()/2);

        Text number = new Text(x-13, y+19,"1");
        number.setFont(Font.font ("Comic Sans MS", 50));
        canvas.getChildren().add(number);
    }
}
