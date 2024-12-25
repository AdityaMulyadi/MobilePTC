package com.example.mobileptc;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import java.io.InputStream;
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
import org.w3c.dom.Text;


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
        TextView suhu, txtkelembapan, txtkecAngin, curah, tanggal, cc, arahAngin, tglKalender, txtAlamat;
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

        tanggal.setText(WaktuParepare("non"));
        tglKalender.setText(WaktuParepare("simpel"));
        tanggal();


        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.open-meteo.com/").addConverterFactory(GsonConverterFactory.create()).build();
        OpenMeteoService service = retrofit.create(OpenMeteoService.class);
        Call<OpenMeteoResponse> call = service.getWeatherData();

        call.enqueue(new Callback<OpenMeteoResponse>() {
            @Override
            public void onResponse(Call<OpenMeteoResponse> call, Response<OpenMeteoResponse> response) {
                if (response.isSuccessful()) {
                    OpenMeteoResponse weatherData = response.body();
                    double currentTemp = weatherData.currentWeather.temperature;
                    String strTemp = currentTemp + "°C";
                    double lembap = weatherData.currentWeather.kelembapan;
                    String kelembapan = lembap + "%";
                    double kecepatanAngin = weatherData.currentWeather.kecAngin;
                    String kecAngin = kecepatanAngin + "km/h";
                    double curahHujan = weatherData.currentWeather.curah;
                    String strCurahHujan = curahHujan + "mm";
                    double nilaiCC = weatherData.currentWeather.cloud_cover;
                    String strCC = nilaiCC + "%";
                    double arahA = weatherData.currentWeather.arah_angin;
                    String strArahA = arahA + "°";

                    arahAngin.setText(strArahA);
                    cc.setText(strCC);
                    suhu.setText(strTemp);
                    txtkelembapan.setText(kelembapan);
                    txtkecAngin.setText(kecAngin);
                    curah.setText(strCurahHujan);

                    int kodeCuaca = weatherData.currentWeather.kode_cuaca;
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

            }

            @Override
            public void onFailure(Call<OpenMeteoResponse> call, Throwable t) {
                Log.e("NetworkError", "Network Error: " + t.getMessage());
                Toast.makeText(Home.this, "Failed to fetch weather data. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DATA_JEMURAN");
        Query query = ref.orderByKey().endAt("2024-12-31_23:59:59").limitToLast(1);
        TextView txtLux, dataStatus;
        ImageView ikonCuaca;
        ikonCuaca = findViewById(R.id.ikonHujan);
        txtLux = findViewById(R.id.dataIntensitasCahaya);
        dataStatus = findViewById(R.id.dataKondisiIot);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String status, dataLux;
                Integer lux1, lux2;
                Boolean cuaca;

                if (snapshot.exists()) {
                    lux1 = snapshot.child("Cahaya1").getValue(Integer.class);
                    lux2 = snapshot.child("Cahaya2").getValue(Integer.class);
                    status = snapshot.child("Status_Jemuran").getValue(String.class);
                    cuaca = snapshot.child("Hujan").getValue(Boolean.class);

                    if (lux1 != null && lux2 != null) {
                        if (lux1 <= 1500 && lux2 <= 1500) {
                            dataLux = "Terang";
                            txtLux.setText(dataLux);
                        } else {
                            dataLux = "Gelap";
                            txtLux.setText(dataLux);
                        }

                    } else {
                        dataLux = "N/A";
                        txtLux.setText(dataLux);

                    }

                    if (status != null) {
                        dataStatus.setText(status);
                    } else {
                        dataStatus.setText("N/A");
                    }

                    if (cuaca != null) {
                        if (cuaca) {
                            ikonCuaca.setImageResource(R.drawable.rainyday);
                        } else {
                            ikonCuaca.setImageResource(R.drawable.sun);
                        }
                    } else {
                        ikonCuaca.setImageResource(R.drawable.fog);
                    }
                } else {
                    dataLux = "N/A";
                    dataStatus.setText("N/A");
                    ikonCuaca.setImageResource(R.drawable.fog);
                    txtLux.setText(dataLux);
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

        try {
            TextView txt = findViewById(R.id.textView8);
//            InputStream inputStream2 = getAssets().open("temp.txt");
//            AssetFileDescriptor ast = getAssets().openFd("model_lstm.tflite");
//            WeatherPredict weatherPredict = new WeatherPredict();
//            PrediksiCuaca prediksiCuaca = new PrediksiCuaca("C:\\Users\\ASUS.LAPTOP-5901OKQB\\AndroidStudioProjects\\MobilePTC\\app\\src\\main\\assets\\model_lstmaftrepb.tflite");
            float[] data = {26.3F, 91, 31.5F, 0.0F, 100, 9.4F, 94};
            float[][] prediksi = prediksiCuaca(data);
            int klasifikasi = klasifikasiCuaca(prediksi);
            txt.setText(String.valueOf(klasifikasi));
//            float[] prediksi = weatherPredict.prediksiCuaca(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public float[][] prediksiCuaca (float[] inputData) throws IOException {
        Interpreter interpreter = new Interpreter(loadModelFile("weather_prediction.tflite"));
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
        byteBuffer.order(ByteOrder.nativeOrder());
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
//        DescriptiveStatistics stats = new DescriptiveStatistics();
//        for (float value : inputData[0]) {
//            stats.addValue(value);
//        }
//        double min = stats.getMin();
//        double max = stats.getMax();
//
//        for (int i = 0; i < inputData[0].length; i++) {
//            inputData[0][i] = (float) scale(inputData[0][i], min, max);
//        }

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(inputData[0].length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
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

