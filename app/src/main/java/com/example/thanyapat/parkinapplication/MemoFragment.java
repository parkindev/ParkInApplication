package com.example.thanyapat.parkinapplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.design.widget.FloatingActionButton;
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

    private static final String TAG = "MemoFragment";

    public static final int REQUEST_CAMERA = 2;

    private static String lotNo="";
    ImageView imageView;
    EditText editText;
    File imageFile;
    Uri uri;
    Bitmap bitmap;
    FloatingActionButton cameraBtn;
    private View rootView;
    
    public MemoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_memo, container, false);
        MainActivity.navigationView.getMenu().getItem(2).setChecked(true);
        getActivity().findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setActionBarTitle("MEMO");
        getActivity().getPreferences(Context.MODE_PRIVATE).edit().putString(SettingsFragment.CURRENT_FRAGMENT, TAG).commit();
        ((MainActivity)getActivity()).changeMenuIcon(R.drawable.blank_icon);

        imageView = (ImageView)rootView.findViewById(R.id.imageView);
        editText = (EditText)rootView.findViewById(R.id.editText);
        cameraBtn = (FloatingActionButton) rootView.findViewById(R.id.camera_fab);
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
        if (requestCode == REQUEST_CAMERA && resultCode == MainActivity.RESULT_OK) {
            getActivity().getContentResolver().notifyChange(uri, null);
            ContentResolver cr = getActivity().getContentResolver();
            //cameraTextView.setVisibility(View.GONE);
            try {
                bitmap = Images.Media.getBitmap(cr, uri);
                imageView.setImageBitmap(bitmap);
                Log.w(TAG, uri.getPath());
                if(getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean(SettingsFragment.WILL_IMAGE_SAVED, true)) {
                    Log.w(TAG, "willImageSaved = " + getActivity().getPreferences(Context.MODE_PRIVATE).getBoolean("willImageSaved", true));
                    saveImage();
                }
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
        Log.w(TAG, "onResume");
        super.onResume();
        if(!lotNo.equals("")){
            editText.setText(lotNo);
        }
    }

    public void onPause() {
        Log.w(TAG, "onPause");
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
