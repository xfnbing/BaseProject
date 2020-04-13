# BaseProject
基础项目，搭建好整体的框架，新项目拿来就可以用


# 使用Step.1
入口初始化 
* ManagerConfig.getInstance().init({application})
                .setBaseHost({service_base_url})
                .initHttpClient()
                .setLogConfig({log_dir}, {is_debug}, {is_write_log});
                
# 使用Step.2
* activity，继承BaseActivity
* 首页fragment，继承BaseMainFragment
* 其他fragment，继承BaseBackFragment
* 自定义Presenter，继承PubPresenter




# 主要使用的框架
* OkGo：网络请求框架
* barlibrary：沉浸式
* glide：图片加载
* fastjson：json数据序列化
* eventbus：跨主件通讯
* fragmentation：fragment第三方框架，支持单个activity+多fragment搭建app
* butterknife：注入式页面框架
* BaseRecyclerViewAdapterHelper：RecyclerView多功能适配器
