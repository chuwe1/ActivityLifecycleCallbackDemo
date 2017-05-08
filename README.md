# ActivityLifecycleCallbackDemo
Demo for ActivityLifecycleCallbacks

------------------------------------
先来问大家一个问题，以往对于Activity回退栈都是如何管理的？是否都是这样？
```
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityManager.getInstance().addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        ActivityManager.getInstance().finishActivity(this);
    }
}
```
再来问大家第二个问题:
在继承第三方SDK的某些功能的时候需要继承该SDK提供的XActivity，Java的单继承又导致XActivity的子类没法继承我们的BaseActivity，
这样一来就不方便管理我们的回退栈了，难道要手动在XActivity的子类里面加上如上的对应代码？这样又会显得代码十分的不优雅，对于
我这种有强迫症的来说是绝对不会允许这样的事情发生的。那么问题来了，怎么办？有人说，这个简单，把我们本身的BaseActivity继承
XActivity就好了嘛。没错这样确实是可以解决问题，然而万一这个XActivity的父类是Activity/FragmentActivity，
而你又想使用Support Repository(Material Design)呢(使用Support Repository需要继承
AppCompatActivity)？这又怎么办？好吧！如你所言，现在的主流第三方SDK都是继承AppCompatActivity了。

但是你有想过Java的继承机制是创建子类实例前会先创建一个父类实例的！！！
这么跟你说吧，可能你的一个Activity完成的就是一个简单的登陆功能，而这个XActivity却有一两千行。也就是说
你的一个登陆功能的类加载了多余的一两千行代码！这就尴尬了，父类里的这些变量什么的该浪费多少内存啊。

So, you get it now?
那么问题来了，大家以前都是这么干的啊（其实我也是这么干的），怎么才能解决这个问题呢？
直到前几天我看到一篇文章[我一行代码都不写实现 Toolbar! 你却还在封装 BaseActivity?](http://www.jianshu.com/p/75a5c24174b2)
在这里我看到了Application的一个方法[registerActivityLifecycleCallbacks](https://developer.android.google.cn/reference/android/app/Application.html#registerActivityLifecycleCallbacks(android.app.Application.ActivityLifecycleCallbacks))对他研究了一番，发现**此法可行**

究竟是怎么个可行法呢？就来看看我的“研究”过程...

先来看看Application对于该方法的实现
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
可以看到对应的生命周期方法里面调用了Application与之对应的dispatchActivityXXX方法，那么我们再去看看Application里面这些方法都做了什么。
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

这样一来我们通过此方法来管理起activity的回退栈来是不是就不需要管他什么BaseActivity，XActivity了呢！反正**都是Activity**。

于是我们就可以在Application的onCreate中调用该方法了：
```
@Override
public void onCreate() {
    super.onCreate();

    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ActivityManager.getInstance().addActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ActivityManager.getInstance().finishActivity(activity);
        }
    });
}
```


