package noxmon.autoinstall;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class NoxMonProvider extends ContentProvider {
    private static final String TAG = "NoxMonInstaller";
    private static final String ASSET_PATH = "noxmon/NoxMon-1.0.0.jar.b64";
    private static final String MOD_FILE = "NoxMon-1.0.0.jar";

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if (context == null) return true;

        try {
            File launcherRoot;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                launcherRoot = context.getExternalFilesDir(null);
            } else {
                launcherRoot = new File(Environment.getExternalStorageDirectory(), "games/Amethyst");
            }

            if (launcherRoot == null) {
                Log.w(TAG, "External storage is not available yet");
                return true;
            }

            File modsDir = new File(new File(launcherRoot, ".minecraft"), "mods");
            if (!modsDir.exists() && !modsDir.mkdirs()) {
                Log.w(TAG, "Could not create mods directory: " + modsDir);
                return true;
            }

            byte[] encoded;
            try (InputStream input = context.getAssets().open(ASSET_PATH);
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                byte[] chunk = new byte[8192];
                int read;
                while ((read = input.read(chunk)) != -1) {
                    buffer.write(chunk, 0, read);
                }
                encoded = buffer.toByteArray();
            }

            byte[] jarBytes = Base64.decode(encoded, Base64.DEFAULT);
            File target = new File(modsDir, MOD_FILE);

            if (!target.exists() || target.length() != jarBytes.length) {
                try (FileOutputStream output = new FileOutputStream(target, false)) {
                    output.write(jarBytes);
                    output.flush();
                }
                Log.i(TAG, "Installed " + MOD_FILE + " to " + target.getAbsolutePath());
            } else {
                Log.i(TAG, MOD_FILE + " is already installed");
            }
        } catch (Exception e) {
            Log.w(TAG, "Could not auto-install NoxMon", e);
        }

        return true;
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}
