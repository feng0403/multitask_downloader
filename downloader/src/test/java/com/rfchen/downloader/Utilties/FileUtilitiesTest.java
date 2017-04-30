package com.rfchen.downloader.Utilties;

import org.junit.Test;

/**
 * Created by feng on 2017/4/29.
 */
public class FileUtilitiesTest {
    @Test
    public void getMd5FileName() throws Exception {
        String md5FileName = FileUtilities.getMd5FileName("http://shouji.360tpcdn.com/170428/92c0ba2f0aeba67693c0239d8cc18df0/com.snda.wifilocating_3115.apk");
        System.out.println(md5FileName);

    }

}