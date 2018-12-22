package com.felhr.serialportexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.felhr.serialportexample.controls.Carousel;
import com.felhr.serialportexample.controls.CarouselAdapter;
import com.felhr.serialportexample.controls.CarouselAdapter.OnItemClickListener;
import com.felhr.serialportexample.controls.CarouselAdapter.OnItemSelectedListener;
import com.felhr.serialportexample.controls.CarouselItem;

public class MenuActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_screen);
        Carousel carousel = (Carousel)findViewById(R.id.carousel);
        carousel.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(CarouselAdapter<?> parent, View view,
					int position, long id) {	
				
				Toast.makeText(MenuActivity.this,
						String.format("%s has been clicked",
						((CarouselItem)parent.getChildAt(position)).getName()), 
						Toast.LENGTH_SHORT).show();
			}
        	
        });

        carousel.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(CarouselAdapter<?> parent, View view,
					int position, long id) {


			}

			public void onNothingSelected(CarouselAdapter<?> parent) {
			}
        	
        }
        );
        
    }
    
}

