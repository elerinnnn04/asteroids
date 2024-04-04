package com.example.oopprojekt;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public abstract class Kuju {
    private Polygon kuju;
    private Point2D liikumine;
    private boolean elus = true;

    public Kuju(Polygon monikulmio, int x, int y) {
        this.kuju = monikulmio;
        this.kuju.setTranslateX(x);
        this.kuju.setTranslateY(y);

        this.liikumine = new Point2D(0, 0);
    }

    public Polygon getKuju() {
        return kuju;
    }

    public void keeraVasakule() {
        this.kuju.setRotate(this.kuju.getRotate() - 5);
    }

    public void keeraParemale() {
        this.kuju.setRotate(this.kuju.getRotate() + 5);
    }

    public void liigu() {
        this.kuju.setTranslateX(this.kuju.getTranslateX() + this.liikumine.getX());
        this.kuju.setTranslateY(this.kuju.getTranslateY() + this.liikumine.getY());

        if (this.kuju.getTranslateX() < 0) {
            this.kuju.setTranslateX(this.kuju.getTranslateX() + AsteroidideMäng.LAIUS);
        }

        if (this.kuju.getTranslateX() > AsteroidideMäng.LAIUS) {
            this.kuju.setTranslateX(this.kuju.getTranslateX() % AsteroidideMäng.LAIUS);
        }

        if (this.kuju.getTranslateY() < 0) {
            this.kuju.setTranslateY(this.kuju.getTranslateY() + AsteroidideMäng.KÕRGUS);
        }

        if (this.kuju.getTranslateY() > AsteroidideMäng.KÕRGUS) {
            this.kuju.setTranslateY(this.kuju.getTranslateY() % AsteroidideMäng.KÕRGUS);
        }
    }

    public Point2D getLiikumine() {
        return liikumine;
    }

    public void setLiikumine(Point2D liikumine) {
        this.liikumine = liikumine;
    }

    public boolean onElus() {
        return elus;
    }

    public void setElus(boolean elus) {
        this.elus = elus;
    }

    public void kiirenda() {
        double muutosX = Math.cos(Math.toRadians(this.kuju.getRotate()));
        double muutosY = Math.sin(Math.toRadians(this.kuju.getRotate()));

        muutosX *= 0.05;
        muutosY *= 0.05;

        this.liikumine = this.liikumine.add(muutosX, muutosY);
    }

    public boolean põrkub(Kuju teine) {
        Shape kokkupõrkeala = Shape.intersect(this.kuju, teine.getKuju());
        return kokkupõrkeala.getBoundsInLocal().getWidth() != -1;
    }
}
