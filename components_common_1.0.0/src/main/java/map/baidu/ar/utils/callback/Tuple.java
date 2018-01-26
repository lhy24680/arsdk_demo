package map.baidu.ar.utils.callback;

/**
 * Tuple对象
 * Created by 享 on 2016/11/23.
 */
public class Tuple<T, V> {
    private final T mItem1;
    private final V mItem2;

    public Tuple(T item1, V item2) {
        mItem1 = item1;
        mItem2 = item2;
    }

    public T getItem1() {
        return mItem1;
    }

    public V getItem2() {
        return mItem2;
    }
}
