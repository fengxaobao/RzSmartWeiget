package com.rz.printf;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;
import java.util.Vector;

@SuppressWarnings("deprecation")
public class ComboBox extends LinearLayout {

	private final static 	String TAG = "ComboBox";
    
	private ListViewItemClickListener	m_listener;
	
    private View	 		m_view;
    private ListView 		m_listView;
    private PopupWindow 	m_popupwindow;
    private ListViewAdapter m_adapter_listview;
	private List<String>	m_data = new Vector<String>(); 
	private Context			m_context;
	private Button          m_Button;
	private EditText        m_EditText;
	private static int 		m_ViewId = 0x70000000;
    
	public ComboBox(Context context) {
		super(context);
		m_context = context;		
		init();
		
	}

	public ComboBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_context = context;
		init();
	}
	
	private void init(){
		
		View cbx = LayoutInflater.from(m_context).inflate(R.layout.dsview_combobox_main, this);
		
		m_Button = (Button) cbx.findViewById(R.id.comboButton);
		m_EditText = (EditText) cbx.findViewById(R.id.comboEditText);
		m_EditText.setId(m_ViewId++);
		
		m_adapter_listview = new ListViewAdapter(m_context);
    	m_view = LayoutInflater.from(m_context).inflate(R.layout.dsview_combobox_listview, new LinearLayout(m_context));
    	
    	m_listView =  (ListView)m_view.findViewById(R.id.id_listview);
    	m_listView.setAdapter(m_adapter_listview);
    	m_listView.setClickable(true);
    	m_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				m_popupwindow.dismiss();
				m_EditText.setText(m_data.get(position));
				
				if (m_listener != null){
					m_listener.onItemClick(position);
				}
			}
		});
    	
    	setListeners();
	}
	
	public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        m_EditText.setEnabled(enabled);
        m_Button.setEnabled(enabled);
    }
	
	public void setText(String text)
	{
		m_EditText.setText(text);
	}
	
	public void setData(List<String> data){
		
		m_data.clear();
		
		if(null != data)
		{
			m_data.addAll(data);
		}
		
		m_adapter_listview.notifyDataSetChanged();
	}

	public List<String> getData() { return m_data; }
	
	public void clear() {
		
		m_data.clear();
		
		m_adapter_listview.notifyDataSetChanged();
	}
	
	public void addString(String text){
		
		m_data.add(text);
		
		m_adapter_listview.notifyDataSetChanged();
	}

	public void setListViewOnClickListener(ListViewItemClickListener listener){
		m_listener = listener;
	}
	
	private void setListeners() {

		m_Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
								
				Log.d(TAG, "Click......");
				 if(m_popupwindow == null){
					 m_popupwindow = new PopupWindow(m_view, ComboBox.this.getWidth(), 600);
					      
					 m_popupwindow.setBackgroundDrawable(new BitmapDrawable());
					 
					 m_popupwindow.setFocusable(true);  
					 m_popupwindow.setOutsideTouchable(true);
					 m_popupwindow.showAsDropDown(ComboBox.this, 0, 0);

					 }else if(m_popupwindow.isShowing()){
	                	m_popupwindow.dismiss();
					 }else{
	                	m_popupwindow.showAsDropDown(ComboBox.this);
				}
			}
			
		});
	}	
	
	 class ListViewAdapter extends BaseAdapter {
        private LayoutInflater 	m_inflate;
        
        public ListViewAdapter(Context context) {        	
            // TODO Auto-generated constructor stub
        	m_inflate = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return m_data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return m_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
            TextView textview = null;
            
            if(convertView==null){
                convertView	= m_inflate.inflate(R.layout.dsview_combobox_item, new LinearLayout(m_context));
                textview = (TextView)convertView.findViewById(R.id.id_txt);
                
                convertView.setTag(textview);
            }else{
            	textview = (TextView) convertView.getTag();
            }

            textview.setText(m_data.get(position));
             
            return convertView;
		}
    }
	
	public String getText(){
		return m_EditText.getText().toString();
	}
	 
	public interface ListViewItemClickListener{
		void onItemClick(int position);
	}
}
