/*
 * This file is derived from ZXing project ( https://github.com/zxing/zxing )
 * and is modified for android-lib-ZXingCaptureActivity project.
 *
 * Copyright (C) 2008 ZXing authors
 * Copyright (C) 2014 NOBUOKA Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.vividcode.android.zxing;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import info.vividcode.android.zxing.camera.CameraManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 * @author Yu Nobuoka
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

  private static final String TAG = CaptureActivity.class.getSimpleName();

  private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;

  private CameraManager cameraManager;
  private CaptureActivityHandler handler;
  private ViewfinderView viewfinderView;
  private TextView statusView;
  private boolean hasSurface;
  private Collection<BarcodeFormat> decodeFormats;
  private Map<DecodeHintType,?> decodeHints;
  private String characterSet;
  private AmbientLightManager ambientLightManager;

  ViewfinderView getViewfinderView() {
    return viewfinderView;
  }

  Handler getHandler() {
    return handler;
  }

  CameraManager getCameraManager() {
    return cameraManager;
  }

  /**
   * Set barcode formats to scan for onto {@code Intent}.
   * This setting precedes to setting by {@code #setDecodeFormatsToIntent} method.
   * In case that {@code formats} is empty collection, this method do nothing.
   * @param intent Target intent.
   * @param formats Barcode formats to scan for.
   */
  public static void setDecodeFormatsToIntent(Intent intent, Collection<BarcodeFormat> formats) {
    StringBuilder sb = new StringBuilder();
    for (BarcodeFormat f : formats) {
      if (sb.length() != 0) sb.append(",");
      sb.append(f.name());
    }
    String formatsStr = sb.toString();
    if (formatsStr.length() != 0) intent.putExtra(Intents.Scan.FORMATS, formatsStr);
  }

  /**
   * Set barcode formats to scan for onto {@code Intent}.
   * @param intent Target intent.
   * @param scanMode Mode which specify set of barcode formats to scan for. Use one of
   *                 {@link Intents.Scan#PRODUCT_MODE}, {@link Intents.Scan#ONE_D_MODE},
   *                 {@link Intents.Scan#QR_CODE_MODE}, {@link Intents.Scan#DATA_MATRIX_MODE}.
   */
  public static void setDecodeModeToIntent(Intent intent, String scanMode) {
    intent.putExtra(Intents.Scan.MODE, scanMode);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#PURE_BARCODE} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintPureBarcodeEnabledToIntent(Intent intent) {
    intent.putExtra(DecodeHintType.PURE_BARCODE.name(), true);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#TRY_HARDER} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintTryHarderEnabledToIntent(Intent intent) {
    intent.putExtra(DecodeHintType.TRY_HARDER.name(), true);
  }

  /**
   * Set allowed lengths of encoded data.
   * @param intent Target intent.
   * @param lengths allowed lengths.
   */
  public static void setDecodeHintAllowedLengthsToIntent(Intent intent, int[] lengths) {
    intent.putExtra(DecodeHintType.ALLOWED_LENGTHS.name(), lengths);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#ASSUME_CODE_39_CHECK_DIGIT} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintAssumeCode39CheckDigitEnabledToIntent(Intent intent) {
    intent.putExtra(DecodeHintType.ASSUME_CODE_39_CHECK_DIGIT.name(), true);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#ASSUME_GS1} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintAssumeGs1EnabledToIntent(Intent intent) {
    intent.putExtra(DecodeHintType.ASSUME_GS1.name(), true);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#RETURN_CODABAR_START_END} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintReturnCodabarStartEndEnabledToIntent(Intent intent) {
    intent.putExtra(DecodeHintType.RETURN_CODABAR_START_END.name(), true);
  }

  /**
   * Set prompt message onto {@code Intent}.
   * @param intent Target intent.
   * @param message Prompt message displayed on activity.
   */
  public static void setPromptMessageToIntent(Intent intent, String message) {
    intent.putExtra(Intents.Scan.PROMPT_MESSAGE, message);
  }

  /**
   * Get prompt message from {@code Intent}.
   * @param intent Target intent. It can be {@code null}.
   */
  public static String getPromptMessageFromIntentOrNull(Intent intent) {
    if (intent == null) return null;
    return intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
  }

  /**
   * Set optional parameters to specify the width and height of the scanning rectangle in pixels
   * to {@code Intent}.
   * @param intent Target intent.
   * @param width Width of scanning rectangle in pixels.
   * @param height Height of scanning rectangle in pixels.
   */
  public static void setSizeOfScanningRectangleInPxToIntent(Intent intent, int width, int height) {
    intent.putExtra(Intents.Scan.WIDTH, width);
    intent.putExtra(Intents.Scan.HEIGHT, height);
  }

  /**
   * Get the width of the scanning rectangle in pixels from {@code Intent}.
   * @param intent Target intent. It can be {@code null}.
   * @return Width of scanning rectangle in pixels if specified, or zero otherwise.
   */
  public static int getWidthOfScanningRectangleInPxFromIntentOrZero(Intent intent) {
    if (intent == null) return 0;
    return intent.getIntExtra(Intents.Scan.WIDTH, 0);
  }

  /**
   * Get the height of the scanning rectangle in pixels from {@code Intent}.
   * @param intent Target intent. It can be {@code null}.
   * @return Height of scanning rectangle in pixels if specified, or zero otherwise.
   */
  public static int getHeightOfScanningRectangleInPxFromIntentOrZero(Intent intent) {
    if (intent == null) return 0;
    return intent.getIntExtra(Intents.Scan.HEIGHT, 0);
  }

  /**
   * Set desired duration for which to pause after a successful scan to {@code Intent}.
   * @param intent It must not be {@code null}.
   * @param duration Desired duration in milliseconds.
   */
  public static void setResultDisplayDurationInMsToIntent(Intent intent, long duration) {
    intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, duration);
  }

  /**
   * Get desired duration for which to pause after a successful scan from {@code Intent}.
   * @param intent It can be {@code null}.
   * @return Desired duration in milliseconds retrieved from {@code intent} or default value.
   */
  public static long getResultDisplayDurationInMsFromIntentOrDefaultValue(Intent intent) {
    if (intent == null) return DEFAULT_INTENT_RESULT_DURATION_MS;
    return intent.getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS,
        DEFAULT_INTENT_RESULT_DURATION_MS);
  }

  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.capture);

    hasSurface = false;
    ambientLightManager = new AmbientLightManager(this);
  }

  @Override
  protected void onResume() {
    super.onResume();

    // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
    // want to open the camera driver and measure the screen size if we're going to show the help on
    // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
    // off screen.
    cameraManager = new CameraManager(getApplication());

    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    viewfinderView.setCameraManager(cameraManager);
    statusView = (TextView) findViewById(R.id.status_view);

    handler = null;

    resetStatusView();

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder);
    } else {
        // Install the callback and wait for surfaceCreated() to init the camera.
        surfaceHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        // See: http://developer.android.com/guide/topics/media/camera.html
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    ambientLightManager.start(cameraManager);

    Intent intent = getIntent();

    decodeFormats = null;
    characterSet = null;

    if (intent != null) {

        // Scan the formats the intent requested, and return the result to the calling activity.
        decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
        decodeHints = DecodeHintManager.parseDecodeHints(intent);

        if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
          int width = getWidthOfScanningRectangleInPxFromIntentOrZero(intent);
          int height = getHeightOfScanningRectangleInPxFromIntentOrZero(intent);
          if (width > 0 && height > 0) {
            cameraManager.setManualFramingRect(width, height);
          }
        }
        
        String customPromptMessage = getPromptMessageFromIntentOrNull(intent);
        if (customPromptMessage != null) {
          statusView.setText(customPromptMessage);
        }

        characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

    }
  }

  @Override
  protected void onPause() {
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    ambientLightManager.stop();
    cameraManager.closeDriver();
    if (!hasSurface) {
      SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
      SurfaceHolder surfaceHolder = surfaceView.getHolder();
      surfaceHolder.removeCallback(this);
    }
    super.onPause();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_BACK:
        setResult(RESULT_CANCELED);
        finish();
        return true;
      case KeyEvent.KEYCODE_FOCUS:
      case KeyEvent.KEYCODE_CAMERA:
        // Handle these events so they don't launch the Camera app
        return true;
      // Use volume up/down to turn on light
      case KeyEvent.KEYCODE_VOLUME_DOWN:
        cameraManager.setTorch(false);
        return true;
      case KeyEvent.KEYCODE_VOLUME_UP:
        cameraManager.setTorch(true);
        return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public final void surfaceCreated(SurfaceHolder holder) {
    if (holder == null) {
      Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
    }
    if (!hasSurface) {
      hasSurface = true;
      initCamera(holder);
    }
  }

  @Override
  public final void surfaceDestroyed(SurfaceHolder holder) {
    hasSurface = false;
  }

  @Override
  public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
  }

  /**
   * A valid barcode has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   * @param scaleFactor amount by which thumbnail was scaled
   * @param barcode   A greyscale bitmap of the camera data which was decoded.
   */
  public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
    boolean fromLiveScan = barcode != null;
    if (fromLiveScan) {
      // Then not from history, so we have an image to draw on
      drawResultPoints(barcode, scaleFactor, rawResult);
    }

    handleDecodeExternally(rawResult, barcode);
  }

  /**
   * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
   *
   * @param barcode   A bitmap of the captured image.
   * @param scaleFactor amount by which thumbnail was scaled
   * @param rawResult The decoded results which contains the points to draw.
   */
  private void drawResultPoints(Bitmap barcode, float scaleFactor, Result rawResult) {
    ResultPoint[] points = rawResult.getResultPoints();
    if (points != null && points.length > 0) {
      Canvas canvas = new Canvas(barcode);
      Paint paint = new Paint();
      paint.setColor(getResources().getColor(R.color.result_points));
      if (points.length == 2) {
        paint.setStrokeWidth(4.0f);
        drawLine(canvas, paint, points[0], points[1], scaleFactor);
      } else if (points.length == 4 &&
                 (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                  rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
        // Hacky special case -- draw two lines, for the barcode and metadata
        drawLine(canvas, paint, points[0], points[1], scaleFactor);
        drawLine(canvas, paint, points[2], points[3], scaleFactor);
      } else {
        paint.setStrokeWidth(10.0f);
        for (ResultPoint point : points) {
          if (point != null) {
            canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
          }
        }
      }
    }
  }

  private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
    if (a != null && b != null) {
      canvas.drawLine(scaleFactor * a.getX(), 
                      scaleFactor * a.getY(), 
                      scaleFactor * b.getX(), 
                      scaleFactor * b.getY(), 
                      paint);
    }
  }

  // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
  private void handleDecodeExternally(Result rawResult, Bitmap barcode) {

    if (barcode != null) {
      viewfinderView.drawResultBitmap(barcode);
    }

    long resultDurationMS = getResultDisplayDurationInMsFromIntentOrDefaultValue(getIntent());
    if (resultDurationMS > 0) {
      String rawResultString = String.valueOf(rawResult);
      if (rawResultString.length() > 32) {
        rawResultString = rawResultString.substring(0, 32) + " ...";
      }
      statusView.setText(rawResultString);
    }
      
      // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
      // the deprecated intent is retired.
      Intent intent = new Intent(getIntent().getAction());
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
      intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
      byte[] rawBytes = rawResult.getRawBytes();
      if (rawBytes != null && rawBytes.length > 0) {
        intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
      }
      Map<ResultMetadataType,?> metadata = rawResult.getResultMetadata();
      if (metadata != null) {
        if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
          intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
                          metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
        }
        Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
        if (orientation != null) {
          intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
        }
        String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
        if (ecLevel != null) {
          intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
        }
        @SuppressWarnings("unchecked")
        Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
        if (byteSegments != null) {
          int i = 0;
          for (byte[] byteSegment : byteSegments) {
            intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
            i++;
          }
        }
      }
      sendReplyMessage(R.id.return_scan_result, intent, resultDurationMS);

  }
  
  private void sendReplyMessage(int id, Object arg, long delayMS) {
    if (handler != null) {
      Message message = Message.obtain(handler, id, arg);
      if (delayMS > 0L) {
        handler.sendMessageDelayed(message, delayMS);
      } else {
        handler.sendMessage(message);
      }
    }
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    if (surfaceHolder == null) {
      throw new IllegalStateException("No SurfaceHolder provided");
    }
    if (cameraManager.isOpen()) {
      Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
      return;
    }
    try {
      cameraManager.openDriver(surfaceHolder);
      // Creating the handler starts the preview, which can also throw a RuntimeException.
      if (handler == null) {
        handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
      }
    } catch (IOException ioe) {
      Log.w(TAG, ioe);
      displayFrameworkBugMessageAndExit();
    } catch (RuntimeException e) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializing camera", e);
      displayFrameworkBugMessageAndExit();
    }
  }

  protected String getBugMessageTitle() {
    return "Error Occurred";
  }

  protected String getDefaultStatusMessage() {
    return getResources().getString(R.string.msg_default_status);
  }

  private void displayFrameworkBugMessageAndExit() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getBugMessageTitle());
    builder.setMessage(getString(R.string.msg_camera_framework_bug));
    builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
    builder.setOnCancelListener(new FinishListener(this));
    builder.show();
  }

  private void resetStatusView() {
    statusView.setText(getDefaultStatusMessage());
    statusView.setVisibility(View.VISIBLE);
    viewfinderView.setVisibility(View.VISIBLE);
  }

  public void drawViewfinder() {
    viewfinderView.drawViewfinder();
  }
}
