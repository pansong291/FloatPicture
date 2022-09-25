package pansong291.floatpic.service;

import android.app.Service;
import android.widget.Toast;
import pansong291.crash.ASControl;
import android.content.SharedPreferences;

public abstract class ZService extends Service {
  protected SharedPreferences sharedPreferences;

  @Override
  public void onCreate() {
    super.onCreate();
    ASControl.getASControl().addService(this);
    sharedPreferences = getSharedPreferences(getPackageName() + "_preferences", 0);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    ASControl.getASControl().removeService(this);
  }

  public void toast(String s) {
    toast(s, 0);
  }

  public void toast(String s, int i) {
    Toast.makeText(this, s, i).show();
  }
}
