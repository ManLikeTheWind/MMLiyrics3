package com.dxiang.mring3.utils;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;

import com.dxiang.mring3.R;
import com.dxiang.mring3.request.GetToneListenAddrReq;
import com.dxiang.mring3.response.GetToneListenAddrRsp;

public class LocalMediaPlayer implements OnCompletionListener, OnErrorListener
{
	private GetToneListenAddrReq addr;
	
	private onException onExe;
	
	private Handler task = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.arg1) {
			case FusionCode.REQUEST_GETTONELISTENADDREVT:
				if(Utils.CheckTextNull(addr.id) && addr.id.equals(toneId) && needListener)
				{
					try 
					{
						GetToneListenAddrRsp rsp = (GetToneListenAddrRsp) msg.obj;
						if(0 == rsp.error_code)
						{
							if(!Utils.CheckTextNull(rsp.getToneAddr()))
							{
								if(onExe != null)
						        {
									onExe.onException(rsp.error_code,  R.string.empty_play_url_1+"");
						        }
								return;
							}
							if(!lastId.equals(toneId))
							{
								lastId = toneId;
								return;
							}
							setDataSource(rsp.getToneAddr());
							prepare();
						}
						else
						{
							if(onExe != null)
					        {
								onExe.onException(rsp.error_code,  rsp.result);
					        }
						}
					}
					catch (Exception e) 
					{
						if(onExe != null)
				        {
							onExe.onException(100056,  R.string.returncode_100056+"");
				        }
					}
				}
				break;

			default:
				break;
			}
		};
	};
	
	public boolean needListener = true;
	
	public void getUrlFromServer(String id, String tonetype)
	{
		needListener = true;
		toneId = id;
		if(addr == null)
		{
			addr = new GetToneListenAddrReq(task);
		}
		if(!Utils.CheckTextNull(lastId)) 
		{
			lastId = id;
		}
		else
		{
			if(Utils.CheckTextNull(id) && !id.equals(lastId))
			{
				lastId = toneId;
			}
		}
		state = PREPARED;
		addr.sendGetToneListenAddrReq(id, tonetype);
	}
	
	private String toneId = "";
	
	private String lastId = "";
	
    private static LocalMediaPlayer operator;

    private MediaPlayer player;

    private Complete complete;
    
    private static int state = 0;
    
    public static final int PREPARED = 2;
    
    public static final int START = 1;
    
    public static final int PAUSE = 5;
    
    public static final int STOP = -1;
    
    public static final int COMPLETED = 3;
    
    public static final int LOOP = 4;

    private String path;

    //回调播放器准备数据就绪
    private onPrepared mPrepared;

    public interface Complete
    {
        public void onComplete();
    }
    
    private ErrorListener errorListener;
    
    public interface ErrorListener
    {
        public void onError(MediaPlayer arg0, int arg1, int arg2);
    }

    public void setCallback(Complete com)
    {
        complete = com;
    }

    public void setErrorListener(ErrorListener error)
    {
        errorListener = error;
    }
    
    public void setException(onException exe)
    {
        onExe = exe;
    }
    
    public static LocalMediaPlayer getInstance()
    {
    	if(operator != null && state == PAUSE)
    	{
    		return operator;
    	}
		operator = new LocalMediaPlayer();
        return operator;
    }

    private LocalMediaPlayer()
    {
        player = createMediaPlayer();
    }

    public void reset()
    {
        player.reset();
    }

    public int duration()
    {
        return player.getDuration();
    }

    public void setDataSource(String p)
    {
        this.path = p;
    }
    
    public boolean isPause()
    {
    	if(state == PAUSE)
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    public void startPlayer()
    {
    	if(mPrepared != null)
    	{
    		mPrepared.onPrepared();
    	}
    	
    	if(player != null)
    	{
    		player.start();
    	}
    	state = START;
    }

    public interface onTimeSeek
    {
        public void getRightPos(int data);
    }

    public boolean isPlaying()
    {
    	if(player == null)
    	{
    		return false;
    	}
        return player.isPlaying();
    }
    
    public int getState()
    {
        return state;
    }
    
    public boolean isLoop()
    {
    	if(player == null)
    	{
    		return false;
    	}
        return player.isLooping();
    }
    
    public boolean isprepared()
    {
        if(state == PREPARED)
        {
            return true;
        }
        
        return false;
    }
    
    public boolean isLooped()
    {
        if(state == LOOP)
        {
            return true;
        }
        
        return false;
    }
    
    public interface onPrepared
    {
        public void onPrepared();
    }
    
    public interface onException
    {
    	public void onException(int code, String des);
    };
    
    public void setOnPrepared(onPrepared prepared)
    {
        mPrepared = prepared;
    }
    
    private OnPreparedListener preparedListener = new OnPreparedListener()
    {
        
        @Override
        public void onPrepared(MediaPlayer arg0)
        {
            if(mPrepared != null)
            {
                mPrepared.onPrepared();
            }
            state = START;
            arg0.start();
        }
    };

    private Runnable run = new Runnable()
    {
        @Override
        public void run()
        {
            try
            {
                try
                {
                    player.reset();
                    player.setDataSource(path);
                }
                catch (IllegalArgumentException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (SecurityException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IllegalStateException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                player.prepareAsync();
            }
            catch (IllegalStateException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    
    public void pause()
    {
    	needListener = false;
    	if(player == null)
    	{
    		return;
    	}
         player.pause();
         task.removeCallbacks(run);
         state = PAUSE; 
    }

    public void cancelPlayer()
    {
    	if(player == null)
    	{
    		return;
    	}
    	if(complete != null)
    	{
    		complete.onComplete();
    	}
        if (player.isPlaying() || state == PAUSE || state == PREPARED || state == START || state == LOOP)
        {
            player.stop();
            if(state == LOOP || state == START)
            {
            	player.release();
            }
            player = null;
            task.removeCallbacks(run);
            operator = null;
        }
        state = STOP;
        needListener = false;
    }
    
    public void stopMusic()
    {
    	 if(player == null)
         {
             return;
         }
    	 if(complete != null)
     	{
     		complete.onComplete();
     	}
    	 
    	 player.stop();
    }

    public void prepare()
    {
        if(player == null)
        {
            player = createMediaPlayer();
        }
        if(player.isLooping())
        {
            return;
        }
        state = LOOP;
        task.post(run);
    }

    private MediaPlayer createMediaPlayer() 
    {
    	player = new MediaPlayer();
        player.reset();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(preparedListener);
        player.setOnErrorListener(this);
        return player;
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        Log.d("SD", "onCOmpleted in media");
        if(complete != null)
        {
            complete.onComplete();
        }
        state = COMPLETED;
    }

    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2)
    {
        if(errorListener != null)
        {
            errorListener.onError(arg0, arg1, arg2);
        }
        return false;
    }
    
    public void uninsMedia()
    {
    	if(operator != null)
    	{
    		operator = null;
    		if(player != null)
    		{
    			player.release();
    			player = null;
    			state = STOP;
    		}
    	}
    }
}
