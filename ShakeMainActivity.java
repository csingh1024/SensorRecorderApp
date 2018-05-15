package assign1.ubicomp.com.shakedetector;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ShakeMainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String NO_SHAKE = "NO SHAKE";
    private Button startBtn, stopBtn , baroBtn ;
    private TextView shakeRes , accVals , baroRes;
    private EditText thresholdVal;
    private SensorManager sensorManager ;
    private Sensor accelerometer ;
    private Sensor barometer;
    private double threshold;
    private String shake_result = NO_SHAKE ;
    private String acc_result = "0.0, 0.0, 0.0";
    private static final String NO_ACC = "0.0, 0.0, 0.0";
    private static final String SHAKE = "SHAKE";
    private static final String NOT_DETECT = "NOT DETECTED";
    private static boolean OBSERVE = false;
    private double[] linear_acc;
    private double millibarPressure = 0.0 ;
    private static boolean showb = false;

    private Context cxt = null;


    public ShakeMainActivity() {
        OBSERVE = false;
        cxt = this;
    }

    private void toast(String str){
        final String string = str;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(cxt,string,Toast.LENGTH_SHORT).show();
            }
        });

    }

    protected void onResume(){
        super.onResume();
        if(sensorManager != null ) {
            sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener((SensorEventListener)this,barometer,SensorManager.SENSOR_DELAY_NORMAL);
            toast("Sensor Service connected");
        }

    }

    protected void onPause(){
        super.onPause();
        if(sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_main);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        List<Sensor> sensorList  =
                sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor currentSensor : sensorList ) {
            Log.e("Chesta" ,(currentSensor.getName()));
        }

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        sensorManager.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener)this,barometer,SensorManager.SENSOR_DELAY_NORMAL);

        shakeRes = findViewById(R.id.textV_shake_res);
        accVals = findViewById(R.id.textV_acc_values);
        thresholdVal = findViewById(R.id.threshhold);
        startBtn = findViewById(R.id.start_button);
        baroBtn = findViewById(R.id.pressure_button);
        baroRes = findViewById(R.id.BarometerReading);


        OBSERVE = false;
        baroBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                showb =true;
                updateUI();
            }
        });

        startBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                threshold = Double.parseDouble(thresholdVal.getText().toString());
                OBSERVE = true;
                acc_result = NO_ACC ;
                shake_result = NO_SHAKE ;
                updateUI();
            }
        });
        stopBtn = findViewById(R.id.stop_button);
        stopBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                toast("stop");
                acc_result = NO_ACC ;
                shake_result = NO_SHAKE ;
                updateUI();
                OBSERVE = false;
            }
        });
        toast("click on start to start reading values");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        linear_acc = new double[]{0.0, 0.0, 0.0};
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (OBSERVE) {
                linear_acc[0] = sensorEvent.values[0];
                linear_acc[1] = sensorEvent.values[1];
                linear_acc[2] = sensorEvent.values[2];
                if (linear_acc[0] * linear_acc[0] + linear_acc[1] * linear_acc[1] + linear_acc[2] * linear_acc[2] > threshold) {
                    acc_result = linear_acc[0] + ", " + linear_acc[1] + ", " + linear_acc[2] + " ";
                    shake_result = SHAKE;
                } else {
                    // acc_result = NO_ACC ;
                    shake_result = NO_SHAKE;
                    toast("Last accelerometer values will be displayed");
                }
                updateUI();
            }
        }
        else
            Log.e("chesta" ,sensorEvent.sensor.getName() );

        if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
            millibarPressure = sensorEvent.values[0];
            updateUI();
        }
    }

    private void updateUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (OBSERVE) {
                    if (shake_result == SHAKE) {
                        shakeRes.setTextColor(Color.RED);
                    } else {
                        shakeRes.setTextColor(Color.DKGRAY);
                    }
                    accVals.setText(acc_result);
                    shakeRes.setText(shake_result);
                }
                if(showb) {
                    baroRes.setText(millibarPressure + " mbar");
                    showb = false;
                }
            }
        });
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
