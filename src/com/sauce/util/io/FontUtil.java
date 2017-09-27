package com.sauce.util.io;

import java.nio.ByteBuffer;

public class FontUtil {

    public class FontInfo{

    }

    public class IOFont extends ResourceUtil.IOResource {

        private FontInfo fontInfo;

        private IOFont(ByteBuffer aBuffer, FontInfo info) {
            super(aBuffer);
            fontInfo = info;
        }
    }
}
