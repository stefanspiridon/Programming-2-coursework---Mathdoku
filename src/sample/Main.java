package sample;
import javafx.animation.RotateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.util.*;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
QUESTIONS:
DO I NEED TO CLEAR A CELL BEFORE I ENTER ANOTHER VALUE?
things to keep in mind:
if a mistake is not corrected immediately it doesn't remember it
does undo need to reverse backspace
when is undo unavailable?
*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Main extends Application {

    //NUMBER TO FILL THE BOXES WITH
    String num;
    boolean showMistakes=true;

    //BOX CLASS
    public class Box{
        Rectangle rec = new Rectangle();
        Text text;
        boolean flag=false;
        String num;
        boolean cageError=false;
        boolean rowError=false;
        boolean columnError=false;
        int fontSize=50;

        Box(){
            text=new Text(null);
            text.setFont(Font.font ("Comic Sans MS", fontSize));;
        }

        public int getFontSize() {
            return fontSize;
        }

        public void setFontSize(int fontSize) {
            this.fontSize = fontSize;
        }

        public Rectangle getRec() {
            return rec;
        }

        public void setRec(Rectangle rec) {
            this.rec = rec;
        }

        public boolean getFlag(){
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public Text getText() {
            return text;
        }

        public void setText(Text text) {
            this.text = text;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public boolean isCageError() {
            return cageError;
        }

        public boolean isRowError() {
            return rowError;
        }

        public boolean isColumnError() {
            return columnError;
        }

        public void setCageError(boolean cageError) {
            this.cageError = cageError;
        }

        public void setRowError(boolean rowError) {
            this.rowError = rowError;
        }

        public void setColumnError(boolean columnError) {
            this.columnError = columnError;
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        int n = 6;

        primaryStage.setTitle("MathDoku");
        VBox vbox = new VBox();

        StackPane stackpane = new StackPane();
        stackpane.setPadding(new Insets(10, 10, 10, 10));

        GridPane gridPane = new GridPane();
        Pane canvas = new Pane();

        stackpane.getChildren().addAll(canvas, gridPane);

        //HBOX TO STORE DIFFERENT BUTTONS
        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(10, 10, 10, 10));
        Button undo = new Button("Undo");
        undo.setDisable(true);
        Button redo = new Button("Redo");
        redo.setDisable(true);
        Button clear = new Button("Clear");
        TextField file = new TextField("input.txt");
        TextField input = new TextField("Load game input");
        CheckBox mistake = new CheckBox("Show Mistakes");
        mistake.setSelected(true);
        Button load = new Button("Load");

        //HBOX TO STORE NUMBER GUI BUTTONS
        HBox numberBox = new HBox(5);
        numberBox.setPadding(new Insets(10, 10, 10, 10));


        //WIN ANIMATION
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(1000));
        rotateTransition.setNode(stackpane);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);
        rotateTransition.setAutoReverse(false);


        //LIST TO STORE BOXES
        ArrayList<Box> boxList = new ArrayList<Box>();

        //STACK FOR UNDO
        Stack<Box> boxStack = new Stack<Box>();

        //STACK FOR REDO
        Stack<Box> memory = new Stack<Box>();

        //NUMBER GUI BUTTONS CREATION AND EVENT HANDLING
        for (int i =1; i<=n; i++){
            Button button = new Button(String.valueOf(i));
            int finalI = i;

            button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                num=String.valueOf(finalI);
                for(Box box : boxList){
                    if(box.getFlag()){
                        int x = (int) ((int) box.getRec().getX() + box.getRec().getWidth()/2);
                        int y = (int) ((int) box.getRec().getY()+box.getRec().getHeight()/2);

                        //Text number = new Text(x-13, y+19,num);
                        box.getText().setX(x-13);
                        box.getText().setY(y+19);
                        //box.getText().setFont(Font.font ("Comic Sans MS", 50));
                        box.setNum(num);
                        box.getText().setText(box.getNum());
                        canvas.getChildren().add(box.getText());
                        boxStack.add(box);
                        //box.setText(box.getText());

                        box.setFlag(false);
                        box.getRec().setStroke(Color.TRANSPARENT);
                    }
                }
            });
            numberBox.getChildren().add(button);
        }

        //BACKSPACE GUI BUTTON
        Button backspace = new Button("⌫");
        backspace.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            for(Box box : boxList){
                if(box.getFlag()){
                    memory.add(box);
                    canvas.getChildren().remove(box.getText());
                    box.getText().setText(null);
                    box.setFlag(false);
                    box.getRec().setStroke(Color.TRANSPARENT);
                }
            }
        });
        numberBox.getChildren().addAll(backspace);

        //DEFAULT BORDER LINES
        Line hline = new Line();
        hline.setStartX(0);
        hline.setStartY(0);
        hline.setEndX(n * 100);
        hline.setEndY(0);
        hline.setStrokeWidth(6);

        Line vline = new Line();
        vline.setStartX(0);
        vline.setStartY(0);
        vline.setEndX(0);
        vline.setEndY(n * 100);
        vline.setStrokeWidth(6);

        canvas.getChildren().addAll(hline, vline);

        //CREATES THE BOXES WITH THE RECTANGLES
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Rectangle rec = new Rectangle();
                rec.setWidth(99);
                rec.setHeight(99);
                rec.setFill(Color.TRANSPARENT);
                //rec.setStroke(Color.YELLOW);
                rec.setStrokeWidth(1);
                //StackPane miniStack = new StackPane(rec);
                GridPane.setRowIndex(rec, i);
                GridPane.setColumnIndex(rec, j);
                rec.setX(j * 100);
                rec.setY(i * 100);
                gridPane.getChildren().addAll(rec);

                Box box = new Box();
                box.setRec(rec);

                boxList.add(box);
            }
        }

        //EVENT HANDLER FOR CHANGING THE SELECTED FLAG
        for(Box box : boxList){
            box.getRec().addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                box.setFlag(true);
                box.getRec().setStroke(Color.PINK);
            });
        }

        //EVENT HANDLER FOR DIGIT KEYS
        vbox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                //System.out.println(keyEvent.getCode());
                for (int i = 1; i <= n; i++) {
                    if ((String.valueOf(keyEvent.getCode())).equals("DIGIT"+String.valueOf(i))) {
                        num = String.valueOf(i);
                        for (Box box : boxList) {
                            if (box.getFlag()) {

                                int x = (int) ((int) box.getRec().getX() + box.getRec().getWidth() / 2);
                                int y = (int) ((int) box.getRec().getY() + box.getRec().getHeight() / 2);

                                box.getText().setX(x - 13);
                                box.getText().setY(y + 19);
                                //box.getText().setFont(Font.font("Comic Sans MS", 50));
                                box.setNum(num);
                                box.getText().setText(box.getNum());
                                canvas.getChildren().add(box.getText());
                                boxStack.add(box);
                                //box.setText(box.getText());

                                box.setFlag(false);
                                box.getRec().setStroke(Color.TRANSPARENT);
                            }
                        }
                    }

                    if(keyEvent.getCode()==KeyCode.BACK_SPACE){
                        for(Box box : boxList){
                            if(box.getFlag()){
                                memory.add(box);
                                canvas.getChildren().remove(box.getText());
                                box.getText().setText(null);
                                box.setFlag(false);
                                box.getRec().setStroke(Color.TRANSPARENT);
                            }
                        }
                    }
                }
            }
        });


        //READING FROM FILE AND CREATING THE GRID
        ArrayList<ArrayList<Integer>> cageList = new ArrayList<ArrayList<Integer>>();
        ArrayList<String> strings = new ArrayList<String>();
        //"D:\\projects\\MathDoku-master\\src\\sample\\input.txt"

        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });

        String filename = file.getText();
        String workingDirectory = System.getProperty("user.dir");
        File inputFile = new File(workingDirectory, filename);
        try {
            //String pathname=file.getText();
            //File inputFile = new File("sample/input.txt");
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNextLine()) {
                ArrayList<Integer> cage = new ArrayList<Integer>();
                String string = scanner.nextLine();

                String number = string.substring(0, string.indexOf(' '));
                strings.add(number);
                String line = string.substring(string.indexOf(' ')+1);
                //System.out.println(number);

                String[] nodes = line.split(",");
                for (String node : nodes) {
                    cage.add(Integer.parseInt(node));
                }
                cageList.add(cage);
            }
        }catch (Exception e){
            System.out.println(e);
        }


        ArrayList<Line> horizontalLines = new ArrayList<Line>();
        ArrayList<Line> verticalLines = new ArrayList<Line>();

        for (int i = 0; i < n * n; i++) {
            Rectangle rec = (Rectangle) gridPane.getChildren().get(i);

            int x1 = (int) rec.getX();
            int y1 = (int) rec.getY();

            Line line1 = new Line();
            line1.setStartX(x1 + 100);
            line1.setStartY(y1);
            line1.setEndX(x1 + 100);
            line1.setEndY(y1 + 100);
            //line1.setStroke(Color.RED);
            line1.setStrokeWidth(6);
            canvas.getChildren().addAll(line1);
            verticalLines.add(line1);

            Line line2 = new Line();
            line2.setStartX(x1);
            line2.setStartY(y1 + 100);
            line2.setEndX(x1 + 100);
            line2.setEndY(y1 + 100);
            //line2.setStroke(Color.BLUE);
            line2.setStrokeWidth(6);
            canvas.getChildren().addAll(line2);
            horizontalLines.add(line2);
        }

        for (ArrayList<Integer> cage : cageList) {

            for(int i=0; i<cage.size(); i++){

                if (i < cage.size() - 1 && cage.get(i) == (cage.get(i + 1)) - 1) {
                    //System.out.println("skip");
                    verticalLines.get(cage.get(i)-1).setStrokeWidth(1);
                }

                if (i < cage.size() - 1 &&cage.get(i) == (cage.get(i + 1)) - n) {
                    //System.out.println("skip");
                    horizontalLines.get(cage.get(i)-1).setStrokeWidth(1);
                }

////////////////////////////////////////////////////////////////////////////////////////////
                if (i < cage.size() - 2 &&cage.get(i) == (cage.get(i + 2)) - n) {
                    //System.out.println("skip");                                                 ///THIS IS REALLY BAD BUT WORKS FOR NOW///
                    horizontalLines.get(cage.get(i)-1).setStrokeWidth(1);
                }
//////////////////////////////////////////////////////////////////////////////////////////////
            }
        }

        ArrayList<Text> boxLables = new ArrayList<>();
        for (int i=0; i<cageList.size(); i++){
            Rectangle rec = (Rectangle) gridPane.getChildren().get(cageList.get(i).get(0)-1);
            int x = (int) rec.getX();
            int y = (int) rec.getY();

            Text text = new Text(x+5, y+24, strings.get(i));
            text.setFont(Font.font ("Comic Sans MS", 20));
            boxLables.add(text);
            canvas.getChildren().add(text);
        }

        //MISTAKE DETECTION
        for(Box box : boxList){
            box.getText().textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    if(!boxStack.isEmpty()){
                        undo.setDisable(false);
                    }else{
                        undo.setDisable(true);
                    }

                    if(!memory.isEmpty()){
                        redo.setDisable(false);
                    }else{
                        redo.setDisable(true);
                    }

                    int win=0;
                    for(Box box : boxList){
                        if(!box.isCageError() && !box.isColumnError() && !box.isRowError()
                                && box.getText().getText()!=null && !box.getText().getText().isEmpty()){
                            win++;
                        }
                    }
                    if(win==boxList.size()){
                        rotateTransition.play();
                        System.out.println("you won bro");
                    }

                    for(Box box : boxList){
                        box.setColumnError(false);
                        box.setRowError(false);
                        box.setCageError(false);
                    }

                    for (int a = 0; a < boxList.size(); a++) {

                        int column = a % 6;// 1 mod 6 = 1
                        int row = (int) a / 6;// 1 / 6 = 0

                        //check for column
                        ArrayList<Box> columnBoxList = new ArrayList<>();
                        for (int j = 0; j < n; j++) {
                            columnBoxList.add(boxList.get(column + n * j));
                        }

                        ArrayList<Integer> numbersInBoxesColumn = new ArrayList<>();
                        for (int j = 0; j < n; j++) {
                            String string = boxList.get(column + n * j).getText().getText();
                            if (string != null && !string.isEmpty()) {
                                int number = Integer.parseInt(string);
                                numbersInBoxesColumn.add(number);
                            }
                        }

                        Set<Integer> boxColumnSet = new HashSet<>(numbersInBoxesColumn);
                        boxColumnSet.addAll(numbersInBoxesColumn);

                        if (numbersInBoxesColumn.size() != boxColumnSet.size()) {
                            for (Box box : columnBoxList) {
                                //box.getRec().setFill(Color.rgb(255, 0, 0, 0.2));
                                box.setColumnError(true);
                            }
                        }

                        //check for row
                        ArrayList<Box> rowBoxList = new ArrayList<>();
                        for (int j = 0; j < n; j++) {
                            rowBoxList.add(boxList.get(row * n + j));
                        }

                        ArrayList<Integer> numbersInBoxesRow = new ArrayList<>();
                        for (int j = 0; j < n; j++) {
                            String string = boxList.get(row * n + j).getText().getText();
                            if (string != null && !string.isEmpty()) {
                                int number = Integer.parseInt(string);
                                numbersInBoxesRow.add(number);
                            }
                        }

                        Set<Integer> boxRowSet = new HashSet<>(numbersInBoxesRow);
                        boxRowSet.addAll(numbersInBoxesRow);

                        if (numbersInBoxesRow.size() != boxRowSet.size()) {
                            for (Box box : rowBoxList) {
                                //box.getRec().setFill(Color.rgb(0, 0, 255, 0.2));
                                box.setRowError(true);
                            }
                        }

                        //CAGES
                        for (int i = 0; i < cageList.size(); i++) {
                            ArrayList<Integer> cage = cageList.get(i);
                            ArrayList<Box> cageBoxList = new ArrayList<>();
                            for (int j = 0; j < cage.size(); j++) {
                                cageBoxList.add(boxList.get(cage.get(j) - 1));
                            }

                            ArrayList<Integer> numbersInBoxesCage = new ArrayList<>();
                            for (int j = 0; j < cage.size(); j++) {
                                String string = boxList.get(cage.get(j) - 1).getText().getText();
                                if (string != null && !string.isEmpty()) {
                                    int number = Integer.parseInt(string);
                                    numbersInBoxesCage.add(number);
                                }
                            }

                            String node = strings.get(i);
                            int target = Integer.parseInt(node.substring(0, node.length() - 1));
                            String operation = node.substring(node.length() - 1);

                            if (numbersInBoxesCage.size() == cage.size()) {

                                if (operation.equals("+")) {
                                    int result = 0;
                                    for (int num1 : numbersInBoxesCage) {
                                        result = result + num1;
                                    }

                                    if (result != target) {
                                        for (Box box : cageBoxList) {
                                            //box.getRec().setFill(Color.rgb(0, 255, 0, 0.2));
                                            box.setCageError(true);
                                        }
                                    }
                                }

                                if (operation.equals("x")) {
                                    int result = 1;
                                    for (int num1 : numbersInBoxesCage) {
                                        result = result * num1;
                                    }

                                    if (result != target) {
                                        for (Box box : cageBoxList) {
                                            //box.getRec().setFill(Color.rgb(0, 255, 0, 0.2));
                                            box.setCageError(true);
                                        }
                                    }
                                }

                                if (operation.equals("-") && numbersInBoxesCage.size() == 2) {
                                    int result1 = numbersInBoxesCage.get(0) - numbersInBoxesCage.get(1);
                                    int result2 = numbersInBoxesCage.get(1) - numbersInBoxesCage.get(0);

                                    if (result1 != target && result2 != target) {
                                        for (Box box : cageBoxList) {
                                            //box.getRec().setFill(Color.rgb(0, 255, 0, 0.2));
                                            box.setCageError(true);
                                        }
                                    }
                                }

                                if (operation.equals("÷") && numbersInBoxesCage.size() == 2) {
                                    int result1 = numbersInBoxesCage.get(0) / numbersInBoxesCage.get(1);
                                    int result2 = numbersInBoxesCage.get(1) / numbersInBoxesCage.get(0);

                                    if (result1 != target && result2 != target) {
                                        for (Box box : cageBoxList) {
                                            //box.getRec().setFill(Color.rgb(0, 255, 0, 0.2));
                                            box.setCageError(true);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(showMistakes) {
                        for (Box box : boxList) {
                            if (box.isColumnError()) {
                                box.getRec().setFill(Color.rgb(255, 0, 0, 0.2));
                            }
                            if (box.isRowError()) {
                                box.getRec().setFill(Color.rgb(0, 0, 255, 0.2));
                            }
                            if (box.isCageError()) {
                                box.getRec().setFill(Color.rgb(0, 255, 0, 0.2));
                            }
                        }
                    }

                    for (Box box : boxList) {
                        if (!box.isColumnError() && !box.isRowError() && !box.isCageError()) {
                            box.getRec().setFill(Color.TRANSPARENT);
                        }
                    }
                }
            });
        }

        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                for(Box box : boxList){
                    canvas.getChildren().remove(box.getText());
                    box.getText().setText(null);
                    box.setFlag(false);
                    box.getRec().setStroke(Color.TRANSPARENT);
                }
            }
        });

        undo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                if(!boxStack.isEmpty()) {
                    Box box = boxStack.pop();
                    memory.push(box);
                    canvas.getChildren().remove(box.getText());

                    box.getText().setText(null);
                    box.setFlag(false);
                    box.getRec().setStroke(Color.TRANSPARENT);
                }
            }
        });

        redo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!memory.isEmpty()) {
                    Box box = memory.pop();

                    int x = (int) ((int) box.getRec().getX() + box.getRec().getWidth() / 2);
                    int y = (int) ((int) box.getRec().getY() + box.getRec().getHeight() / 2);

                    box.getText().setX(x - 13);
                    box.getText().setY(y + 19);
                    //box.getText().setFont(Font.font("Comic Sans MS", 50));
                    box.getText().setText(box.getNum());
                    canvas.getChildren().add(box.getText());
                    boxStack.push(box);
                    //box.setText(box.getText());

                    box.setFlag(false);
                    box.getRec().setStroke(Color.TRANSPARENT);
                }
            }
        });

        mistake.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showMistakes=mistake.isSelected();
            }
        });

        Label select = new Label("Select Font size");
        ChoiceBox font = new ChoiceBox();
        font.getItems().add("small");
        font.getItems().add("medium");
        font.getItems().add("big");

        font.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if(t1.equals(0)){
                    for(Box box : boxList){
                        /*Text text = box.getText();
                        text.setX(text.getX() + 6);*/
                        box.getText().setFont(Font.font ("Comic Sans MS", 40));
                        box.setFontSize(40);

                    }

                    for (Text text : boxLables){
                        text.setFont(Font.font ("Comic Sans MS", 15));
                        //text.setY(text.getY()-3);
                    }

                }

                if(t1.equals(1)){
                    for(Box box : boxList){
                        box.getText().setFont(Font.font ("Comic Sans MS", 50));
                        box.setFontSize(50);
                    }

                    for (Text text : boxLables){
                        text.setFont(Font.font ("Comic Sans MS", 20));
                    }
                }

                if(t1.equals(2)){
                    for(Box box : boxList){
                        /*Text text = box.getText();
                        text.setX(text.getX() + 10);
                        text.setY(text.getY() + 5);*/
                        box.getText().setFont(Font.font ("Comic Sans MS", 60));
                        box.setFontSize(60);
                    }

                    for (Text text : boxLables){
                        text.setFont(Font.font ("Comic Sans MS", 25));
                        //text.setY(text.getY()+5);
                    }
                }
            }
        });



        numberBox.getChildren().addAll(select, font);

        hbox.getChildren().addAll(undo, redo, clear, file, input, load, mistake);
        vbox.getChildren().addAll(stackpane, hbox, numberBox);
        primaryStage.setScene(new Scene(vbox));
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}