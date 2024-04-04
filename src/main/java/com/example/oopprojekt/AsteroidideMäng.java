package com.example.oopprojekt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AsteroidideMäng extends Application {

    public static int LAIUS = 300;
    public static int KÕRGUS = 200;

    private String kasutajaNimi;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage lava) throws Exception {
        TextInputDialog dialoog = new TextInputDialog("Mängija");
        dialoog.setTitle("Tere tulemast Asteroidide mängu!");
        dialoog.setHeaderText("Mängija info");
        dialoog.setContentText("Palun sisesta oma nimi:");

        dialoog.showAndWait().ifPresent(nimi -> kasutajaNimi = nimi);

        Pane paneel = new Pane();
        paneel.setPrefSize(LAIUS, KÕRGUS);

        Text tekst = new Text(10, 20, "Punktid: 0");
        Text mängijaNimiTekst = new Text(LAIUS - 100, 20, "Mängija: " + kasutajaNimi);
        AtomicInteger punktid = new AtomicInteger();


        Laev laev = new Laev(LAIUS / 2, KÕRGUS / 2);
        List<Asteroid> asteroidid = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(LAIUS / 3), rnd.nextInt(KÕRGUS));
            asteroidid.add(asteroid);
        }
        List<Kuul> kuulid = new ArrayList<>();

        paneel.getChildren().addAll(tekst, mängijaNimiTekst);
        paneel.getChildren().add(laev.getKuju());
        asteroidid.forEach(asteroid -> paneel.getChildren().add(asteroid.getKuju()));

        Scene stseen = new Scene(paneel);

        Map<KeyCode, Boolean> vajutatudNupud = new HashMap<>();

        stseen.setOnKeyPressed(event -> {
            vajutatudNupud.put(event.getCode(), Boolean.TRUE);
        });

        stseen.setOnKeyReleased(event -> {
            vajutatudNupud.put(event.getCode(), Boolean.FALSE);
        });

        new AnimationTimer() {
            @Override
            public void handle(long praeguHetk) {
                if (vajutatudNupud.getOrDefault(KeyCode.LEFT, false)) {
                    laev.keeraVasakule();
                }

                if (vajutatudNupud.getOrDefault(KeyCode.RIGHT, false)) {
                    laev.keeraParemale();
                }

                if (vajutatudNupud.getOrDefault(KeyCode.UP, false)) {
                    laev.kiirenda();
                }

                if (vajutatudNupud.getOrDefault(KeyCode.SPACE, false) && kuulid.size() < 7) {
                    // Tulistamine
                    Kuul kuul = new Kuul((int) laev.getKuju().getTranslateX(), (int) laev.getKuju().getTranslateY());
                    kuul.getKuju().setRotate(laev.getKuju().getRotate());
                    kuulid.add(kuul);

                    kuul.kiirenda();
                    kuul.setLiikumine(kuul.getLiikumine().normalize().multiply(3));

                    paneel.getChildren().add(kuul.getKuju());
                }

                laev.liigu();
                asteroidid.forEach(asteroid -> asteroid.liigu());
                kuulid.forEach(kuul -> kuul.liigu());

                asteroidid.forEach(asteroid -> {
                    if (laev.põrkub(asteroid)) {
                        stop();
                    }
                });

                kuulid.forEach(kuul -> {
                    asteroidid.forEach(asteroid -> {
                        if (kuul.põrkub(asteroid)) {
                            kuul.setElus(false);
                            asteroid.setElus(false);
                        }
                    });

                    if(!kuul.onElus()) {
                        tekst.setText("Punktid: " + punktid.addAndGet(1000));
                    }
                });

                kuulid.stream()
                        .filter(kuul -> !kuul.onElus())
                        .forEach(kuul -> paneel.getChildren().remove(kuul.getKuju()));
                kuulid.removeAll(kuulid.stream()
                        .filter(kuul -> !kuul.onElus())
                        .collect(Collectors.toList()));

                asteroidid.stream()
                        .filter(asteroid -> !asteroid.onElus())
                        .forEach(asteroid -> paneel.getChildren().remove(asteroid.getKuju()));
                asteroidid.removeAll(asteroidid.stream()
                        .filter(asteroid -> !asteroid.onElus())
                        .collect(Collectors.toList()));

                if(Math.random() < 0.005) {
                    Asteroid uusAsteroid = new Asteroid(LAIUS, KÕRGUS);
                    if(!uusAsteroid.põrkub(laev)) {
                        asteroidid.add(uusAsteroid);
                        paneel.getChildren().add(uusAsteroid.getKuju());
                    }
                }

            }
        }.start();

        lava.setTitle("Asteroidid!");
        lava.setScene(stseen);
        lava.show();
    }
}
