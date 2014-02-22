package info.vividcode.android.zxing;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

public class CaptureActivityTest extends ActivityInstrumentationTestCase2<CaptureActivity> {

  public CaptureActivityTest() {
    super("info.vividcode.android.zxing", CaptureActivity.class);
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
