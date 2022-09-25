package pansong291.floatpic;

import pansong291.floatpic.activity.CrashActivity;
import pansong291.crash.CrashApplication;

public class MyApplication extends CrashApplication {
  @Override
  public Class<?> getPackageActivity() {
    setShouldShowDeviceInfo(false);
    return CrashActivity.class;
  }
}
