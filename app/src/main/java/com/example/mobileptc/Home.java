package com.example.mobileptc;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.example.mobileptc.ml.ModelLstm40SudahDariPb;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.annotations.SerializedName;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.tensorflow.lite.Interpreter;


public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView cuaca1, cuaca2, cuaca3;
        TextView suhu, txtkelembapan, txtkecAngin, curah, tanggal, cc, arahAngin, tglKalender, txtAlamat, txtRain, txtAppTemp;
        txtAlamat = findViewById(R.id.textView2);
        suhu = findViewById(R.id.textView4);
        txtkelembapan = findViewById(R.id.textView12);
        txtkecAngin = findViewById(R.id.textView13);
        curah = findViewById(R.id.textViewd);
        cuaca1 = findViewById(R.id.imageView4);
        cuaca2 = findViewById(R.id.imageView5);
        cuaca3 = findViewById(R.id.imageView6);
        tanggal = findViewById(R.id.textView3);
        cc = findViewById(R.id.textViewf);
        arahAngin = findViewById(R.id.textViewh);
        tglKalender = findViewById(R.id.textView5);
        txtRain = findViewById(R.id.textViewAppTemp);
        txtAppTemp = findViewById(R.id.textViewRain);

        tanggal.setText(WaktuParepare("non"));
        tglKalender.setText(WaktuParepare("simpel"));
        tanggal();


        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.open-meteo.com/").addConverterFactory(GsonConverterFactory.create()).build();
        OpenMeteoService service = retrofit.create(OpenMeteoService.class);
        Call<OpenMeteoResponse> call = service.getWeatherData();

        final float[] currentTemp = new float[1];
        float[] lembap = new float[1];
        float[] kecepatanAngin = new float[1];
        float[] curahHujan = new float[1];
        float[] nilaiCC = new float[1];
        float[] arahA = new float[1];
        float[] apparentTemp = new float[1];
        float[] rain = new float[1];

//        MutableFloat data1 = new MutableFloat(lembap[0]);
        MutableFloat data2 = new MutableFloat(kecepatanAngin[0]);
