package com.itcalf.renhe.context.contacts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itcalf.renhe.R;
import com.itcalf.renhe.adapter.SeparatedListAdapter;
import com.itcalf.renhe.adapter.SeparatedListAdapterTemp;
import com.itcalf.renhe.context.innermsg.ReceiverGridAdapter;
import com.itcalf.renhe.context.template.BaseActivity;
import com.itcalf.renhe.po.Contact;
import com.itcalf.renhe.utils.AsyncImageLoader;
import com.itcalf.renhe.utils.PinyinUtil;
import com.itcalf.renhe.view.SideBar;
import com.itcalf.renhe.view.SideBar.OnTouchingLetterChangedListener;

/**
 * Feature:站内信选择我的联系人 Desc:初始化加载我的好友联系人，以分割标题的方式显示，默认按字母顺序排序,增加了多选框选项。
 * 
 * @author xp
 * 
 */
public class SelectContactsActivity extends BaseActivity {

	// 联系人快速定位视图组件
	private SideBar sideBar;
	// 联系人关键字查询条件
	private EditText mKeywordEdt;
	// 联系人关键字查询条件
	private TextView mContactCountTxt;
	// 联系人列表
	private ListView mContactsListView;
	// 字母显示
	private TextView mLetterTxt;

	private Handler mNotifHandler;
	// 带标题分割的Adapter
	private SeparatedListAdapter mAdapter;
	// 联系人数据
	private Map<String, List<Contact>> mContactsMap = new TreeMap<String, List<Contact>>();
	// 当前选择的联系人数据
	private Map<String, Contact> mSelectedMap = new HashMap<String, Contact>();

	private Drawable imgCloseButton;

	private List<Contact> mReceiverList;
	private ReceiverGridAdapter receiverGridAdapter;
//	private GridView receiverGridView;

	private ListView mContactsListViewTemp;
	private SeparatedListAdapterTemp mAdapterTemp;
	private int tempCount = 0;
	private final static String COMPLETE = "完成";
	private final static String SURE = "确定";
	private Gallery gl;
	private String currentTitle = COMPLETE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTemplate().doInActivity(this, R.layout.selected_contacts);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void findView() {
		super.findView();
		sideBar = (SideBar) findViewById(R.id.contact_cv);
		mKeywordEdt = (EditText) findViewById(R.id.keyword_edt);
		mContactCountTxt = (TextView) findViewById(R.id.count_txt);
		mContactsListView = (ListView) findViewById(R.id.contacts_list);
		mLetterTxt = (TextView) findViewById(R.id.letter_txt);
		imgCloseButton = getResources().getDrawable(R.drawable.relationship_input_del);
		gl = (Gallery) this.findViewById(R.id.gallery_view);
//		receiverGridView = (GridView) findViewById(R.id.add_receiver_grid);

		mReceiverList = (List<Contact>) getIntent().getExtras().getSerializable("contacts");
		if (null == mReceiverList) {
			mReceiverList = new ArrayList<Contact>();
		}
		if (mReceiverList.size() > 0) {
//			receiverGridView.setVisibility(View.VISIBLE);
			gl.setVisibility(View.VISIBLE);
			mContactCountTxt.setVisibility(View.GONE);
		} else {
//			receiverGridView.setVisibility(View.GONE);
			gl.setVisibility(View.GONE);
			mContactCountTxt.setVisibility(View.VISIBLE);
		}
		receiverGridAdapter = new ReceiverGridAdapter(this, mReceiverList);
//		receiverGridView.setAdapter(receiverGridAdapter);

		mContactsListViewTemp = (ListView) findViewById(R.id.contacts_list_temp);
		gl.setAdapter(receiverGridAdapter);
		gl.setSelection(gl.getCount()/2);
	}

