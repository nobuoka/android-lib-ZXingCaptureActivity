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

/**
 * <p>Encapsulates the result of a barcode scan invoked by {@link CaptureActivity}.</p>
 *
 * @author NOBUOKA Yu
 */
public final class CaptureResult {

    private final String mContents;
    private final String mFormatName;

    /**
     * @param data {@code Intent} object, which represents the result of a barcode scan.
     * @return {@code CaptureResult} object, which encapsulates the result represented by {@code data}.
     */
    public static CaptureResult parseResultIntent(Intent data) {
        String contents = data.getStringExtra(Intents.Scan.RESULT);
        String formatName = data.getStringExtra(Intents.Scan.RESULT_FORMAT);
        return new CaptureResult(contents, formatName);
    }

    private CaptureResult(String contents, String formatName) {
        mContents = contents;
        mFormatName = formatName;
    }

    /**
     * @return content of barcode.
     */
    public String getContents() {
        return mContents;
    }

    /**
     * @return format name of barcode, like "QR_CODE", "UPC_A".
     */
    public String getFormatName() {
        return mFormatName;
    }

}
