package waterhole.commonlibs.structure;

import android.annotation.SuppressLint;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 后进先出阻塞队列
 *
 * @author kzw on 2017/1/23.
 */
@SuppressLint("NewApi")
public final class LIFOLinkedBlockingDeque<T> extends LinkedBlockingDeque<T> {

    private static final long serialVersionUID = -4114786347960826192L;

    public LIFOLinkedBlockingDeque() {
        super();
    }

    @Override
    public boolean offer(final T e) {
        return super.offerFirst(e);
    }

    @Override
    public T remove() {
        return super.removeFirst();
    }
}
