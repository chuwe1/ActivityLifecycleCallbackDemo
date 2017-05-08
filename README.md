# ActivityLifecycleCallbackDemo
Demo for ActivityLifecycleCallbacks

------------------------------------
相信大家都碰到过这个问题:
在继承第三方SDK的某些功能的时候需要继承该SDK提供的XActivity,
这就与我们本身的BaseActivity（通常用来管理Activity回退栈）向冲突了，怎么办？
有人说，这个简单，把我们本身的BaseActivity继承XActivity就好了嘛。
没错这样确实是可以解决问题，然而万一这个XActivity的父类是Activity/FragmentActivity，
而你又想使用Support Repository(Material Design)呢(使用Support Repository需要继承
AppCompatActivity)？这又怎么办？好吧！如果所言，现在的主流第三方SDK都是继承AppCompatActivity
了。

但是你有想过java的继承机制是创建子类实例前会先创建一个父类实例的！！！
这么跟你说吧，可能你的一个Activity完成的就是一个简单的登陆功能，而这个XActivity却有一两千行。也就是说
你的一个登陆功能的类加载了多余的一两千行代码！这可是对内存的极大浪费啊。

So, you get it now?
那么问题来了，大家以前都是这么干的啊（其实我也是这么干的），
直到今天我看到一个文章[我一行代码都不写实现 Toolbar! 你却还在封装 BaseActivity?](http://www.jianshu.com/p/75a5c24174b2)
在这里我看到了Application的一个方法[registerActivityLifecycleCallbacks](https://developer.android.google.cn/reference/android/app/Application.html#registerActivityLifecycleCallbacks(android.app.Application.ActivityLifecycleCallbacks))对他研究一番，发现**此法可行**

那么我们就先来看看Application对于该方法的实现
```
public class Application extends ContextWrapper implements ComponentCallbacks2 {

    private ArrayList<ActivityLifecycleCallbacks> mActivityLifecycleCallbacks =
            new ArrayList<ActivityLifecycleCallbacks>();

    public interface ActivityLifecycleCallbacks {
        void onActivityCreated(Activity activity, Bundle savedInstanceState);
        void onActivityStarted(Activity activity);
        void onActivityResumed(Activity activity);
        void onActivityPaused(Activity activity);
        void onActivityStopped(Activity activity);
        void onActivitySaveInstanceState(Activity activity, Bundle outState);
        void onActivityDestroyed(Activity activity);
    }

    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        synchronized (mActivityLifecycleCallbacks) {
            mActivityLifecycleCallbacks.add(callback);
        }
    }

    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        synchronized (mActivityLifecycleCallbacks) {
            mActivityLifecycleCallbacks.remove(callback);
        }
    }
}
```
首先是定义了一个接口，然后有一个成员变量的list用来保存开发者们注册的该接口实例，注销就是从list里面remove掉该实例。
没错就是这么简单，那么该接口又是如何作用到Activity的生命周期上的呢？我们再来看一下Activity的生命周期方法（已onCreate、onDestory）为例，
其他的都是一样的。
```
protected void onCreate(@Nullable Bundle savedInstanceState) {
    // ...
    getApplication().dispatchActivityCreated(this, savedInstanceState);
    // ...
}
    
protected void onDestroy() {
    // ...
    getApplication().dispatchActivityDestroyed(this);
}
```
可以看到对应的生命周期方法里面调用了Application与之对应的dispatchActivityXXX方法，那么我们再去看看Application里面这些方法都租了什么。
```
private Object[] collectActivityLifecycleCallbacks() {
    Object[] callbacks = null;
    synchronized (mActivityLifecycleCallbacks) {
        if (mActivityLifecycleCallbacks.size() > 0) {
            callbacks = mActivityLifecycleCallbacks.toArray();
        }
    }
    return callbacks;
}

/* package */ void dispatchActivityCreated(Activity activity, Bundle savedInstanceState) {
    Object[] callbacks = collectActivityLifecycleCallbacks();
    if (callbacks != null) {
        for (int i=0; i<callbacks.length; i++) {
            ((ActivityLifecycleCallbacks)callbacks[i]).onActivityCreated(activity,
                    savedInstanceState);
        }
    }
}

/* package */ void dispatchActivityDestroyed(Activity activity) {
    Object[] callbacks = collectActivityLifecycleCallbacks();
    if (callbacks != null) {
        for (int i=0; i<callbacks.length; i++) {
            ((ActivityLifecycleCallbacks)callbacks[i]).onActivityDestroyed(activity);
        }
    }
}
```
这样一看就很明显了就是拿到刚才开发者们注册的接口实例，对其遍历依次调用与之对应的抽象方法，并且会把该activity作为参数传过去。

这样一来我们通过此方法来管理activity的回退栈，就不需要担心什么是我们的BaseActivity还是第三方SDK的XActivity了！
