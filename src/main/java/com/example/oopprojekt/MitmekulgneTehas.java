package com.example.oopprojekt;

import java.util.Random;
import javafx.scene.shape.Polygon;

public class MitmekulgneTehas {

    public Polygon looMitmekulgne() {
        Random rnd = new Random();

        double suurus = 10 + rnd.nextInt(10);

        Polygon mitmekulgne = new Polygon();
        double c1 = Math.cos(Math.PI * 2 / 5);
        double c2 = Math.cos(Math.PI / 5);
        double s1 = Math.sin(Math.PI * 2 / 5);
        double s2 = Math.sin(Math.PI * 4 / 5);

        mitmekulgne.getPoints().addAll(
                suurus, 0.0,
                suurus * c1, -1 * suurus * s1,
                -1 * suurus * c2, -1 * suurus * s2,
                -1 * suurus * c2, suurus * s2,
                suurus * c1, suurus * s1);

        for (int i = 0; i < mitmekulgne.getPoints().size(); i++) {
            int muutus = rnd.nextInt(5) - 2;
            mitmekulgne.getPoints().set(i, mitmekulgne.getPoints().get(i) + muutus);
        }

        return mitmekulgne;
    }
}
