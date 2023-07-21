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
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {
    private ImageButton cameraBtn, galleryBtn;
    private Button idclassfybtn;
    private ImageView idimageView;
    private TextView diagnosis, confidence;
    private Bitmap bitmap;
    private ByteBuffer byteBuffer;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanFragment newInstance(String param1, String param2) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
                classifyDisease();
            }
        });

        return view;
    }


    private void classifyDisease() {
        TensorImage resizedImage;
        try {

            Finalmodel model = Finalmodel.newInstance(requireContext());

            // Create the TensorImage from the bitmap
            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(bitmap);

            // Resize the image to the model's input size
            int imageWidth = 224;
            int imageHeight = 224;
            resizedImage = tensorImage.resize(new TensorShape(1, imageWidth, imageHeight, 3));

        // Convert the resized TensorImage to a TensorBuffer
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, imageWidth, imageHeight, 3}, DataType.FLOAT32);
            resizedImage.loadBuffer(inputFeature0);


        if (bitmap != null) {
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            // Runs model inference and gets result.
            Finalmodel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] probabilities = outputFeature0.getFloatArray();
            float sum = 0.0f;
            for (float probability : probabilities) {
                sum += probability;
            }
            for (int i = 0; i < probabilities.length; i++) {
                probabilities[i] = probabilities[i] / sum;
            }

            // Get the predicted class index and confidence
            int predictedClassIndex = getMax(outputFeature0.getFloatArray());
            float confidenceValue = probabilities[predictedClassIndex] * 100;

            // Get the diagnosis text for the predicted class
            String diagnosisText = getDiagnosisText(predictedClassIndex);

            // Display the result and confidence
            diagnosis.setText(diagnosisText);
            confidence.setText(String.format("%.2f", confidenceValue));

            // Release model resources if no longer used.
            int diagnosisIndex = getMax(outputFeature0.getFloatArray());
            diagnosis.setText(diagnosisText);
            model.close();
        } else {
            Toast.makeText(requireActivity(), "No Image seleted", Toast.LENGTH_SHORT).show();
        }

    } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    public String getDiagnosisText(int diagnosisIndex) {
        switch (diagnosisIndex) {
            case 0:

                return "Actinic Keratosis - Cancer Absent";
            case 1:
                return "Basal Cell Carcinoma - Cancer Infected";
            case 2:
                return "Dermatofibroma - Cancer Absent";
            case 3:
                return "Melanoma - Cancer Infected";
            case 4:
                return "Nevus - Cancer Absent";
            case 5:
                return "Pigmented Benign Keratosis - Cancer Absent";
            case 6:
                return "Seborrheic Keratosis - Cancer Absent";
            case 7:
                return "Squamous Cell Carinoma - Cancer Infected";
            case 8:
                return "Vascular Lesion- Cancer Absent";
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


    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Check for the camera permission being granted
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Requesting the camera permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.getPermission();
                } else {
                    Toast.makeText(requireContext(), "Camera permission denied. Cancerware cannot use the camera.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10 && data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                idimageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == 12 && resultCode == Activity.RESULT_OK && data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
                idimageView.setImageBitmap(bitmap);


            }
            else {
                Toast.makeText(requireActivity(), "Image capture cancelled or failed.", Toast.LENGTH_SHORT).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
}