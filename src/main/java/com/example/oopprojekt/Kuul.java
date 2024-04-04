package com.example.oopprojekt;

import javafx.scene.shape.Polygon;

public class Kuul extends Kuju {

    public Kuul(int x, int y) {
        super(new Polygon(2, -2, 2, 2, -2, 2, -2, -2), x, y);
    }

}
