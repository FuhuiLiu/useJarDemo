package com.example.usejardemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.dynamic.IDynamic;

import dalvik.system.DexClassLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener
{
  private static String TAG = "MainActivity";
  private Button mbtnToast;
  private Button mbtnPhoneInfo;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mbtnToast = (Button)findViewById(R.id.btnToast);
    mbtnPhoneInfo = (Button)findViewById(R.id.btnPhoneInfo);
    mbtnToast.setOnClickListener(this);
    mbtnPhoneInfo.setOnClickListener(this);
    releaseLib();
    
  }
  //保存文件
  private void saveLibs(InputStream is, String fullPath) {
    int size = 1024;
    try {
        byte[] byteAry = new byte[size];
        FileOutputStream fos = new FileOutputStream(fullPath);
        while(true) {
            int readSize = is.read(byteAry);
            if(readSize == -1) {
                break;
            }

            fos.write(byteAry, 0, readSize);
        }

        is.close();
        fos.flush();
        fos.close();
        return;
    }
    catch(Exception v0_1) {
        v0_1.printStackTrace();
        return;
    }
}
  //释放jar文件到我们的app目录
  public void releaseLib()
  {
    int v0 = 0;
    AssetManager amg = getAssets();
    
    try
    {
      //获取对应目录下的文件名列表
      String[] strAry = amg.list("firstpay/libs");
      int v3 = strAry.length;
      if (v3 <= 0)
      {
        Log.i(TAG, "path no found!");
        return;
      }
      //将assets/firstpay/libs目录下的所有文件都保存到app目录下
      while (v0 < v3)
      {
        String str = strAry[v0];
        //获取app自身存储目录
        String fullPath = String.valueOf(getDir("firstpaylibs", 0)
            .getAbsolutePath()) + File.separator + str;
        //如果存在就先删除该文件
        if (new File(fullPath).exists())
        {
          new File(fullPath).delete();
        }
        //保存文件到目录
        saveLibs(amg.open("firstpay/libs/" + str), fullPath);
        v0++;
        Log.i(TAG, fullPath);
      }
    }
    catch (IOException v0_1)
    {
      Log.i(TAG, v0_1.toString());
      v0_1.printStackTrace();
    }

    return;
  }

  @Override
  public void onClick(View v)
  {
    switch(v.getId())
    {
    case R.id.btnToast:
      onBtnToast();
      break;
    case R.id.btnPhoneInfo:
      onPhoneInfo();
      break;
    }
  }
  
  @SuppressLint("NewApi")
  private void onPhoneInfo()
  {
    //获取要动态加载的类的path
    String fullPath = String.valueOf(getDir("firstpaylibs", 0)
        .getAbsolutePath()) + File.separator + "phoneInfo.jar";
    //调用DexClassLoader加载我们的dex文件
    DexClassLoader cl = new DexClassLoader(fullPath, 
        Environment.getExternalStorageDirectory().toString(),
        null, getClassLoader());
    Class libProviderClass = null;
    try
    {
      //动态加载我们的类并调用
      libProviderClass = cl.loadClass("com.example.dakongyi.librarydem.PhoneInfo");
      Constructor constructor = libProviderClass.getConstructor(new Class[] {});
      Object phoneInfoObj = constructor.newInstance(new Object[] {});
      
      Method getMoney = libProviderClass.getMethod("getDeviceId", null);
      getMoney.setAccessible(true);
      Object money = getMoney.invoke(phoneInfoObj, null);
      Toast.makeText(this, money.toString(), Toast.LENGTH_LONG).show();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
  
  @SuppressLint("NewApi")
  private void onBtnToast()
  {
    //获取要动态加载的类的path
    String fullPath = String.valueOf(getDir("firstpaylibs", 0)
        .getAbsolutePath()) + File.separator + "firstpay.jar";
    //调用DexClassLoader加载我们的dex文件
    DexClassLoader cl = new DexClassLoader(fullPath, 
        Environment.getExternalStorageDirectory().toString(),
        null, getClassLoader());
    
    Class libProviderClass = null;
    
    try
    {
      //动态加载我们的类并调用
      libProviderClass = cl.loadClass("com.dynamic.DynamicTest");
      IDynamic lib = (IDynamic)libProviderClass.newInstance();
      Toast.makeText(MainActivity.this, lib.getHelloWorld(), Toast.LENGTH_SHORT).show();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
