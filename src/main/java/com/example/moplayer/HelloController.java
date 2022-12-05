package com.example.moplayer;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import static javafx.scene.effect.Light.*;


public class HelloController implements Initializable {

    //define variables
    @FXML
    private Button play,stop,puse,forWard,backWord,openVideo,mute,speed;
    @FXML
    Slider timeSlider,voiceSlider;
    @FXML
    VBox parent;
    @FXML
    HBox timeHBox,buttonHBox,voiceHBox;

    @FXML
    private Label currentTimeLabel,totalTime,voiceIconLabel,fullScrenn;
    @FXML
    private MediaView myMediaView;
    private MediaPlayer mediaPlayer;
    private Media media;
    private FileChooser fileChooser;
    private File file;
    private Stage stage;
    private    boolean state = true;
    private Duration duration = null;

    //define boolean variable that will be used in the program to deal with functionality of the program.
    private boolean atEndofVideo=false;
    private boolean videoPlaying=true;
    private boolean isMute=true;

    // Use the images&icons in your program to give more visualization
    private ImageView iPlay;
    private ImageView iPause;
    private ImageView iRestart;
    private ImageView iVolume;
    private ImageView iFullScreen;
    private ImageView iMute;
    private ImageView iExit;
    private ActionEvent e;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // initialize all the node you nned such as the images of the button with the properties
//            Button.setGraphic(ImageView); in this way you can add picture to your button.
        Image voiceIcon=new Image("speaker.png");
        iVolume=new ImageView(voiceIcon);
        iVolume.setFitHeight(25);
        iVolume.setFitWidth(25);
        voiceIconLabel.setGraphic(iVolume);

//        openV(e);
//        file=new File("C:\\Users\\moham\\Downloads\\r.mp4");
//        file=new File("basic.mkv");
//        media=new Media(file.toURI().toString());
//        mediaPlayer=new MediaPlayer(media);
//        myMediaView.setMediaPlayer(mediaPlayer);//to view the video




        //bind the both prop volume of the media player and the value of the voiceSlider.
//            mediaPlayer.volumeProperty().bindBidirectional(voiceSlider.valueProperty());

        while (mediaPlayer!=null) {
            bindCurrentTime();
            voiceBinding();
        }


