import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.image.IndexColorModel;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.canvas.Canvas;

public class ClientWindow extends Application
{
    public Socket socket = null;
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        try
        {
            socket = new Socket("127.0.0.1", 8086);
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



        final ArrayList<myPoint> myPoints = new ArrayList<myPoint>();
        final ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK);

        final Button clear = new Button("Clear my points");
        final Color background = Color.WHITE;
        final Color brushcolor = Color.BLACK;
        final Point brushsize = new Point(3, 3);

        primaryStage.setTitle("Drawing Client");
        Group root = new Group();
        final Canvas canvas = new Canvas(600, 600);

        BorderPane layout = new BorderPane();
        /*layout.setMaxWidth(600);
        layout.setMinWidth(600);

        layout.setMaxHeight(600);
        layout.setMinHeight(600);*/
        HBox buttons = new HBox();
        buttons.setSpacing(30);
        buttons.getChildren().addAll(colorPicker, clear);

        layout.setTop(buttons);
        layout.setBottom(canvas);
        root.getChildren().add(layout);
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setMaxHeight(600);
        primaryStage.setMaxWidth(600);
        primaryStage.show();

        final GraphicsContext gc = canvas.getGraphicsContext2D();


        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                gc.setFill(colorPicker.getValue());
            }
        });

        clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                int k = myPoints.size();
                myPoint pnt;
                gc.setFill(background);
                while (k > 0) {
                    pnt = myPoints.get(k - 1);
                    gc.fillOval(pnt.x, pnt.y, brushsize.x, brushsize.y);
                    myPoints.remove(k - 1);
                    k--;
                }
                gc.setFill(brushcolor);
            }
        });

        // Draw a point where the user clicks
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        int x = (int) t.getX();
                        int y = (int) t.getY();
                        gc.fillOval(x, y, brushsize.x, brushsize.y);
                        myPoints.add(new myPoint(x, y));
                        try
                        {
                            DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                            os.writeBytes("x=" + Integer.toString(x) + "&y=" + Integer.toString(y) + "&color=" + colorPicker.getValue().toString()+'\n');
                            System.out.println("x=" + Integer.toString(x) + "&y=" + Integer.toString(y) + "&color=" + colorPicker.getValue().toString());
                            //os.close();
                            //os.close();
                            //socket.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

        Thread listener = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (true)
                {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    try
                {
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    if (in.available()>0)
                    {
                    String s;
                    try
                    {
                        while ((s = in.readLine()) != null)
                        {
                            System.out.println(s);

                            HashMap<String,String> res = parseAnswer(s);
                            gc.setFill(Color.valueOf(res.get("color")));
                            gc.fillOval(Float.parseFloat(res.get("x")), Float.parseFloat(res.get("y")), brushsize.x, brushsize.y);
                            myPoints.add(new myPoint(Integer.parseInt(res.get("x")), Integer.parseInt(res.get("y"))));
                        }
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    }
                }
                catch (Exception ioe)
                {
                    ioe.printStackTrace();
                }
                }
            }
        });
        listener.start();

}
    static public HashMap<String,String> parseAnswer(String s)
    {

        HashMap<String,String>res = new HashMap<String, String>();
        if (s.length()==0)
            return res;
        String str = s.toString();
        String[] str_arr = str.split("&");
        for (int i = 0; i<=str_arr.length-1; i++)
        {
            String[]key_value = str_arr[i].split("=");
            if (key_value.length!=2)
                continue;

            res.put(key_value[0],key_value[1]);
        }

        return  res;
    }
}
