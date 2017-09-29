package com.lab603.picencyclopedias;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.TensorFlowClassifier;

import java.io.FileNotFoundException;
import java.util.List;


/**
 * Created by EdwardZhang on 2017/9/10.
 */

public class CameraFragment extends Fragment {
    private static final String MODEL_FILE = "file:///android_asset/tensorflow_inception_graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";
    private static final int NUM_CLASSES = 1001;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input:0";
    private static final String OUTPUT_NAME = "output:0";
    private final TensorFlowClassifier tensorflow = new TensorFlowClassifier();
    private TextView mResultText;

    public static MenuFragment newInstance(int index) {
        MenuFragment fragment = new MenuFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        initCameraView(view);
        return view;
    }

    /**
     * Init the fragment
     */
    private void initCameraView(View view) {
        // test1 load tensorflow
        final AssetManager assetManager = getActivity().getAssets();
        tensorflow.initializeTensorFlow(
                assetManager, MODEL_FILE, LABEL_FILE, NUM_CLASSES, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD,
                INPUT_NAME, OUTPUT_NAME);

        // test1 end


        Button button = (Button) view.findViewById(R.id.b01);
        button.setText("选择图片");
        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getActivity().getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                dealPics(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void dealPics(Bitmap bitmap) {
        ImageView imageView = (ImageView) getView().findViewById(R.id.iv01);
                /* 将Bitmap设定到ImageView */

        int width = bitmap.getWidth();

        int height = bitmap.getHeight();
        System.out.println(width + "&&" + height);
        float scaleWidth = ((float) INPUT_SIZE) / width;

        float scaleHeight = ((float) INPUT_SIZE) / height;
        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        imageView.setImageBitmap(newbm);
        final List<Classifier.Recognition> results = tensorflow.recognizeImage(newbm);
        for (final Classifier.Recognition result : results) {
            System.out.println("Result: " + result.getTitle());
        }
        mResultText = (TextView) getView().findViewById(R.id.t01);
        mResultText.setText("Detected = " + results.get(0).getTitle());

        System.out.println(newbm.getWidth() + "&&" + newbm.getHeight());


    }
}
