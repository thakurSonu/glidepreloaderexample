import android.content.Context;
import android.os.Process;
import android.util.Log;


import com.bumptech.glide.request.FutureTarget;
import com.virinchi.api.model.channel.MixList;
import com.virinchi.api.model.event.MediaList;
import com.virinchi.mychat.ui.feed.util.ProductUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A class for interfacing with Flickr's http API.
 */
public final class GlidePreloadUtils {

  private static String TAG = "GlidePre";


  public static class BackgroundThumbnailFetcher implements Runnable {
    private final Context context;
    private final List<MixList> photos;

    private boolean isCancelled;

    public BackgroundThumbnailFetcher(Context context, List<MixList> photos) {
      this.context = context;
      this.photos = photos;
    }

    public void cancel() {
      isCancelled = true;
    }

    @Override
    public void run() {
      Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);

      try {
        if (isCancelled) {
          return;
        }

        for (MixList photo : photos) {

         if (photo.getProduct_type() == 1) {

            for (MediaList mediaList : photo.getProduct().getMedia_list()) {

              String downloadUrl = "";
              if (mediaList.getFileType().equalsIgnoreCase("image"))
                downloadUrl = mediaList.getFileUrl();
              else if (mediaList.getFileType().equalsIgnoreCase("video")
                      || mediaList.getFileType().equalsIgnoreCase("document") ||
                      mediaList.getFileType().equalsIgnoreCase("video"))
                downloadUrl = mediaList.getPreview_url();

              mediaWork(downloadUrl, context);
            }

          }

        }
      }catch (Exception ex){
        Log.e(TAG, " ex "+ex.getMessage());
      }
    }
  }


  private static void mediaWork(String downloadUrl, Context context){

    FutureTarget<File> futureTarget = GlideApp.with(context)
            .downloadOnly()
            .load(downloadUrl)
            .submit(SQUARE_THUMB_SIZE, SQUARE_THUMB_SIZE);

    try {
      futureTarget.get();
    } catch (InterruptedException e) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Interrupted waiting for background downloadOnly", e);
      }
    } catch (ExecutionException e) {
      if (Log.isLoggable(TAG, Log.DEBUG)) {
        Log.d(TAG, "Got ExecutionException waiting for background downloadOnly", e);
      }
    }
    GlideApp.with(context).clear(futureTarget);


  }

}
