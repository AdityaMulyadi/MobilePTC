package com.example.mobileptc;

public class MutableFloat {
    public float value;
    public MutableFloat(float value) {
        this.value = value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }
}
