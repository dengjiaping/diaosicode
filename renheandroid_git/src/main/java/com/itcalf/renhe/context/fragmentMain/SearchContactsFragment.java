package com.itcalf.renhe.context.fragmentMain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.itcalf.renhe.R;
import com.itcalf.renhe.RenheApplication;
import com.itcalf.renhe.adapter.SeparatedListAdapter;
import com.itcalf.renhe.adapter.WeiboAdapter;
import com.itcalf.renhe.command.IContactCommand;
import com.itcalf.renhe.context.archives.MyHomeArchivesActivity;
import com.itcalf.renhe.dto.ContactList;
import com.itcalf.renhe.dto.ContactList.Member;
import com.itcalf.renhe.dto.UserInfo;
import com.itcalf.renhe.po.Contact;
import com.itcalf.renhe.utils.PinyinUtil;
import com.itcalf.renhe.view.SideBar;
import com.itcalf.renhe.view.SideBar.OnTouchingLetterChangedListener;
import com.umeng.analytics.MobclickAgent;

/**
 * Feature:搜索我的联系人
 * Desc:初始化加载我的好友联系人，以分割标题的方式显示，默认按字母顺序排序。
 * @author xp
 *
 */
public class SearchContactsFragment extends Fragment {
	private boolean isFromMenu;
	//登出标识（两次按下返回退出时的标识字段）
	private boolean logoutFlag;
	//联系人快速定位视图组件
	private SideBar sideBar;
	//联系人关键字查询条件
	private EditText mKeywordEdt;
	//联系人数量
	private TextView mContactCountTxt;
	//联系人列表
	private ListView mContactsListView;
	//字母显示
	private TextView mLetterTxt;

