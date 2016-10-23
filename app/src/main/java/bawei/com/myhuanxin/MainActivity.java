package bawei.com.myhuanxin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener{

    Button btn_add,btn_back;
    ListView listView;
    MyAdapter adapter;
    List<String> list;
    EditText et_add_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取联系人列表
        getcontacts();
        //初始化控件
        initview();

    }

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 1:
                    list= (List<String>) msg.obj;
                    adapter.addresst(list);
                    Log.d("usernames", list.size() + "");
                    break;
                case 2:

                    break;
                case 3:
                    String name = (String) msg.obj;
                    friend(name);
                    break;
            }

        }
    };

    private void friend(final String name) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否添加好友?"); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog

                argeFriend argeFriend = new argeFriend(name);
                argeFriend.start();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                refuseFriend argeFriend = new refuseFriend(name);
                argeFriend.start();
            }
        });


        //参数都设置完成了，创建并显示出来
        builder.show();
    }

    //同意好友请求
    class argeFriend extends Thread{

        String username;

        argeFriend(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            super.run();

            try {
                EMClient.getInstance().contactManager().acceptInvitation(username);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    //jujue好友请求
    class refuseFriend extends Thread{

        String username;

        refuseFriend(String username) {
            this.username = username;
        }

        @Override
        public void run() {
            super.run();

            try {
                EMClient.getInstance().contactManager().declineInvitation(username);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }


    private void getcontacts() {

        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    list=EMClient.getInstance().contactManager().getAllContactsFromServer();

                    //将获取到的联系人数据发送给handler
                    Message message=Message.obtain();
                    message.what=1;
                    message.obj=list;
                    handler.sendMessage(message);

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }



    private void initview() {

        listView= (ListView) findViewById(R.id.list_view);
        //设置适配器
        adapter=new MyAdapter(MainActivity.this);
        listView.setAdapter(adapter);

        //设置监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);

                intent.putExtra(Main2Activity.CHAT_USERNAME, list.get(position));
                startActivity(intent);
                finish();
            }
        });
        btn_add= (Button) findViewById(R.id.btn_add);
        btn_back= (Button) findViewById(R.id.btn_back);
        et_add_name= (EditText) findViewById(R.id.et_addname);
        btn_add.setOnClickListener(this);
        btn_back.setOnClickListener(this);


        //监听好友状态
        friendstatus();
    }

    private void friendstatus() {

        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
                //addfriend();
                Log.d("username", "好友请求被同意" + username);
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
                Log.d("username", "好友请求被拒绝" + username);
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                Log.d("username", "收到好友邀请" + username);


                Message msg = handler.obtainMessage();
                msg.what = 3;
                msg.obj = username;
                handler.sendMessage(msg);

            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
                Log.d("username", "被删除时回调此方法" + username);
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
                Log.d("username", "增加了联系人时回调此方法" + username);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add :
                addfriend();
                break;
            case R.id.btn_back:

                break;
        }
    }

    private void addfriend() {

        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    String addname=et_add_name.getText().toString();
                    EMClient.getInstance().contactManager().addContact(addname, "你好");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
