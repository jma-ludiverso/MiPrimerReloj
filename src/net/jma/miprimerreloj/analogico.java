package net.jma.miprimerreloj;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class analogico extends SurfaceView implements SurfaceHolder.Callback {
	
	Paint pExt;
	Paint pInt;
	Paint pManillaH;
	Paint pManillaM;
	Paint pb;
	
	RectF rManillaHoras;
	RectF rManillaMinutos;	
	
	int hora;
	int minutos;
	int minutoPrev = 0;
	boolean bCreado = false;
	boolean pulsadoHoras = false;
	boolean pulsadoMinutos = false;
	
	float dimenMargenRadioReloj;
	float dimenRadioInteriorReloj;
	float dimenRadioInferiorManillas;
	
	public HiloReloj myThread;
	
	public analogico(Context context) {
		super(context);
		this.Inicializa();		
	}

	public analogico(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.Inicializa();
	}

	public analogico(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.Inicializa();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		try{
			int ancho = this.getWidth();
			int alto = this.getHeight();
						
			int altoSobrante = (alto - this.getBackground().getIntrinsicHeight())/2;
			canvas.drawRect(0, 0, ancho, altoSobrante, pb);
			canvas.drawRect(0, alto - altoSobrante - 1, ancho, alto, pb);
			int anchoSobrante = (ancho - this.getBackground().getIntrinsicWidth())/2;
			canvas.drawRect(0, 0, anchoSobrante, alto, pb);
			canvas.drawRect(ancho - anchoSobrante - 1, 0, ancho, alto, pb);
			
			int cx = ancho/2;
			int cy = alto/2;
			float radio = 0;
			if(ancho<=alto){
				radio = (ancho/2)-dimenMargenRadioReloj;
			}else{
				radio = (alto/2)-dimenMargenRadioReloj;
			}
			canvas.drawCircle(cx, cy, radio, pExt);
			for (int i = 0;i<=11;i++){
				this.DibujaManillas(canvas, cx, cy, radio, i, false);
			}
			this.DibujaManillas(canvas, cx, cy, radio, -1, true);
			this.DibujaManillas(canvas, cx, cy, radio, -1, false);
			canvas.drawCircle(cx, cy, dimenRadioInteriorReloj, pInt);
			bCreado = true;					
		}catch (Exception ex){
			Log.e("onDraw", ex.getMessage());
		}
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int posX = (int)event.getX();
		int posY = (int)event.getY();
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(rManillaMinutos.contains(posX,posY)){
				pulsadoMinutos = true;
			}else{
				if(rManillaHoras.contains(posX,posY)){
					pulsadoHoras = true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(pulsadoMinutos){
				CalculaMinuto(posX, posY);
			}else{
				if(pulsadoHoras){
					CalculaHora(posX, posY);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_OUTSIDE:
			if(pulsadoHoras || pulsadoMinutos){
				Reloj rel = (Reloj)this.getContext();
				rel.CambiaHora(hora, minutos);				
			}
			pulsadoHoras = false;
			pulsadoMinutos = false;
			break;
		}
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
//		try{
//			Log.e("surfaceChanged ", "cambio ");
//		}catch (Exception ex){
//			myThread = null;
//		}		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		myThread = new HiloReloj(getHolder(), this);		
		myThread.setRunning(true);
		myThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		try{
			boolean retry = true;
			myThread.setRunning(false);
			while(retry){
				try{
					myThread.join();
					retry = false;
				}catch(InterruptedException ex){				
				}
			}					
		}catch (Exception ex){
			myThread = null;
		}
	}	
	
	private double CalculaAngulo(int cx, int cy, int x, int y){
		double ret = 0;
		int catetoA;
		int catetoO;
		if(x>=cx){
			if(y>=cy){
				//cuadrante 1 (0º a 90º)
				catetoA = x - cx;
				catetoO = cy - y;
				ret = Math.atan2(catetoO, catetoA);
				ret = Math.toDegrees(ret);
				ret = 90 - ret;
			}else{
				//cuadrante 2 (90º a 180º)
				catetoA = x - cx;
				catetoO = y - cy;				
				ret = Math.atan2(catetoO, catetoA);
				ret = Math.toDegrees(ret);
				ret = 90 + ret;
			}
		}else{
			if(y>=cy){
				//cuadrante 3 (180º a 270º)
				catetoA = cx - x;
				catetoO = y - cy;								
				ret = Math.atan2(catetoO, catetoA);
				ret = Math.toDegrees(ret);
				ret = 180 + (90 - ret);
			}else{
				//cuadrante 4 (270º a 360º)
				catetoA = cx - x;
				catetoO = cy - y;												
				ret = Math.atan2(catetoO, catetoA);
				ret = Math.toDegrees(ret);
				ret = 270 + ret;
			}			
		}				
		return ret;
	}
	
	private void CalculaHora(int x, int y){
		int cx = this.getWidth()/2;
		int cy = this.getHeight()/2;
		double angulo = CalculaAngulo(cx, cy, x, y);
		hora = (int)Math.round(angulo/30);
		if(bCreado){
			this.invalidate();
		}
	}
	
	private void CalculaMinuto(int x, int y){
		int cx = this.getWidth()/2;
		int cy = this.getHeight()/2;
		double angulo = CalculaAngulo(cx, cy, x, y);
		minutos = (int)Math.round(angulo/6);
		if(minutos==60){
			minutos = 0;
		}
		//Log.e("cambioHora ", "Minutos: " + minutos + "; Min.Prev: " + minutoPrev + "; Hora: " + hora);		
		if(minutos!=minutoPrev){
			if (minutoPrev==59 && minutos==0){
				hora = hora + 1;
				if(hora>12){
					hora = hora - 12;
				}
			}
			minutoPrev = minutos;
			if(bCreado){
				this.invalidate();
			}		
		}
	}	
		
	private void DibujaManillas(Canvas canvas, float cx, float cy, float radio, int iHora, boolean bManillaHora){
		float stopX;
		float stopY;
		float anguloHora;		
		double anguloX;
		double inicioMY1 = (cy - dimenRadioInferiorManillas);
		double inicioMY2 = (cy + dimenRadioInferiorManillas);
		double inicioMX1 = (cx - dimenRadioInferiorManillas);
		double inicioMX2 = (cx + dimenRadioInferiorManillas);
		String textoHora = "";
		if(iHora!=-1){
			anguloHora = iHora * 30;
			radio = (float) (radio * 0.9);
			if(iHora==0){
				textoHora = "12";
			}else{
				textoHora = String.valueOf(iHora);
			}
		}else{
			if (bManillaHora){
				anguloHora = (hora * 30) + ((minutos * 30)/60);
				radio = (float) (radio * 0.6);
			}else{
				anguloHora = minutos * 6;
				radio = (float) (radio * 0.75);
			}
		}
		if(anguloHora<=90){
			anguloX = Math.toRadians(90 - anguloHora);
			stopX = (float) ((radio * Math.cos(anguloX)) + cx);
			stopY = cy - (float) (radio * Math.sin(anguloX));
		}else{
			if(anguloHora<=180){
				anguloX = Math.toRadians(anguloHora - 90);
				stopX = (float) (radio * Math.cos(anguloX)) + cx;
				stopY = (float) (radio * Math.sin(anguloX)) + cy;
				inicioMY1 = (cy + dimenRadioInferiorManillas);
				inicioMY2 = (cy - dimenRadioInferiorManillas);				
			}else{
				if(anguloHora<=270){
					anguloX = Math.toRadians(270 - anguloHora);
					stopX = cx - (float) (radio * Math.cos(anguloX));
					stopY = (float) (radio * Math.sin(anguloX)) + cy;
				}else{
					anguloX = Math.toRadians(anguloHora - 270);
					stopX = cx - (float) (radio * Math.cos(anguloX));
					stopY = cy - (float) (radio * Math.sin(anguloX));
					inicioMY1 = (cy + dimenRadioInferiorManillas);
					inicioMY2 = (cy - dimenRadioInferiorManillas);				
				}
			}
		}				
		if(iHora!=-1){
			canvas.drawText(textoHora, stopX, stopY, pExt);							
		}else{
			Path pReg = new Path();
			pReg.moveTo((float) inicioMX1, (float)inicioMY1);
			pReg.lineTo(stopX, stopY);
			pReg.lineTo((float) inicioMX2, (float) inicioMY2);
			pReg.lineTo((float) inicioMX1, (float)inicioMY1);
			pReg.close();			
			if(bManillaHora){
				canvas.drawPath(pReg, pManillaH);
				rManillaHoras = new RectF();
				pReg.computeBounds(rManillaHoras, false);				
			}else{
				canvas.drawPath(pReg, pManillaM);
				rManillaMinutos = new RectF();
				pReg.computeBounds(rManillaMinutos, false);				
			}
		}
	}

	public void Inicializa(){
		getHolder().addCallback(this);		
		Resources res = this.getResources();
		pb = new Paint();
		pb.setColor(Color.WHITE);
		pExt = new Paint();
		pExt.setAntiAlias(true);
		pExt.setColor(Color.RED);
		pExt.setStyle(Paint.Style.STROKE);
		pExt.setStrokeWidth(res.getDimension(R.dimen.anchoLineaReloj));
		pExt.setTextSize(res.getDimensionPixelSize(R.dimen.anchoTexto));
		pExt.setTypeface(Typeface.SANS_SERIF);
		//pExt.setShadowLayer(5, 1, 1, Color.GRAY);
		pInt = new Paint(pExt);
		pInt.setColor(Color.DKGRAY);
		pInt.setStyle(Paint.Style.FILL);
		pManillaH = new Paint(pInt);
		pManillaM = new Paint(pManillaH);
		pManillaM.setColor(Color.GRAY);
		dimenMargenRadioReloj = res.getDimension(R.dimen.margen_radio_reloj);
		dimenRadioInteriorReloj = res.getDimension(R.dimen.radio_interior_reloj);
		dimenRadioInferiorManillas = res.getDimension(R.dimen.radio_inferior_manillas);		
	}	
	
	public void setHora(int h, int m){
		if (h>12){
			hora = h - 12;
		}else{
			hora = h;
		}
		if(h==12){
			hora = 0;
		}
		minutos = m;
		if(bCreado){
			this.invalidate();
		}
	}
	
}
