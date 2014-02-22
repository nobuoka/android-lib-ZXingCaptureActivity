/*
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

import android.content.Intent;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import java.util.Collection;

public class CaptureActivityIntents {
  private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;

  /**
   * Set barcode formats to scan for onto {@code Intent}.
   * This setting precedes to setting by {@code #setDecodeFormats} method.
   * In case that {@code formats} is empty collection, this method do nothing.
   * @param intent Target intent.
   * @param formats Barcode formats to scan for.
   */
  public static void setDecodeFormats(Intent intent, Collection<BarcodeFormat> formats) {
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
  public static void setDecodeMode(Intent intent, String scanMode) {
    intent.putExtra(Intents.Scan.MODE, scanMode);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#PURE_BARCODE} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintPureBarcodeEnabled(Intent intent) {
    intent.putExtra(DecodeHintType.PURE_BARCODE.name(), true);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#TRY_HARDER} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintTryHarderEnabled(Intent intent) {
    intent.putExtra(DecodeHintType.TRY_HARDER.name(), true);
  }

  /**
   * Set allowed lengths of encoded data.
   * @param intent Target intent.
   * @param lengths allowed lengths.
   */
  public static void setDecodeHintAllowedLengths(Intent intent, int[] lengths) {
    intent.putExtra(DecodeHintType.ALLOWED_LENGTHS.name(), lengths);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#ASSUME_CODE_39_CHECK_DIGIT} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintAssumeCode39CheckDigitEnabled(Intent intent) {
    intent.putExtra(DecodeHintType.ASSUME_CODE_39_CHECK_DIGIT.name(), true);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#ASSUME_GS1} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintAssumeGs1Enabled(Intent intent) {
    intent.putExtra(DecodeHintType.ASSUME_GS1.name(), true);
  }

  /**
   * Set decode hint to intent to enable {@link DecodeHintType#RETURN_CODABAR_START_END} feature.
   * @param intent Target intent.
   */
  public static void setDecodeHintReturnCodabarStartEndEnabled(Intent intent) {
    intent.putExtra(DecodeHintType.RETURN_CODABAR_START_END.name(), true);
  }

  /**
   * Set prompt message onto {@code Intent}.
   * @param intent Target intent.
   * @param message Prompt message displayed on activity.
   */
  public static void setPromptMessage(Intent intent, String message) {
    intent.putExtra(Intents.Scan.PROMPT_MESSAGE, message);
  }

  /**
   * Get prompt message from {@code Intent}.
   * @param intent Target intent. It can be {@code null}.
   */
  public static String getPromptMessageOrNull(Intent intent) {
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
  public static void setSizeOfScanningRectangleInPx(Intent intent, int width, int height) {
    intent.putExtra(Intents.Scan.WIDTH, width);
    intent.putExtra(Intents.Scan.HEIGHT, height);
  }

  /**
   * Get the width of the scanning rectangle in pixels from {@code Intent}.
   * @param intent Target intent. It can be {@code null}.
   * @return Width of scanning rectangle in pixels if specified, or zero otherwise.
   */
  public static int getWidthOfScanningRectangleInPxOrZero(Intent intent) {
    if (intent == null) return 0;
    return intent.getIntExtra(Intents.Scan.WIDTH, 0);
  }

  /**
   * Get the height of the scanning rectangle in pixels from {@code Intent}.
   * @param intent Target intent. It can be {@code null}.
   * @return Height of scanning rectangle in pixels if specified, or zero otherwise.
   */
  public static int getHeightOfScanningRectangleInPxOrZero(Intent intent) {
    if (intent == null) return 0;
    return intent.getIntExtra(Intents.Scan.HEIGHT, 0);
  }

  /**
   * Set desired duration for which to pause after a successful scan to {@code Intent}.
   * @param intent Target intent.
   * @param duration Desired duration in milliseconds.
   */
  public static void setResultDisplayDurationInMs(Intent intent, long duration) {
    intent.putExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS, duration);
  }

  /**
   * Get desired duration for which to pause after a successful scan from {@code Intent}.
   * @param intent Target intent.
   * @return Desired duration in milliseconds retrieved from {@code intent} or default value.
   */
  public static long getResultDisplayDurationInMsOrDefaultValue(Intent intent) {
    if (intent == null) return DEFAULT_INTENT_RESULT_DURATION_MS;
    return intent.getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS,
        DEFAULT_INTENT_RESULT_DURATION_MS);
  }
}
