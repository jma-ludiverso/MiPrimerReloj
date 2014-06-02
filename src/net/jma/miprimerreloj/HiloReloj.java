package net.jma.miprimerreloj;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class HiloReloj extends Thread {

	private SurfaceHolder sh;
	private analogico sv;
	private boolean run;
	
	public HiloReloj(SurfaceHolder sh, analogico sv) {
		this.sh = sh;
		this.sv = sv;
		this.run = false;
	}

	public void setRunning(boolean run){
		this.run = run;
	}
	
	@SuppressLint("WrongCall")
	public void run() {		
		while(run){
			if(!sh.getSurface().isValid())
				continue;

			Canvas canvas = null;
			try{
				canvas = sh.lockCanvas(null);
				synchronized(sh){
					sv.onDraw(canvas);
				}
			}finally{
				if(canvas != null){
					sh.unlockCanvasAndPost(canvas);
				}
			}			
		}
	}

}
