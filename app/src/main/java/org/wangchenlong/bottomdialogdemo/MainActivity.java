package org.wangchenlong.bottomdialogdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 显示底部Dialog
     *
     * @param view 视图
     */
    public void showBottomDialog(View view) {
        FragmentManager fm = getSupportFragmentManager();
        BottomDialogFragment editNameDialog = new BottomDialogFragment();
        editNameDialog.show(fm, "fragment_edit_name");
    }
}
