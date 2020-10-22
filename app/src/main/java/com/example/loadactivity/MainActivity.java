package com.example.loadactivity;

import androidx.appcompat.app.AppCompatActivity;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //Объявляем используемые переменные:
    private ImageView imageView;
    private final int Pick_image = 1;
    private static final String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Связываемся с нашим ImageView:
        imageView = (ImageView)findViewById(R.id.imageView);

        //Связываемся с нашей кнопкой Button:
        Button PickImage = (Button) findViewById(R.id.button);
        //Настраиваем для нее обработчик нажатий OnClickListener:
        PickImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                //Тип получаемых объектов - image:
                photoPickerIntent.setType("image/*");
                //Запускаем переход с ожиданием обратного результата в виде информации об изображении:
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });
    }


    public static int getExifAngle(String path) {
        int angle = 0;
        Log.i(TAG,path + "");
        try {
            Log.i(TAG,"зашли");
            ExifInterface ei = new ExifInterface(path);
            Log.i(TAG,"Конструктор создан");
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
            }
        } catch (Exception e) {
            Log.i(TAG, e.getLocalizedMessage());
        }
        Log.i(TAG,"угол=" + angle);
        return angle;
    }

    //Обрабатываем результат выбора в галерее:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case Pick_image:
                if(resultCode == RESULT_OK){
                    try {
                        //Получаем URI изображения, преобразуем его в Bitmap
                        //объект и отображаем в элементе ImageView нашего интерфейса:
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(selectedImage, //масштабируем битмап
                                500, 500, true);

                        //для переворота:
                        Matrix matrix = new Matrix();
                        matrix.postRotate(getExifAngle(imageUri.getPath()));

                        //переворачиваем:
                        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
                                scaledBitmap.getHeight(), matrix, true);

                        imageView.setImageBitmap(rotatedBitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}





