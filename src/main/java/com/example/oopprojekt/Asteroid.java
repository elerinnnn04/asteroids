package com.example.oopprojekt;

import java.util.Random;

public class Asteroid extends Kuju {

    private double pöörlemisliikumine;

    public Asteroid(int x, int y) {
        super(new MitmekulgneTehas().looMitmekulgne(), x, y);

        Random rnd = new Random();

        super.getKuju().setRotate(rnd.nextInt(360));

        int kiirendusteArv = 1 + rnd.nextInt(10);
        for (int i = 0; i < kiirendusteArv; i++) {
            kiirenda();
        }

        this.pöörlemisliikumine = 0.5 - rnd.nextDouble();
    }

    @Override
    public void liigu() {
        super.liigu();
        super.getKuju().setRotate(super.getKuju().getRotate() + pöörlemisliikumine);
    }
}
