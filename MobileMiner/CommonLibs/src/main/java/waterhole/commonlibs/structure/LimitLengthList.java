package waterhole.commonlibs.structure;

import java.util.ArrayList;

/**
 * 固定长度的List，超过长度add时将移除表头数据
 *
 * @author liuyan on 2016/08/15.
 */
public final class LimitLengthList<T> extends ArrayList<T> {

    private final int maxLen;

    public LimitLengthList(int maxLen) {
        this.maxLen = maxLen;
    }

    @Override
    public boolean add(T object) {
        if (maxLen > 0 && size() >= maxLen) {
            remove(0);
            add(object);
            return true;
        }

        return super.add(object);
    }
}
