package project.tarun.moviemeter;

/**
 * Created by mafia on 8/4/16.
 */
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class CustomUtil {

    private static final Uri TMDB_IMAGE_BASE_URI = Uri.parse("http://image.tmdb.org/t/p/");

    private interface TMDbImageWidth {
        String getWidthString();
        int getMaxWidth();
    }

    public enum TMDbBackdropWidth implements TMDbImageWidth {
        ORIGINAL(Integer.MAX_VALUE);

        public final int maxWidth;
        TMDbBackdropWidth(int maxWidth)
        {
            this.maxWidth = maxWidth;
        }
        public int getMaxWidth()
        {
            return this.maxWidth;

        }

        public String getWidthString() {
            return (this == ORIGINAL) ? "original" : "w" + this.maxWidth;
        }
    }

    public static String buildBackdropUrl(String backdropPath, int backdropWidth) {
        return buildImageUrl(backdropPath, computeNextLowestBackdropWidth(backdropWidth));
    }

    private static <T extends TMDbImageWidth> String buildImageUrl(String imagePath, T tmdbImageWidth) {
        if (BuildConfig.DEBUG) {
            Log.d("Picasso", "Loading image of width " + tmdbImageWidth.getMaxWidth() + "px");
        }
        String relativePath = tmdbImageWidth.getWidthString() + "/" + imagePath;
        return Uri.withAppendedPath(TMDB_IMAGE_BASE_URI, relativePath).toString();
    }

    public static int getScreenWidth(@NonNull Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private static TMDbBackdropWidth computeNextLowestBackdropWidth(int backdropWidth) {
        for (TMDbBackdropWidth enumWidth : TMDbBackdropWidth.values()) {
            if (0.8 * backdropWidth <= enumWidth.maxWidth) {
                return enumWidth;
            }
        }
        return TMDbBackdropWidth.ORIGINAL;
    }

}