	private Handler mNotifHandler;
	//带标题分割的Adapter
	private SeparatedListAdapter mAdapter;
	//联系人数据
	private Map<String, List<Contact>> mContactsMap;
	private Drawable imgCloseButton;
	private Context context;
	private View rootView;
	private DialogFragment dialogFragment;
	private String tag = "my_dialog"; 
	private static final int REQUEST_DELAY_TIME = 500;
	public SearchContactsFragment() {
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		getActivity().setTitle("联系人");
		if (getActivity() instanceof MainFragment) {
			MainFragment fca = (MainFragment) getActivity();
			fca.switchTitle("联系人");
		} 
		if(null == rootView)
		{
			rootView = inflater.inflate(R.layout.search_contacts, null);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();  
        if (parent != null) {  
            parent.removeView(rootView);  
        }   
		context = (Context)getActivity();
		findView(rootView);
		initData();
		initListener();
//		RenheApplication.getInstance().addActivity(this);
		return rootView;
	}

	protected void findView(View view) {
		sideBar = (SideBar) view.findViewById(R.id.contact_cv);
		mKeywordEdt = (EditText) view.findViewById(R.id.keyword_edt);
		mContactCountTxt = (TextView) view.findViewById(R.id.count_txt);
		mContactsListView = (ListView) view.findViewById(R.id.contacts_list);
		mLetterTxt = (TextView) view.findViewById(R.id.letter_txt);
		imgCloseButton = getResources().getDrawable(R.drawable.relationship_input_del);
	}

	protected void initData() {
		mContactsMap = new TreeMap<String, List<Contact>>();
		isFromMenu = getActivity().getIntent().getBooleanExtra("isFromMenu", false);
//		setTextValue(R.id.title_txt, "联系人");
		sideBar.setTextView(mLetterTxt);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
//				// 该字母首次出现的位置
				int section = mAdapter.getPositionForTag(s.charAt(0)+"");
				if (-1 != section) {
					int positon = mAdapter.getPositionForSection(section);
					mContactsListView.setSelection(positon + 1);
				}
			}
		});
		mAdapter = new SeparatedListAdapter(context, R.layout.contact_list_header);
		mNotifHandler = new Handler(new Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					String keyword = (String) msg.obj;
					populateContacts(keyword);
					break;
				case 1:
//					getActivity().removeDialog(1);
					removeDialog();
					populateContacts(null);
					mContactsListView.setAdapter(mAdapter);
					break;
				}
				return false;
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
//		getActivity().showDialog(1);
	    showDialog();
		String sid = getActivity().getIntent().getStringExtra("sid");
		String userName = getActivity().getIntent().getStringExtra("friendName");
		if (TextUtils.isEmpty(sid)) {
			localSearch();
		} else {
			remoteSearch(sid);
		}
		if(!TextUtils.isEmpty(userName)){
//			setTextValue(R.id.title_txt, userName+"的联系人");
			getActivity().setTitle(userName+"的联系人");
		}
		// Set bounds of the Clear button so it will look ok
		if(imgCloseButton != null){
			imgCloseButton.setBounds(0, 0, imgCloseButton.getIntrinsicWidth(), imgCloseButton.getIntrinsicHeight());
		}
	}

	/**
	 * 查看好友的联系人，需要访问HTTP
	 * @param sid
	 */
	private void remoteSearch(final String sid) {
		final IContactCommand contactCommand = RenheApplication.getInstance().getContactCommand();
		final UserInfo userInfo = RenheApplication.getInstance().getUserInfo();
		Runnable loadRemoteRun = new Runnable() {
			@Override
			public void run() {
				try {
					ContactList clist = contactCommand.getContactList(sid, userInfo.getSid(), userInfo.getAdSId());
					if (1 == clist.getState()) {
						Member[] ml = clist.getMemberList();
						if (null != ml && ml.length > 0) {
							for (int i = 0; i < ml.length; i++) {
								Contact ct = new Contact();
								ct.setId(ml[i].getSid());
								ct.setName(ml[i].getName());
								ct.setJob(ml[i].getTitle());
								ct.setCompany(ml[i].getCompany());
								ct.setContactface(ml[i].getUserface());
								ct.setAccountType(ml[i].getAccountType());
								ct.setRealname(ml[i].isRealname());
								String namePinyin = PinyinUtil.cn2FirstSpell(ct.getName());
								if (null != namePinyin && namePinyin.length() > 0) {
									String n = namePinyin.substring(0, 1).toUpperCase();
									List<Contact> ctList = mContactsMap.get(n);
									if (null == ctList) {
										ctList = new ArrayList<Contact>();
									}
									ctList.add(ct);
									mContactsMap.put(n, ctList);
								}
							}
						}
					}
				} catch (Exception e) {
					System.out.println(e);
				}
				mNotifHandler.sendEmptyMessage(1);
			}
		};
		mNotifHandler.postDelayed(loadRemoteRun, REQUEST_DELAY_TIME);
	}

	/**
	 * 加载自己的好友，查询本地数据库
	 */
	private void localSearch() {
		Runnable loadCacheRun = new Runnable() {
			@Override
			public void run() {
				try {
					Contact[] cts = RenheApplication.getInstance().getContactCommand().getAllContact(
							RenheApplication.getInstance().getUserInfo().getEmail());
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
		};
		mNotifHandler.postDelayed(loadCacheRun, REQUEST_DELAY_TIME);
	}

	private String ITEM_ID = "id";
	private String ITEM_USERNAME = "username";
	private String ITEM_JOB = "job";
	private String ITEM_COMPANY = "company";
	private String ITEM_CONTACTFACE = "contactFace";
	private String ITEM_USERFACE = "userface";
	private String ITEM_ACCOUNTTYPE = "accountType";
	private String ITEM_REALNAME = "isRealName";

	public Map<String, ?> createItem(String id, String username, String job, String company, String userface,int accounttype,boolean isRealname) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_ID, id);
		item.put(ITEM_USERNAME, username);
		item.put(ITEM_JOB, job);
		item.put(ITEM_COMPANY, company);
		item.put(ITEM_CONTACTFACE, R.drawable.avatar);
		item.put(ITEM_USERFACE, userface);
		item.put(ITEM_ACCOUNTTYPE, accounttype);
		item.put(ITEM_REALNAME, isRealname);
		return item;
	}

