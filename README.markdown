android-lib-ZXingCaptureActivity
========================================

This library provides an activity which has the feature of barcode reader, based on ZXing core
library. This project contains files derived from ZXing project.

* [ZXing project](https://github.com/zxing/zxing)

## Maven Central

* [Maven Central Repository Search Engine — info.vividcode.android.zxing:capture-activity](http://search.maven.org/#search|gav|1|g%3A%22info.vividcode.android.zxing%22%20AND%20a%3A%22capture-activity%22)

## Usage

This library is published to Maven Central.
You can use android-libZXingCaptureActivity by writing dependencies on your build.gradle as below.

```groovy
repositories {
    mavenCentral()
}
dependencies {
    compile 'info.vividcode.android.zxing:capture-activity:2.3.0-1.+'
}
```

An activity which this library provides is `info.vividcode.android.zxing.CaptureActivity`.
To use it, you need to declares it.
(You can also define subclass of `CaptureActivity` and use it, if you want.)

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="org.example.your.app.pkg">
    <application>
        <activity android:name="info.vividcode.android.zxing.CaptureActivity"
            android:theme="@style/Theme.ZXingCaptureActivity"
            android:screenOrientation="landscape"/>
```

To start `CaptureActivity` activity and receive result of barcode scanning, call
`startActivityForResult` method as below.

```java
  // Create intent.
  Intent captureIntent = new Intent(this, CaptureActivity.class);
  // Using `CaptureActivityIntents`, set parameters to an intent.
  // (There is no requisite parameter to set to an intent.)
  // For instance, `setPromptMessage` method set prompt message displayed on `CaptureActivity`.
  CaptureActivityIntents.setPromptMessage(captureIntent, "Barcode scanning...");
  // Start activity.
  startActivityForResult(captureIntent, 1);
```

After reading barcode, you receive the result through `onActivityResult` callback method.

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data){
  super.onActivityResult(requestCode, resultCode, data);
  if (requestCode == 1) {
    if(resultCode == RESULT_OK) {
      CaptureResult res = CaptureResult.parseResultIntent(data);
      Toast.makeText(this, res.getContents() + " (" + res.getFormatName() + ")", Toast.LENGTH_LONG).show();
    } else {
      // Process comes here when “back” button was clicked for instance.
    }
  }
}
```

## License

This project is released under the Apache License, Version 2.0.

* [The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
