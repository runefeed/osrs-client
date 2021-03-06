package org.osrs.updater;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * User: Johan
 * Date: 2013-03-18
 * Time: 14:56
 */
public class ClientUpdater {
    private static ClientUpdater ourInstance = new ClientUpdater();

    public static ClientUpdater getInstance() {
        return ourInstance;
    }

    private ClientUpdater() {
    }

    public boolean hasUpdate() {
        int internalVersion = getCurrentVersion();
        int latestVersion = getLatestVersion();

        return latestVersion > internalVersion;
    }

    private int getLatestVersion() {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://72.44.88.164/osrs/version.txt");

        try {
            HttpResponse response = client.execute(get);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new Exception();
            }
            return Integer.parseInt(new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean hasLocalUpdate() {
        return new File("./update.tmp").exists();
    }

    public void applyLocalUpdate() {
        File f = new File("./update.tmp");
        f.renameTo(new File("./osrs.jar"));
        System.exit(0);
    }

    private int getCurrentVersion() {
        Enumeration resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        Attributes mainAttribs = manifest.getMainAttributes();
                        String version = mainAttribs.getValue("Build-Number");
                        if(version != null) {
                            return Integer.valueOf(version);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println("Current: " + ClientUpdater.getInstance().getCurrentVersion());
        System.out.println("Latest: " + ClientUpdater.getInstance().getLatestVersion());
    }

}
