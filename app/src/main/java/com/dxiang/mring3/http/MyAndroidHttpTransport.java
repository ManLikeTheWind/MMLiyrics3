package com.dxiang.mring3.http;


import java.io.IOException;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import android.os.Handler;

import com.dxiang.mring3.utils.Commons;


@SuppressWarnings("unused")
public class MyAndroidHttpTransport extends HttpTransportSE
{

    private Handler handle = null;

    public MyAndroidHttpTransport(String url)
    {
        super(url);
    }

    public MyAndroidHttpTransport(Handler handle, String url)
    {
        super(url);
        this.handle = handle;
    }

    /**
     * 关闭链接
     * @throws IOException
     */
    protected void disConnection() throws IOException
    {
        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url);
        serviceConnection.disconnect();
    }

    @Override
    protected ServiceConnection getServiceConnection() throws IOException
    {
        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url);
        serviceConnection.setConnectionTimeOut(Commons.OUTTIME);
        serviceConnection.setConnectTimeOut(Commons.OUTTIME);

        return serviceConnection;
    }
}