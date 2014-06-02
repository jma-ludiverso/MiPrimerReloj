package net.jma.miprimerreloj;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class Reloj extends Activity implements OnTimeChangedListener {
	
	analogico reloj;
	TimePicker tDigital;
	RelativeLayout rlAnalogico;
	boolean bEvento=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reloj);
		
		tDigital = (TimePicker)findViewById(R.id.timeDigital);
		tDigital.setIs24HourView(true);
		tDigital.setOnTimeChangedListener(this);
		
		rlAnalogico = (RelativeLayout)findViewById(R.id.lAnalogico);
		reloj = (analogico)findViewById(R.id.analogico);		
		reloj.setHora(tDigital.getCurrentHour(),tDigital.getCurrentMinute());		
		Resources res = getResources();
		BitmapDrawable bm = (BitmapDrawable) res.getDrawable(R.drawable.fondo_reloj);
		bm.setGravity(Gravity.CENTER);
		reloj.setBackgroundDrawable(bm);		
		
		if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
			this.onConfigurationChanged(this.getResources().getConfiguration());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reloj, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_24AMPM:
			tDigital.setIs24HourView(!tDigital.is24HourView());
			return true;
		case R.id.action_Hora:
			Time t = new Time();
			t.setToNow();
			tDigital.setCurrentHour(t.hour);
			tDigital.setCurrentMinute(t.minute);
			//reloj.setHora(t.hour,t.minute);					
			return true;
		case R.id.action_Config:
			return true;
		default:
			return super.onOptionsItemSelected(item);			
		}
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		if(bEvento){
			reloj.setHora(hourOfDay, minute);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		RelativeLayout.LayoutParams pars = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams parsRL = null;
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			pars.addRule(RelativeLayout.ALIGN_LEFT);
			pars.addRule(RelativeLayout.CENTER_VERTICAL);
			parsRL = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
			parsRL.addRule(RelativeLayout.RIGHT_OF, tDigital.getId());
			parsRL.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		}else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
			pars.addRule(RelativeLayout.CENTER_HORIZONTAL);
			parsRL = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
			parsRL.addRule(RelativeLayout.BELOW, tDigital.getId());			
		}
		tDigital.setLayoutParams(pars);
		rlAnalogico.setLayoutParams(parsRL);
	}

	public void CambiaHora(int hora, int minutos){
		bEvento = false;
		if(hora==0 && !tDigital.is24HourView()){
			hora = 12;
		}
		if(tDigital.is24HourView() && tDigital.getCurrentHour()>12){
			hora = hora + 12;
		}
		tDigital.setCurrentHour(hora);
		tDigital.setCurrentMinute(minutos);
		bEvento = true;
	}

}
