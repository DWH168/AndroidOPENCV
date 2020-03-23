package com.android.opencv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {
    private Button but;
    private  Button but1;
    private ImageView img;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        but = findViewById(R.id.but);
        img= findViewById(R.id.img);
        but1=findViewById(R.id.use);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent可以应用于广播和发起意图，其中属性有：ComponentName,action,data等
                Intent intent=new Intent();
                intent.setType("image/*");
                //action表示intent的类型，可以是查看、删除、发布或其他情况；我们选择ACTION_GET_CONTENT，系统可以根据Type类型来调用系统程序选择Type
                //类型的内容给你选择
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //如果第二个参数大于或等于0，那么当用户操作完成后会返回到本程序的onActivityResult方法
                startActivityForResult(intent, 1);
            }
        });
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_8888, false);
                Mat src = new Mat(bit.getHeight(), bit.getWidth(), CvType.CV_8UC(3));
                Utils.bitmapToMat(bit, src);
                Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
                Utils.matToBitmap(src, bitmap);
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //用户操作完成，结果码返回是-1，即RESULT_OK
        if(resultCode==RESULT_OK){
            //获取选中文件的定位符
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            //使用content的接口
            ContentResolver cr = this.getContentResolver();
            try {
                //获取图片
                bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                img.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }else{
            //操作错误或没有选择图片
            Log.i("MainActivtiy", "operation error");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {

            Log.i("cv", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.i("cv", "OpenCV library found inside package. Using it!");
        }
    }
    Handler handler=new Handler(){

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case 1:img.setImageBitmap(bitmap);break;
            }
        }
    };
}
