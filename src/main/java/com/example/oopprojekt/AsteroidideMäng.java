package com.example.oopprojekt;

import java.io.*;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AsteroidideMäng extends Application {
    public static int LAIUS = 300;
    public static int KÕRGUS = 200;
    private static String KORGEIMAD_TULEMUSED_FAIL = "korgeimadtulemused.txt";
    private String kasutajaNimi;
    private Pane paneel;
    private List<Asteroid> asteroidid;
    private List<Kuul> kuulid;
    private Laev laev;
    private Text tekst;
    private Text mängijaNimiTekst;
    private Text korgeimadTulemusedTekst;
    private AtomicInteger punktid;
    private Button prooviUuestiNupp;
    private AnimationTimer timer;

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

        this.paneel = new Pane();
        paneel.setPrefSize(LAIUS, KÕRGUS);

        this.tekst = new Text(10, 20, "Punktid: 0");
        this.mängijaNimiTekst = new Text(LAIUS - 100, 20, "Mängija: " + kasutajaNimi);
        this.korgeimadTulemusedTekst = new Text(10, 40, "");
        this.punktid = new AtomicInteger();

        this.laev = new Laev(LAIUS / 2, KÕRGUS / 2);
        this.asteroidid = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(LAIUS / 3), rnd.nextInt(KÕRGUS));
            asteroidid.add(asteroid);
        }
        this.kuulid = new ArrayList<>();

        paneel.getChildren().addAll(tekst, mängijaNimiTekst, korgeimadTulemusedTekst);
        paneel.getChildren().add(laev.getKuju());
        asteroidid.forEach(asteroid -> paneel.getChildren().add(asteroid.getKuju()));

        prooviUuestiNupp = new Button("Proovi Uuesti");
        prooviUuestiNupp.setVisible(false);
        prooviUuestiNupp.setOnAction(e -> alustaUuestiMängu());
        paneel.getChildren().add(prooviUuestiNupp);

        Scene stseen = new Scene(paneel);

        Map<KeyCode, Boolean> vajutatudNupud = new HashMap<>();

        stseen.setOnKeyPressed(event -> {
            vajutatudNupud.put(event.getCode(), Boolean.TRUE);
        });

        stseen.setOnKeyReleased(event -> {
            vajutatudNupud.put(event.getCode(), Boolean.FALSE);
        });

        lava.widthProperty().addListener((obs, vana, uus) -> {
            LAIUS = uus.intValue();
            kohandaElemendid();
        });

        lava.heightProperty().addListener((obs, vana, uus) -> {
            KÕRGUS = uus.intValue();
            kohandaElemendid();
        });

        this.timer = new AnimationTimer() {
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
                        gameOver();
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
        };

        timer.start();

        lava.setTitle("Asteroidid!");
        lava.setScene(stseen);
        lava.show();
    }

    private void alustaUuestiMängu() {
        paneel.getChildren().clear();
        paneel.getChildren().addAll(tekst, mängijaNimiTekst, korgeimadTulemusedTekst, prooviUuestiNupp);
        tekst.setText("Punktid: 0");
        punktid.set(0);

        laev = new Laev(LAIUS / 2, KÕRGUS / 2);
        asteroidid = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            Asteroid asteroid = new Asteroid(rnd.nextInt(LAIUS / 3), rnd.nextInt(KÕRGUS));
            asteroidid.add(asteroid);
        }
        kuulid = new ArrayList<>();

        paneel.getChildren().add(laev.getKuju());
        asteroidid.forEach(asteroid -> paneel.getChildren().add(asteroid.getKuju()));

        prooviUuestiNupp.setVisible(false);
        korgeimadTulemusedTekst.setText("");

        timer.start();
    }

    private void gameOver() {
        timer.stop();
        prooviUuestiNupp.setVisible(true);
        prooviUuestiNupp.setLayoutX((LAIUS - prooviUuestiNupp.getWidth()) / 2);
        prooviUuestiNupp.setLayoutY((KÕRGUS - prooviUuestiNupp.getHeight()) / 2);

        salvestaPunktid();

        kuvaKorgeimadTulemused();
    }

    private void kohandaElemendid() {
        paneel.setPrefSize(LAIUS, KÕRGUS);
        mängijaNimiTekst.setX(LAIUS - 100);
        prooviUuestiNupp.setLayoutX((LAIUS - prooviUuestiNupp.getWidth()) / 2);
        prooviUuestiNupp.setLayoutY((KÕRGUS - prooviUuestiNupp.getHeight()) / 2);
    }

    private void salvestaPunktid() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(KORGEIMAD_TULEMUSED_FAIL, true))) {
            writer.write(kasutajaNimi + ": " + punktid.get() + "\n");
        } catch (IOException e) {
            System.out.println("Viga punktide salvestamisel: " + e.getMessage());
        }
    }

    private void kuvaKorgeimadTulemused() {
        StringBuilder korgeimadTulemused = new StringBuilder("Kõrgeimad tulemused:\n");
        try (BufferedReader reader = new BufferedReader(new FileReader(KORGEIMAD_TULEMUSED_FAIL))) {
            String line;
            while ((line = reader.readLine()) != null) {
                korgeimadTulemused.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Viga punktide lugemisel: " + e.getMessage());
        }
        korgeimadTulemusedTekst.setText(korgeimadTulemused.toString());
    }
}
