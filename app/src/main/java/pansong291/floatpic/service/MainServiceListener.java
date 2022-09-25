package pansong291.floatpic.service;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import pansong291.floatpic.R;
import pansong291.floatpic.activity.MainActivity;
import pansong291.floatpic.activity.Zactivity;
import pansong291.floatpic.view.ResizableImageView;

public class MainServiceListener implements OnClickListener, OnItemSelectedListener, OnTouchListener {
  private MainService mainService;
  private Point pBefore = new Point(), pNow = new Point();


  public MainServiceListener(MainService main) {
    mainService = main;
  }

  @Override
  public void onClick(View v) {
    int offset=0;
    switch (v.getId()) {
      case R.id.btn_plus_1: offset = 1; break;
      case R.id.btn_plus_10: offset = 10; break;
      case R.id.btn_plus_100: offset = 100; break;
      case R.id.btn_minus_1: offset = -1; break;
      case R.id.btn_minus_10: offset = -10; break;
      case R.id.btn_minus_100: offset = -100; break;
    }
    if (offset != 0) {
      mainService.offsetResizableImageView(offset);
      mainService.updateTextViewValue();
      return;
    }
    switch (v.getId()) {
      case R.id.btn_save:
        mainService.saveData();
        mainService.toast("Saved");
        break;
      case R.id.btn_close:
        mainService.removeEditViewContainer();
        break;
    }
  }

  @Override
  public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
    mainService.setResizeType(ResizableImageView.ResizeType.values()[p3]);
    mainService.updateTextViewValue();
  }

  @Override
  public void onNothingSelected(AdapterView<?> p1) {
  }

  @Override
  public boolean onTouch(View p1, MotionEvent p2) {
    switch (p2.getAction()) {
      case MotionEvent.ACTION_DOWN:
        pBefore.set((int) p2.getRawX(), (int) p2.getRawY());
        break;
      case MotionEvent.ACTION_MOVE:
        pNow.set((int) p2.getRawX(), (int) p2.getRawY());
        mainService.updateContainerLayout(pNow.x - pBefore.x, pNow.y - pBefore.y);
        pBefore.set(pNow.x, pNow.y);
        break;
    }
    return true;
  }
}
