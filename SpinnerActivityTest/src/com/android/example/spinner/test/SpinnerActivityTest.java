package com.android.example.spinner.test;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.example.spinner.SpinnerActivity;

public class SpinnerActivityTest extends
		ActivityInstrumentationTestCase2<SpinnerActivity> {
	private static final String TAG = SpinnerActivityTest.class.getSimpleName();
	
	private SpinnerActivity mActivity;
	private Spinner mSpinner;
	private SpinnerAdapter mPlanetData;
	private String mSelection;
	private int mPos;
	
	public static final int ADAPTER_COUNT = 9;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 5;
	public static final int TEST_STATE_DESTROY_POSITION = 2;
	public static final String TEST_STATE_DESTROY_SELECTION = "Earth";
	public static final int TEST_STATE_PAUSE_POSITION = 4;
	public static final String TEST_STATE_PAUSE_SELECTION = "Jupiter";
	
	/**
	 * このコンストラクタの目的は、スーパークラスに情報を渡すことです。
	 */
	public SpinnerActivityTest() {
		super("com.android.example.spinner", SpinnerActivity.class);
	}
	
	/**
	 * setUpメソッド：全てのテストの前に、呼び出される。
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		setActivityInitialTouchMode(false);
		
		mActivity = getActivity();
		
		mSpinner = (Spinner)mActivity.findViewById(
				com.android.example.spinner.R.id.Spinner01
				);
		
		mPlanetData = mSpinner.getAdapter();
	}
	
	/**
	 * 初期条件テスト：テスト対象のアプリが、正しく初期化されたかを検証する。
	 */
	public void testPreConditions() {
		assertTrue(mSpinner.getOnItemSelectedListener() != null);
		assertTrue(mPlanetData != null);
		assertEquals(mPlanetData.getCount(), ADAPTER_COUNT);
	}
	
	/**
	 * UIテスト
	 */
	public void testSpinnerUI() {
		 
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mSpinner.requestFocus();
				mSpinner.setSelection(INITIAL_POSITION);
			}
		});
		
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		for(int i = 1; i <= TEST_POSITION; i++) {
			this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
		}
		this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
		
		mPos = mSpinner.getSelectedItemPosition();
		mSelection = (String)mSpinner.getItemAtPosition(mPos);
		TextView resultView = (TextView)mActivity.findViewById(
				com.android.example.spinner.R.id.SpinnerResult
				);
		
		String resultText = (String)resultView.getText();
		
		assertEquals(resultText, mSelection);
	}
	
	/**
	 * 終了と再起動に渡る状態管理テスト
	 * （アクティビティが状態を管理するメカニズムを知っていて、それを前提としてそれらのテストが作り上げられている。）
	 */
	public void testStateDestroy() {
		// Spinnerの選択をテストの値にセット
		mActivity.setSpinnerPosition(TEST_STATE_DESTROY_POSITION);
		mActivity.setSpinnerSelection(TEST_STATE_DESTROY_SELECTION);
		
		// アクティビティを終了してから再起動
		mActivity.finish();
		mActivity = this.getActivity();
		
		// アクティビティから現在のSpinnerの設定を取得
		int currentPosition = mActivity.getSpinnerPosition();
		String currentSelection = mActivity.getSpinnerSelection();
		
		// 現在の設定とテストの値とを検証
		assertEquals(TEST_STATE_DESTROY_POSITION, currentPosition);
		assertEquals(TEST_STATE_DESTROY_SELECTION, currentSelection);
	}
	
	/**
	 * 一時停止と再開に渡る状態管理テスト
	 * （アクティビティが状態を管理するメカニズムを知っていて、それを前提としてそれらのテストが作り上げられている。）
	 */
	@UiThreadTest
	public void testStatePause() {
		// onPause()とonResume()メソッドを呼び出すために、
		// テストは以下のアプリケーションを制御しているInstrumentationオブジェクトを取得。
		Instrumentation mInstr = this.getInstrumentation();
		
		// Spinnerの選択にテストの値をセット
		mActivity.setSpinnerPosition(TEST_STATE_PAUSE_POSITION);
		mActivity.setSpinnerSelection(TEST_STATE_PAUSE_SELECTION);
		
		// アクティビティを強制的に一時停止にするために、onPause()を直接呼び出す処理
		mInstr.callActivityOnPause(mActivity);
		
		// Spinnerに異なる選択を強制
		mActivity.setSpinnerPosition(0);
		mActivity.setSpinnerSelection("");
		
		// onResume()メソッドを直接呼び出す
		mInstr.callActivityOnResume(mActivity);
		
		// Spinnerの現在の状態を取得
		int currentPosition = mActivity.getSpinnerPosition();
		String currentSelection = mActivity.getSpinnerSelection();
		
		// 現在のSpinnerの状態とテストの値とを検証
		assertEquals(TEST_STATE_PAUSE_POSITION, currentPosition);
		assertEquals(TEST_STATE_PAUSE_SELECTION, currentSelection);
	}
}
