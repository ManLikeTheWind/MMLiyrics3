package com.dxiang.mring3.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.gem.imgcash.ImageDownLoader;
import com.dxiang.mring3.R;
import com.dxiang.mring3.bean.ToneInfo;
import com.dxiang.mring3.db.DBOperator;
import com.dxiang.mring3.db.DbHelper;
import com.dxiang.mring3.file.SharedPreferenceService;
import com.dxiang.mring3.request.QryToneByNameRequest;
import com.dxiang.mring3.request.QryToneBySingerRequest;
import com.dxiang.mring3.response.QryToneByNameRsp;
import com.dxiang.mring3.response.QryToneBySingerRsp;
import com.dxiang.mring3.utils.Commons;
import com.dxiang.mring3.utils.EllipsizeUtils;
import com.dxiang.mring3.utils.FusionCode;
import com.dxiang.mring3.utils.LocalMediaPlayer;
import com.dxiang.mring3.utils.UtlisReturnCode;
import com.dxiang.mring3.utils.LocalMediaPlayer.Complete;
import com.dxiang.mring3.utils.LocalMediaPlayer.ErrorListener;
import com.dxiang.mring3.utils.LocalMediaPlayer.onException;
import com.dxiang.mring3.utils.LocalMediaPlayer.onPrepared;
import com.dxiang.mring3.utils.Utils;
import com.dxiang.mring3.view.RoundedImageView;

public class SearchActivity extends BaseActivity {
	public static final String TAG = "SearchActivity";

	private EditText mSearchText;

	private Button mStartSearchBtn;

	// 显示历史搜索记录
	private LinearLayout mShowHistory;

	// 用于显示搜索的数据
	private RelativeLayout mShowDatas;

	// 列表的适配器
	private HistortyAdapter mHistortyAdapter;

	private List<String> mAllRecords;

	private DbHelper mDbHelper;

	private GridView mRecordView;

	private TextView mSearchNoData;

	private int pageNoSinger = 0;

	private int pageNoTone = 0;

	private int showNum = 20;

	// 铃音列表播放器
	private LocalMediaPlayer mediaPlayer;// 播放器
	private boolean isPlayer = false;// 判断是否正在播放

	// 数据子集
	private List<ToneInfo> mData;

	private List<ToneInfo> mSingerListTones;

	private List<ToneInfo> mNameListTones;

	private ListView listview;

	private ListViewAdapter mAdapter;

	private boolean isSingerNeedToRefresh = true;

	private boolean isNameNeedToRefresh = true;

	// onscroll 的时候需要判断一下是不是需要加载新的服务端数据
	private boolean isLoadingSingerDatas = false;

	private boolean isLoadingNameDatas = false;

	private static final String ORDER_TYPE = "1";

	private static final String ORDER_BY = "1";

	private SharedPreferenceService preferenceService;
	private ImageView liear_title;

