package myapplication.test.com.lockpattern;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.lockpatternview)
   LockPatternView  mLockpatternview;

    private static final int mode_setting_ONE = 1;
    private static final int mode_setting_TWO = 2;
    private static final int mode_enter = 3;

    private int mode = mode_setting_ONE;

    private String mPattern = "";
    private LockPatternUtils mLockPatternUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLockPatternUtils = new LockPatternUtils(this);
        mLockPatternUtils.clearLock();
        long time = System.nanoTime();
        if (mLockPatternUtils.savedPatternExists()) {
            mode = mode_enter;
        }


        mLockpatternview.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

                Log.d("TB", "onPatternStart");
            }

            @Override
            public void onPatternCleared() {
                Log.d("TB", "onPatternCleared");


            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
                Log.d("TB", "onPatternCellAdded");
            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
                Log.d("TB", "onPatternDetected");
                if (pattern == null || pattern.size() == 0) {
                    return;
                }
                Log.d("TB","mode="+mode);
                switch (mode) {
                    case mode_setting_ONE:
                        Toast.makeText(MainActivity.this, "请再次输入", Toast.LENGTH_SHORT).show();
                        mPattern = LockPatternUtils.patternToString(pattern);
                        mLockpatternview.clearPattern();
                        mode = mode_setting_TWO;
                        break;
                    case mode_setting_TWO:

                        String temppattern = LockPatternUtils.patternToString(pattern);
                        if (!mPattern.equals(temppattern)) {

                            mode = mode_setting_ONE;

                            mLockpatternview.setDisplayMode(LockPatternView.DisplayMode.Wrong);

                        } else {
                            long time = System.nanoTime();


                            mLockPatternUtils.saveLockPattern(pattern);
                            mLockpatternview.setDisplayMode(LockPatternView.DisplayMode.Correct);
                            mode = mode_enter;
                            Log.d("TB", "保存时间-->" + (System.nanoTime() - time));
                        }

                        break;
                    case mode_enter:
                        boolean isCorrect = mLockPatternUtils.checkPattern(pattern);
                        Log.d("TB", "isCorrect=" + isCorrect);
                        if (!isCorrect) {

                            mLockpatternview.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                            Toast.makeText(MainActivity.this, "输入不争正确", Toast.LENGTH_SHORT).show();
                        } else {
                            mLockPatternUtils.saveLockPattern(pattern);
                            mLockpatternview.setDisplayMode(LockPatternView.DisplayMode.Correct);

                        }

                        break;
                }
            }
        });
    }
}
