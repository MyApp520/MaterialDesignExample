package org.smile.mde.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import org.smile.mde.R;

/**
 * https://blog.csdn.net/lmj623565791/article/details/78011599
 */
public class ConstraintLayoutActivity extends Activity {

    /**
        1、app:layout_constraintDimensionRatio="W,1:2"
            结论，分几种情况：

                1）能确定控件宽度：
                                app:layout_constraintDimensionRatio="W,1:2" 表示高宽比
                                app:layout_constraintDimensionRatio="H,2:1" 表示宽高比

                                 <TextView //确定控件宽度为填充父布局，（高度未知，按比例来）
                                     android:id="@+id/banner"
                                     android:layout_width="0dp"
                                     android:layout_height="0dp"
                                     android:background="#765"
                                     android:gravity="center"
                                     android:text="Banner"
                                     app:layout_constraintDimensionRatio="W,1:2"
                                     app:layout_constraintLeft_toLeftOf="parent"
                                     app:layout_constraintRight_toRightOf="parent" />

                2）能确定控件高度：
                                app:layout_constraintDimensionRatio="W,1:2" 表示宽高比
                                app:layout_constraintDimensionRatio="H,2:1" 表示高宽比

                                 <TextView //确定控件高度为填充父布局，（宽度未知，按比例来）
                                     android:id="@+id/banner"
                                     android:layout_width="0dp"
                                     android:layout_height="0dp"
                                     android:background="#765"
                                     android:gravity="center"
                                     android:text="Banner"
                                     app:layout_constraintDimensionRatio="W,1:2"
                                     app:layout_constraintBottom_toBottomOf="parent"
                                     app:layout_constraintTop_toTopOf="parent" />

                3）控件宽高一个都不能确定的情况下, layout_constraintDimensionRatio表示的都是宽高比，只不过基准不一样：

                                             <TextView
                                                 android:id="@+id/banner"
                                                 android:layout_width="0dp"
                                                 android:layout_height="0dp"
                                                 android:background="#765"
                                                 android:gravity="center"
                                                 android:text="Banner"
                                                 app:layout_constraintBottom_toBottomOf="parent"
                                                 1、"W,1:2" 表示：高度填充父布局，宽高比为1：2；
                                                 2、"H,2:1" 表示：宽度填充父布局，宽高比为1：2；
                                                 app:layout_constraintDimensionRatio="W,1:2"
                                                 app:layout_constraintLeft_toLeftOf="parent"
                                                 app:layout_constraintRight_toRightOf="parent"
                                                 app:layout_constraintTop_toTopOf="parent" />


        2、倾向（Bias）
            搭配bias，能使约束偏向某一边，默认是0.5，有以下属性：
           layout_constraintHorizontal_bias (0最左边 1最右边)
           layout_constraintVertical_bias (0最上边 1 最底边)

           比如：加入app:layout_constraintHorizontal_bias="0.9" ,则会在水平方向上向右偏移至90%；



     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constraint_layout);
    }
}
