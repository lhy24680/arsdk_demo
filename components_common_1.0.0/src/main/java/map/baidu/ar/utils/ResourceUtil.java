package map.baidu.ar.utils;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * 动态加载资源使用
 */
public class ResourceUtil {

	public static int getLayoutId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "layout", paramContext.getPackageName());
	}

	public static int getStringId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "string", paramContext.getPackageName());
	}

	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "drawable", paramContext.getPackageName());
	}

	public static int getStyleId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "style", paramContext.getPackageName());
	}

	public static int getId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "id", paramContext.getPackageName());
	}

	public static int getColorId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "color", paramContext.getPackageName());
	}

	public static int getAnimId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "anim", paramContext.getPackageName());
	}

	/**
	 * 获取Attrs资源Id
	 *
	 * @param paramContext
	 * @param paramString
	 * @author Hanyonglu@duoku.com
	 * @return
	 */
	public static int getAttrId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "attr", paramContext.getPackageName());
	}

	/**
	 * 获取styleable资源Id
	 *
	 * @param paramContext
	 * @param paramString
	 * @author Hanyonglu@duoku.com
	 * @return
	 */
	public static int getStyleableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString, "styleable", paramContext.getPackageName());
	}

	/**
	 * 通过反射来读取int[]类型资源Id
	 *
	 * @param context
	 * @param name
	 * @author Hanyonglu@duoku.com
	 * @return
	 */
	public static final int[] getResourceDeclareStyleableIntArray(Context context, String name) {
		try {
			Field[] fields2 = Class.forName(context.getPackageName() + ".R$styleable").getFields();
			for (Field f : fields2) {
				if (f.getName().equals(name)) {
					int[] ret = (int[]) f.get(null);
					return ret;
				}
			}
		} catch (Throwable t) {

		}

		return null;
	}


	public static int getLayoutId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "layout", defPackageName);
	}

	public static int getStringId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "string", defPackageName);
	}

	public static int getDrawableId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "drawable", defPackageName);
	}

	public static int getStyleId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "style", defPackageName);
	}

	public static int getDimenId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "dimen", defPackageName);
	}

	public static int getId(Context paramContext, String paramString, String defPackageName) {
		return ResourcesApi.getComRes().getIdentifier(paramString, "id", defPackageName);
	}

	public static int getColorId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "color", defPackageName);
	}

	public static int getAnimId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "anim", defPackageName);
	}

	/**
	 * 获取Attrs资源Id
	 * @param paramContext
	 * @param paramString
	 * @author Hanyonglu@duoku.com
	 * @return
	 */
	public static int getAttrId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "attr", defPackageName);
	}

	/**
	 * 获取styleable资源Id
	 * @param paramContext
	 * @param paramString
	 * @author Hanyonglu@duoku.com
	 * @return
	 */
	public static int getStyleableId(Context paramContext, String paramString, String defPackageName) {
		return paramContext.getResources().getIdentifier(paramString, "styleable", defPackageName);
	}

	/**
	 * 通过反射来读取int[]类型资源Id
	 * @param context
	 * @param name
	 * @author Hanyonglu@duoku.com
	 * @return
	 */
	public static final int[] getResourceDeclareStyleableIntArray(Context context, String name, String defPackageName) {
		try {
			Field[] fields2 = Class.forName(context.getPackageName() + ".R$styleable" ).getFields();
			for (Field f : fields2 ){
				if (f.getName().equals(name)){
					int[] ret = (int[])f.get(null);
					return ret;
				}
			}
		}
		catch (Throwable t){

		}

		return null;
	}
}