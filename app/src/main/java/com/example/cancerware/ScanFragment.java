package com.example.cancerware;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cancerware.ml.Finalmodel;
import com.example.cancerware.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class ScanFragment extends Fragment {
    private ImageButton cameraBtn, galleryBtn;
    private Button idclassfybtn;
    private ImageView idimageView;
    private TextView diagnosis, confidence, extraconfidence;
    private Bitmap bitmap;
    private ByteBuffer byteBuffer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        galleryBtn = view.findViewById(R.id.galleryBtn);
        idclassfybtn = view.findViewById(R.id.idclassfybtn);
        cameraBtn = view.findViewById(R.id.cameraBtn);
        diagnosis = view.findViewById(R.id.diagnosis);
        idimageView = view.findViewById(R.id.idimageview);
        confidence = view.findViewById(R.id.confidence);
        extraconfidence = view.findViewById(R.id.extraconfidence);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                diagnosis.setText("");
                confidence.setText("");
                getPermission(); //permission
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);
                diagnosis.setText("");
                confidence.setText("");
            }
        });

        idclassfybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                diagnosis.setText("");
                classfyDisease();
            }
        });

        return view;
    }


    void classfyDisease(){
        try {
            Model model = Model.newInstance(getContext().getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);

            if (bitmap != null){
                bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
                inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());
                // Runs model inference and gets result.
                Model.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                // Normalize the probabilities
                float[] probabilities = outputFeature0.getFloatArray();
                float sum = 0.0f;
                for (float probability : probabilities) {
                    sum += probability;
                }
                for (int i = 0; i < probabilities.length; i++) {
                    probabilities[i] = probabilities[i] / sum;
                }

                // Output the confidence values for each class or value
                StringBuilder confidenceBuilder = new StringBuilder();
                for (int i = 0; i < probabilities.length; i++) {
                    String className = getClassNameForIndex(i);
                    float confidence = probabilities[i] * 100;
                    String confidenceString = String.format("%.2f", confidence);
                    confidenceBuilder.append(className).append(": ").append(confidenceString).append("%\n");
                    System.out.println(className + ": " + confidenceString + "%");
                }

                extraconfidence.setVisibility(View.VISIBLE);
                extraconfidence.setText(confidenceBuilder.toString());

                // Releases model resources if no longer used.
                int results = getMax(outputFeature0.getFloatArray());
                if (results == 0){
                    diagnosis.setText("Skin Cancer");
                    confidence.setText(String.format("%.2f", probabilities[results] * 100));

                } else if(results == 1) {
                    diagnosis.setText("Not Skin Cancer");
                    confidence.setText(String.format("%.2f", probabilities[results] * 100));

                }else if(results == 2){
                    diagnosis.setText("Unknown");
                    confidence.setText(String.format("%.2f", probabilities[results] * 100));
                }else{
                    Toast.makeText(getContext(), "Error !", Toast.LENGTH_SHORT).show();
                }
                model.close();

            }else{
                Toast.makeText(getContext(), "No Image selected", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            // TODO Handle the exception
        }

    }
    private String getClassNameForIndex(int index) {
        // Implement your logic to map index to class name
        // Return the class name corresponding to the index

        // Example:
        switch (index) {
            case 0:
                return "Actinic Keratosis - Cancer Absent";
            case 1:
                return "Basal Cell Carcinoma - Cancer Infected";
            default:
                return "Unknown Diagnosis";
        }
    }


    private int getMax(float[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > arr[max]) max = i;
        }
        return max;
    }


    void getPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Check if the camera permission is granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Request the camera permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 11);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                    idimageView.setImageBitmap(bitmap);
//                    ImageUri = uri;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 12) {
            if (resultCode == Activity.RESULT_OK) {
                // The image was captured successfully
                bitmap = (Bitmap) data.getExtras().get("data");
                idimageView.setImageBitmap(bitmap);
                Uri imageUri = getImageUriFromBitmap(bitmap);
                // Continue with further processing of the captured image
            } else {
                // Image capture was canceled or failed
                // Handle the error or show a message to the user
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11){
            if(grantResults.length>0){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    this.getPermission();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
}