        //Work??
        //Change the dimension of the  video when we change the size of window then the scene will change
        parent.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                if (oldScene == null && newScene != null) {
                    //??? logics more study with properties
                    myMediaView.fitHeightProperty().bind(newScene.heightProperty().subtract(
                            timeHBox.heightProperty().add(buttonHBox.heightProperty()).add(voiceHBox.heightProperty()).add(30)));
//                        myMediaView.fitWidthProperty().bind(newScene.widthProperty().subtract(20));
                }
            }
        });

        //To mechanise to deal with full screen property
        // add onclick listener  on the label to handle with the event
        //explain points
        /***
         *
         */
        fullScrenn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Label label = (Label) mouseEvent.getSource();
                Stage stage1 = (Stage) label.getScene().getWindow();
                if (stage1.isFullScreen()) {
                    stage1.setFullScreen(false);
                    fullScrenn.setText("FullScreen");
                } else {
                    stage1.setFullScreen(true);
                    fullScrenn.setText("Exit");
                }
                /*Here we are dealing with ESC key where in case we pressed on this key we will be back to the normal screen
                But also we need set the label to the previous state
                 */
                stage1.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                        if (keyEvent.getCode() == KeyCode.ESCAPE) { //if the pressed key == to KeyCode.ESCAPE we should execute the code
                            fullScrenn.setText("FullScreen");
                        }
                    }
                });
            }
        });
        if(mediaPlayer!=null) {
            //--------------------
        /*
        Deal with time slider of the video
         */
            //Add a listener to the total duration property of the mediaPlayer to handle any change.
            mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                    //set the max value of the time slider
                    timeSlider.setMax(newDuration.toSeconds());
                    // newDuration represent the duration of the new video, whereas the function getTime(Duration) will calculate the full duration in second.
                    totalTime.setText(getTime(newDuration));


                }
            });
        }//end if



        //Add a listener to the valueChangingProperty of the timeSlider to handle any change in the value of the slider.
        timeSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
                /*while the state of the change is true so the user is changing the valur of the slider
                when it is stopped changed "new[second] state of the method chang"=false then we should get the value of the slider and assigned it to the
                mediaPlayer duration
                 */
                if (!isChanging) {//mean if the changing stopped
                    //assigned the new value of the time slider to the video
                    mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
                }

            }
        });

        //Add a listener to the valueProperty of the timeSlider to handle any change in the value of the slider.
        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldV, Number newV) {
                //if the new value of the timeSlider bigger than the current time of the mediaPlayer then we should assign the value of the MediaPlayer to the new value.
                //that eqaul to the time slider value
                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                if (Math.abs(currentTime - newV.doubleValue()) > 0.5) {
                    mediaPlayer.seek(Duration.seconds(newV.doubleValue()));
                }
                //Function to check if the user goes to the end of the video
//                labelMatchEndVideo(currentTimeLabel.getText(),totalTime.getText()); //not use this idea for our work now
            }
        });

        /*
        Add listener to the mediaplayer for the currentTime property for “check if the value of the time slider is changing,
         if not we should set the timer to be the new time in seconds”
            +++ we are mentoring the current time value of the video and build our logic in this listener on it
            +++ Whereas if the timeSlider value is not changing we will assign the current time value of the video to the Time slider
         */
        if(mediaPlayer!=null) {
            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                    if (!timeSlider.isValueChanging()) {
                        timeSlider.setValue(newTime.toSeconds());
                    }
//                labelMatchEndVideo(currentTimeLabel.getText(),totalTime.getText()); //not use this idea for our work now
                }
            });
        }

    }//end of initialize method

    /***
     * Bind the current time of the video to current time Label
     * bind text prop of current time Label to the current time prop of the media player
     */
    public void bindCurrentTime(){

        // bind the textProperty() of currentTimeLable with currentTimeProperty() of media player
        currentTimeLabel.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {

                return getTime(mediaPlayer.getCurrentTime()) + " / ";
            }
        }, mediaPlayer.currentTimeProperty()));
    }



    //getTime Method

    /***
     *
     * @param duration
     * @return String represent the formatted time of the media "video"
     */
    public String getTime(Duration duration){
        int hours=(int)duration.toHours();
        int minutes=(int) duration.toMinutes();
        int seconds=(int) duration.toSeconds();

        if (seconds>59) seconds=seconds%60;
        if (minutes>59) minutes=minutes % 60;
        if (hours>59) hours=hours % 60;

        if (hours>0) return String.format("%d:%02d:%02d",
                hours,
                minutes,
                seconds);
        else return String.format("%02d:%02d",
                minutes,seconds);
//        else return String.format("%02d",
//                    seconds);


    }

    //Function to check if the user goes to the end of the video
    public void  labelMatchEndVideo(String cureentTime, String totlaTime){
        //this maybe will not use in our work for now
        for (int i=0;i<totlaTime.length();i++){
            if (cureentTime.charAt(i)!=totlaTime.charAt(i)){
                //51:00
            }
//                atEndofVideo=false;
//                if(isPlainf) .....
//                else...
//                break....}
//            else{
//                atEndofVideo=true;
//                butt....
//            }
        }



    }


    public void voiceSlider() {
        voiceSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (voiceSlider.isValueChanging()) {
                    mediaPlayer.setVolume(voiceSlider.getValue());
//                int value=Integer.parseInt(String.valueOf(voiceSlider.getValue()));
//                volume.setText(String.valueOf(Integer.parseInt(String.valueOf(voiceSlider.getValue()))));
                }
            }
        });

    }

    public void voiceBinding(){
        mediaPlayer.volumeProperty().bindBidirectional(voiceSlider.valueProperty());
    }


    public void openV(ActionEvent e){
        FileChooser fileChooser=new FileChooser();
        FileChooser.ExtensionFilter ex=new FileChooser.ExtensionFilter("mp4-video","*.mp4"); //Add as you need
        FileChooser.ExtensionFilter ex1=new FileChooser.ExtensionFilter("3jp-video","*.3jp"); //Add as you need
        FileChooser.ExtensionFilter ex2=new FileChooser.ExtensionFilter("mkv-video","*.mkv"); //Add as you need
        fileChooser.setTitle("Choice your video");
        fileChooser.getExtensionFilters().addAll(ex,ex1,ex2);
        fileChooser.setInitialDirectory(new File("D:/"));
        file=fileChooser.showOpenDialog(stage); // Last sentence code
        media=new Media(file.toURI().toString());
        mediaPlayer=new MediaPlayer(media);
        myMediaView.setMediaPlayer(mediaPlayer);//to view the video
        mediaPlayer.volumeProperty().bindBidirectional(voiceSlider.valueProperty());//move here to work when choice a video

//        Image mm=new Image(file.toURI().toString());
//        myImgView.setImage(mm);
//        myMediaView.setFitHeight(400);
//        myMediaView.setFitWidth(500);

    }
    public void play(){
        mediaPlayer.play();
    }
    public void stop(){
        //reset Part
//if(mediaPlayer.getStatus()!= MediaPlayer.Status.READY){
//    mediaPlayer.seek(Duration.seconds(0.0));}
        mediaPlayer.stop();

    }
    public void pause(){
        mediaPlayer.pause();
    }
    public void forWord(){
        double v= mediaPlayer.getCurrentTime().toSeconds()+10;
        mediaPlayer.seek(Duration.seconds(v));
    }
    public void backWord(){
        double v= mediaPlayer.getCurrentTime().toSeconds()-10;
        mediaPlayer.seek(Duration.seconds(v));
    }
    public void mute() {
        state=!state;
        if (!state) { //simplifier
            mediaPlayer.setMute(true);
        } else mediaPlayer.setMute(false);
//        mediaPlayer.setVolume();



    }
    public void takeShoot() throws IOException {

        FileChooser fileC=new FileChooser();
        FileChooser.ExtensionFilter ex = new FileChooser.ExtensionFilter("jpg_Image", "*.jpg"); //Add as you need
        FileChooser.ExtensionFilter exx = new FileChooser.ExtensionFilter("Png", "*.png"); //Add as you need
        fileC.getExtensionFilters().addAll(ex, exx);
        fileC.setInitialDirectory(new File("C:/"));

        File imageFile= fileC.showSaveDialog(null);


//        myMediaView.snapshot(null,null,null);
        ImageIO.write(SwingFXUtils.fromFXImage(myMediaView.snapshot(null,null),null), "png", imageFile);

    }

    public void speed(){
        String title=speed.getText();
        if(title.equals("X1")){
            speed.setText("X2");
            mediaPlayer.setRate(2);
        }else{
            speed.setText("X1");
            mediaPlayer.setRate(1);
        }

    }


    //Effects------------------
    private boolean lightState=false;
    public void lightEffect(){
        if (lightState == false) {


            lightState=true;
            //instantiating the Light.Point class
            Light.Point light = new Light.Point();

            //Setting the color of the light
            light.setColor(Color.YELLOW);

            //Setting the position of the light
            light.setX(170);
            light.setY(155);
            light.setZ(145);

            //Instantiating the Lighting class
            Lighting lighting = new Lighting();

            //Setting the light
            lighting.setLight(light);
            myMediaView.setEffect(lighting);
        }else{
            lightState=false;
            myMediaView.setEffect(null);}
    }


//    public void convert(){
//        Media conertV= myMediaView.getMediaPlayer().getMedia();
//        FileChooser fileChooser=new FileChooser();
//        FileChooser.ExtensionFilter ex=new FileChooser.ExtensionFilter("mp4-video","*.mp4"); //Add as you need
//        FileChooser.ExtensionFilter ex1=new FileChooser.ExtensionFilter("3jp-video","*.3jp"); //Add as you need
//        FileChooser.ExtensionFilter ex2=new FileChooser.ExtensionFilter("mkv-video","*.mkv"); //Add as you need
//        fileChooser.setTitle("Choice your video type");
//        fileChooser.getExtensionFilters().addAll(ex,ex1,ex2);
//        fileChooser.setInitialDirectory(new File("D:/"));
//
//        File imageFile= fileChooser.showSaveDialog(stage);
//
//
//
//    }



}//end of controller