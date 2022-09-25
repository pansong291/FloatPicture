package pansong291.floatpic.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import pansong291.crash.ASControl;

public class Zactivity extends Activity {
  protected SharedPreferences sharedPreferences;

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ASControl.getASControl().addActivity(this);
    sharedPreferences = getSharedPreferences(getPackageName() + "_preferences", 0);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ASControl.getASControl().removeActivity(this);
  }

  public void toast(String s) {
    toast(s, 0);
  }

  public void toast(String s, int i) {
    Toast.makeText(this, s, i).show();
  }
}
