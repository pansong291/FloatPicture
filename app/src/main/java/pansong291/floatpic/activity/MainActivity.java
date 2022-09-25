package pansong291.floatpic.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import pansong291.floatpic.R;
import pansong291.floatpic.service.MainService;

public class MainActivity extends Zactivity {
  private static final int CHOOSE_IMAGE_FILE_CODE = 9831;
  private EditText edt_file_path;
  private String originPath;

  private void initView() {
    edt_file_path = findViewById(R.id.edt_file_path);
    originPath = sharedPreferences.getString("file_path", null);
    if (originPath != null) edt_file_path.setText(originPath);
  }

  private boolean isPathChanged() {
    String str = edt_file_path.getText().toString();
    if (!str.isEmpty()) {
      return originPath == null || !originPath.equals(str);
    }
    return false;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
    requestFloatWindow(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (isPathChanged()) {
      sharedPreferences.edit().putString("file_path", edt_file_path.getText().toString()).commit();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //此处的requestCode用于判断接收的Activity是不是你想要的那个
    if (resultCode == RESULT_OK && requestCode == CHOOSE_IMAGE_FILE_CODE) {
      //获得图片的uri
      Uri uri = data.getData();
      String picPath;
      try {
        String[]proj = {MediaStore.Images.Media.DATA};
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        //按我个人理解 这个是获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(proj[0]);
        //将光标移至开头，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径
        picPath = cursor.getString(column_index);
      } catch (Exception e) {
        picPath = uri.getPath();
      }
      edt_file_path.setText(picPath);
    } else if (resultCode == RESULT_OK) {
      toast("请重新选择图片");
    }
  }

  public void onChoosePictureClick(View v) {
    Intent it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(Intent.createChooser(it, "选择图片"), CHOOSE_IMAGE_FILE_CODE);
  }

  public void onStartClick(View v) {
    Intent intent = new Intent(this, MainService.class);
    if (isPathChanged()) {
      intent.putExtra(MainService.IMG_FILE_PATH, edt_file_path.getText().toString());
    }
    startService(intent);
  }

  public void onStopClick(View v) {
    Intent intent = new Intent(this, MainService.class);
    stopService(intent);
  }

  public static void requestFloatWindow(final Zactivity ac) {
    if (Build.VERSION.SDK_INT >= 23) {
      if (!Settings.canDrawOverlays(ac)) {
        //若没有权限，提示获取.
        new AlertDialog.Builder(ac)
          .setTitle("提示")
          .setMessage("需要为本应用开启悬浮窗权限，请在弹出的页面中设置为允许。\n\nvivo等设备若重新打开软件仍显示此对话框，请自行前往设置-更多设置-权限管理，为本软件开启悬浮窗权限。")
          .setCancelable(false)
          .setNegativeButton("取消", null)
          .setPositiveButton("确定", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface p1, int p2) {
              Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
              ac.startActivity(intent);
              ac.finish();
            }
          }).show();
      }
    }
  }
}
