package waterhole.commonlibs;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * 单元测试基类
 * <pre>单元测试主要测试输入和输出是否满足设计目的，测试各种边界情况。
 * 单元测试不测试性能，质量等方面的东西，那属于另一种测试。</pre>
 *
 * @author kzw on 2017/06/26.
 */
@RunWith(AndroidJUnit4.class)
public class BaseTest extends InstrumentationTestCase {

    public Context mContext;
    public Application mApplication;

    @Before
    public void setUp() throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        mContext = InstrumentationRegistry.getTargetContext();
        mApplication = InstrumentationRegistry.getInstrumentation().newApplication(Application.class, mContext);
    }
}
