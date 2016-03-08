package com.example.thanyapat.parkinapplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

public class MemoFragment extends StatedFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static final int REQUEST_CAMERA = 2;

    private static String lotNo="";
    ImageView imageView;
    ImageButton cameraBtn;
    EditText editText;
    File imageFile;
    Uri uri;
    Bitmap bitmap;
    private View rootView;
    
    public MemoFragment() {
        // Required empty public constructor
    }
    public static MemoFragment newInstance(String param1, String param2) {
        MemoFragment fragment = new MemoFragment();
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
        rootView = inflater.inflate(R.layout.fragment_memo, container, false);

        imageView = (ImageView)rootView.findViewById(R.id.imageView);
        editText = (EditText)rootView.findViewById(R.id.editText);
        cameraBtn = (ImageButton) rootView.findViewById(R.id.imageButton);
        cameraBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "IMG_" + timeStamp + ".jpg";
                imageFile = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera/" + imageFileName);
                uri = Uri.fromFile(imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(Intent.createChooser(intent, "Take a picture with"), REQUEST_CAMERA);
            }
        });
        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {
            getActivity().getContentResolver().notifyChange(uri, null);
            ContentResolver cr = getActivity().getContentResolver();
            //cameraTextView.setVisibility(View.GONE);
            try {
                bitmap = Images.Media.getBitmap(cr, uri);
                imageView.setImageBitmap(bitmap);
                Log.w("MemoFragment", uri.getPath());
                saveImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //Saving image to Gallery
    public void saveImage() {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        getActivity().sendBroadcast(intent);
    }
    // delete image after image dismissed
    public void deleteImage(){
        imageFile.delete();
        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, uri));
    }

    public void onResume() {
        Log.w("MemoFragment", "onResume");
        super.onResume();
        if(!lotNo.equals("")){
            editText.setText(lotNo);
        }
    }

    public void onPause() {
        Log.w("MemoFragment", "onPause");
        super.onPause();
    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // For example:
        //outState.putString("text", tvSample.getText().toString());
        if(!lotNo.equals("")){
            outState.putString("LOT_NO_TXT", lotNo);
        }
        if(bitmap!=null){
            outState.putParcelable("LOT_NO_IMG", bitmap);
        }
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        // For example:
        //tvSample.setText(savedInstanceState.getString("text"));
        if(savedInstanceState.getString("LOT_NO_TXT")!=null){
            editText.setText(savedInstanceState.getString("LOT_NO_TXT"));
        }
        if(savedInstanceState.getParcelable("LOT_NO_IMG")!=null){
            imageView.setImageBitmap((Bitmap) savedInstanceState.getParcelable("LOT_NO_IMG"));
        }

    }

}