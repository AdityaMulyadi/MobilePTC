package com.example.mobileptc;

public class DataScaler {
    // Konstanta mean dan std
    private static final float[] SCALER_MEAN = {26.2958278f, 81.2470768f, 30.24774449f, 1.38435756f, 78.25265745f, 9.1647688f, 166.96724688f};
    private static final float[] SCALER_STD = {2.28639095f, 13.47205581f, 2.4287146f, 3.52033765f, 29.44686967f, 5.32398296f, 78.05539713f};

    // Fungsi untuk menormalisasi data
    public static float[][] scaleData2D(float[][] inputData) {
        int numRows = inputData.length;
        int numCols = inputData[0].length;

        // Validasi jumlah kolom
        if (numCols != SCALER_MEAN.length) {
            throw new IllegalArgumentException("Jumlah kolom dalam data input harus sesuai dengan panjang mean dan std.");
        }

        // Membuat array hasil
        float[][] scaledData = new float[numRows][numCols];

        // Normalisasi setiap elemen
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                scaledData[i][j] = (inputData[i][j] - SCALER_MEAN[j]) / SCALER_STD[j];
            }
        }

        return scaledData;
    }
}
