package com.stallone.ajsgolf2789.distancecalculator;


import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements SensorEventListener {


    Sensor accelerometer;
    SensorManager sm;
    TextView acceleration;
    TextView angle;
    TextView distance;
    double thetaglobal;
    EditText height;
    double heightInFeet;
    ImageView iv;

    private SurfaceView preview = null;
    private SurfaceHolder previewHolder=null;
    private Camera camera=null;
    private boolean inPreview=false;
    private boolean cameraConfigured=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        distance = (TextView) findViewById(R.id.distance);
        preview=(SurfaceView)findViewById(R.id.preview);
        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        ImageView iv= (ImageView)findViewById(R.id.img);
        iv.setImageResource(R.drawable.sight);
        height = (EditText)findViewById(R.id.heightInFeet);

    }

    @Override
    public void onResume() {
        super.onResume();

        camera=Camera.open();
        startPreview();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera=null;
        inPreview=false;

        super.onPause();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                }
                else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private void initPreview(int width, int height) {
        if (camera!=null && previewHolder.getSurface()!=null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
               // Log.e("PreviewDemo-surfaceCallback",
                       // "Exception in setPreviewDisplay()", t);
                // Toast
                // .makeText(PreviewDemo.this, t.getMessage(), Toast.LENGTH_LONG)
                //.show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=camera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height,
                        parameters);

                if (size!=null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera!=null) {
            camera.startPreview();
            inPreview=true;
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            camera.setDisplayOrientation(90);
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Toast.makeText(getApplicationContext(), "Please enter a height in feet usually \n2 - 3 inches below your height",
                    Toast.LENGTH_LONG).show();
            //super.onOptionsItemSelected(item)
            return true;
        }

        return true;
    }

    public void measure(View v) {
        String text = height.getText().toString();

        if (text != null && text.trim().length() > 0) {
            heightInFeet = Double.valueOf(height.getText().toString());
            thetaglobal = Math.toRadians(thetaglobal);
            float dist = (float) (heightInFeet * Math.tan(thetaglobal));

            distance.setText("distance " + dist + " feet");
        }else {
            Toast.makeText(getApplicationContext(), "Please enter a height in feet",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double div = (double) event.values[1] / event.values[2];

        float theta = (float) Math.toDegrees(Math.atan(div));
        thetaglobal = theta;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
