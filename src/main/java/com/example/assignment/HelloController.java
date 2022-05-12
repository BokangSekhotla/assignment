package com.example.assignment;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;


public class HelloController implements Initializable{
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    @FXML
    public void playVideo(ActionEvent event){
        mediaPlayer.play();
    }
    @FXML
    public void stopVideo(ActionEvent event){
        mediaPlayer.stop();
    }
    @FXML
    public void pauseVideo(ActionEvent event){
        mediaPlayer.pause();
    }
    @FXML
    public void Exiting(ActionEvent event){
        System.exit(0);
    }
    @FXML
    private Slider slider;
    @FXML
    private Slider seekSlider;
    @FXML
    private Label totaltime;
    @FXML
    private Label labelCurrenttime;

    private boolean atEndofVideo=false;
    private  boolean isPlaying=true;
    private boolean isMuted=true;






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String video=getClass().getResource("spinning_and_drifting.mp4").toExternalForm();
        Media media=new Media(video);
        mediaPlayer=new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.volumeProperty().bindBidirectional(slider.valueProperty());
        bindCurrentTimeLabel();


        seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
                if(atEndofVideo){
                    seekSlider.setValue(0);
                    atEndofVideo=false;
                    isPlaying=false;
                }

            }
        });
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
                seekSlider.setValue(newValue.toSeconds());
            }
        });
        seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
            }
        });
        mediaPlayer.play();
        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(slider.getValue());

            }
        });
        mediaPlayer.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
              seekSlider.setMax(newDuration.toSeconds());
              totaltime.setText(getTime(newDuration));
            }
        });
        seekSlider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isChanging) {
                if(!isChanging){
                    mediaPlayer.seek(Duration.seconds(seekSlider.getValue()));
                }
            }
        });
        seekSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                double currentTime=mediaPlayer.getCurrentTime().toSeconds();
                if(Math.abs(currentTime-newValue.doubleValue())>0.5){
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });
        labelMatchEndVideo(labelCurrenttime.getText(), totaltime.getText());

        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                if(seekSlider.isValueChanging()){
                    seekSlider.setValue(newTime.toSeconds());
                }
                labelMatchEndVideo(labelCurrenttime.getText(), totaltime.getText());
            }
        });









    }
    public void bindCurrentTimeLabel(){
        labelCurrenttime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTime(mediaPlayer.getCurrentTime())+ "/";
            }
        },mediaPlayer.currentTimeProperty()));
    }
    public String getTime(Duration time){
        int hours=(int) time.toHours();
        int minutes= (int) time.toMinutes();
        int seconds=(int) time.toSeconds();
        if(seconds>59) seconds=seconds % 60;
        if(minutes>59) minutes=minutes % 60;
        if(hours>59) hours=hours % 60;

        if(hours>0) return String.format("%d:%02d:%02", hours,minutes,seconds);

        else return String.format("%02d:%02d", minutes,seconds);
    }

    public void labelMatchEndVideo(String labeltime, String labeltotal){
        for (int i=0; i<labeltotal.length();i++){
            if(labeltime.charAt(i)!= labeltotal.charAt(i)){


            atEndofVideo=false;
            break;
        }else{
                atEndofVideo=true;
            }
        }
    }
}
