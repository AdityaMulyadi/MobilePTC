package com.example.mobileptc;

import static com.example.mobileptc.PrediksiCuaca.loadModelFile;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class WeatherPredict extends Service {
    private Interpreter interpreter;

    public WeatherPredict() throws IOException {
        interpreter = new Interpreter(loadModelFile());
    }

    public float[] prediksiCuaca (float[] inputData) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (float value : inputData) {
            stats.addValue(value);
        }
        double min = stats.getMin();
        double max = stats.getMax();

        for (int i = 0; i < inputData.length; i++) {
            inputData[i] = (float) scale(inputData[i], min, max);
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(inputData.length * 4);
        for (float value : inputData) {
            byteBuffer.putFloat(value);
        }

        float[] output = new float[7];
        interpreter.run(byteBuffer, output);

        return output;
    }

    private double scale(double value, double min, double max) {
        return (value - min) / (max - min);
    }

    private MappedByteBuffer loadModelFile () throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd("model_lstm.tflite");
            FileInputStream inputStream = fileDescriptor.createInputStream();
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            Log.e("Home", "Error membuka file model_lstm.tflite", e);
            throw new IOException("Error loading model file", e);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}