//        MutableFloat data3 = new MutableFloat(curahHujan[0]);
        MutableFloat data4 = new MutableFloat(nilaiCC[0]);
        MutableFloat data5 = new MutableFloat(arahA[0]);
        MutableFloat data6 = new MutableFloat(apparentTemp[0]);
        MutableFloat data7 = new MutableFloat(rain[0]);

        call.enqueue(new Callback<OpenMeteoResponse>() {
            @Override
            public void onResponse(Call<OpenMeteoResponse> call, Response<OpenMeteoResponse> response) {
                if (response.isSuccessful()) {
                    OpenMeteoResponse weatherData = response.body();
                    assert weatherData != null;
                    currentTemp[0] = (float) weatherData.currentWeather.temperature;
                    String strTemp = currentTemp[0] + "°C";
                    lembap[0] = (float) weatherData.currentWeather.kelembapan;
                    String kelembapan = lembap[0] + "%";
                    kecepatanAngin[0] = (float) weatherData.currentWeather.kecAngin;
                    String kecAngin = kecepatanAngin[0] + "km/h";
                    curahHujan[0] = (float) weatherData.currentWeather.curah;
                    String strCurahHujan = curahHujan[0] + "mm";
                    nilaiCC[0] = (float) weatherData.currentWeather.cloud_cover;
                    String strCC = nilaiCC[0] + "%";
                    arahA[0] = (float) weatherData.currentWeather.arah_angin;
                    String strArahA = arahA[0] + "°";
                    apparentTemp[0] = (float) weatherData.currentWeather.appTemp;
                    rain[0] = (float) weatherData.currentWeather.rain;

//                    data1.setValue(lembap[0]);
                    data2.setValue(kecepatanAngin[0]);
//                    data3.setValue(curahHujan[0]);
                    data4.setValue(nilaiCC[0]);
                    data5.setValue(arahA[0]);
                    data6.setValue(apparentTemp[0]);
                    data7.setValue(rain[0]);

                    arahAngin.setText(strArahA);
                    cc.setText(strCC);
                    txtkelembapan.setText(kelembapan);
                    txtkecAngin.setText(kecAngin);
                    curah.setText(strCurahHujan);
                    txtRain.setText(rain[0] + "mm");
                    txtAppTemp.setText(apparentTemp[0] + "°C");

                    int kodeCuaca = weatherData.currentWeather.kode_cuaca;
                    kondisiCuaca(kodeCuaca);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DATA_JEMURAN");
                    Query query = ref.orderByKey().endAt("2024-12-31_23:59:59").limitToLast(1);
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            if (snapshot.exists()) {
                                float suhu, hum;
                                suhu = snapshot.child("Suhu").getValue(Float.class);
                                hum = snapshot.child("Kelembaban").getValue(Float.class);


                                try {
                                    float[][] hasilPrediksi = new float[12][];
                                    int[] hasilKlasifikasi = new int[12];
                                    for (int i = 0; i < 12; i++) {
                                        float[] data = {suhu, hum, data6.getValue(), data7.getValue(), data4.getValue(), data2.getValue(), data5.getValue()};

                                        float[][] prediksi = prediksiCuaca(data);
                                        hasilPrediksi[i] = prediksi[0];

                                        int klasifikasi = klasifikasiCuaca(DataScaler.scaleData2D(prediksi));
                                        hasilKlasifikasi[i] = klasifikasi;

                                        Log.d("Prediksi Iterasi " + i, "Prediksi: " + Arrays.toString(prediksi[0]) + ", Klasifikasi: " + klasifikasi);

//                                        suhu = hasilPrediksi[i][0];
//                                        hum = hasilPrediksi[i][1];
//                                        data6.setValue(hasilPrediksi[i][2]);
//                                        data7.setValue(hasilPrediksi[i][3]);
//                                        data4.setValue(hasilPrediksi[i][4]);
//                                        data2.setValue(hasilPrediksi[i][5]);
//                                        data5.setValue(hasilPrediksi[i][6]);

                                        suhu = prediksi[0][0];
                                        hum = prediksi[0][1];
                                        data6.setValue(prediksi[0][2]);
                                        data7.setValue(prediksi[0][3]);
                                        data4.setValue(prediksi[0][4]);
                                        data2.setValue(prediksi[0][5]);
                                        data5.setValue(prediksi[0][6]);

                                    }

                                    TableRow besok, besokLusa;
                                    Intent intent = getIntent();
                                    String nama, alamat, email, pass;
                                    nama = intent.getStringExtra("nama");
                                    alamat = intent.getStringExtra("alamat");
                                    email = intent.getStringExtra("email");
                                    pass = intent.getStringExtra("pass");
                                    besok = findViewById(R.id.rowBesok);
                                    besokLusa = findViewById(R.id.rowBesokLusa);

                                    float finalSuhu = hasilPrediksi[0][0];
                                    float finalHum = hasilPrediksi[0][1];
                                    float finalappTemp = hasilPrediksi[0][2];
                                    float finalRain = hasilPrediksi[0][3];
                                    float finalCC = hasilPrediksi[0][4];
                                    float finalWS = hasilPrediksi[0][5];
                                    float finalWD = hasilPrediksi[0][6];
                                    int finalWC = hasilKlasifikasi[0];

                                    besok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(Home.this, Besok.class);
                                            intent.putExtra("suhu", finalSuhu);
                                            intent.putExtra("humidity", finalHum);
                                            intent.putExtra("apparentTemp", finalappTemp);
                                            intent.putExtra("rain", finalRain);
                                            intent.putExtra("CC", finalCC);
                                            intent.putExtra("WS", finalWS);
                                            intent.putExtra("WD", finalWD);
                                            intent.putExtra("nama", nama);
                                            intent.putExtra("alamat", alamat);
                                            intent.putExtra("email", email);
                                            intent.putExtra("pass", pass);
                                            intent.putExtra("WC", finalWC);
                                            startActivity(intent);
                                        }
                                    });

                                    float finalSuhu2 = hasilPrediksi[0][0];
                                    float finalHum2 = hasilPrediksi[0][1];
                                    float finalappTemp2 = hasilPrediksi[0][2];
                                    float finalRain2 = hasilPrediksi[0][3];
                                    float finalCC2 = hasilPrediksi[0][4];
                                    float finalWS2 = hasilPrediksi[0][5];
                                    float finalWD2 = hasilPrediksi[0][6];
                                    int finalWC2 = hasilKlasifikasi[0];

                                    besokLusa.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(Home.this, BesokLusa.class);
                                            intent.putExtra("suhu", finalSuhu2);
                                            intent.putExtra("humidity", finalHum2);
                                            intent.putExtra("apparentTemp", finalappTemp2);
                                            intent.putExtra("rain", finalRain2);
                                            intent.putExtra("CC", finalCC2);
                                            intent.putExtra("WS", finalWS2);
                                            intent.putExtra("WD", finalWD2);
                                            intent.putExtra("nama", nama);
                                            intent.putExtra("alamat", alamat);
                                            intent.putExtra("email", email);
                                            intent.putExtra("pass", pass);
                                            intent.putExtra("WC", finalWC2);
                                            startActivity(intent);
                                        }
                                    });

                                    kondisiCuaca2(hasilKlasifikasi[0]);
                                    kondisiCuaca3(hasilKlasifikasi[1]);

                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }

            @Override
            public void onFailure(Call<OpenMeteoResponse> call, Throwable t) {
                Log.e("NetworkError", "Network Error: " + t.getMessage());
                Toast.makeText(Home.this, "Failed to fetch weather data. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DATA_JEMURAN");
        Query query = ref.orderByKey().endAt("2024-12-31_23:59:59").limitToLast(1);
        TextView txtLux, dataStatus, txtHum;
        ImageView ikonCuaca;
        ikonCuaca = findViewById(R.id.ikonHujan);
        txtLux = findViewById(R.id.dataIntensitasCahaya);
        dataStatus = findViewById(R.id.dataKondisiIot);
        txtHum = findViewById(R.id.dataIoTHumidity);
        final String[] status = new String[1];
        final String[] dataLux = new String[1];
        float[] temp = new float[1];
        float[] hum = new float[1];
        final Integer[] lux1 = new Integer[1];
        final Integer[] lux2 = new Integer[1];
        final Boolean[] cuaca = new Boolean[1];

        MutableFloat data8 = new MutableFloat(temp[0]);
        MutableFloat data9 = new MutableFloat(hum[0]);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.exists()) {
                    lux1[0] = snapshot.child("Cahaya1").getValue(Integer.class);
                    lux2[0] = snapshot.child("Cahaya2").getValue(Integer.class);
                    status[0] = snapshot.child("Status_Jemuran").getValue(String.class);
                    cuaca[0] = snapshot.child("Hujan").getValue(Boolean.class);
                    temp[0] = snapshot.child("Suhu").getValue(Float.class);
                    hum[0] = snapshot.child("Kelembaban").getValue(Float.class);

                    data8.setValue(temp[0]);
                    data9.setValue(hum[0]);

                    Log.d("FirebaseData", "Temperature: " + temp[0] + ", Humidity: " + hum[0]);

                    if (temp[0] > 0) {
                        suhu.setText(temp[0] + "°C");

                    } else {
                        suhu.setText("N/A");
                    }

                    if (hum[0] > 0) {
                        txtHum.setText(hum[0] + "%");
                    } else {
                        txtHum.setText("N/A");
                    }


                    if (lux1[0] != null && lux2[0] != null) {
                        if (lux1[0] <= 3000 && lux2[0] <= 3000) {
                            dataLux[0] = "Terang";
                            txtLux.setText(dataLux[0]);
                        } else {
                            dataLux[0] = "Gelap";
                            txtLux.setText(dataLux[0]);
                        }

                    } else {
                        dataLux[0] = "N/A";
                        txtLux.setText(dataLux[0]);

                    }

                    if (status[0] != null) {
                        dataStatus.setText(status[0]);
                    } else {
                        dataStatus.setText("N/A");
                    }

                    if (cuaca[0] != null) {
                        if (cuaca[0]) {
                            ikonCuaca.setImageResource(R.drawable.rainyday);
                        } else {
                            ikonCuaca.setImageResource(R.drawable.sun);
                        }
                    } else {
                        ikonCuaca.setImageResource(R.drawable.fog);
                    }
                } else {
                    dataLux[0] = "N/A";
                    dataStatus.setText("N/A");
                    ikonCuaca.setImageResource(R.drawable.fog);
                    txtLux.setText(dataLux[0]);
                    suhu.setText("N/A");
                    txtHum.setText("N/A");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent = getIntent();
        String nama, alamat, email, pass;
        nama = intent.getStringExtra("nama");
        alamat = intent.getStringExtra("alamat");
        email = intent.getStringExtra("email");
        pass = intent.getStringExtra("pass");
        txtAlamat.setText(alamat);
        ImageView prf = findViewById(R.id.profile);

        prf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Home.this, Profile.class);
                intent1.putExtra("nama", nama);
                intent1.putExtra("alamat", alamat);
                intent1.putExtra("email", email);
                intent1.putExtra("pass", pass);
                startActivity(intent1);
            }
        });

        controlIoT();



    }

    public float[][] prediksiCuaca (float[] inputData) throws IOException {
        Interpreter interpreter = new Interpreter(loadModelFile("weather_prediction(2.0).tflite"));
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (float value : inputData) {
            stats.addValue(value);
        }
        double min = stats.getMin();
        double max = stats.getMax();

        for (int i = 0; i < inputData.length; i++) {
            inputData[i] = (float) scale(inputData[i], min, max);
        }

//        byte[] paddedData = new byte[336];
//        for (int i = 0; i < inputData.length; i++) {
//            // Konversi nilai float (0.0 - 1.0) ke byte (0 - 255)
//            paddedData[i] = (byte) inputData[i];
//        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(inputData.length * 4);
//        for (float value : inputData) {
//            byteBuffer.putFloat(value);
//        }
//        byteBuffer.order(ByteOrder.nativeOrder());
        for (float value : inputData) {
            byteBuffer.putFloat(value);
        }
//        byteBuffer.put(paddedData);

        float[][] output = new float[1][7];
        interpreter.run(byteBuffer, output);

        return reverseScaleOutput(output, min, max);
    }

    public int klasifikasiCuaca(float[][] inputData) throws IOException {
        Interpreter interpreter = new Interpreter(loadModelFile("weather_classification.tflite"));

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(inputData[0].length * 4);
//        byteBuffer.order(ByteOrder.nativeOrder());
        for (float value : inputData[0]) {
            byteBuffer.putFloat(value);
        }

        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(4 * 66);
        outputBuffer.order(ByteOrder.nativeOrder());


        interpreter.run(byteBuffer, outputBuffer);


        outputBuffer.rewind();
        float[] probabilities = new float[66];
        outputBuffer.asFloatBuffer().get(probabilities);


        int predictedClass = -1;
        float maxProbability = Float.MIN_VALUE;
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > maxProbability) {
                maxProbability = probabilities[i];
                predictedClass = i;
            }
        }

        return predictedClass;
    }

    private MappedByteBuffer loadModelFile (String path) throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = getAssets().openFd(path);
            FileInputStream inputStream = fileDescriptor.createInputStream();
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            Log.e("Home", "Error membuka file " + path, e);
            throw new IOException("Error loading model file", e);
        }
    }

    private float reverseScale(float value, double min, double max) {
        return (float) (value * (max - min) + min);
    }

    public float[][] reverseScaleOutput(float[][] scaledOutput, double min, double max) {
        int rows = scaledOutput.length;
        int cols = scaledOutput[0].length;
        float[][] reversedOutput = new float[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                reversedOutput[i][j] = reverseScale(scaledOutput[i][j], min, max);
            }
        }
        return reversedOutput;
    }

    void kondisiCuaca(int kodeCuaca) {
        ImageView cuaca1 = findViewById(R.id.imageView4);
        if (kodeCuaca == 0) {
            cuaca1.setImageResource(R.drawable.sun);
        } else if (kodeCuaca <= 3) {
            cuaca1.setImageResource(R.drawable.cloudy);
        } else if (kodeCuaca == 45 || kodeCuaca == 48) {
            cuaca1.setImageResource(R.drawable.fog);
        } else if (kodeCuaca >= 51 && kodeCuaca <= 55) {
            cuaca1.setImageResource(R.drawable.drizzle);
        } else if (kodeCuaca >= 61 && kodeCuaca <= 65) {
            cuaca1.setImageResource(R.drawable.rainyday);
        } else if (kodeCuaca >= 80 && kodeCuaca <= 83) {
            cuaca1.setImageResource(R.drawable.shower);
        } else if (kodeCuaca >= 95 && kodeCuaca <= 99) {
            cuaca1.setImageResource(R.drawable.thunderstorm);
        }
    }

    void kondisiCuaca2(int kodeCuaca) {
        ImageView cuaca1 = findViewById(R.id.imageView5);
        if (kodeCuaca == 0) {
            cuaca1.setImageResource(R.drawable.sun);
        } else if (kodeCuaca <= 3) {
            cuaca1.setImageResource(R.drawable.cloudy);
        } else if (kodeCuaca == 45 || kodeCuaca == 48) {
            cuaca1.setImageResource(R.drawable.fog);
        } else if (kodeCuaca >= 51 && kodeCuaca <= 55) {
            cuaca1.setImageResource(R.drawable.drizzle);
        } else if (kodeCuaca >= 61 && kodeCuaca <= 65) {
            cuaca1.setImageResource(R.drawable.rainyday);
        } else if (kodeCuaca >= 80 && kodeCuaca <= 83) {
            cuaca1.setImageResource(R.drawable.shower);
        } else if (kodeCuaca >= 95 && kodeCuaca <= 99) {
            cuaca1.setImageResource(R.drawable.thunderstorm);
        }
    }

    void kondisiCuaca3(int kodeCuaca) {
        ImageView cuaca1 = findViewById(R.id.imageView6);
        if (kodeCuaca == 0) {
            cuaca1.setImageResource(R.drawable.sun);
        } else if (kodeCuaca <= 3) {
            cuaca1.setImageResource(R.drawable.cloudy);
        } else if (kodeCuaca == 45 || kodeCuaca == 48) {
            cuaca1.setImageResource(R.drawable.fog);
        } else if (kodeCuaca >= 51 && kodeCuaca <= 55) {
            cuaca1.setImageResource(R.drawable.drizzle);
        } else if (kodeCuaca >= 61 && kodeCuaca <= 65) {
            cuaca1.setImageResource(R.drawable.rainyday);
        } else if (kodeCuaca >= 80 && kodeCuaca <= 83) {
            cuaca1.setImageResource(R.drawable.shower);
        } else if (kodeCuaca >= 95 && kodeCuaca <= 99) {
            cuaca1.setImageResource(R.drawable.thunderstorm);
        }
    }

    public interface OpenMeteoService {
        @GET("v1/forecast?latitude=-4.04&longitude=119.627&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,weather_code,cloud_cover,wind_speed_10m,wind_direction_10m")
        Call<OpenMeteoResponse> getWeatherData();
    }

    public class OpenMeteoResponse {
        @SerializedName("current")
        public CurrentWeather currentWeather;
    }

    public class CurrentWeather {
        @SerializedName("temperature_2m")
        public double temperature;

        @SerializedName("relative_humidity_2m")
        public double kelembapan;

        @SerializedName("apparent_temperature")
        public double appTemp;

        @SerializedName("rain")
        public double rain;

        @SerializedName("wind_speed_10m")
        public double kecAngin;

        @SerializedName("precipitation")
        public double curah;

        @SerializedName("cloud_cover")
        public double cloud_cover;

        @SerializedName("wind_direction_10m")
        public double arah_angin;

        @SerializedName("weather_code")
        public int kode_cuaca;
    }

    private String WaktuParepare(String mode) {
        if (mode.equals("simpel")) {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            return formatter.format(date);
        } else if (mode.equals("data")) {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(date);
        } else {
            ZoneId zoneId = ZoneId.of("Asia/Makassar");
            ZonedDateTime waktuSkrg = ZonedDateTime.now(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            return waktuSkrg.format(formatter);
        }
    }

    public void tanggal() {
        TextView txt1,txt2;
        txt1 = findViewById(R.id.textView7);
        txt2 = findViewById(R.id.textView9);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String tanggalBesok = sdf.format(calendar.getTime());
        txt1.setText(tanggalBesok);

        calendar.add(Calendar.DATE, 1);
        String tanggalBesokLusa = sdf.format(calendar.getTime());
        txt2.setText(tanggalBesokLusa);
    }

    private void controlIoT() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = db.getReference("status");

        TextView tombolOn, tombolOff;
        tombolOn = findViewById(R.id.textView14);
        tombolOff = findViewById(R.id.textView15);

        tombolOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myRef.setValue(1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                tombolOn.setBackground(getDrawable(R.drawable.shape_left_pressed));
                tombolOff.setBackground(getDrawable(R.drawable.shape_right));
            }
        });

        tombolOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myRef.setValue(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                tombolOff.setBackground(getDrawable(R.drawable.shape_right_pressed));
                tombolOn.setBackground(getDrawable(R.drawable.shape_left));
            }
        });

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue().toString();
                if (status.equals("0")) {
                    tombolOff.setBackground(getDrawable(R.drawable.shape_right_pressed));
                } else if (status.equals("1")) {
                    tombolOn.setBackground(getDrawable(R.drawable.shape_left_pressed));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private double scale(double value, double min, double max) {
        return (value - min) / (max - min);
    }

}

