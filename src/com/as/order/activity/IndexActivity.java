package com.as.order.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.as.order.R;

public class IndexActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        
        Button loginBtn = (Button) findViewById(R.id.login_with_phone_num);
        Button regBtn = (Button) findViewById(R.id.reg_with_phone_num);
        
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.login_with_phone_num:
			Intent loginActivityIntent = new Intent(IndexActivity.this, LoginActivity.class);
			startActivity(loginActivityIntent);
			break;
			
			default:
				break;
		}
	}
    
}