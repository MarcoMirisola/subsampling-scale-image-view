package com.davemorrissey.labs.subscaleview.test.extension;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.plugin.CoordinatePlugin;
import com.davemorrissey.labs.subscaleview.test.R;
import com.davemorrissey.labs.subscaleview.test.R.id;
import com.davemorrissey.labs.subscaleview.test.R.layout;
import com.davemorrissey.labs.subscaleview.test.extension.views.PinView;
import com.davemorrissey.labs.subscaleview.test.extension.views.PinView.DefaultOnMapListener;

public class ExtensionPinFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(layout.extension_pin_fragment, container, false);
        final ExtensionActivity activity = (ExtensionActivity)getActivity();
        if (activity != null) {
            rootView.findViewById(id.next).setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { activity.next(); }
            });
        }


        final PinView imageView = rootView.findViewById(id.imageView);
        imageView.setImage(ImageSource.asset("mappa_radicofani_new.jpg"));
        imageView.configure(11.762639, 42.900583, 11.772611, 42.893028);
        imageView.setOnMapListener(new DefaultOnMapListener(){
            @Override
            public void onMarkerClicked() {
                Log.i("TOUCH", "onSingleTapConfirmed: TOUCH");
            }

            @Override
            public void onMapClicked() {
                super.onMapClicked();
            }

            @Override
            public void onMapReady() {
                ImageView marker = new ImageView(getContext());
                marker.setTag("item");
                marker.setImageResource(R.drawable.pushpin_blue);
                imageView.addMarker(marker, imageView.longitudeToX(11.77), imageView.latitudeToY(42.90));
            }
        });


        return rootView;
    }

}
