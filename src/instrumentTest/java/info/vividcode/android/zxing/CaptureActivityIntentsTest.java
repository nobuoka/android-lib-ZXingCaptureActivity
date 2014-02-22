package info.vividcode.android.zxing;

import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.test.MoreAsserts;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class CaptureActivityIntentsTest extends InstrumentationTestCase {

  public static void test_setDecodeFormats() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeFormats(intent,
          Arrays.asList(BarcodeFormat.CODE_39, BarcodeFormat.AZTEC, BarcodeFormat.CODABAR));
      MoreAsserts.assertContentsInAnyOrder(DecodeFormatManager.parseDecodeFormats(intent),
          BarcodeFormat.CODE_39, BarcodeFormat.AZTEC, BarcodeFormat.CODABAR);
    }
    { // One item
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeFormats(intent,
          Arrays.asList(BarcodeFormat.CODE_39));
      MoreAsserts.assertContentsInAnyOrder(DecodeFormatManager.parseDecodeFormats(intent),
          BarcodeFormat.CODE_39);
    }
    { // Empty list
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeFormats(intent, Arrays.<BarcodeFormat>asList());
      assertNull("In case that empty formats list is passed, value is not set",
          DecodeFormatManager.parseDecodeFormats(intent));
    }
  }

  private static void doTestSetDecodeMode(
      String modeStr, Collection<BarcodeFormat> expectedFormats) {
    Intent intent = new Intent("DUMMY_ACTION");
    CaptureActivityIntents.setDecodeMode(intent, modeStr);
    Collection<BarcodeFormat> decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
    MoreAsserts.assertContentsInAnyOrder(decodeFormats, expectedFormats.toArray());
  }

  public static void test_setDecodeMode() {
    {
      doTestSetDecodeMode(Intents.Scan.PRODUCT_MODE, DecodeFormatManager.PRODUCT_FORMATS);
      doTestSetDecodeMode(Intents.Scan.QR_CODE_MODE, DecodeFormatManager.QR_CODE_FORMATS);
      doTestSetDecodeMode(Intents.Scan.ONE_D_MODE, DecodeFormatManager.ONE_D_FORMATS);
      doTestSetDecodeMode(
          Intents.Scan.DATA_MATRIX_MODE, DecodeFormatManager.DATA_MATRIX_FORMATS);
    }
    { // Invalid mode string
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeMode(intent, "");
      Collection<BarcodeFormat> decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
      assertNull(decodeFormats);
    }
  }

  public static void test_setDecodeHintMethods() {
    class TestProcess {
      void assertThatSpecifiedHintTypeEnabled(Intent intent, DecodeHintType type) {
        Map<DecodeHintType, Object> hintMap = DecodeHintManager.parseDecodeHints(intent);
        assertEquals(1, hintMap.size());
        assertTrue(hintMap.containsKey(type));
        Object value = hintMap.get(type);
        MoreAsserts.assertAssignableFrom(Boolean.class, value.getClass());
        assertEquals(Boolean.TRUE, value);
      }
    }
    TestProcess p = new TestProcess();
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeHintPureBarcodeEnabled(intent);
      p.assertThatSpecifiedHintTypeEnabled(intent, DecodeHintType.PURE_BARCODE);
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeHintTryHarderEnabled(intent);
      p.assertThatSpecifiedHintTypeEnabled(intent, DecodeHintType.TRY_HARDER);
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeHintAssumeCode39CheckDigitEnabled(intent);
      p.assertThatSpecifiedHintTypeEnabled(intent, DecodeHintType.ASSUME_CODE_39_CHECK_DIGIT);
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeHintAssumeGs1Enabled(intent);
      p.assertThatSpecifiedHintTypeEnabled(intent, DecodeHintType.ASSUME_GS1);
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeHintReturnCodabarStartEndEnabled(intent);
      p.assertThatSpecifiedHintTypeEnabled(intent, DecodeHintType.RETURN_CODABAR_START_END);
    }
  }

  public static void test_setDecodeHintAllowedLengths() {
    class TestProcess {
      void exec(int[] lengths) {
        Intent intent = new Intent("DUMMY_ACTION");
        CaptureActivityIntents.setDecodeHintAllowedLengths(intent, lengths);
        Map<DecodeHintType, Object> hintMap = DecodeHintManager.parseDecodeHints(intent);
        assertEquals(1, hintMap.size());
        assertTrue(hintMap.containsKey(DecodeHintType.ALLOWED_LENGTHS));
        Object value = hintMap.get(DecodeHintType.ALLOWED_LENGTHS);
        MoreAsserts.assertAssignableFrom(int[].class, value.getClass());
        MoreAsserts.assertEquals(lengths, (int[]) value);
      }
    }
    TestProcess p = new TestProcess();
    p.exec(new int[] { 1, 2, 3 });
    p.exec(new int[] {}); // empty array
  }

  public static void test_setDecodeHintCharacterSet() {
    Intent intent = new Intent("DUMMY_ACTION");
    CaptureActivityIntents.setDecodeHintCharacterSet(intent, "characterSet");
    assertEquals("characterSet", CaptureActivityIntents.getDecodeHintCharacterSetOrNull(intent));
  }

  public static void test_getDecodeHintCharacterSetOrNull() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setDecodeHintCharacterSet(intent, "characterSet");
      assertEquals("characterSet", CaptureActivityIntents.getDecodeHintCharacterSetOrNull(intent));
    }
    { // In case that character set is not specified
      Intent intent = new Intent("DUMMY_ACTION");
      assertNull(CaptureActivityIntents.getDecodeHintCharacterSetOrNull(intent));
    }
    { // In case that `intent` is `null`
      assertNull(CaptureActivityIntents.getDecodeHintCharacterSetOrNull(null));
    }
  }

  public void test_setFrontLightAutoModeEnabled() {
    Intent intent = new Intent("DUMMY_ACTION");
    CaptureActivityIntents.setFrontLightAutoModeEnabled(intent);
    assertEquals(true, CaptureActivityIntents.getFrontLightAutoMode(intent));
  }

  public void test_getFrontLightAutoMode() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setFrontLightAutoModeEnabled(intent);
      assertEquals(true, CaptureActivityIntents.getFrontLightAutoMode(intent));
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      assertEquals(false, CaptureActivityIntents.getFrontLightAutoMode(intent));
    }
    {
      assertEquals(false, CaptureActivityIntents.getFrontLightAutoMode(null));
    }
  }

  public void test_setPromptMessage() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setPromptMessage(intent, "message");
      assertEquals("message", CaptureActivityIntents.getPromptMessageOrNull(intent));
    }
  }

  public void test_getPromptMessageOrNull() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setPromptMessage(intent, "message");
      assertEquals("message", CaptureActivityIntents.getPromptMessageOrNull(intent));
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      assertNull(CaptureActivityIntents.getPromptMessageOrNull(intent));
    }
    {
      assertNull(CaptureActivityIntents.getPromptMessageOrNull(null));
    }
  }

  public void test_setSizeOfScanningRectangleInPx() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setSizeOfScanningRectangleInPx(intent, 5, 10);
      assertEquals(5, CaptureActivityIntents.getWidthOfScanningRectangleInPxOrZero(intent));
      assertEquals(10, CaptureActivityIntents.getHeightOfScanningRectangleInPxOrZero(intent));
    }
  }

  public void test_getWidthAndHeightOfScanningRectangleInPx() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      CaptureActivityIntents.setSizeOfScanningRectangleInPx(intent, 5, 10);
      assertEquals(5, CaptureActivityIntents.getWidthOfScanningRectangleInPxOrZero(intent));
      assertEquals(10, CaptureActivityIntents.getHeightOfScanningRectangleInPxOrZero(intent));
    }
    { // In case that size is not specified, it returns zero.
      Intent intent = new Intent("DUMMY_ACTION");
      assertEquals(0, CaptureActivityIntents.getWidthOfScanningRectangleInPxOrZero(intent));
      assertEquals(0, CaptureActivityIntents.getHeightOfScanningRectangleInPxOrZero(intent));
    }
    { // In case that argument is `null`, it returns zero.
      assertEquals(0, CaptureActivityIntents.getWidthOfScanningRectangleInPxOrZero(null));
      assertEquals(0, CaptureActivityIntents.getHeightOfScanningRectangleInPxOrZero(null));
    }
  }

  public void test_setResultDisplayDurationInMs() {
    {
      Intent intent = new Intent("DUMMY_ACTION");
      long setValue = 300L;
      try {
        CaptureActivityIntents.setResultDisplayDurationInMs(intent, setValue);
        assertTrue("Error not occurred", true);
      } catch (Throwable err) {
        assertTrue("Error occurred: " + err.getMessage(), false);
      }
    }
  }

  public void test_getResultDisplayDurationInMs() {
    long defaultValue = 1500L;
    {
      Intent intent = new Intent("DUMMY_ACTION");
      long setValue = 300L;
      CaptureActivityIntents.setResultDisplayDurationInMs(intent, setValue);
      long val = CaptureActivityIntents.getResultDisplayDurationInMsOrDefaultValue(intent);
      assertEquals("Returns set value", setValue, val);
    }
    {
      long val = CaptureActivityIntents.getResultDisplayDurationInMsOrDefaultValue(null);
      assertEquals("Returns default value if argument is `null`", defaultValue, val);
    }
    {
      Intent intent = new Intent("DUMMY_ACTION");
      long val = CaptureActivityIntents.getResultDisplayDurationInMsOrDefaultValue(intent);
      assertEquals("Returns default value if `intent` doesn't have display duration",
          defaultValue, val);
    }
  }

}
