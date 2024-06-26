package me.bartholdy.endlessjump.Server;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Detect changes of the JAR file
 * and shutdown the server when it has changed.
 * <p>
 * For the purpose of a JLine bug fix
 */
public class Debugger {
    private static final ComponentLogger LOGGER = ComponentLogger.logger(Debugger.class);
    private String path;
    private long lastModified;

    public Debugger() {
        try {
            path = Debugger.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException("MD5 fucked up!");
        }

        File serverJar = getPluginJar();
        path = serverJar.getPath();
        lastModified = serverJar.lastModified();
//        nice cock


        // Run every 5 seconds
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runnable(), 0, 3, TimeUnit.SECONDS);
    }

    private Runnable runnable() {
        return () -> {
            if (lastModified != getPluginJar().lastModified()) {
                LOGGER.info(Component.text("JAR updated: Stopping server…", NamedTextColor.DARK_GREEN));
                System.exit(0);
            }
        };
    }

    private File getPluginJar() {
        return new File("./EndlessJump-1.0-SNAPSHOT.jar");
    }

//    @Override
//    public void run() {
//        LOGGER.info("Scanning file for changes…");
//        String checksum;
//        try {
//            checksum = MD5Checksum.getMD5Checksum(path);
//        } catch (Exception e) {
//            LOGGER.error("Failed to create checksum for " + path);
//            throw new RuntimeException(e);
//        }
//
//        if (previousChecksum == null) {
//            previousChecksum = checksum;
//            LOGGER.info("Checksum set");
//            return;
//        }
//
//        if (!Objects.equals(previousChecksum, checksum)) {
//            LOGGER.info(Component.text("JAR updated: Stopping server…", NamedTextColor.DARK_GREEN));
//            MinecraftServer.stopCleanly();
//            System.exit(0);
//        }


}

class MD5Checksum {

    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

}
