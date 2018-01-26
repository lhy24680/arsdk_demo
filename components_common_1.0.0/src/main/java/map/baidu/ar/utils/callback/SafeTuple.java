package map.baidu.ar.utils.callback;

import org.jetbrains.annotations.NotNull;

/**
 * Created by 享 on 2016/12/30.
 */

public class SafeTuple<T, V> {
    @NotNull
    private final T mItem1;
    @NotNull
    private final V mItem2;

    public SafeTuple(@NotNull T item1, @NotNull V item2) {
        mItem1 = item1;
        mItem2 = item2;
    }

    @NotNull
    public T getItem1() {
        return mItem1;
    }

    @NotNull
    public V getItem2() {
        return mItem2;
    }
}
