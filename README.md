# TODO (Android)
## A TODO App 
一个简单的todo app（任务清单app），基本实现了常用功能（包括统计页面，日历视图未完成）<br>
项目效果截图 ：[App页面截图](AppShot)<br>
使用的统计页面第三方库为[MPAAndroidChart](https://github.com/PhilJay/MPAndroidChart.git)


**备注：** 本人运行的代码虚拟机为Android 8版本（高版本运行不了不知道为什么，解决不了），所以拍照打卡如果相机调用不了，可以修改CameraActivity,也可以修改ListFragment中的override fun finishItem(todoId: Long, prove: Int)的startActivity(Intent(requireContext(), CameraActivity::class.java).putExtras(bundle))启动代码，可以调用系统相机。

**备注：** 项目Android Gradle Plugin Version为8.2.2 |  Gradle Version为8.2
