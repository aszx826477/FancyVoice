package top.yelbee.www.library;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Toast;



/**
 * 圆盘式的view
 *
 * @author chroya
 *
 */
public class RoundSpinView extends View {
	private Paint mPaint = new Paint();
	private PaintFlagsDrawFilter pfd;

	private int startMenu;   //菜单的第一张图片的资源id

	// stone列表
	private BigStone[] mStones;
	// 数目
	private static final int STONE_COUNT = 5;

	// 圆心坐标
	private int mPointX = 0, mPointY = 0;
	// 半径
	private int mRadius = 0;
	// 每两个点间隔的角度
	private int mDegreeDelta;

	private int menuRadius; // 菜单的半径

	private int mCur = -1; // 正在被移动的menu;

	private boolean[] quadrantTouched;   //对每个象限触摸情况的记录

	// Touch detection
	private GestureDetector mGestureDetector;

	private onRoundSpinViewListener mListener;  //自定义事件监听器

	private final static int TO_ROTATE_BUTTON = 0;  //旋转按钮；

	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case TO_ROTATE_BUTTON:
					float velocity = Float.parseFloat(msg.obj.toString());
					rotateButtons(velocity/75);
					velocity /= 1.0666F;
					new Thread(new FlingRunnable(velocity)).start();
					break;

