package com.example.oopprojekt;

import javafx.scene.shape.Polygon;

public class Laev extends Kuju {
    public Laev(int x, int y) {
        super(new Polygon(-5, -5, 10, 0, -5, 5), x, y);
    }
}