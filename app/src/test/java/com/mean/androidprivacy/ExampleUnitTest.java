package com.mean.androidprivacy;

import com.mean.androidprivacy.server.RemoteServer;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getResult(){
        String xml = null;
        try {
            xml = RemoteServer.getInstance().getResult("5E7D6134D494CAC48A8A1E34BB356577");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(xml);
    }
}