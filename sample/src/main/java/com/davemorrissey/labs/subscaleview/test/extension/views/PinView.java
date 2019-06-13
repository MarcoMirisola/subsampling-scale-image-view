package com.davemorrissey.labs.subscaleview.test.extension.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.davemorrissey.labs.subscaleview.test.R.drawable;

import java.util.ArrayList;


/**
 * Note that coordinates are generally expressed as lat, lng
 * while 2D space is generally x, y
 * these are reversed - latitude is the y-axis of the earth, and longitude is the x-axis
 *
 * North and south are longitude; east and west are latitude.
 */
public class PinView extends SubsamplingScaleImageView implements SubsamplingScaleImageView.OnStateChangedListener {

    private ArrayList<PointF> sPin = new ArrayList<>();
    private ArrayList<Object> pinNames = new ArrayList<>();
    private Bitmap pin;

    private float mScale = 1;

    private double mWest;  // lat
    private double mNorth; // lng

    private double mDistanceLatitude;
    private double mDistanceLongitude;

    private int mPixelWidth;
    private int mPixelHeight;

    public void configure(double west, double north, double east, double south) {
        mWest = west;
        mNorth = north;
        mDistanceLongitude = east - west;
        mDistanceLatitude = south - north;

        this.post(new Runnable() {
            @Override
            public void run() {
//                mScale = getScale();
                mPixelWidth = getContentWidth();
                mPixelHeight = getContentHeight();
            }
        });

    }

    public PinView(Context context) {
        this(context, null);
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public boolean addMarker(ImageView marker, float x, float y) {
        return addMarker(marker, new PointF(x,y));
    }

    public boolean addMarker(ImageView marker, PointF point) {
        if (pinNames.contains(marker.getTag())){
            return false;
        } else {
            this.sPin.add(point);
            pinNames.add(marker.getTag());
            initialise();
            invalidate();
            return true;
        }
    }

    public PointF getPin(String name) {

        return sPin.get(pinNames.indexOf(name));
    }

    public boolean removePin(String name){
        if (pinNames.contains(name)){
            sPin.remove(pinNames.indexOf(name));
            pinNames.remove(name);
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<Object> getPinNames(){
        return pinNames;
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), drawable.pushpin_blue);
        float w = (density/420f) * pin.getWidth();
        float h = (density/420f) * pin.getHeight();
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        for (PointF point : sPin){
            if (point != null && pin != null) {
                PointF vPin = sourceToViewCoord(point);
                float vX = vPin.x - (pin.getWidth()/2);
                float vY = vPin.y - pin.getHeight();
                canvas.drawBitmap(pin, vX, vY, paint);
            }
        }
    }



    /**
     * Translate longitude coordinate to an x pixel value.
     *
     * @param longitude The longitude.
     * @return The pixel value.
     */
    public int longitudeToX(double longitude) {
        return (int) (longitudeToUnscaledX(longitude) * mScale);
    }

    /**
     * Translate longitude coordinate to an x pixel value, without considering scale.
     *
     * @param longitude The longitude.
     * @return The pixel value.
     */
    public int longitudeToUnscaledX(double longitude) {
        double factor = (longitude - mWest) / mDistanceLongitude;
        return (int) (mPixelWidth * factor);
    }

    /**
     * Translate latitude coordinate to a y pixel value.
     *
     * @param latitude The latitude.
     * @return The pixel value.
     */
    public int latitudeToY(double latitude) {
        return (int) (latitudeToUnscaledY(latitude) * mScale);
    }

    /**
     * Translate latitude coordinate to a y pixel value, without considering scale
     *
     * @param latitude The latitude.
     * @return The pixel value.
     */
    public int latitudeToUnscaledY(double latitude) {
        double factor = (latitude - mNorth) / mDistanceLatitude;
        return (int) (mPixelHeight * factor);
    }

    /**
     * Translate an x pixel value to a longitude.
     *
     * @param x The x value to be translated.
     * @return The longitude.
     */
    public double xToLongitude(int x) {
        return mWest + (x / mScale) * mDistanceLongitude / mPixelWidth;
    }

    /**
     * Translate a y pixel value to a latitude.
     *
     * @param y The y value to be translated.
     * @return The latitude.
     */
    public double yToLatitude(int y) {
        return mNorth + (y / mScale) * mDistanceLatitude / mPixelHeight;
    }

    @Override
    public void onScaleChanged(float newScale, int origin) {
        mScale = newScale;
    }

    @Override
    public void onCenterChanged(PointF newCenter, int origin) {

    }

}

