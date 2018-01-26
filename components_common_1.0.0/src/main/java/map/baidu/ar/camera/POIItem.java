package map.baidu.ar.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public  class POIItem extends RelativeLayout {

    private int[] vertex = new int[4];

    public POIItem(Context context) {
        super(context);
    }

    public POIItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public POIItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setVertex(int[] vertex) {
        this.vertex = vertex;
    }

    public int[] getVertex() {
        return vertex;
    }



}
