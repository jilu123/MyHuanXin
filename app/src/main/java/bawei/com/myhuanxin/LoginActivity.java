package bawei.com.myhuanxin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends Activity implements View.OnClickListener{


    Button btn_login,btn_register;
    EditText edt_password;
    EditText edt_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register= (Button) findViewById(R.id.btn_register);
        edt_username = (EditText)findViewById(R.id.edt_username);
        edt_password = (EditText)findViewById(R.id.edt_password);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                String name=edt_username.getText().toString();
                String pass=edt_password.getText().toString();

                //判断密码 用户名是否为空
                if (null==name || name.equals("")){
                    Toast.makeText(getApplicationContext(),"用户名不能为空",0).show();
                    return;
                }
                if (null==pass || pass.equals("")){
                    Toast.makeText(getApplicationContext(),"密码不能为空",0).show();
                    return;
                }

                login(name,pass);
                break;

            case R.id.btn_register:
                String name2=edt_username.getText().toString();
                String pass2=edt_password.getText().toString();
                register(name2,pass2);
                break;
        }
    }

    private void register(String name,String pass) {

        //注册失败会抛出HyphenateException
        try {
            EMClient.getInstance().createAccount(name, pass);//同步方法
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }

    private void login(String name, String pass) {

        EMClient.getInstance().login(name,pass,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");

                //登录成功获取联系人
                getcontacts();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });

    }

    private void getcontacts() {

        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
