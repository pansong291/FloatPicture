package pansong291.floatpic.service;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import pansong291.floatpic.R;
import pansong291.floatpic.notification.MainNotification;
import pansong291.floatpic.view.ResizableImageView;
import pansong291.floatpic.view.ResizableImageView.ResizeType;

public class MainService extends ZService {
  public static final String START_FROM_NOTIFICATION = "START_FROM_NOTIFICATION";
  public static final String IMG_FILE_PATH = "IMG_FILE_PATH";
  private static final String DATA_CACHE = "DATA_CACHE";

  private WindowManager windowManager;
  // ------------ views
  private ResizableImageView resizableImageView;
  private ViewGroup editViewContainer;
  private Button btn_save;
  private Button btn_close;
  private Spinner spin_mode;
  private TextView txt_value;
  private Button btn_minus_1;
  private Button btn_plus_1;
  private Button btn_minus_10;
  private Button btn_plus_10;
  private Button btn_minus_100;
  private Button btn_plus_100;

  private LayoutParams containerLayoutParams;
  private MainServiceListener listener;

  private ResizeType resizeType;

  private void initView(View view) {
    this.btn_save = view.findViewById(R.id.btn_save);
    this.btn_close = view.findViewById(R.id.btn_close);
    this.spin_mode = view.findViewById(R.id.spin_mode);
    this.txt_value = view.findViewById(R.id.txt_value);
    this.btn_minus_1 = view.findViewById(R.id.btn_minus_1);
    this.btn_plus_1 = view.findViewById(R.id.btn_plus_1);
    this.btn_minus_10 = view.findViewById(R.id.btn_minus_10);
    this.btn_plus_10 = view.findViewById(R.id.btn_plus_10);
    this.btn_minus_100 = view.findViewById(R.id.btn_minus_100);
    this.btn_plus_100 = view.findViewById(R.id.btn_plus_100);
  }

  @Override
  public IBinder onBind(Intent p1) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    windowManager = (WindowManager)getApplication().getSystemService(WINDOW_SERVICE);
    LayoutParams params = new LayoutParams();
    //设置window type
    params.type = LayoutParams.TYPE_PHONE;
    //设置图片格式，效果为背景透明
    params.format = PixelFormat.RGBA_8888;
    //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
    params.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_TOUCHABLE;
    //调整悬浮窗显示的停靠位置为左侧置顶
    params.gravity = Gravity.LEFT | Gravity.TOP;
    //以屏幕左上角为原点，设置x、y初始值，相对于gravity
    params.x = 0;
    params.y = 0;

    //设置悬浮窗口长宽数据  
    params.width = LayoutParams.MATCH_PARENT;
    params.height = LayoutParams.MATCH_PARENT;

    resizableImageView = new ResizableImageView(this);
    windowManager.addView(resizableImageView, params);
    resizableImageView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    String data = sharedPreferences.getString(DATA_CACHE, null);
    if (data != null) {
      resizableImageView.setInitialState(data);
    }
  }

  private void initEditView() {
    if (editViewContainer == null) {
      LayoutInflater inflater = LayoutInflater.from(getApplication());
      editViewContainer = (ViewGroup) inflater.inflate(R.layout.float_control, null);
      initView(editViewContainer);
      listener = new MainServiceListener(this);
      spin_mode.setAdapter(new ArrayAdapter(this, R.layout.simple_spinner_item, ResizableImageView.ResizeType.names()));
      spin_mode.setOnItemSelectedListener(listener);
      btn_save.setOnClickListener(listener);
      btn_close.setOnClickListener(listener);
      txt_value.setOnTouchListener(listener);
      btn_minus_1.setOnClickListener(listener);
      btn_minus_10.setOnClickListener(listener);
      btn_minus_100.setOnClickListener(listener);
      btn_plus_1.setOnClickListener(listener);
      btn_plus_10.setOnClickListener(listener);
      btn_plus_100.setOnClickListener(listener);
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    MainNotification.startNotification(this);

    String imgPath = intent.getStringExtra(IMG_FILE_PATH);
    if (imgPath != null && !imgPath.isEmpty()) {
      resizableImageView.setImage(imgPath);
    }
    if (intent.getBooleanExtra(START_FROM_NOTIFICATION, false)) {
      try {
        initEditView();
        if (containerLayoutParams == null) {
          containerLayoutParams = new LayoutParams();
          containerLayoutParams.type = LayoutParams.TYPE_PHONE;
          containerLayoutParams.format = PixelFormat.RGBA_8888;
          containerLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
          containerLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
          containerLayoutParams.x = sharedPreferences.getInt("float_win_x", 0);
          containerLayoutParams.y = sharedPreferences.getInt("float_win_y", 0);
          containerLayoutParams.width = LayoutParams.WRAP_CONTENT;
          containerLayoutParams.height = LayoutParams.WRAP_CONTENT;
        }
        windowManager.addView(editViewContainer, containerLayoutParams);
        editViewContainer.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return super.onStartCommand(intent, flags, startId);
  }

  public void setResizeType(ResizeType type) {
    resizeType = type;
  }

  public void offsetResizableImageView(int offset) {
    resizableImageView.offset(resizeType, offset);
  }

  public void updateTextViewValue() {
    txt_value.setText(String.valueOf(resizableImageView.getCurrentValue(resizeType)));
  }

  public void updateContainerLayout(int dx, int dy) {
    int x = containerLayoutParams.x + dx;
    int y = containerLayoutParams.y + dy;
    if (x >= 0 && x <= windowManager.getDefaultDisplay().getWidth() - editViewContainer.getMeasuredWidth()) {
      containerLayoutParams.x = x;
    }
    if (y >= 0 && y <= windowManager.getDefaultDisplay().getHeight() - editViewContainer.getMeasuredHeight()) {
      containerLayoutParams.y = y;
    }
    windowManager.updateViewLayout(editViewContainer, containerLayoutParams);
  }

  public void saveData() {
    String data = resizableImageView.getCurreState();
    sharedPreferences.edit()
      .putString(DATA_CACHE, data)
      .putInt("float_win_x", containerLayoutParams.x)
      .putInt("float_win_y", containerLayoutParams.y)
      .commit();
  }

  public void removeEditViewContainer() {
    if (editViewContainer != null) {
      try {
        windowManager.removeView(editViewContainer);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MainNotification.stopNotification(this, true);
    if (resizableImageView != null) {
      resizableImageView.destroy();
      windowManager.removeView(resizableImageView);
    }
    removeEditViewContainer();
  }
}
