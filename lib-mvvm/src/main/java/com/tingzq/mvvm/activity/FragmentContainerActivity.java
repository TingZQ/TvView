package com.tingzq.mvvm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tingzq.mvvm.R;
import com.tingzq.mvvm.constant.AppConstant;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;

/**
 *<p>
 * 碎片容器活动
 * 碎片充当活动,避免创建一个活动就要取注册一次
 * https://juejin.cn/post/6998439234734391332#heading-26
 *</p>
 *
 */
public class FragmentContainerActivity extends RxAppCompatActivity {

    protected WeakReference<Fragment> mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = fragmentManager.getFragment(savedInstanceState, AppConstant.TEMP_CONTAINER_FRAGMENT_INSTANCE_STATE);
        }
        if (fragment == null) {
            fragment = createFragment(getIntent());
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment);
        fragmentTransaction.commitAllowingStateLoss();
        mFragment = new WeakReference(fragment);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前碎片实例,下次创建自身活动时候可以直接拿出上次保存过的使用
        getSupportFragmentManager().putFragment(outState, AppConstant.TEMP_CONTAINER_FRAGMENT_INSTANCE_STATE, mFragment.get());
    }

    /** 创建碎片页面 */
    protected Fragment createFragment(Intent data) {
        if (data == null) throw new RuntimeException("您必须提供页面信息才能显示!");
        try {
            String fragmentName = data.getStringExtra(AppConstant.CONTAINER_FRAGMENT_NAME);
            if (TextUtils.isEmpty(fragmentName)) throw new IllegalArgumentException("找不到碎片页面名称!");
            Class<?> fragmentClass = Class.forName(fragmentName);
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            Bundle args = data.getBundleExtra(AppConstant.BUNDLE);
            // 创建碎片时传递参数
            if (args != null) fragment.setArguments(args);
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException("片段初始化失败!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.get().onActivityResult(requestCode, resultCode, data);
    }

}
