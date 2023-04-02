package ma.enset.sdia.tp.blocking;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatClient extends Application {
    PrintWriter pw;
    public static void main(String[] args) {

        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Chat Client");
        BorderPane borderPane=new BorderPane();
        Scene scene=new Scene(borderPane,500,520);
        Label labelHost=new Label("host");
        TextField textFieldHost=new TextField();
        Label labelPort=new Label("port");
        TextField textFielport=new TextField();
        Button buttonConnecter=new Button("connecter");
        HBox hBox=new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        hBox.setBackground(new Background(new BackgroundFill(Color.PURPLE,null,null)));
        hBox.getChildren().addAll(labelHost,textFieldHost,labelPort,textFielport,buttonConnecter);
        borderPane.setTop(hBox);
        VBox vBox=new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        ObservableList<String> observableList= FXCollections.observableArrayList();
        ListView<String> listView=new ListView<String>(observableList);
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);
        Label labelMessage=new Label("Message");
        TextField textFieldMessage=new TextField();
        textFieldMessage.setPrefSize(320,25);
        Button buttonEnvoyer=new Button("Envoyer");
        HBox hBox1=new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(20));
        hBox1.getChildren().addAll(labelMessage,textFieldMessage,buttonEnvoyer);
        borderPane.setBottom(hBox1);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        buttonConnecter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String host=textFieldHost.getText();
                int port=Integer.parseInt(textFielport.getText());
                try {
                    Socket socket=new Socket(host,port);
                    InputStream is= socket.getInputStream();
                    InputStreamReader isr=new InputStreamReader(is);
                    BufferedReader br=new BufferedReader(isr);
                    OutputStream os=socket.getOutputStream();
                    pw=new PrintWriter(os,true);
                    new Thread(()->{

                            while (true) {

                                    try {
                                        String response = br.readLine();
                                        Platform.runLater(()->{
                                            observableList.add(response);
                                        });

                                    }  catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }


                            }

                    }).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        buttonEnvoyer.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String message=textFieldMessage.getText();
                pw.println(message);
            }
        });
    }
}
