package com.ourcodeworld.plugins.filebrowser;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.nononsenseapps.filepicker.*;

public class FileOnlyPickerActivity extends FilePickerActivity {
  static int itemHeight = 0;

  public FileOnlyPickerActivity() {
      super();
  }

  @Override
  protected AbstractFilePickerFragment<File> getFragment(
          @Nullable final String startPath, final int mode, final boolean allowMultiple,
          final boolean allowCreateDir, final boolean allowExistingFile,
          final boolean singleClick) {
      AbstractFilePickerFragment<File> fragment = new CustomFilePickerFragment(this);
      // startPath is allowed to be null. In that case, default folder should be SD-card and not "/"
      fragment.setArgs(startPath != null ? startPath : Environment.getExternalStorageDirectory().getPath(),
              mode, allowMultiple, allowCreateDir, allowExistingFile, singleClick);
      return fragment;
  }



  public static class CustomFilePickerFragment extends FilePickerFragment {

      // File extension to filter on
      private static final String[] EXTENSIONS = new String[] {".torrent",".mp4", ".webm", ".mov", ".mkv"};

      public Activity activity;

      public CustomFilePickerFragment(Activity activity) {
        this.activity = activity;
      }
      /**
       *
       * @param file
       * @return The file extension. If file has no extension, it returns null.
       */
      private String getExtension(@NonNull File file) {
          String path = file.getPath();
          int i = path.lastIndexOf(".");
          if (i < 0) {
              return null;
          } else {
              return path.substring(i);
          }
      }

      @Override
      protected boolean isItemVisible(final File file) {
          boolean ret = super.isItemVisible(file);
          if (ret && !isDir(file) && (mode == MODE_FILE || mode == MODE_FILE_AND_DIR)) {
              String ext = getExtension(file);
              return ext != null && Arrays.asList(EXTENSIONS).contains(ext.toLowerCase());
          }
          return ret;
      }

      @Override
      public void onBindViewHolder(@NonNull DirViewHolder vh, int position, @NonNull File data) {
        super.onBindViewHolder(vh, position, data);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) vh.itemView.getLayoutParams();

        if (FileOnlyPickerActivity.itemHeight == 0)
            FileOnlyPickerActivity.itemHeight = params.height;

        int orgHeight = FileOnlyPickerActivity.itemHeight;
        params.height = (int)(orgHeight * 0.75);
        params.leftMargin = (int)(orgHeight * 0.15);
        vh.itemView.setLayoutParams(params);

        LinearLayout.LayoutParams iconParams = (LinearLayout.LayoutParams)vh.icon.getLayoutParams();
        iconParams.width = (int)(orgHeight * 0.75);
        iconParams.height = (int)(orgHeight * 0.75);
        vh.icon.setLayoutParams(iconParams);

        vh.icon.setVisibility(View.VISIBLE);
        if (!isDir(data)) {
          ImageView icon = (ImageView)vh.icon;
          
          String ext = getExtension(data).toLowerCase();
          ext = ext.substring(1, ext.length());
          Map<String, String> colors = new HashMap<String, String>();
          colors.put("torrent", "#2dd36f");
          colors.put("mp4", "#5260ff");
          colors.put("webm", "#3dc2ff");
          colors.put("mov", "#ffc409");
          colors.put("mkv", "#92949c");

          String color = colors.get(ext);
          if (color == null) return;

          int resourceId = getActivity().getResources().getIdentifier(ext, "drawable", getActivity().getPackageName());
          if (resourceId == 0) return;

          icon.setImageResource(resourceId);
          icon.setColorFilter(Color.parseColor(color));
          vh.text.setTextColor(Color.parseColor("#f4f5f8"));
        }
      }
  }
}