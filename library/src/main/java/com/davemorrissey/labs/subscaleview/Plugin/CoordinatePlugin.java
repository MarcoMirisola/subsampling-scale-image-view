package com.davemorrissey.labs.subscaleview.plugin;

import android.graphics.PointF;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * Note that coordinates are generally expressed as lat, lng
 * while 2D space is generally x, y
 * these are reversed - latitude is the y-axis of the earth, and longitude is the x-axis
 *
 * North and south are longitude; east and west are latitude.
 */
public class CoordinatePlugin implements SubsamplingScaleImageView.OnStateChangedListener {

  private float mScale = 1;

  private double mWest;  // lat
  private double mNorth; // lng

  private double mDistanceLatitude;
  private double mDistanceLongitude;

  private int mPixelWidth;
  private int mPixelHeight;

  public CoordinatePlugin(double west, double north, double east, double south) {
    mWest = west;
    mNorth = north;
    mDistanceLongitude = east - west;
    mDistanceLatitude = south - north;
  }

  public CoordinatePlugin install(final SubsamplingScaleImageView scaleImageView) {
    scaleImageView.post(new Runnable() {
      @Override
      public void run() {
        mScale = scaleImageView.getScale();
        mPixelWidth = scaleImageView.getContentWidth();
        mPixelHeight = scaleImageView.getContentHeight();
      }
    });
    return this;
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
