package pansong291.floatpic.view;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import org.json.JSONObject;
import org.json.JSONException;

public class ResizableImageView extends View {
  private String imgPath;
  private Bitmap bitMap;
  private Paint paint;
  private Rect srcRect;
  private Rect dstRect;

  public ResizableImageView(Context context) {
    super(context);
    init();
  }

  public ResizableImageView(Context context, AttributeSet attrs) {
    super(context);
    init();
  }

  public ResizableImageView(Context context, AttributeSet attrs, int defStyle) {
    super(context);
    init();
  }

  private void init() {
    paint = new Paint();
    srcRect = new Rect(0, 0, 200, 200);
    dstRect = new Rect(0, 0, 200, 200);
  }

  public void setInitialState(String json) {
    Data data = new Data(json);
    srcRect.set(data.src);
    dstRect.set(data.dst);
    setImage(data.img);
  }

  public String getCurreState() {
    return new Data(imgPath, srcRect, dstRect).toString();
  }

  public int getCurrentValue(ResizeType type) {
    switch (type) {
      case TOP:
      case WIN_Y:
      case ALL_Y:
        return dstRect.top;
      case BOTTOM:
        return dstRect.bottom;
      case LEFT:
      case WIN_X:
      case ALL_X:
        return dstRect.left;
      case RIGHT:
        return dstRect.right;
      case IMG_X:
        return srcRect.left;
      case IMG_Y:
        return srcRect.top;
    }
    return 0;
  }

  public void setImage(String filepath) {
    if (filepath == null || filepath.isEmpty()) return;
    imgPath = filepath;
    Bitmap bm = BitmapFactory.decodeFile(filepath);
    destroy();
    bitMap = bm;
    postInvalidate();
  }

  public void offset(ResizeType type, int offset) {
    if (type == null) return;
    switch (type) {
      case TOP:
        offsetWindowTop(offset);
        break;
      case BOTTOM:
        offsetWindowBottom(offset);
        break;
      case LEFT:
        offsetWindowLeft(offset);
        break;
      case RIGHT:
        offsetWindowRight(offset);
        break;
      case IMG_X:
        moveImageX(offset);
        break;
      case IMG_Y:
        moveImageY(offset);
        break;
      case WIN_X:
        moveWindowX(offset);
        break;
      case WIN_Y:
        moveWindowY(offset);
        break;
      case ALL_X:
        moveTogetherX(offset);
        break;
      case ALL_Y:
        moveTogetherY(offset);
        break;
    }
  }

  public void offsetWindowTop(int top) {
    srcRect.top += top;
    dstRect.top += top;
    postInvalidate();
  }

  public void offsetWindowBottom(int bottom) {
    srcRect.bottom += bottom;
    dstRect.bottom += bottom;
    postInvalidate();
  }

  public void offsetWindowLeft(int left) {
    srcRect.left += left;
    dstRect.left += left;
    postInvalidate();
  }

  public void offsetWindowRight(int right) {
    srcRect.right += right;
    dstRect.right += right;
    postInvalidate();
  }

  public void moveWindowX(int dx) {
    srcRect.offset(dx, 0);
    dstRect.offset(dx, 0);
    postInvalidate();
  }

  public void moveWindowY(int dy) {
    srcRect.offset(0, dy);
    dstRect.offset(0, dy);
    postInvalidate();
  }

  public void moveImageX(int dx) {
    srcRect.offset(dx, 0);
    postInvalidate();
  }

  public void moveImageY(int dy) {
    srcRect.offset(0, dy);
    postInvalidate();
  }

  public void moveTogetherX(int dx) {
    dstRect.offset(dx, 0);
    postInvalidate();
  }

  public void moveTogetherY(int dy) {
    dstRect.offset(0, dy);
    postInvalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (bitMap == null) return;
    canvas.drawBitmap(bitMap, srcRect, dstRect, paint);
  }

  public void destroy() {
    if (bitMap != null)
      bitMap.recycle();
  }

  public enum ResizeType {
    TOP, BOTTOM, LEFT, RIGHT,
    WIN_X, WIN_Y,
    IMG_X, IMG_Y,
    ALL_X, ALL_Y;
    private static String[] NAMES;
    public static String[] names() {
      if(NAMES == null)
      {
        ResizeType[] values = values();
        NAMES = new String[values.length];
        for(int i = 0; i < values.length; i++)
        {
          NAMES[i] = values[i].name();
        }
      }
      return NAMES;
    }
  }

  private static class Data {
    private Rect src;
    private Rect dst;
    private String img;
    private Data(String img, Rect src, Rect dst) {
      this.img = img;
      this.src = src;
      this.dst = dst;
    }
    private Data(String json) {
      try {
        JSONObject obj = new JSONObject(json);
        img = obj.optString("img");
        src = deserializeRect(obj.optJSONObject("src"));
        dst = deserializeRect(obj.optJSONObject("dst"));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    private Rect deserializeRect(JSONObject obj) {
      Rect rect = new Rect();
      rect.top = obj.optInt("t");
      rect.bottom = obj.optInt("b");
      rect.left = obj.optInt("l");
      rect.right = obj.optInt("r");
      return rect;
    }

    private JSONObject serializeRect(Rect rect) {
      JSONObject obj = new JSONObject();
      try {
        obj.put("t", rect.top)
          .put("b", rect.bottom)
          .put("l", rect.left)
          .put("r", rect.right);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return obj;
    }

    @Override
    public String toString() {
      JSONObject obj = new JSONObject();
      try {
        obj.put("img", img)
          .put("src", serializeRect(src))
          .put("dst", serializeRect(dst));
      } catch (JSONException e) {
        e.printStackTrace();
      }
      return obj.toString();
    }
  }
}
