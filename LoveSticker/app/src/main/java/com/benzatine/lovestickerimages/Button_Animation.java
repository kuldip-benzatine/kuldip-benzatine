package com.benzatine.lovestickerimages;

/**
 * Created by ADMIN on 12-May-17.
 */

public class Button_Animation implements android.view.animation.Interpolator {
    double mAmplitude = 1;
    double mFrequency = 10;

    Button_Animation(double amplitude, double frequency) {
        mAmplitude = amplitude;
        mFrequency = frequency;
    }

    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}