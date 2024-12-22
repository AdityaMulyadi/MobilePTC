package com.example.mobileptc;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.tensorflow.lite.Interpreter;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;


public class PrediksiCuaca {
    private Interpreter interpreter;

    public PrediksiCuaca (String modelPath) throws Exception {
        interpreter = new Interpreter(loadModelFile(modelPath));
    }

    public float[] predictWeather(float[] inputData) {
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

    static MappedByteBuffer loadModelFile(String path) throws Exception {
        // Load the model file into a ByteBuffer
        Path pathToModel = Paths.get(path);
        FileChannel fileChannel = FileChannel.open(pathToModel);
        MappedByteBuffer mb = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        fileChannel.close();
        return mb;
    }

    private double scale(double value, double min, double max) {
        return (value - min) / (max - min);
    }
}
