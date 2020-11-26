package theimagedownloader;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.io.FileUtils;

public class TheImageDownloader extends Application {
    private final Label lbl = new Label();
    private final FileChooser fileChooser = new FileChooser();
    private final Button btn = new Button("START");
    private Workbook xls;
    private String[][] str;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH'h' mm'm'");
    @Override
    public void start(Stage stage) {
        lbl.setPadding(new Insets(60, 0, 0, 0));
        lbl.setVisible(false);
        fileChooser.setTitle("Open .xls file");
        fileChooser.getExtensionFilters().add(new ExtensionFilter(".xls", "*.xls"));
        btn.setFocusTraversable(false);
        btn.setOnAction(event -> {
            try {
                LocalDateTime time = LocalDateTime.now();
                lbl.setVisible(true);
                changeLabel("Browsing..", "#000000");
                xls = Workbook.getWorkbook(fileChooser.showOpenDialog(stage));
                btn.setDisable(true);
                Sheet sheet = xls.getSheet(0);
                str = new String[sheet.getRows()][sheet.getColumns()];
                for (int r = 0; r < sheet.getRows(); r++)
                    for (int c = 0; c < sheet.getColumns(); c++)
                        str[r][c] = sheet.getCell(c, r).getContents();
                Task task = new Task<Void>() {
                    @Override
                    public Void call() {
                        for (int i = 1; i <= sheet.getRows(); i++) {
                            updateProgress(i, sheet.getRows());
                            try {
                                FileUtils.copyURLToFile(new URL(str[i - 1][0]), new File("images/" + dtf.format(time) + "/" + i + "-" + str[i - 1][0].substring(69, 80) + ".png"));
                            } catch (Exception exception) {
                                System.out.println(exception);
                              }
                        }
                        return null;
                    }
                };
                lbl.textProperty().bind(Bindings.createStringBinding(() -> Integer.toString((int) (task.progressProperty().get() * sheet.getRows())) + " / " + sheet.getRows(), task.progressProperty()));
                task.setOnSucceeded(e -> {
                    btn.setDisable(false);
                    lbl.textProperty().unbind();
                    changeLabel("Done!", "#338833");
                });
                new Thread(task).start();
            } catch (Exception exception) {
                if (exception instanceof BiffException)
                    changeLabel("Invalid .xls file", "#ff0000");
                if (exception instanceof NullPointerException)
                    changeLabel("You didn't open anything", "#ff0000");
                System.out.println(exception);
              }
        });
        stage.setScene(new Scene(new StackPane(lbl, btn), 300, 250));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/carussel.png")));
        stage.setTitle("Image Downloader 1.4");
        stage.setResizable(false);
        stage.show();
    }
    public void changeLabel(String text, String color) {
        lbl.setText(text);
        lbl.setTextFill(Color.web(color));
    }
    public static void main(String[] args) {
        launch(args);
    }
}