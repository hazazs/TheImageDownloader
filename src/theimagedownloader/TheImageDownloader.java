package theimagedownloader;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.io.FileUtils;

public class TheImageDownloader extends Application {
    private final Button btn = new Button("Browse..");
    private final FileChooser fileChooser = new FileChooser();
    private final String image = "https://media-cdn.tripadvisor.com/media/photo-s/10/04/ec/46/el-sitio-mas-espectacular.jpg";
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH'h' mm'm'");
    private final Label label = new Label();
    @Override
    public void start(Stage stage) {
        btn.setFocusTraversable(false);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LocalDateTime time = LocalDateTime.now();
                label.setVisible(true);
                changeLabel("Browsing..", "#000000");
                Workbook xls;
                String[][] data;
                try {
                    xls = Workbook.getWorkbook(fileChooser.showOpenDialog(stage));
                    changeLabel("In progress..", "#000000");
                    Sheet sheet  = xls.getSheet(0);
                    data = new String[sheet.getRows()][sheet.getColumns()];
                    for (int r = 0; r < sheet.getRows(); r++)
                        for (int c = 0; c < sheet.getColumns(); c++) {
                            Cell cell = sheet.getCell(c, r);
                            data[r][c] = cell.getContents();
                        }
                    for (int i = 0; i < sheet.getRows(); i++) {
                        System.out.println("Saving image " + (i + 1) + "/" + sheet.getRows());
                        changeLabel("Saving image " + (i + 1) + "/" + sheet.getRows(), "#000000");
                        FileUtils.copyURLToFile(new URL(image), new File("images/" + dtf.format(time) + "/" + data[i][0] + data[i][1] + data[i][2]));
                    }
                    changeLabel("Done!", "#338833");
                } catch (Exception exception) {
                    changeLabel("Not a valid .xls file", "#ff0000");
                    System.out.println(exception);
                }
            }
        });
        
        fileChooser.setTitle("Open .xls");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
        
        label.setPadding(new Insets(60, 0, 0, 0));
        label.setVisible(false);
        
        stage.setScene(new Scene(new StackPane(label, btn), 300, 250));
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/carussel.png")));
        stage.setTitle("Image Downloader 1.0");
        stage.setResizable(false);
        stage.show();
    }
    public void changeLabel(String text, String color) {
        label.setText(text);
        label.setTextFill(Color.web(color));
    }
    public static void main(String[] args) {
        launch(args);
    }
}