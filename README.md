# 使用 DialogFragment 实现底部弹窗布局

> 欢迎Follow我的GitHub: https://github.com/SpikeKing

Android对于底部弹窗已经在23.2新的实现方式, 即[BottomSheet](https://material.google.com/components/bottom-sheets.html). 然而对于低版本, 我们仍需使用**DialogFragment**. 底部弹窗与普通Dialog不同, 需要紧贴应用下部, 但本质仍是一个Fragment, 通过继承**DialogFragment**类, 定制不同样式的**Fragment**.

本文源码的GitHub[下载地址](https://github.com/SpikeKing/BottomDialogDemo)

## 定制DialogFragment

底部弹窗, 需要紧贴应用下部. 设置Dialog样式(Style), 全屏/布局/外部取消. 设置Dialog位置(LayoutParams), 底部/宽度最大.

``` java
@NonNull @Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
    // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
    Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
    dialog.setContentView(R.layout.fragment_bottom);
    dialog.setCanceledOnTouchOutside(true); // 外部点击取消

    // 设置宽度为屏宽, 靠近屏幕底部。
    Window window = dialog.getWindow();
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.gravity = Gravity.BOTTOM; // 紧贴底部
    lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
    window.setAttributes(lp);

    ButterKnife.bind(this, dialog); // Dialog即View

    initClickTypes();

    return dialog;
}
```

> ButterKnife绑定View, 但是Layout布局在Dialog初始化中设置, 所以选择绑定Dialog, 因为都继承自View.

Dialog的样式. 宽度最大, 高度匹配, 是否浮现于Activity之上, 关闭背景暗色.

``` xml
<style name="BottomDialog" parent="@style/AppTheme">
    <item name="android:layout_width">match_parent</item>
    <item name="android:layout_height">wrap_content</item>
    <item name="android:windowIsFloating">true</item>
    <item name="android:backgroundDimEnabled">false</item>
</style>
```

---

# 控制逻辑

初始化控件组, 把图片\文字\框架组成三个控件组; 初始化框架点击事件, 点击不同的框架, 替换文字颜色与选中图片. 点击发送按钮``mTvSend``, 根据当前选中状态, 动态更新金币.

``` java
private void initClickTypes() {
    initViewArray(); // 初始化控件组
    initLayout(); // 初始化框架点击事件
    
    mTvSend.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
            String typeStr = "金币不足";
            switch (mType) {
                case 0:
                    if (countCoins(100))
                        typeStr = "一字之师";
                    break;
                case 1:
                    if (countCoins(200))
                        typeStr = "妙语连珠";
                    break;
                case 2:
                    if (countCoins(800))
                        typeStr = "学识丰富";
                    break;
                case 3:
                    if (countCoins(1200))
                        typeStr = "博学多才";
                    break;
                default:
                    break;
            }
            Toast.makeText(getContext(), typeStr, Toast.LENGTH_SHORT).show();
        }
    });
}
```

初始化控件组, 框架, 文字与图片.

``` java
private void initViewArray() {
    mLayouts = new ArrayList<>();
    mTvTypes = new ArrayList<>();
    mIvTypes = new ArrayList<>();

    mLayouts.add(mLlFirstContainer);
    mLayouts.add(mLlSecondContainer);
    mLayouts.add(mLlThirdContainer);
    mLayouts.add(mLlForthContainer);

    mTvTypes.add(mTv100Coins);
    mTvTypes.add(mTv2Yuan);
    mTvTypes.add(mTv8Yuan);
    mTvTypes.add(mTv12Yuan);

    mIvTypes.add(mIv100Coins);
    mIvTypes.add(mIv2Yuan);
    mIvTypes.add(mIv8Yuan);
    mIvTypes.add(mIv12Yuan);
}
```

> Tv代表TextView的缩写, Iv代表ImageView的缩写.

初始化布局的点击事件. 点击不同布局, 选择不同类型. **mType**保存当前选中的类型.

``` java
private void initLayout() {
    chooseRegardsType(mType); // 选择默认类型

    for (int i = 0; i < mLayouts.size(); i++) {
        final int tmp = i;
        LinearLayout ll = mLayouts.get(i);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mType = tmp;
                chooseRegardsType(mType);
            }
        });
    }
}
```

选择类型, 根据选择类型, 设置状态, 通过状态控制

``` java
private void chooseRegardsType(int type) {
    int size = mTvTypes.size();
    for (int i = 0; i < size; ++i) {
        if (i != type) {
            mTvTypes.get(i).setEnabled(true);
            mIvTypes.get(i).setEnabled(true);
        } else {
            mTvTypes.get(i).setEnabled(false);
            mIvTypes.get(i).setEnabled(false);
        }
    }
}
```

例如, Text文字的颜色, 随着状态改变.

``` java
android:textColor="@color/regard_text_bkg"
```

默认是灰色, 选中enable是false, 颜色是白色.

``` xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:color="#848484" android:state_enabled="true"/>
    <item android:color="#FFFFFF" android:state_enabled="false"/>
</selector>
```

---

## 启动Dialog

通过FragmentManager, 使用DialogFragment的show方法, 显示Dialog.

``` java
public void showBottomDialog(View view) {
    FragmentManager fm = getSupportFragmentManager();
    BottomDialogFragment editNameDialog = new BottomDialogFragment();
    editNameDialog.show(fm, "fragment_bottom_dialog");
}
```

---

效果

![效果](https://raw.githubusercontent.com/SpikeKing/BottomDialogDemo/master/articles/demo-anim.gif)

在DialogFragment中定制布局样式, 处理逻辑关系, 使用FragmentManager显示Dialog, 实现经典的底部弹窗样式.

OK, that's all! Enjoy it!