	@Override
	protected void reqXmlSucessed(Message msg) {
		super.reqXmlSucessed(msg);

		// 解析返回的数据
		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneByName:
			QryToneByNameRsp rspName = (QryToneByNameRsp) msg.obj;
			List<ToneInfo> infoNames = rspName.getmToneInfos();
			if (infoNames.size() > 0) {
				Log.e("searchinfo", infoNames.get(0).getSingerName());
			}
			if (infoNames != null && infoNames.size() > 0) {
				if (/* infoNames.size() == 1 || */infoNames.get(0)
						.isEmptyRecord() || infoNames.isEmpty()) {
					Log.d(TAG,
							"----------REQUEST_QryToneByName--------------------Empty  record");
				} else {
					mData.addAll(removeDuplicate(infoNames));
					mNameListTones.addAll(infoNames);
					mAdapter.notifyDataSetChanged();
				}
			}
			pageNoTone++;
			checkNameScrollable();
			isLoadingNameDatas = false;
			stopBar();
			setVisible();
			break;
		case FusionCode.REQUEST_QryToneBySinger:
			QryToneBySingerRsp rsp = (QryToneBySingerRsp) msg.obj;
			List<ToneInfo> infos = rsp.getmToneInfos();
			if (infos != null && infos.size() > 0) {
				if (/* infos.size() == 1 || */infos.get(0).isEmptyRecord()
						|| infos.isEmpty()) {
					Log.d(TAG,
							"--------------REQUEST_QryToneBySinger----------------Empty  record");
				} else {
					mData.addAll(removeDuplicate(infos));
					mSingerListTones.addAll(infos);
					mAdapter.notifyDataSetChanged();
				}
			}
			pageNoSinger++;
			checkSingerScrollable();
			isLoadingSingerDatas = false;
			stopBar();
			setVisible();
			break;

		default:
			break;
		}
	}

	// 删除ArrayList中重复元素
	public List<ToneInfo> removeDuplicate(List<ToneInfo> infos) {
		for (int i = 0; i < infos.size() - 1; i++) {
			for (int j = infos.size() - 1; j > i; j--) {
				if (infos.get(j).getToneID().equals(infos.get(i).getToneID())) {
					infos.remove(j);
				}
			}
		}
		return infos;
	}

	private void stopBar() {
		if ((mData != null && mData.size() > 0)) {
			stopPbarU();
		} else if (!isLoadingNameDatas && !isLoadingSingerDatas) {
			stopPbarU();
		}
	}

	private void checkSingerScrollable() {
		int temp = pageNoSinger * showNum;
		int all = mSingerListTones.size();
		if (all >= temp) {
			isSingerNeedToRefresh = true;
		} else {
			isSingerNeedToRefresh = false;
		}
	}

	private void checkNameScrollable() {
		int temp = pageNoTone * showNum;
		int all = mNameListTones.size();
		if (all >= temp) {
			isNameNeedToRefresh = true;
		} else {
			isNameNeedToRefresh = false;
		}
	}

	@Override
	protected void reqXmlFail(Message msg) {
		super.reqXmlFail(msg);

		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneByName:
			QryToneByNameRsp rspName = (QryToneByNameRsp) msg.obj;
			Utils.showTextToast(SearchActivity.this,
					UtlisReturnCode.ReturnCode(rspName.getResult(), mContext));
			isLoadingNameDatas = false;
			isNameNeedToRefresh = false;
			stopBar();
			setVisible();
			break;
		case FusionCode.REQUEST_QryToneBySinger:
			QryToneBySingerRsp rsp = (QryToneBySingerRsp) msg.obj;
			Utils.showTextToast(SearchActivity.this,
					UtlisReturnCode.ReturnCode(rsp.getResult(), mContext));
			mSingerListTones.clear();
			isLoadingSingerDatas = false;
			isSingerNeedToRefresh = false;
			stopBar();
			setVisible();
			break;

		default:
			break;
		}
	}

	@Override
	protected void reqError(Message msg) {
		super.reqError(msg);

		switch (msg.arg1) {
		case FusionCode.REQUEST_QryToneByName:
			isLoadingNameDatas = false;
			isNameNeedToRefresh = false;
			stopBar();
			setVisible();
			break;
		case FusionCode.REQUEST_QryToneBySinger:
			isLoadingSingerDatas = false;
			isSingerNeedToRefresh = false;
			mSingerListTones.clear();
			stopBar();
			setVisible();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.search_layout);

		initView();

		// initAnimation();

		getHistoryRecords();
		initSubView();
		initSubDatas();

		if (mAllRecords == null || mAllRecords.size() == 0) {
			mShowHistory.setVisibility(View.GONE);
			mShowDatas.setVisibility(View.VISIBLE);
		} else {
			mShowHistory.setVisibility(View.VISIBLE);
			mShowDatas.setVisibility(View.GONE);
		}
		initData();
		input_hintSize();

	}

	private void input_hintSize() {
		preferenceService = SharedPreferenceService.getInstance(this);
		String language = preferenceService.get("language", "");
		if (Utils.CheckTextNull(language)) {
			Utils.hintSize(mSearchText, 14);
		}
		mSearchText.setLines(2);
		mSearchText.setSingleLine(false);

	}

	private void initData() {
		mAdapter = new ListViewAdapter(this);
		listview.setAdapter(mAdapter);

		mSingerListTones = new ArrayList<ToneInfo>();
		mNameListTones = new ArrayList<ToneInfo>();
		mData = new ArrayList<ToneInfo>();
	}

	// private void initAnimation() {
	// animation = new RotateAnimation(0, +360, Animation.RELATIVE_TO_SELF,
	// 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	// // 动画开始到结束的执行时间(1000 = 1 秒)
	// animation.setDuration(800);
	// // 动画重复次数(-1 表示一直重复)
	// animation.setRepeatCount(-1);
	// animation.setRepeatMode(Animation.INFINITE);
	// animation.setInterpolator(new LinearInterpolator());// 匀速动画
	// }

	/**
	 * 初始化播放器
	 */
	private void initMediaPlayer() {
		mediaPlayer = LocalMediaPlayer.getInstance();
		mediaPlayer.setCallback(onComplete);
		mediaPlayer.setOnPrepared(onPrepred);
		mediaPlayer.setErrorListener(errorListener);
		mediaPlayer.setException(exception);
	}

	/**
	 * 播放完成
	 */
	Complete onComplete = new Complete() {

		@Override
		public void onComplete() {
			mAdapter.pos = -1;
			mAdapter.notifyDataSetChanged();
			isPlayer = false;
		}

	};

	/**
	 * 播放准备
	 */
	onPrepared onPrepred = new onPrepared() {

		@Override
		public void onPrepared() {
			if (mAdapter.pos != -1 && mAdapter.pos != -2 && mediaPlayer != null) {
				isPlayer = true;
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	/**
	 * 播放异常
	 */
	ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onError(MediaPlayer arg0, int arg1, int arg2) {
			stopMusic();
			mAdapter.notifyDataSetChanged();
			mAdapter.pos = -1;
			isPlayer = false;
		}

	};

	onException exception = new onException() {

		@Override
		public void onException(int code, String des) {
			if (Utils.CheckTextNull(des)) {
				Utils.showTextToast(SearchActivity.this,
						UtlisReturnCode.ReturnCode(des, mContext));
			}
			stopMusic();
			mAdapter.notifyDataSetChanged();
			mAdapter.pos = -1;
			isPlayer = false;
		}

	};

	/**
	 * 停止播放
	 */
	private void stopMusic() {
		isPlayer = false;
		if (mediaPlayer == null) {
			initMediaPlayer();
			return;
		}
		mediaPlayer.cancelPlayer();
	}

	private void getTonesByName(String key, int page, boolean needShow) {
		isLoadingNameDatas = true;
		QryToneByNameRequest qryToneByNameRequest = new QryToneByNameRequest(
				handler);
		qryToneByNameRequest.sendQueryToneByNameRequest(key, ORDER_TYPE,
				ORDER_BY, showNum, page);

		if (needShow) {
			startPbarU();
		}
	}

	private void getTonesBySinger(String key, int page, boolean needShow) {
		isLoadingSingerDatas = true;
		QryToneBySingerRequest qryToneBySingerRequest = new QryToneBySingerRequest(
				handler);
		qryToneBySingerRequest.sendQryToneBySingerRequest(key, ORDER_TYPE,
				ORDER_BY, showNum, page);
		if (needShow) {
			startPbarU();
		}
	}

	// private List<ToneInfo> packageData() {
	// List<ToneInfo> allList = new ArrayList<ToneInfo>();
	// if (pageNo < 5) {
	// for (int i = 0; i < 20; i++) {
	// ToneInfo info = new ToneInfo();
	// String url = "http://221.226.179.185/music_mmh/drive.wav";
	// info.setSingerName("Jay");
	// info.setToneName("十月围城");
	// if (i % 2 == 0) {
	// info.setSingerName("陈慧琳");
	// info.setToneName("记事本");
	// url = "http://221.226.179.185/music_mmh/advring/20150505145856896.wav";
	// }
	// info.setTonePreListenAddress(url);
	// allList.add(info);
	// }
	//
	// } else {
	// for (int i = 0; i < 10; i++) {
	// ToneInfo info = new ToneInfo();
	// String url = "http://221.226.179.185/music_mmh/drive.wav";
	// info.setSingerName("Jay");
	// info.setToneName("心情一战");
	// if (i % 2 == 0) {
	// info.setSingerName("陈慧琳");
	// info.setToneName("号码");
	// url = "http://221.226.179.185/music_mmh/advring/20150505145856896.wav";
	// }
	// info.setTonePreListenAddress(url);
	// allList.add(info);
	// }
	// }
	// return allList;
	// }

	private void getHistoryRecords() {
		mDbHelper = DbHelper.getInstance(this);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		Cursor cursor = DBOperator.queryAll(db);
		mAllRecords = new ArrayList<String>();
		if (cursor == null || cursor.getCount() == 0) {
			return;
		}

		int content = cursor.getColumnIndex(DbHelper.CONTENT);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String c = cursor.getString(content);
			mAllRecords.add(c);
		}
		cursor.close();
		db.close();
		sortHistoryRecords(null);
	}

	private void initSubDatas() {
		if (mHistortyAdapter == null) {
			mHistortyAdapter = new HistortyAdapter(mAllRecords);
		}
		mRecordView.setAdapter(mHistortyAdapter);
	}

	private void initSubView() {
		int w = getWindowManager().getDefaultDisplay().getWidth();
		int h = getWindowManager().getDefaultDisplay().getHeight();
		mShowHistory = (LinearLayout) findViewById(R.id.history_result);
		mShowHistory.findViewById(R.id.clear_history).setOnClickListener(
				mAllViewListener);
		mRecordView = (GridView) mShowHistory.findViewById(R.id.show_history);
		mRecordView.setColumnWidth((w - 80) / 2);
		mRecordView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String text = mAllRecords.get(arg2).trim();
				if (Utils.isEmptyString(text)) {
					Utils.showTextToast(SearchActivity.this,
							R.string.input_null);
					return;
				}
				if (!Utils.isNetworkAvailable(SearchActivity.this)) {
					Utils.showTextToast(SearchActivity.this, R.string.nowifi);
					return;
				}
				mShowDatas.setVisibility(View.VISIBLE);
				mShowHistory.setVisibility(View.GONE);
				Utils.hideInput(SearchActivity.this);
				mSearchText.setText(text);
				mSearchText.setSelection(text.length());
				gotoSearch(text, true);
			}
		});
	}

	private void initView() {
		mShowDatas = (RelativeLayout) findViewById(R.id.search_result);
		mSearchText = (EditText) findViewById(R.id.input_search_key);
		mStartSearchBtn = (Button) findViewById(R.id.search_button);
		((TextView) findViewById(R.id.title_tv)).setText(R.string.search);
		findViewById(R.id.title_iv).setVisibility(View.VISIBLE);
		findViewById(R.id.title_iv).setOnClickListener(mAllViewListener);
		listview = (ListView) findViewById(R.id.show_search_results);
		mSearchText.setOnTouchListener(mSearchInput);
		mStartSearchBtn.setOnClickListener(mAllViewListener);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		listview.setOnScrollListener(onScrollListener);
		mSearchText.addTextChangedListener(watcher);
		mSearchNoData = (TextView) findViewById(R.id.no_datas);
		liear_title = (ImageView) findViewById(R.id.liear_title);
		liear_title.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (Utils.isFastDoubleClick()) {
					return;
				}
				startActivity(new Intent(SearchActivity.this,
						MainGroupActivity.class));
				finish();
			}
		});

		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == 0 || actionId == 6 || actionId == 5
						|| actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_GO) {
					String text = mSearchText.getText().toString().trim();
					if (Utils.isEmptyString(text)) {
						Utils.showTextToast(SearchActivity.this,
								R.string.input_null);
						return true;
					}
					if (!Utils.isNetworkAvailable(SearchActivity.this)) {
						Utils.showTextToast(SearchActivity.this,
								R.string.nowifi);
						return true;
					}
					if (Utils.isFastDoubleClick()) {
						return true;
					}
					mShowDatas.setVisibility(View.VISIBLE);
					mShowHistory.setVisibility(View.GONE);
					Utils.hideInput(SearchActivity.this);
					gotoSearch(text, true);
					return true;
				}
				return false;
			}
		});
	}

	// 设置为可见是将无记录显示成可见
	private void setVisible() {
		if (!isLoadingNameDatas && !isLoadingSingerDatas
				&& (mData == null || mData.size() == 0)) {
			mSearchNoData.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		} else {
			mSearchNoData.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
		}
	}

	private OnTouchListener mSearchInput = new OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			return false;
		}
	};

	private TextWatcher watcher = new TextWatcher() {
		private CharSequence text = "";

		int editStart = 0;
		int editEnd = 0;

		@Override
		public void onTextChanged(CharSequence s, int begin, int before,
				int count) {
			if (s == null || s.length() == 0) {
				if (mAllRecords == null || mAllRecords.size() == 0) {
					mShowDatas.setVisibility(View.VISIBLE);
					mShowHistory.setVisibility(View.GONE);
				} else {
					mShowDatas.setVisibility(View.GONE);
					mShowHistory.setVisibility(View.VISIBLE);
				}
			}
			text = s;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s == null || s.length() == 0) {
				return;
			}
			// ^[^_][a-zA-Z0-9_\u4e00-\u9fa5][^_]$
			// ^[0-9a-zA-Z _-]+$
			// ^w+$
			editStart = mSearchText.getSelectionStart();
			editEnd = mSearchText.getSelectionEnd();
			// if (!s.toString().matches("^[//.0-9a-zA-Z_-]+$+?\\s*")) {
			// s.delete(editStart - 1, editEnd);
			// mSearchText.setText("");
			// Utils.showTextToast(SearchActivity.this, R.string.string_format);
			// }
		}
	};

	// 监听键盘按键方式
	private OnKeyListener keyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
			return false;
		}
	};

	private OnClickListener mAllViewListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.search_button:
				String text = mSearchText.getText().toString().trim();
				if (Utils.isEmptyString(text)) {
					Utils.showTextToast(SearchActivity.this,
							R.string.input_null);
					return;
				}

				if (!Utils.isNetworkAvailable(SearchActivity.this)) {
					Utils.showTextToast(SearchActivity.this, R.string.nowifi);
					return;
				}
				mShowDatas.setVisibility(View.VISIBLE);
				mShowHistory.setVisibility(View.GONE);
				Utils.hideInput(SearchActivity.this);
				gotoSearch(text, true);
				break;
			case R.id.title_iv:
				stopMusic();
				finish();
				break;
			case R.id.clear_history:
				deleteAllHistoryRecords();
				break;
			default:
				break;
			}
		}

	};

	private void deleteAllHistoryRecords() {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		for (String temp : mAllRecords) {
			DBOperator.delete(db, temp);
		}
		db.close();
		mAllRecords.clear();
		mShowDatas.setVisibility(View.VISIBLE);
		mShowHistory.setVisibility(View.GONE);
	}

	private void gotoSearch(String text, boolean flag) {
		if (mAllRecords == null || !mAllRecords.contains(text)) {
			gotoSaveRecords(text);
		}
//		String text_temp = text.replaceAll("\\s*", "");
		sortHistoryRecords(text);
		initSubDatas();

		mNameListTones.clear();
		mData.clear();
		mSingerListTones.clear();
		getTonesByName(text, 0, true);
		getTonesBySinger(text, 0, true);
	}

	private void sortHistoryRecords(String data) {
		if (Utils.isEmptyString(data) && mAllRecords != null) {
			List<String> temp = new ArrayList<String>();
			temp.addAll(mAllRecords);
			mAllRecords.clear();
			for (int i = temp.size() - 1; i >= 0; i--) {
				mAllRecords.add(temp.get(i));
			}
		} else {
			if (mAllRecords.contains(data)) {
				SQLiteDatabase db = mDbHelper.getWritableDatabase();
				int i = 0;
				for (; i < mAllRecords.size(); i++) {
					if (data.equals(mAllRecords.get(i))) {
						break;
					}
				}
				mAllRecords.remove(i);
				mAllRecords.add(0, data);
				DBOperator.delete(db, data);
				DBOperator.insertData(db, data);
				db.close();
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		stopMusic();
	}

	private void gotoSaveRecords(String record) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (mAllRecords == null || mAllRecords.size() < 10) {
			if (mAllRecords == null) {
				mAllRecords = new ArrayList<String>();
			}
			if (DBOperator.insertData(db, record)) {
				mAllRecords.add(record);
			}
		} else {
			boolean flag = DBOperator.update(db, mAllRecords.get(9), record);
			if (flag) {
				mAllRecords.remove(9);
				mAllRecords.add(0, record);
			}
			db.close();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		showInput();
		initMediaPlayer();
	}

	private void showInput() {
		mSearchText.requestFocus();
		mSearchText.setFocusable(true);

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mediaPlayer == null) {
			return;
		}
		if (mediaPlayer.isPlaying()
				|| mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
		Utils.hideInput(this);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	class HistortyAdapter extends BaseAdapter {
		LayoutInflater mInflater;

		private List<String> mAllHistorys;

		public HistortyAdapter(List<String> historys) {
			mInflater = getLayoutInflater();

			mAllHistorys = historys;
		}

		@Override
		public int getCount() {
			if (mAllHistorys == null) {
				return 0;
			}
			return mAllHistorys.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ViewHolder holder;
			if (arg1 == null) {
				holder = new ViewHolder();
				arg1 = mInflater.inflate(R.layout.search_textview_item_layout,
						null);
				arg1.setTag(holder);
				holder.title = (TextView) arg1
						.findViewById(R.id.search_history_text);
			} else {
				holder = (ViewHolder) arg1.getTag();
			}

			holder.title.setText(mAllHistorys.get(arg0));
			holder.title.setText(EllipsizeUtils.textEllpsize(holder.title, mAllHistorys.get(arg0), mContext,130));
			return arg1;
		}

		class ViewHolder {
			TextView title;
		}

	}

	public class ListViewAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;
		public ViewHolder viewholder = null;
		public int pos = -2;

		public int old;

		private ListViewAdapter(Context context) {
			this.context = context;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (mData == null) {
				return 0;
			}
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			if (mData == null) {
				return null;
			}
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.item_list_fragment_layout, null);
				viewholder = new ViewHolder();
				viewholder.all_item = (RelativeLayout) convertView
						.findViewById(R.id.all_item);
				viewholder.singer = (TextView) convertView
						.findViewById(R.id.singer);
				viewholder.song = (TextView) convertView
						.findViewById(R.id.song);
				viewholder.mHot = (ImageView) convertView
						.findViewById(R.id.hot);
				viewholder.mIv_Play_Stop_Loading = (ImageView) convertView
						.findViewById(R.id.music_play_stop_loading);
				viewholder.mLoading = (ProgressBar) convertView
						.findViewById(R.id.music_loading);
				viewholder.round_bg = (RoundedImageView) convertView
						.findViewById(R.id.logo);
				convertView.setTag(viewholder);
				convertView.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) convertView.getTag();
			}
//			viewholder.singer.setText(mData.get(position).getSingerName());
			viewholder.singer.setText(EllipsizeUtils.textEllpsize(viewholder.singer, mData.get(position).getSingerName(), mContext,220));

//			viewholder.song.setText(mData.get(position).getToneName());
			viewholder.song.setText(EllipsizeUtils.textEllpsize(viewholder.song, mData.get(position).getToneName(), mContext,220));
			
			String saveDir = Commons.getImageSavedpath()
					+ mData.get(position).getToneClassID();
			if (new File(saveDir).exists()) {
				Bitmap bm = ImageDownLoader.getShareImageDownLoader()
						.showCacheBitmap(saveDir);
				viewholder.round_bg.setImageBitmap(bm);
			}
			final int temp = position;
//			if (position < 3) {
//				viewholder.mHot.setVisibility(View.VISIBLE);
//			} else {
//				viewholder.mHot.setVisibility(View.GONE);
//			}

			viewholder.all_item.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					pos = -1;
					listview.setStackFromBottom(false);
					notifyDataSetChanged();
					viewholder.mLoading.setVisibility(View.INVISIBLE);
					isPlayer = false;
					stopMusic();
					ToneInfo info = mData.get(position);
					Intent intent = new Intent(SearchActivity.this,
							DetailsRingtoneActivity.class);
					intent.putExtra("bean", info);
					startActivity(intent);
				}
			});

			viewholder.mIv_Play_Stop_Loading
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (Utils.isFastDoubleClick()) {
								return;
							}
							int oldPos = pos;
							pos = temp;
							// 点击时间过快，不操作

							if (mediaPlayer.isPause() && oldPos == -1
									&& old == temp) {
								mediaPlayer.startPlayer();
								pos = temp;
								isPlayer = true;
								return;
							}
							if (oldPos == temp || oldPos == -2 || oldPos == -1) {
								if (oldPos != temp) {
									mediaPlayer.cancelPlayer();
									isPlayer = false;
									pos = position;
									mediaPlayer.getUrlFromServer(
											mData.get(position).getToneID(),
											mData.get(position).getToneType());
									notifyDataSetChanged();
									return;
								}
								if (mediaPlayer.isprepared()) {
									return;
								}

								if (!isPlayer) {
									// 播放
									pos = position;
									mediaPlayer.getUrlFromServer(
											mData.get(position).getToneID(),
											mData.get(position).getToneType());
									listview.setStackFromBottom(false);
									notifyDataSetChanged();

								} else {
									old = pos;
									pos = -1;
									listview.setStackFromBottom(false);
									notifyDataSetChanged();
									mediaPlayer.pause();
									isPlayer = false;
								}
							} else {
								mediaPlayer.cancelPlayer();
								pos = position;
								isPlayer = false;
								mediaPlayer.getUrlFromServer(mData
										.get(position).getToneID(),
										mData.get(position).getToneType());
								notifyDataSetChanged();
							}
						}
					});
			if (pos == position) {
				if (isPlayer) {
					// viewholder.mLoading.setAnimation(null);
					viewholder.mLoading.setVisibility(View.INVISIBLE);
					viewholder.mIv_Play_Stop_Loading
							.setBackgroundResource(R.drawable.music_stop);

				} else {
					// 处理试听加载效果
					viewholder.mIv_Play_Stop_Loading.setBackgroundResource(0);
					// if (viewholder.mLoading.getAnimation() == null) {
					// viewholder.mLoading.setAnimation(animation);
					// animation.startNow();
					// }
					viewholder.mLoading.setVisibility(View.VISIBLE);
				}
			} else {
				// viewholder.mLoading.setAnimation(null);
				viewholder.mLoading.setVisibility(View.INVISIBLE);
				viewholder.mIv_Play_Stop_Loading
						.setBackgroundResource(R.drawable.music_play);

			}
			return convertView;
		}
	}

	public class ViewHolder {
		RelativeLayout all_item;
		private ImageView mIv_Play_Stop_Loading, mHot;
		private ProgressBar mLoading;
		private TextView song, singer;
		private RoundedImageView round_bg;
	}

	private OnScrollListener onScrollListener = new OnScrollListener() {
		private int state = 0;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			state = scrollState;
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if ((state == SCROLL_STATE_IDLE || state == SCROLL_STATE_FLING)
					&& !isLoadingNameDatas && !isLoadingSingerDatas) {
				int last = firstVisibleItem + visibleItemCount;
				int delt = totalItemCount - last;
				if (delt == 4) {
					if (isLoadingNameDatas) {
						return;
					}
					if (isLoadingSingerDatas) {
						return;
					}
					String key = mSearchText.getText().toString();
					if (isNameNeedToRefresh) {
						getTonesByName(key, pageNoSinger, false);
					}

					if (isSingerNeedToRefresh) {
						getTonesBySinger(key, pageNoTone, false);
					}
				}
				state = 0;
			}
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		if (mediaPlayer == null) {
			return;
		}
		if (mediaPlayer.isPlaying()
				|| mediaPlayer.getState() == LocalMediaPlayer.PAUSE) {
			pause();
		} else {
			stopMusic();
		}
	}

	private void pause() {
		if (mediaPlayer.getState() != LocalMediaPlayer.PAUSE) {
			mAdapter.old = mAdapter.pos;
			mAdapter.pos = -1;
			mAdapter.notifyDataSetChanged();
			mediaPlayer.pause();
			isPlayer = false;
		}
	}
}
