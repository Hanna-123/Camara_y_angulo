package com.example.camarayangulo;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.core.content.FileProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnTouchListener{
    Button jbn;
    RadioButton jbr0,jrb1,jrb2;
    Intent myIntent;
    int TAKE_A_PICTURE = 1;
    int SELECT_PICTURE = 2;
    String s ="";
    File TempImage = null;
    ImageView jiv1;

    // VARIABLE ANGULO
    TextView jtv;
    Button btn;
    AbsoluteLayout jll = null;
    double x ;
    double y;
    int countPuntos = 0;
    double [] puntosX = new double[3];
    double [] puntosY = new double[3];
    Lienzo l;
    boolean borrarCanvas = false;
    @Override
    protected void onCreate( Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        jbn =(Button)findViewById(R.id.xbn1);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }

        jbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jbr0 = (RadioButton)findViewById(R.id.xrb0);
                jrb2=(RadioButton)findViewById(R.id.xrb2);

                int code = TAKE_A_PICTURE;
                if(jbr0.isChecked()){
                    myIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(myIntent,TAKE_A_PICTURE);
                } else if(jrb2.isChecked()){
                    myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    code = SELECT_PICTURE;
                    startActivityForResult(myIntent, code);
                }

            }
        });
        jtv = (TextView) findViewById(R.id.xtv);
        jll= (AbsoluteLayout) findViewById(R.id.xll);
        //jiv1 = (ImageView) findViewById(R.id.xiv1);
        btn = (Button) findViewById(R.id.xbtn);
        l = new Lienzo(this);
        l.setOnTouchListener(this);
        jll.addView(l);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_A_PICTURE) {
            if (data != null) {
                if (data.hasExtra("data")) {
                    ImageView iv = (ImageView)findViewById(R.id.xiv1);
                    iv.setImageBitmap((Bitmap) data.getParcelableExtra("data"));
                }
            }
            else {
                //ImageView iv = (ImageView)findViewById(R.id.xiv1);
                //iv.setImageBitmap(BitmapFactory.decodeFile(s));
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    private MediaScannerConnection msc = null; {
                        msc = new MediaScannerConnection(getApplicationContext(), this);
                        msc.connect();
                    }
                    public void onMediaScannerConnected() {
                        msc.scanFile(s, null);
                    }
                    public void onScanCompleted(String path, Uri uri) {
                        msc.disconnect();
                    }
                };
            }
        } else if (requestCode == SELECT_PICTURE){
            Uri image = data.getData();
            InputStream is;
            try {
                is = getContentResolver().openInputStream(image);
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                ImageView iv = (ImageView)findViewById(R.id.xiv1);
                iv.setImageBitmap(bitmap);
            }
            catch (FileNotFoundException e) {

            }
        }
    }
    private String getCode(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddhhmmss");
        String name = "picture_"+sdf.format(new Date());
        return name;
    }

    public void borrarTodo(View v){
        jtv.setText("");
        for(int i =0; i < 3; i++){
            puntosX[i] = 0.0;
            puntosY[i] = 0.0;
            x = 0;
            y = 0;
        }
        countPuntos = 0;
        jtv.append("\t ====Vuelva a pulsar 3 veces=== \n\n");


    }
    public boolean onTouch(View v, MotionEvent event) {
        if (countPuntos < 3){
            if(event.getAction() == MotionEvent.ACTION_DOWN){

                x = event.getX();
                y = event.getY();
                puntosX[countPuntos] = x;
                puntosY[countPuntos] = y;
                countPuntos ++;
                l.invalidate();
            }

        }


        return true;
    }

    public double magnitud(double x1, double y1, double x2, double y2){
        double redMagnitud = 0;

        double difX = x2 - x1;
        double difY = y2 - y1;

        double powX = Math.pow(difX,2);
        double powY = Math.pow(difY,2);

        redMagnitud = Math.sqrt(powX+powY);

        return redMagnitud;
    }
    public double pendiente(double x1, double y1, double x2, double y2){

        double difX = x2 - x1;
        double difY = y2 - y1;
        if (difX == 0) difX = 1;
        if (difY == 0) difY = 1;

        return  difY/difX;
    }
    public double angulo(double x1, double y1, double x2, double y2, double xr, double yr ){

        double angle1 = Math.atan2(y1 - yr, x1 - xr);
        double angle2 = Math.atan2(y2 - yr, x2 - xr);

        return Math.toDegrees(angle1 - angle2);
    }

    public class Lienzo extends View{


        public Lienzo(Context c){
            super(c);
        }
        protected void onDraw(Canvas c) {
            super.onDraw(c); // Canvas pinta atributos
            Paint p = new Paint(); // Paint asigna atributo
            p.setColor(Color.TRANSPARENT); // Fondo blanco
            c.drawPaint(p);
            p.setColor(Color.rgb(0, 0, 255)); // Ejes azules
            for (int i = 0; i < 3; i++){
                c.drawCircle((float) puntosX[i], (float) puntosY[i], 20, p);
                p.setTextSize(50);
                c.drawText("P"+(i+1),(float) puntosX[i]+20,(float) puntosY[i]+20, p);

            }

            p.setStrokeWidth(5);

            if (countPuntos > 2){
                c.drawLine((float) puntosX[0],(float) puntosY[0], (float) puntosX[1],(float) puntosY[1], p);
                c.drawLine((float) puntosX[0],(float) puntosY[0], (float) puntosX[2],(float) puntosY[2], p);
            }

            if (countPuntos > 2) {
                jtv.append("Magnitud P1P2: " + magnitud(puntosX[0], puntosY[0], puntosX[1], puntosY[1])+"\n");
                jtv.append("Magnitud P1P3: " + magnitud(puntosX[0], puntosY[0], puntosX[2], puntosY[2])+"\n");
                jtv.append("Magnitud P2P3: " + magnitud(puntosX[1], puntosY[1], puntosX[2], puntosY[2])+"\n");
                double p1 = pendiente(puntosX[0], puntosY[0], puntosX[1], puntosY[1]);
                double p2 = pendiente(puntosX[0], puntosY[0], puntosX[2], puntosY[2]);
                double anguloTot = angulo(puntosX[1], puntosY[1], puntosX[2], puntosY[2], puntosX[0], puntosY[0]);
                if (anguloTot < 0) anguloTot = anguloTot *-1;
                if (anguloTot > 180) anguloTot = 360 - anguloTot;
                jtv.append("Angulo: " +  anguloTot+"\n");
                p.setColor(Color.RED);
                double [] puntoMedioX = new double[2];
                double [] puntoMedioY = new double[2];
                puntoMedioX[0] =  (puntosX[1] + puntosX[0])/2;
                puntoMedioY[0] =  (puntosY[1] + puntosY[0])/2;
                puntoMedioX[1] =  (puntosX[2] + puntosX[0])/2;
                puntoMedioY[1] =  (puntosY[2] + puntosY[0])/2;

                c.drawCircle((float) puntoMedioX[0], (float) puntoMedioY[0], 16, p);
                c.drawCircle((float) puntoMedioX[1], (float) puntoMedioY[1], 16, p);
                c.drawLine((float) puntoMedioX[0],(float) puntoMedioY[0], (float) puntoMedioX[1],(float) puntoMedioY[1], p);


            }
            if(borrarCanvas){
                p.setColor(Color.WHITE); // Fondo blanco
                c.drawPaint(p);
            }



        }
    }
}


