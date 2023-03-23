package com.example.catdog;

import static com.example.catdog.api.ApiService.gson;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.catdog.api.ApiService;
import com.example.catdog.api.Const;
import com.example.catdog.model.Animal;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ImageView uploadCard, imageCard;
    Button predictBtn;
    TextView resultTv;
    private Uri mImageUri;
    private ProgressDialog mProgressDialog;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadCard = findViewById(R.id.uploadCard);
        imageCard = findViewById(R.id.imageCard);
        predictBtn = findViewById(R.id.predictBtn);
        resultTv = findViewById(R.id.resultTv);

        //Init progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait");

        imageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start(100);
            }
        });

        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageUri != null)
                {
                    callApiPredictImage();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            mImageUri = data.getData();
            imageCard.setImageURI(mImageUri);
            //Toast.makeText(UploadCiCardActivity.this, "Success", Toast.LENGTH_SHORT).show();
            uploadCard.setVisibility(View.GONE);
        }
    }

    private void callApiPredictImage() {
        mProgressDialog.show();

        String strRealPath = RealPathUtil.getRealPath(this, mImageUri);

        File file = new File(strRealPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipartBodyPredict = MultipartBody.Part.createFormData(Const.KEY_FILE, file.getName(), requestBody);


        ApiService.apiService.sendImageFile(multipartBodyPredict).enqueue(new Callback<Animal>() {
            @Override
            public void onResponse(Call<Animal> call, Response<Animal> response) {
                mProgressDialog.dismiss();

                Animal animal = response.body();
                if (animal != null)
                {
                    resultTv.setText(animal.getSpecies());
                }
            }
            @Override
            public void onFailure(Call<Animal> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Call Api failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}