//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//		case 1:
//			ProgressDialog findPd = new ProgressDialog(this);
//			findPd.setMessage("正在加载联系人,请稍候...");
////			findPd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			findPd.setCanceledOnTouchOutside(false);
//			return findPd;
//		default:
//			return null;
//		}
//	}

	protected void initListener() {
		mContactsListView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Map<String, Object> map = ((Map<String, Object>) mAdapter.getItem(position));
				Intent intent = new Intent(context, MyHomeArchivesActivity.class);
				intent.putExtra(MyHomeArchivesActivity.FLAG_INTENT_DATA, (String) map.get(ITEM_ID));
				startActivity(intent);
			}

		});
	}

	Handler handler = new Handler();
	Runnable run = new Runnable() {

		@Override
		public void run() {
			populateContacts(mKeywordEdt.getText().toString());
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
				mKeywordEdt.setCompoundDrawablePadding(1);
			} else {
				mKeywordEdt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
			}
			handler.postDelayed(run, REQUEST_DELAY_TIME);
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
				if (curX > v.getWidth() - v.getPaddingRight() - imgCloseButton.getIntrinsicWidth() && !TextUtils.isEmpty(mKeywordEdt.getText().toString())) {
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

	/**
	 * 查询联系人，支持拼音简写、字母查询
	 * @param keyword
	 */
	private void populateContacts(String keyword) {
		mAdapter.clear();
		int count = 0;
		if (null != mContactsMap && !mContactsMap.isEmpty()) {
			Set<Entry<String, List<Contact>>> set = mContactsMap.entrySet();
			Iterator<Entry<String, List<Contact>>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<java.lang.String, java.util.List<com.itcalf.renhe.po.Contact>> entry = (Map.Entry<java.lang.String, java.util.List<com.itcalf.renhe.po.Contact>>) it
						.next();
				List<Contact> contactsList = entry.getValue();
				if (null != contactsList && !contactsList.isEmpty()) {
					List<Map<String, ?>> list = new LinkedList<Map<String, ?>>();
					for (int i = 0; i < contactsList.size(); i++) {
						Contact ct = contactsList.get(i);
						if (null != keyword
								&& null != ct.getName()
								&& (ct.getName().toUpperCase().startsWith(keyword.toUpperCase()) || PinyinUtil.cn2FirstSpell(
									ct.getName()).startsWith(keyword.toUpperCase()))) {
							++count;
							list.add(createItem(ct.getId(), ct.getName(), ct.getJob(), ct.getCompany(), ct.getContactface(),ct.getAccountType(),ct.isRealname()));
						} else if (TextUtils.isEmpty(keyword)) {
							++count;
							list.add(createItem(ct.getId(), ct.getName(), ct.getJob(), ct.getCompany(), ct.getContactface(),ct.getAccountType(),ct.isRealname()));
						}
					}
					if (!list.isEmpty()) {
						mAdapter.addSection(entry.getKey(), new WeiboAdapter(context, list,
								R.layout.contact_list_item, new String[] { ITEM_USERNAME, ITEM_JOB, ITEM_COMPANY,
										ITEM_CONTACTFACE }, new int[] { R.id.username_txt, R.id.job_txt, R.id.company_txt,
										R.id.avatar_img }, RenheApplication.getInstance().getUserInfo().getEmail(), mContactsListView));
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

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(null != mAdapter){
			mAdapter.clear();
		}
		if(null != mContactsMap){
			mContactsMap = null;
		}
	}
	@Override
	public void onDetach() {
		super.onDetach();
	}
	@Override
	public void onPause() {
		super.onPause();
		 MobclickAgent.onPageEnd("SearchContactsScreen"); 
	}
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SearchContactsScreen"); //统计页面
	}
	@Override
	public void onStart() {
		super.onStart();
	}
	@Override
	public void onStop() {
		super.onStop();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
//	@Override
//	public void finish() {
//		super.finish();
//		mAdapter.clear();
//		mContactsMap = null;
//	}
	private void showDialog(){
		if(null == dialogFragment){
			dialogFragment = MyDialogFragment.newInstance("正在加载联系人...");  
		}
		if(!dialogFragment.isAdded()){
			dialogFragment.show(getFragmentManager(), tag);
		}
	}
	private void removeDialog(){
		if( null != dialogFragment && dialogFragment.isAdded() && !dialogFragment.isDetached() && !dialogFragment.isHidden()){
			dialogFragment.dismiss();
		}
	}
}