				default:
					break;
			}
		};
	};

	public interface onRoundSpinViewListener{
		public void onSingleTapUp(int position);  //监听每个菜单的单击事件
	}

	public RoundSpinView(Context context,AttributeSet attrs) {
		super(context,attrs);
		if(attrs!=null){
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.RoundSpinView);
			startMenu = a.getResourceId(R.styleable.RoundSpinView_menuStart, 0);
		}
		pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(2);
		mPaint.setAntiAlias(true); //消除锯齿
		mPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
		PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
		mPaint.setPathEffect(effects);


		quadrantTouched = new boolean[] { false, false, false, false, false };
		mGestureDetector = new GestureDetector(getContext(),
				new MyGestureListener());

		setupStones();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mPointX = this.getMeasuredWidth()/2;
		mPointY = this.getMeasuredHeight()/2;

		//初始化半径和菜单半径
		mRadius = mPointX-mPointX/5;
		menuRadius = (int)(mPointX/5.5);

		computeCoordinates();
	}

	/**
	 * 初始化每个点
	 */
	private void setupStones() {
		mStones = new BigStone[STONE_COUNT];
		BigStone stone;
		int angle = 270;
		mDegreeDelta = 360 / STONE_COUNT;

		for (int index = 0; index < STONE_COUNT; index++) {
			stone = new BigStone();
			if (angle >= 360) {
				angle -= 360;
			}else if(angle < 0){
				angle += 360;
			}
			stone.angle = angle;
			stone.bitmap = BitmapFactory.decodeResource(getResources(),
					startMenu + index);
			angle += mDegreeDelta;

			mStones[index] = stone;
		}
	}

	/**
	 * 重新计算每个点的角度
	 */
	private void resetStonesAngle(float x, float y) {
		int angle = computeCurrentAngle(x, y);
		Log.d("RoundSpinView", "angle:" + angle);
		for (int index = 0; index < STONE_COUNT; index++) {
			mStones[index].angle = angle;
			angle += mDegreeDelta;
		}
	}

	/**
	 * 计算每个点的坐标
	 */
	private void computeCoordinates() {
		BigStone stone;
		for (int index = 0; index < STONE_COUNT; index++) {
			stone = mStones[index];
			stone.x = mPointX
					+ (float) (mRadius * Math.cos(Math.toRadians(stone.angle)));
			stone.y = mPointY
					+ (float) (mRadius * Math.sin(Math.toRadians(stone.angle)));
		}
	}

	/**
	 * 计算某点的角度
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private int computeCurrentAngle(float x, float y) {
		float distance = (float) Math
				.sqrt(((x - mPointX) * (x - mPointX) + (y - mPointY)
						* (y - mPointY)));
		int degree = (int) (Math.acos((x - mPointX) / distance) * 180 / Math.PI);
		if (y < mPointY) {
			degree = -degree;
		}

		Log.d("RoundSpinView", "x:" + x + ",y:" + y + ",degree:" + degree);
		return degree;
	}

	private double startAngle;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// resetStonesAngle(event.getX(), event.getY());
		// computeCoordinates();
		// invalidate();

		int x, y;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			x = (int) event.getX();
			y = (int) event.getY();
			mCur = getInCircle(x, y);
			if (mCur == -1) {
				startAngle = computeCurrentAngle(x, y);
			}
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			x = (int) event.getX();
			y = (int) event.getY();
			if (mCur != -1) {
				mStones[mCur].x = x;
				mStones[mCur].y = y;
				invalidate();
			} else {
				double currentAngle = computeCurrentAngle(x, y);
				rotateButtons(startAngle - currentAngle);
				startAngle = currentAngle;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			x = (int) event.getX();
			y = (int) event.getY();
			if (mCur != -1) {
				computeCoordinates();
				int cur = getInCircle(x, y);
				if (cur != mCur && cur != -1) {
					int angle = mStones[mCur].angle;
					mStones[mCur].angle = mStones[cur].angle;
					mStones[cur].angle = angle;
				}
				computeCoordinates();
				invalidate();
				mCur = -1;
			}
		}

		// set the touched quadrant to true
		quadrantTouched[getQuadrant(event.getX() - mPointX,
				mPointY - event.getY())] = true;
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	private class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
							   float velocityY) {
			// get the quadrant of the start and the end of the fling
			int q1 = getQuadrant(e1.getX() - mPointX, mPointY - e1.getY());
			int q2 = getQuadrant(e2.getX() - mPointX, mPointY - e2.getY());

			// the inversed rotations
			if ((q1 == 2 && q2 == 2 && Math.abs(velocityX) < Math
					.abs(velocityY))
					|| (q1 == 3 && q2 == 3)
					|| (q1 == 1 && q2 == 3)
					|| (q1 == 4 && q2 == 4 && Math.abs(velocityX) > Math
					.abs(velocityY))
					|| ((q1 == 2 && q2 == 3) || (q1 == 3 && q2 == 2))
					|| ((q1 == 3 && q2 == 4) || (q1 == 4 && q2 == 3))
					|| (q1 == 2 && q2 == 4 && quadrantTouched[3])
					|| (q1 == 4 && q2 == 2 && quadrantTouched[3])) {

				// CircleLayout.this.post(new FlingRunnable(-1
				// * (velocityX + velocityY)));
				new Thread(new FlingRunnable(velocityX+velocityY)).start();
			} else {
				// the normal rotation
				// CircleLayout.this
				// .post(new FlingRunnable(velocityX + velocityY));
				new Thread(new FlingRunnable(-(velocityX+velocityY))).start();
			}

			return true;

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			int cur = getInCircle((int)e.getX(),(int)e.getY());
			if(cur!=-1){
				if(mListener!=null){
					mListener.onSingleTapUp(cur);
				}
//				Toast.makeText(getContext(), "position:"+cur, 0).show();
				return true;
			}
			return false;
		}

	}

	private class FlingRunnable implements Runnable{

		private float velocity;

		public FlingRunnable(float velocity){
			this.velocity = velocity;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(Math.abs(velocity)>=200){
				Message message = Message.obtain();
				message.what = TO_ROTATE_BUTTON;
				message.obj = velocity;
				handler.sendMessage(message);
			}
		}

	}

	/**
	 * @return The selected quadrant.
	 */
	private static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
		}
		return y >= 0 ? 2 : 3;
	}

	/*
	 * 旋转菜单按钮
	 */
	private void rotateButtons(double degree) {
		for (int i = 0; i < STONE_COUNT; i++) {
			mStones[i].angle -= degree;
			if (mStones[i].angle < 0) {
				mStones[i].angle += 360;
			}else if(mStones[i].angle >=360){
				mStones[i].angle -= 360;
			}
		}

		computeCoordinates();
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		//画一个白色的圆环
		canvas.drawCircle(mPointX, mPointY, mRadius, mPaint);

		//将每个菜单画出来
		for (int index = 0; index < STONE_COUNT; index++) {
			if (!mStones[index].isVisible)
				continue;
			drawInCenter(canvas, mStones[index].bitmap, mStones[index].x,
					mStones[index].y);
		}
	}

	/**
	 * 把中心点放到中心处
	 *
	 * @param canvas
	 * @param bitmap
	 * @param left
	 * @param top
	 */
	private void drawInCenter(Canvas canvas, Bitmap bitmap, float left,
							  float top) {
		Rect dst = new Rect();
		dst.left = (int) (left - menuRadius);
		dst.right = (int) (left + menuRadius);
		dst.top = (int) (top - menuRadius);
		dst.bottom = (int) (top + menuRadius);
		canvas.setDrawFilter(pfd);
		canvas.drawBitmap(bitmap, null, dst, mPaint);
	}

	private int getInCircle(int x, int y) {
		for (int i = 0; i < STONE_COUNT; i++) {
			BigStone stone = mStones[i];
			int mx = (int) stone.x;
			int my = (int) stone.y;
			if (((x - mx) * (x - mx) + (y - my) * (y - my)) < menuRadius
					* menuRadius) {
				return i;
			}
		}
		return -1;
	}

	public void setOnRoundSpinViewListener(onRoundSpinViewListener listener){
		this.mListener = listener;
	}

	class BigStone {

		// 图片
		Bitmap bitmap;

		// 角度
		int angle;

		// x坐标
		float x;

		// y坐标
		float y;

		// 是否可见
		boolean isVisible = true;
	}
}