	@Override
	protected void initData() {
		super.initData();
		setTextValue(R.id.title_txt, "联系人");
		sideBar.setTextView(mLetterTxt);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				//				// 该字母首次出现的位置
				int section = mAdapter.getPositionForTag(s.charAt(0) + "");
				if (-1 != section) {
					int positon = mAdapter.getPositionForSection(section);
					mContactsListView.setSelection(positon + 1);
				}
			}
		});
		mKeywordEdt.addTextChangedListener(tbxEdit_TextChanged);
		mKeywordEdt.setOnTouchListener(txtEdit_OnTouch);
		mKeywordEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				return true;
			}
		});
		mAdapter = new SeparatedListAdapter(this, R.layout.contact_list_header);
		mNotifHandler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					String keyword = (String) msg.obj;
					populateContact(keyword);
					break;
				case 1:
					removeDialog(1);
					populateContact(null);
					mContactsListView.setAdapter(mAdapter);
					break;
				}
				return false;
			}
		});
		showDialog(1);
		localSearch();
		// Set bounds of the Clear button so it will look ok
		if (imgCloseButton != null) {
			imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight());
		}
		mAdapterTemp = new SeparatedListAdapterTemp(this, R.layout.contact_list_header);

	}

	/**
	 * 加载自己的好友，查询本地数据库
	 */
	private void localSearch() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Contact[] cts = getRenheApplication().getContactCommand().getAllContact(
							getRenheApplication().getUserInfo().getEmail());
					if (null != cts && cts.length > 0) {
						for (int i = 0; i < cts.length; i++) {
							String namePinyin = PinyinUtil.cn2FirstSpell(cts[i].getName());
							if (null != namePinyin && namePinyin.length() > 0) {
								String n = namePinyin.substring(0, 1).toUpperCase();
								List<Contact> ctList = mContactsMap.get(n);
								if (null == ctList) {
									ctList = new ArrayList<Contact>();
								}
								ctList.add(cts[i]);
								mContactsMap.put(n, ctList);
							}
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				}
				mNotifHandler.sendEmptyMessage(1);
			}
		}).start();
	}

	private String ITEM_ID = "id";
	private String ITEM_USERNAME = "username";
	private String ITEM_JOB = "job";
	private String ITEM_COMPANY = "company";
	private String ITEM_CONTACTFACE = "contactFace";
	private String ITEM_USERFACE = "userface";

	public Map<String, ?> createItem(String id, String username, String job, String company, String userface) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_ID, id);
		item.put(ITEM_USERNAME, username);
		item.put(ITEM_JOB, job);
		item.put(ITEM_COMPANY, company);
		item.put(ITEM_CONTACTFACE, R.drawable.avatar);
		item.put(ITEM_USERFACE, userface);
		return item;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog findPd = new ProgressDialog(this);
			findPd.setMessage("正在加载联系人,请稍候...");
			//			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			findPd.setCanceledOnTouchOutside(false);
			return findPd;
		default:
			return null;
		}
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem save = menu.findItem(R.id.item_save);
		save.setVisible(true);
		if (!TextUtils.isEmpty(mKeywordEdt.getText().toString())) {
			save.setTitle(SURE);
			currentTitle = SURE;
		}else{
			save.setTitle(COMPLETE);
			currentTitle = COMPLETE;
		}
		return super.onPrepareOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case android.R.id.home:
			if (currentTitle.equals(COMPLETE)) {
				onResultBack();
			} else if (currentTitle.equals(SURE)) {
				mKeywordEdt.setText("");
			}
			return true;
		case R.id.item_save:

			if (currentTitle.equals(COMPLETE)) {
				onResultBack();
			} else if (currentTitle.equals(SURE)) {
				mKeywordEdt.setText("");
			}
			return true;
		
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void initListener() {
		super.initListener();
	       gl.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Contact contact = (Contact) arg0.getItemAtPosition(arg2);
				manageReceiver(contact, false);
				if (TextUtils.isEmpty(mKeywordEdt.getText().toString().trim())) {
					updateView(contact.getId(), 0);
				} else {
					updateViewTemp(contact.getId(), 0);
				}
			}
		});
	}

	private void onResultBack() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contacts", (Serializable) mReceiverList);
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
		finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	Handler handler = new Handler();
	Runnable run = new Runnable() {

		@Override
		public void run() {
			if (!TextUtils.isEmpty(mKeywordEdt.getText().toString())) {
				mContactsListView.setVisibility(View.GONE);
				mContactsListViewTemp.setVisibility(View.VISIBLE);
				populateContactTemp(mKeywordEdt.getText().toString());
				if (tempCount == 0) {
					mContactsListViewTemp.setAdapter(mAdapterTemp);
					tempCount += 1;
				}
//				findViewById(R.id.item_save).setText(SURE);
				getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
			} else {
				mContactsListView.setVisibility(View.VISIBLE);
				mContactsListViewTemp.setVisibility(View.GONE);
				populateContact(mKeywordEdt.getText().toString());
//				mSendBt.setText(COMPLETE);
				getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
			}
		}
	};

	/** 搜索框输入状态监听 **/
	private TextWatcher tbxEdit_TextChanged = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s.toString())) {
				mKeywordEdt.setCompoundDrawablesWithIntrinsicBounds(null, null,
						getResources().getDrawable(R.drawable.clearbtn_selected), null);
			} else {
				mKeywordEdt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
			handler.postDelayed(run, 500);
		}

	};

	/** 搜索框点击事件监听 **/
	private OnTouchListener txtEdit_OnTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			/** 手指离开的事件 */
			case MotionEvent.ACTION_UP:
				/** 手指抬起时候的坐标 **/
				int curX = (int) event.getX();
				if (curX > v.getWidth() - v.getPaddingRight() - imgCloseButton.getIntrinsicWidth()
						&& !TextUtils.isEmpty(mKeywordEdt.getText().toString())) {
					mKeywordEdt.setText("");
					int cacheInputType = mKeywordEdt.getInputType();
					// setInputType 可以更改 TextView 的输入方式
					mKeywordEdt.setInputType(InputType.TYPE_NULL);// EditText始终不弹出软件键盘
					mKeywordEdt.onTouchEvent(event);
					mKeywordEdt.setInputType(cacheInputType);
					return true;
				}
				break;
			}
			return false;
		}
	};

	private void populateContact(String keyword) {
		mAdapter.clear();
		mSelectedMap.clear();
		int count = 0;
		if (null != mContactsMap && !mContactsMap.isEmpty()) {
			Set<Entry<String, List<Contact>>> set = mContactsMap.entrySet();
			Iterator<Entry<String, List<Contact>>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<java.lang.String, java.util.List<com.itcalf.renhe.po.Contact>> entry = (Map.Entry<java.lang.String, java.util.List<com.itcalf.renhe.po.Contact>>) it
						.next();
				List<Contact> contactsList = entry.getValue();
				if (null != contactsList && !contactsList.isEmpty()) {
					List<Contact> ctList = new ArrayList<Contact>();
					for (int i = 0; i < contactsList.size(); i++) {
						Contact ct = contactsList.get(i);
						if (null != keyword
								&& null != ct.getName()
								&& (ct.getName().toUpperCase().startsWith(keyword.toUpperCase()) || PinyinUtil.cn2FirstSpell(
										ct.getName()).startsWith(keyword.toUpperCase()))) {
							++count;
							ctList.add(ct);
						} else if (TextUtils.isEmpty(keyword)) {
							++count;
							ctList.add(ct);
						}
					}
					if (!ctList.isEmpty()) {
						mAdapter.addSection(entry.getKey(), new ContactsAdapter(ctList, SelectContactsActivity.this));
					}
				}
			}
		}
		if (count > 0) {
			mContactCountTxt.setText(count + "个联系人");
		} else {
			mContactCountTxt.setText("");
		}
	}

	public final int LAYOUT_INDEX = 0;
	public final int CHECKBOX_INDEX = 10000;

	class ContactsAdapter extends BaseAdapter {

		private List<Contact> cts;
		private LayoutInflater mLayoutInf;

		public ContactsAdapter(List<Contact> cts, Context ct) {
			super();
			this.cts = cts;
			mLayoutInf = LayoutInflater.from(ct);
		}

		@Override
		public int getCount() {
			return cts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return cts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup group) {
			final HolderView holderView;
			if (null == view) {
				view = mLayoutInf.inflate(R.layout.selected_contact_list_item, null);
				holderView = new HolderView();
				holderView.username = (TextView) view.findViewById(R.id.username_txt);
				holderView.job = ((TextView) view.findViewById(R.id.job_txt));
				holderView.company = ((TextView) view.findViewById(R.id.company_txt));
				holderView.userFace = ((ImageView) view.findViewById(R.id.contactface_img));
				holderView.checkbox = ((CheckBox) view.findViewById(R.id.selected_ck));
				holderView.vipIv = ((ImageView) view.findViewById(R.id.vipImage));
				holderView.realNameIv = ((ImageView) view.findViewById(R.id.realnameImage));
				view.setTag(holderView);
			} else {
				holderView = (HolderView) view.getTag();
			}
			holderView.checkbox.setId(CHECKBOX_INDEX + position);
			holderView.username.setText(cts.get(position).getName());
			holderView.job.setText(cts.get(position).getJob());
			holderView.company.setText(cts.get(position).getCompany());
			int mid = holderView.checkbox.getId() - CHECKBOX_INDEX;
			view.setId(LAYOUT_INDEX + position);

			boolean isExist = false;
			for (Contact contact : mReceiverList) {
				if (contact.getId().equals(cts.get(mid).getId())) {
					isExist = true;
					break;
				}
			}
			if (isExist) {
				holderView.checkbox.setChecked(true);
			} else {
				holderView.checkbox.setChecked(false);
			}
			int accountType = cts.get(position).getAccountType();
			boolean isRealName = cts.get(position).isRealname();
			switch (accountType) {
			case 0:
				holderView.vipIv.setVisibility(View.GONE);
				break;
			case 1:
				holderView.vipIv.setVisibility(View.VISIBLE);
				holderView.vipIv.setImageResource(R.drawable.vip_1);
				break;
			case 2:
				holderView.vipIv.setVisibility(View.VISIBLE);
				holderView.vipIv.setImageResource(R.drawable.vip_2);
				break;
			case 3:
				holderView.vipIv.setVisibility(View.VISIBLE);
				holderView.vipIv.setImageResource(R.drawable.vip_3);
				break;

			default:
				break;
			}
			if (isRealName && accountType <= 0) {
				holderView.realNameIv.setVisibility(View.VISIBLE);
				holderView.realNameIv.setImageResource(R.drawable.realname);
			} else {
				holderView.realNameIv.setVisibility(View.GONE);
			}

			AsyncImageLoader
					.getInstance()
					.populateData(SelectContactsActivity.this, getRenheApplication().getUserInfo().getEmail(), false, true, false)
					.loadPic(holderView.userFace, cts.get(position).getId(), cts.get(position).getContactface(), 70, 70);
			holderView.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int id = holderView.checkbox.getId() - CHECKBOX_INDEX;
					if (isChecked) {
						mSelectedMap.put(cts.get(id).getId(), cts.get(id));
					} else {
						mSelectedMap.remove(cts.get(id).getId());
					}
					manageReceiver(cts.get(id), isChecked);
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int id = v.getId() - LAYOUT_INDEX;
					CheckBox cb = holderView.checkbox;
					if (!cb.isChecked()) {
						cb.setChecked(true);
						mSelectedMap.put(cts.get(id).getId(), cts.get(id));
						manageReceiver(cts.get(id), true);
					} else {
						cb.setChecked(false);
						mSelectedMap.remove(cts.get(id).getId());
						manageReceiver(cts.get(id), false);
					}

				}
			});
			return view;
		}

	}

	public static class HolderView {

		public TextView username;
		public TextView job;
		public TextView company;
		public ImageView userFace;
		public CheckBox checkbox;
		public ImageView vipIv;
		public ImageView realNameIv;
	}

	public void updateView(String id, int type) {
		int itemIndex = -1;
		for (int i = 0; i < mContactsListView.getAdapter().getCount(); i++) {
			if (mContactsListView.getAdapter().getItem(i) instanceof Contact) {
				Contact contact = (Contact) mContactsListView.getAdapter().getItem(i);
				if (id.equals(contact.getId())) {
					itemIndex = i;
					break;
				}
			}
		}
		//得到第一个可显示控件的位置，
		int visiblePosition = mContactsListView.getFirstVisiblePosition();
		//只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
		if (itemIndex >= 0 && itemIndex - visiblePosition >= 0) {
			//得到要更新的item的view
			View view = mContactsListView.getChildAt(itemIndex - visiblePosition);//自定义的listview有header会算作listview的子itemview，故加1
			//从view中取得holder
			if (null != view) {
				HolderView holderView = (HolderView) view.getTag();
				if (null != holderView) {
					if (type == 1) {//选中
						holderView.checkbox.setChecked(true);
					} else if (type == 0) {
						holderView.checkbox.setChecked(false);
					}
				}
			}
		}
	}

	private void manageReceiver(Contact mContact, boolean isChecked) {
		boolean ifExist = false;
		int flag = -1;
		for (int i = 0; i < mReceiverList.size(); i++) {
			Contact contact2 = mReceiverList.get(i);
			if (contact2.getId().equals(mContact.getId())) {
				ifExist = true;
				flag = i;
				break;
			}
		}
		if (isChecked) {
			if (!ifExist) {
				mReceiverList.add(mContact);
			}
		} else {
			if (ifExist && flag >= 0) {
				mReceiverList.remove(flag);
			}
		}
		receiverGridAdapter.notifyDataSetChanged();
		gl.setSelection(gl.getCount()/2);
		if (mReceiverList.size() > 0) {
//			receiverGridView.setVisibility(View.VISIBLE);
			gl.setVisibility(View.VISIBLE);
			mContactCountTxt.setVisibility(View.GONE);
		} else {
//			receiverGridView.setVisibility(View.GONE);
			gl.setVisibility(View.GONE);
			mContactCountTxt.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void finish() {
		super.finish();
		mAdapter.clear();
		mAdapterTemp.clear();
	}

	private void populateContactTemp(String keyword) {
		mAdapterTemp.clear();
		int count = 0;
		if (null != mContactsMap && !mContactsMap.isEmpty()) {
			Set<Entry<String, List<Contact>>> set = mContactsMap.entrySet();
			Iterator<Entry<String, List<Contact>>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<java.lang.String, java.util.List<com.itcalf.renhe.po.Contact>> entry = (Map.Entry<java.lang.String, java.util.List<com.itcalf.renhe.po.Contact>>) it
						.next();
				List<Contact> contactsList = entry.getValue();
				if (null != contactsList && !contactsList.isEmpty()) {
					List<Contact> ctList = new ArrayList<Contact>();
					for (int i = 0; i < contactsList.size(); i++) {
						Contact ct = contactsList.get(i);
						if (null != keyword
								&& null != ct.getName()
								&& (ct.getName().toUpperCase().startsWith(keyword.toUpperCase()) || PinyinUtil.cn2FirstSpell(
										ct.getName()).startsWith(keyword.toUpperCase()))) {
							++count;
							ctList.add(ct);
						} else if (TextUtils.isEmpty(keyword)) {
							++count;
							ctList.add(ct);
						}
					}
					if (!ctList.isEmpty()) {
						mAdapterTemp.addSection(entry.getKey(), new ContactsAdapterTemp(ctList, SelectContactsActivity.this));
					}
				}
			}
		}
		if (count > 0) {
			mContactCountTxt.setText(count + "个联系人");
		} else {
			mContactCountTxt.setText("");
		}
	}

	public final int LAYOUT_INDEXTemp = 0;
	public final int CHECKBOX_INDEXTemp = 10000;

	class ContactsAdapterTemp extends BaseAdapter {

		private List<Contact> cts;
		private LayoutInflater mLayoutInf;

		public ContactsAdapterTemp(List<Contact> cts, Context ct) {
			super();
			this.cts = cts;
			mLayoutInf = LayoutInflater.from(ct);
		}

		@Override
		public int getCount() {
			return cts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return cts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View view, ViewGroup group) {
			final HolderViewTemp holderView1;
			//			if (null == view) {
			view = mLayoutInf.inflate(R.layout.selected_contact_list_item, null);
			holderView1 = new HolderViewTemp();
			holderView1.username = (TextView) view.findViewById(R.id.username_txt);
			holderView1.job = ((TextView) view.findViewById(R.id.job_txt));
			holderView1.company = ((TextView) view.findViewById(R.id.company_txt));
			holderView1.userFace = ((ImageView) view.findViewById(R.id.contactface_img));
			holderView1.checkbox = ((CheckBox) view.findViewById(R.id.selected_ck));
			holderView1.vipIv = ((ImageView) view.findViewById(R.id.vipImage));
			holderView1.realNameIv = ((ImageView) view.findViewById(R.id.realnameImage));
			view.setTag(holderView1);
			//			}else {
			//				holderView1 = (HolderViewTemp) view.getTag();
			//			}
			//				
			holderView1.checkbox.setId(CHECKBOX_INDEX + position);
			holderView1.username.setText(cts.get(position).getName());
			holderView1.job.setText(cts.get(position).getJob());
			holderView1.company.setText(cts.get(position).getCompany());
			int mid = holderView1.checkbox.getId() - CHECKBOX_INDEX;
			view.setId(LAYOUT_INDEX + position);

			boolean isExist = false;
			for (Contact contact : mReceiverList) {
				if (contact.getId().equals(cts.get(mid).getId())) {
					isExist = true;
					break;
				}
			}
			if (isExist) {
				holderView1.checkbox.setChecked(true);
			} else {
				holderView1.checkbox.setChecked(false);
			}
			int accountType = cts.get(position).getAccountType();
			boolean isRealName = cts.get(position).isRealname();
			switch (accountType) {
			case 0:
				holderView1.vipIv.setVisibility(View.GONE);
				break;
			case 1:
				holderView1.vipIv.setVisibility(View.VISIBLE);
				holderView1.vipIv.setImageResource(R.drawable.vip_1);
				break;
			case 2:
				holderView1.vipIv.setVisibility(View.VISIBLE);
				holderView1.vipIv.setImageResource(R.drawable.vip_2);
				break;
			case 3:
				holderView1.vipIv.setVisibility(View.VISIBLE);
				holderView1.vipIv.setImageResource(R.drawable.vip_3);
				break;

			default:
				break;
			}
			if (isRealName && accountType <= 0) {
				holderView1.realNameIv.setVisibility(View.VISIBLE);
				holderView1.realNameIv.setImageResource(R.drawable.realname);
			} else {
				holderView1.realNameIv.setVisibility(View.GONE);
			}
			AsyncImageLoader
					.getInstance()
					.populateData(SelectContactsActivity.this, getRenheApplication().getUserInfo().getEmail(), false, true, false)
					.loadPic(holderView1.userFace, cts.get(position).getId(), cts.get(position).getContactface(), 70, 70);
			holderView1.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					int id = holderView1.checkbox.getId() - CHECKBOX_INDEX;
					manageReceiver(cts.get(id), isChecked);
				}
			});
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int id = v.getId() - LAYOUT_INDEX;
					CheckBox cb = holderView1.checkbox;
					if (!cb.isChecked()) {
						cb.setChecked(true);
						manageReceiver(cts.get(id), true);
					} else {
						cb.setChecked(false);
						manageReceiver(cts.get(id), false);
					}
				}
			});
			return view;
		}
	}

	public static class HolderViewTemp {

		public TextView username;
		public TextView job;
		public TextView company;
		public ImageView userFace;
		public CheckBox checkbox;
		public ImageView vipIv;
		public ImageView realNameIv;
	}

	public void updateViewTemp(String id, int type) {
		int itemIndex = -1;
		for (int i = 0; i < mContactsListViewTemp.getAdapter().getCount(); i++) {
			if (mContactsListViewTemp.getAdapter().getItem(i) instanceof Contact) {
				Contact contact = (Contact) mContactsListViewTemp.getAdapter().getItem(i);
				if (id.equals(contact.getId())) {
					itemIndex = i;
					break;
				}
			}
		}
		//得到第一个可显示控件的位置，
		int visiblePosition = mContactsListViewTemp.getFirstVisiblePosition();
		//只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
		if (itemIndex >= 0 && itemIndex - visiblePosition >= 0) {
			//得到要更新的item的view
			View view = mContactsListViewTemp.getChildAt(itemIndex - visiblePosition);//自定义的listview有header会算作listview的子itemview，故加1
			//从view中取得holder
			if (null != view) {
				HolderViewTemp holderView = (HolderViewTemp) view.getTag();
				if (null != holderView) {
					if (type == 1) {//选中
						holderView.checkbox.setChecked(true);
					} else if (type == 0) {
						holderView.checkbox.setChecked(false);
					}
				}
			}
		}
	}
}
