import animatefx.animation.SlideInUp;
import animatefx.animation.SlideOutDown;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class dashboardController implements Initializable {
    @FXML
    private VBox songsVBox;

    @FXML
    private JFXButton playButton;

    @FXML
    private JFXButton pauseButton;

    @FXML
    private JFXButton playButton1;

    @FXML
    private JFXButton pauseButton1;

    @FXML
    private JFXButton upButton;

    @FXML
    private Label songNameLabel;

    @FXML
    private AnchorPane songDetails;

    @FXML
    private AnchorPane baseControls;

    @FXML
    private ScrollPane songScrollPane;

    @FXML
    private Label libraryLabel;

    @FXML
    private Label noUSBLabel;

    @FXML
    private JFXButton recheckButton;

    @FXML
    private Label nosongsFoundLabel;

    @FXML
    private Label artistNameLabel;

    ArrayList<String> songNames = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //get all the files...
        Task<Void> startTasks = new Task<Void>() {
            @Override
            protected Void call() {
                try
                {
                    fetchMusicFiles();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(startTasks).start();
    }

    public void fetchMusicFiles()
    {
        File sda = new File("/dev/sda");
        if(!sda.exists())
        {
            System.out.println("USB not found!!!");
            libraryLabel.setVisible(false);
            noUSBLabel.setVisible(true);
            nosongsFoundLabel.setVisible(false);
            recheckButton.setVisible(true);
            recheckButton.setDisable(false);
            recheckButton.toFront();
            return;
        }
        else
        {
            try
            {
                Process p = Runtime.getRuntime().exec("sudo mount /dev/sda /home/pi/usb_music/");
                Thread.sleep(2000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        File musicFolder = new File("/home/pi/usb_music");
        File listOfFiles[] = musicFolder.listFiles(new MyFileNameFilter(".mp3"));

        if(listOfFiles == null)
        {
            libraryLabel.setVisible(false);
            noUSBLabel.setVisible(false);
            nosongsFoundLabel.setVisible(true);
            recheckButton.setVisible(true);
            recheckButton.setDisable(false);
            recheckButton.toFront();
            return;
        }
        else
        {
            libraryLabel.setVisible(true);
            noUSBLabel.setVisible(false);
            nosongsFoundLabel.setVisible(false);
            recheckButton.setVisible(false);
            recheckButton.setDisable(true);
            recheckButton.toBack();
        }

        for (File eachMusicFile : listOfFiles)
        {
            songNames.add(eachMusicFile.getName());
            Label songName = new Label(eachMusicFile.getName());
            songName.setTextFill(Paint.valueOf("#FFF"));
            songName.setFont(Font.font(17));
            songName.setBackground(new Background(new BackgroundFill(Color.color(1,1,1,0.1), new CornerRadii(5), new Insets(5,5,5,5))));
            songName.setMaxWidth(170);
            songName.setPadding(new Insets(10,10,10,10));

            songName.setOnTouchReleased(new EventHandler<TouchEvent>() {
                @Override
                public void handle(TouchEvent event) {
                    System.out.println("Clicked69");
                    Task<Void> s = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception{
                            System.out.println("slol");
                            playSong(eachMusicFile.getName());
                            return null;
                        }
                    };
                    new Thread(s).start();
                }
            });
            songsVBox.getChildren().add(songName);
        }


    }

    MediaPlayer player = null;
    String title,artist;
    public void playSong(String fileName)
    {
        try
        {
            System.out.println("MADARCHOD");
            String pureFileName = fileName;
            //fileName = fileName.replace(" ","%20");
            //fileName = fileName.replace("[","%5B");
            ///fileName = fileName.replace("]","%5D");
            System.out.println("xxxx");
            try
            {
                System.out.println("ko");
                Media lol = new Media("file:///home/pi/usb_music/"+fileName);
                System.out.println("gg");
            }
            catch (Exception e)
            {
                System.out.println("sc");
                e.printStackTrace();
            }

            Media pick = new Media(new File("usb_music/"+fileName).toURI().toString());
            System.out.println("yyyy");
            if(player != null)
            {
                player.stop();
            }
            else
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        upButton.setVisible(true);
                        upButton.setDisable(false);
                    }
                });
            }
            System.out.println("MK69");
            player = new MediaPlayer(pick);
            System.out.println("LOLOL");
            player.play();

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    playButton.setVisible(false);
                    playButton.setDisable(true);
                    pauseButton.setVisible(true);
                    pauseButton.setDisable(false);

                    playButton1.setVisible(false);
                    playButton1.setDisable(true);
                    pauseButton1.setVisible(true);
                    pauseButton1.setDisable(false);
                }
            });


            try
            {
                title = pick.getMetadata().get("title").toString();
            }
            catch (Exception e)
            {
                title = pureFileName;
            }

            try
            {
                artist = pick.getMetadata().get("artist").toString();
            }
            catch (Exception e)
            {
                artist = "";
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    songNameLabel.setText(title);
                    artistNameLabel.setText(artist);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    public void pauseSong()
    {
        player.pause();

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        playButton1.setVisible(true);
                        playButton1.setDisable(false);
                        pauseButton1.setVisible(false);
                        pauseButton1.setDisable(true);

                        playButton.setVisible(true);
                        playButton.setDisable(false);
                        pauseButton.setVisible(false);
                        pauseButton.setDisable(true);
                    }
                });
            }
        };

        new Timer(true).schedule(t,100);
    }

    @FXML
    public void playSong()
    {
        player.play();

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        playButton1.setVisible(false);
                        playButton1.setDisable(true);
                        pauseButton1.setVisible(true);
                        pauseButton1.setDisable(false);

                        playButton.setVisible(false);
                        playButton.setDisable(true);
                        pauseButton.setVisible(true);
                        pauseButton.setDisable(false);
                    }
                });
            }
        };

        new Timer(true).schedule(t,100);
    }

    public static class MyFileNameFilter implements FilenameFilter {

        private String ext;

        public MyFileNameFilter(String ext) {
            this.ext = ext.toLowerCase();
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(ext);
        }

    }

    @FXML
    public void loadMusicDetails()
    {
        libraryLabel.setVisible(false);
        songScrollPane.setVisible(false);
        baseControls.setVisible(false);

        new SlideInUp(songDetails).play();
    }

    @FXML
    public void unloadMusicDetails()
    {
        new SlideOutDown(songDetails).play();

        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        libraryLabel.setVisible(true);
                        songScrollPane.setVisible(true);
                        baseControls.setVisible(true);
                    }
                });
            }
        };

        new Timer(true).schedule(t,200);
    }
}
