package info.vividcode.android.zxing;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.MoreAsserts;

import com.google.zxing.BarcodeFormat;

import java.util.Arrays;
import java.util.Collection;

public class CaptureActivityTest extends ActivityInstrumentationTestCase2<CaptureActivity> {

  public CaptureActivityTest() {
    super("info.vividcode.android.zxing", CaptureActivity.class);
  }

  public static void test_setDecodeFormatsToIntent() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivity.setDecodeFormatsToIntent(intent,
          Arrays.asList(BarcodeFormat.CODE_39, BarcodeFormat.AZTEC, BarcodeFormat.CODABAR));
      MoreAsserts.assertContentsInAnyOrder(DecodeFormatManager.parseDecodeFormats(intent),
          BarcodeFormat.CODE_39, BarcodeFormat.AZTEC, BarcodeFormat.CODABAR);
    }
    { // One item
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivity.setDecodeFormatsToIntent(intent,
          Arrays.asList(BarcodeFormat.CODE_39));
      MoreAsserts.assertContentsInAnyOrder(DecodeFormatManager.parseDecodeFormats(intent),
          BarcodeFormat.CODE_39);
    }
    { // Empty list
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivity.setDecodeFormatsToIntent(intent, Arrays.<BarcodeFormat>asList());
      assertNull("In case that empty formats list is passed, value is not set",
          DecodeFormatManager.parseDecodeFormats(intent));
    }
  }

  private static void doTestSetDecodeModeToIntent(
      String modeStr, Collection<BarcodeFormat> expectedFormats) {
    Intent intent = new Intent("DUMMY_ACTION");
    CaptureActivity.setDecodeModeToIntent(intent, modeStr);
    Collection<BarcodeFormat> decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
    MoreAsserts.assertContentsInAnyOrder(decodeFormats, expectedFormats.toArray());
  }

  public static void test_setDecodeModeToIntent() {
    {
      doTestSetDecodeModeToIntent(Intents.Scan.PRODUCT_MODE, DecodeFormatManager.PRODUCT_FORMATS);
      doTestSetDecodeModeToIntent(Intents.Scan.QR_CODE_MODE, DecodeFormatManager.QR_CODE_FORMATS);
      doTestSetDecodeModeToIntent(Intents.Scan.ONE_D_MODE, DecodeFormatManager.ONE_D_FORMATS);
      doTestSetDecodeModeToIntent(
          Intents.Scan.DATA_MATRIX_MODE, DecodeFormatManager.DATA_MATRIX_FORMATS);
    }
    { // Invalid mode string
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivity.setDecodeModeToIntent(intent, "");
      Collection<BarcodeFormat> decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
      assertNull(decodeFormats);
    }
  }

  public void test_setResultDisplayDurationInMsFromIntent() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      long setValue = 300L;
      try {
        CaptureActivity.setResultDisplayDurationInMsToIntent(intent, setValue);
        assertTrue("Error not occurred", true);
      } catch (Throwable err) {
        assertTrue("Error occurred: " + err.getMessage(), false);
      }
    }
  }

  public void test_getResultDisplayDurationInMsFromIntent() {
    long defaultValue = 1500L;
    {
      Intent intent = new Intent("DUMMY_ACTION");
      long setValue = 300L;
      CaptureActivity.setResultDisplayDurationInMsToIntent(intent, setValue);
      long val = CaptureActivity.getResultDisplayDurationInMsFromIntentOrDefaultValue(intent);
      assertEquals("Returns set value", setValue, val);
    }
    {
      long val = CaptureActivity.getResultDisplayDurationInMsFromIntentOrDefaultValue(null);
      assertEquals("Returns default value if argument is `null`", defaultValue, val);
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      long val = CaptureActivity.getResultDisplayDurationInMsFromIntentOrDefaultValue(intent);
      assertEquals("Returns default value if `intent` doesn't have display duration",
          defaultValue, val);
    }
  }

}
